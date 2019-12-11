package io.nybbles.progcalc.shell;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.shell.contracts.Configuration;
import io.nybbles.progcalc.shell.gui.GuiShell;
import io.nybbles.progcalc.shell.terminal.TerminalShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Program {
    private static final Logger s_logger = LoggerFactory.getLogger(Program.class);
    private Configuration _configuration;

    public Program(Configuration configuration) {
        _configuration = configuration;
    }

    private static void formatMessages(Result r) {
        for (var msg : r.getMessages()) {
            System.out.println(String.format(
                    "[%s] %s",
                    msg.getCode(),
                    msg.getMessage()));
        }
    }

    private int runTerminal(String[] args) {
        var shell = new TerminalShell(_configuration);
        var r = new Result();
        if (!shell.initialize(r)) {
            formatMessages(r);
            return 1;
        }
        var rc = shell.run();
        return rc.isSuccess() ? 0 : 1;
    }

    private void runGui(String[] args) {
        try {
            System.setProperty(
                    "apple.laf.useScreenMenuBar",
                    "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "Programmers' Calculator");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            s_logger.error("Unable to set macOS properties: {}", e.getMessage());
        }

        var shell = new GuiShell(_configuration);
        var r = new Result();
        if (!shell.initialize(r)) {
            formatMessages(r);
            return;
        }
        shell.run();
    }

    public static void main(String[] args) {
        var options = new DefaultOptions();
        options.parse(args);

        var configuration = new DefaultConfiguration(options);
        configuration.load();

        Program main = new Program(configuration);
        main.runGui(args);
    }
}
