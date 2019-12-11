package io.nybbles.progclac.compiler;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.vm.*;
import io.nybbles.progclac.compiler.ast.*;

public class ByteCodeCompiler {
    private int _labelId = 1;
    private VirtualMachine _vm;
    private ProgramAstNode _program;
    private SymbolTable _symbolTable;
    private RegisterAllocator _registerAllocator = new RegisterAllocator();

    private String makeLabel(String prefix) {
        return String.format("%s%d", prefix, _labelId++);
    }

    private static class VisitResult {
        public VisitResult(boolean success) {
            this.success = success;
        }
        public boolean success;
        public RegisterName target;
    }

    private void addError(
            Result r,
            String code,
            String message,
            int start,
            int end) {
        var underline = "^";
        var length = (end - start) - 1;
        if (length > 0) {
            underline += "~".repeat(length) + ">";
        }
        var indicator = String.format(
                "%s %s",
                underline,
                message);
        String buffer = _program.getSource() +
                "\n" +
                String.format("%" + (start + 7 + indicator.length()) + "s", indicator);
        r.addError(code, buffer);
    }

    private VisitResult visitNode(
            Result r,
            ByteCodeEmitter emitter,
            AstNode node) {
        switch (node.getType()) {
            case Program -> {
                var result = new VisitResult(true);
                var program = (ProgramAstNode) node;
                emitter.label("start");
                for (var statement : program.getStatements()) {
                    var statementResult = visitNode(r, emitter, statement);
                    if (!statementResult.success) {
                        result.success = false;
                        return result;
                    } else {
                        result.target = statementResult.target;
                    }
                }
                emitter.exit();
                return result;
            }
            case Statement -> {
                var statement = (StatementAstNode) node;
                return visitNode(r, emitter, statement.getExpression());
            }
            case Identifier -> {
                var result = new VisitResult(true);
                var identifier = (IdentifierAstNode) node;
                var symbolName = identifier.getValue();
                if (!_symbolTable.hasSymbol(symbolName)) {
                    addError(
                            r,
                            "C013",
                            "undefined symbol",
                            identifier.getToken().getStart(),
                            identifier.getToken().getEnd());
                    result.success = false;
                    return result;
                }
                var symbolAddress = _symbolTable.getSymbolAddress(symbolName);
                var registerType = _symbolTable.getSymbolType(identifier.getValue()).getRegisterType();
                result.target = _registerAllocator.allocateRegister(registerType);
                var addressReg = _registerAllocator.allocateRegister(RegisterType.U64);
                emitter
                        .move(addressReg, symbolAddress)
                        .load(addressReg, result.target);
                _registerAllocator.releaseRegister(addressReg);
                return result;
            }
            case Assignment -> {
                var result = new VisitResult(true);
                var assignment = (AssignmentAstNode) node;
                var valueResult = visitNode(r, emitter, assignment.getValue());
                if (!valueResult.success) {
                    result.success = false;
                    return result;
                }
                var valueNumericType = valueResult.target.getType().getNumericType();
                var symbolAddress = _symbolTable.allocateSymbol(
                        valueNumericType,
                        ((IdentifierAstNode) assignment.getIdentifier()).getValue());
                var addressReg = _registerAllocator.allocateRegister(RegisterType.U64);
                emitter
                        .move(addressReg, symbolAddress)
                        .store(addressReg, valueResult.target);
                _registerAllocator.releaseRegister(addressReg);
                result.target = valueResult.target;
                return result;
            }
            case FloatLiteral -> {
                var result = new VisitResult(true);
                var literal = (DoubleLiteralAstNode) node;
                result.target = _registerAllocator.allocateRegister(RegisterType.F64);
                emitter.move(result.target, literal.getValue());
                return result;
            }
            case UnaryOperator -> {
                var result = new VisitResult(true);
                var unaryOp = (UnaryOperatorAstNode) node;
                var lhsResult = visitNode(r, emitter, unaryOp.getLhs());
                if (!lhsResult.success) {
                    result.success = false;
                    return result;
                }
                result.target = _registerAllocator.allocateRegister(lhsResult.target.getType());
                switch (unaryOp.getOperatorType()) {
                    case Print     -> {
                        emitter
                                .move(result.target, lhsResult.target)
                                .push(lhsResult.target)
                                .push(lhsResult.target.getType().getNumericType().ordinal())
                                .push(1)
                                .trap(io.nybbles.progcalc.vm.Constants.Traps.PRINT);
                    }
                    case Negate    -> emitter.neg(result.target, lhsResult.target);
                    case BinaryNot -> emitter.not(result.target, lhsResult.target);
                }
                _registerAllocator.releaseRegister(lhsResult.target);
                return result;
            }
            case IntegerLiteral -> {
                var result = new VisitResult(true);
                var literal = (IntegerLiteralAstNode) node;
                result.target = _registerAllocator.allocateRegister(RegisterType.U64);
                emitter.move(result.target, literal.getValue());
                return result;
            }
            case BooleanLiteral -> {
                var result = new VisitResult(true);
                var literal = (BooleanLiteralAstNode) node;
                result.target = _registerAllocator.allocateRegister(RegisterType.U64);
                emitter.move(result.target, literal.getValue() ? 1 : 0);
                return result;
            }
            case BinaryOperator -> {
                var result = new VisitResult(true);
                var binOp = (BinaryOperatorAstNode) node;
                var rhsResult = visitNode(r, emitter, binOp.getRhs());
                var lhsResult = visitNode(r, emitter, binOp.getLhs());
                if (!rhsResult.success || !lhsResult.success) {
                    result.success = false;
                    return result;
                }
                result.target = switch (binOp.getOperatorType()) {
                    case Or, Add, Sub,
                        Mul, Div, Mod,
                        Shl, Shr, Rol,
                        Ror, Pow, And, Xor -> _registerAllocator.allocateRegister(lhsResult.target.getType());
                    case Equals, NotEquals, LessThan,
                        LogicalOr, LogicalAnd, GreaterThan,
                        LessThanEquals, GreaterThanEquals -> _registerAllocator.allocateRegister(RegisterType.U64);
                };
                switch (binOp.getOperatorType()) {
                    case Or  -> emitter.or(result.target, lhsResult.target, rhsResult.target);
                    case Add -> emitter.add(result.target, lhsResult.target, rhsResult.target);
                    case Sub -> emitter.sub(result.target, lhsResult.target, rhsResult.target);
                    case Mul -> emitter.mul(result.target, lhsResult.target, rhsResult.target);
                    case Div -> emitter.div(result.target, lhsResult.target, rhsResult.target);
                    case Mod -> emitter.mod(result.target, lhsResult.target, rhsResult.target);
                    case Shl -> emitter.shl(result.target, lhsResult.target, rhsResult.target);
                    case Shr -> emitter.shr(result.target, lhsResult.target, rhsResult.target);
                    case Rol -> emitter.rol(result.target, lhsResult.target, rhsResult.target);
                    case Ror -> emitter.ror(result.target, lhsResult.target, rhsResult.target);
                    case Pow -> emitter.pow(result.target, lhsResult.target, rhsResult.target);
                    case And -> emitter.and(result.target, lhsResult.target, rhsResult.target);
                    case Xor -> emitter.xor(result.target, lhsResult.target, rhsResult.target);
                    case Equals -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.sete(result.target, result.target);
                    }
                    case NotEquals -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.setne(result.target, result.target);
                    }
                    case LessThan -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.setl(result.target, result.target);
                    }
                    case LogicalOr -> {
                        var tempReg = _registerAllocator.allocateRegister(RegisterType.U64);
                        var successLabel = makeLabel("success");
                        var shortCircuitLabel = makeLabel("short_circuit");
                        emitter
                                .move(tempReg, 1L)
                                .cmp(result.target, lhsResult.target, tempReg)
                                .beq(result.target, successLabel)
                                .cmp(result.target, rhsResult.target, tempReg)
                                .bne(result.target, shortCircuitLabel)
                                .label(successLabel)
                                .sete(result.target, result.target)
                                .label(shortCircuitLabel);
                        _registerAllocator.releaseRegister(tempReg);
                    }
                    case LogicalAnd -> {
                        var tempReg = _registerAllocator.allocateRegister(RegisterType.U64);
                        var shortCircuitLabel = makeLabel("short_circuit");
                        emitter
                                .move(tempReg, 1L)
                                .cmp(result.target, lhsResult.target, tempReg)
                                .bne(result.target, shortCircuitLabel)
                                .cmp(result.target, rhsResult.target, tempReg)
                                .bne(result.target, shortCircuitLabel)
                                .sete(result.target, result.target)
                                .label(shortCircuitLabel);
                        _registerAllocator.releaseRegister(tempReg);
                    }
                    case GreaterThan -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.setg(result.target, result.target);
                    }
                    case LessThanEquals -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.setle(result.target, result.target);
                    }
                    case GreaterThanEquals -> {
                        emitter.cmp(result.target, lhsResult.target, rhsResult.target);
                        emitter.setge(result.target, result.target);
                    }
                }
                _registerAllocator.releaseRegister(lhsResult.target);
                _registerAllocator.releaseRegister(rhsResult.target);
                return result;
            }
            case CharacterLiteral -> {
                var result = new VisitResult(true);
                var literal = (CharacterLiteralAstNode) node;
                result.target = _registerAllocator.allocateRegister(RegisterType.U64);
                emitter.move(result.target, literal.getValue());
                return result;
            }
        }
        return new VisitResult(false);
    }

    public ByteCodeCompiler(
            VirtualMachine vm,
            SymbolTable symbolTable,
            ProgramAstNode program) {
        _vm = vm;
        _program = program;
        _symbolTable = symbolTable;
    }

    public Program compile(Result r) {
        var emitter = _vm.getEmitter(0);
        var result = visitNode(r, emitter, _program);
        if (!result.success)
            return null;
        if (!emitter.emit(r))
            return null;
        var reader = _vm.getReader(
                emitter.getLabelAddress("start"),
                emitter.getAddress());
        var program = reader.read();
        program.setTargetRegister(result.target);
        return program;
    }
}
