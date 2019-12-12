### Programmers' Calculator 

Ever wonder how to build a compiler and a byte code virtual machine/interpreter?
That's the primary reason I wrote this project.  Hopefully the code is clear and concise.

I don't normally use Java, but I'm impressed with how well it runs. There are many areas 
I could improve and enhance.  Nothing is ever done, only in a state of being perfected.

<div align="center">
    <a href="https://raw.github.com/jeffpanici75/programmers-calculator/master/assets/prog-calc-example.png">
        <img src="/assets/prog-calc-example.png" width="50%" height="50%" />
    </a>
</div>

### Example usage

Any standard infix expression works, for example:

`(empty:0) > 2 * 2`

The parser recognizes decimal (default), hexadecimal with the `$` prefix, 
octal with the `@` prefix, and binary with the `%` prefix.  Numbers may also contain
`_` characters as separators; these are ignored.

`(empty:0) > $ff shl 2`

Valid binary operators are: `+`, `-`, `*`, `/`, `%`, `^`, `&`, `|`, `||`, `&&`, `shl`, `shr`, `rol`, `ror`, `xor`

Valid unary operators are: `-`, and `~`

Valid statement commands are: `?`

The `?` command outputs a table of base conversions and 
shows the result value sized as `byte`, `word`, `dword`, and `qword`.

All values inside of the virtual machine are 64-bit (`qword`) values.

Here's an example of the `?` command:

`(empty:0) > ? -2 * $fe`

The `(empty:0)` prefix on the prompt is showing the top of the stack and the stack depth. I'll be adding
intrinsic functions that allow pushing to and popping from the stack.

Symbols are supported.  Any expression can be assigned to a symbol:

`(empty:0) > PI := 3.14159; R := 16.1297; C := (PI * R) ^ 2`

`(empty:0) > ? C`

Will show the result of the expression evaluation for the symbol `C`.  The `F2` key will show you
the entire symbol table.

The `F3` key shows the registers of the virtual machine.  And `F1` turns byte code disassembly on/off.

### Working with the code

I use IntelliJ as my IDE, but the code should work in Eclipse.  Gradle
is the build system.

The code is built against and requires Java 13 to run.  I make use of preview
features in the Java language, so the `--enable-preview` is required for compiling
and running.

The compiler and virtual machine don't make use of any 3rd party libraries.  The tests 
do require JUnit.

There are two shell implementations: one runs in a normal terminal and the other
is terminal-like but a GUI application.  The raw terminal version isn't as
full-featured.  To do the raw terminal version properly I'd need to use
ncurses, but I sense a rabbit hole here so it's on my TODO list.

The GUI version is much nicer and makes use of AsciiPanel.  It has a nice DOS retro feel
to it, and this version should run everywhere as-is.