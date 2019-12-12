package io.nybbles.progcalc.shell;

public final class Constants {
    public static final String PROGRAM_VERSION = "1.0";
    public static final String COPYRIGHT_NOTICE = "Copyright (C) 2019 Jeff Panici. All rights reserved.";

    public static final class Traps {
        public static final int QUIT = 0x21;
        public static final int POP  = 0x23;
        public static final int PUSH = 0x22;
        public static final int CLEAR_SCREEN = 0x20;
    }

    public static final class Terminal {
        public static final int WIDTH = 130;
        public static final int HEIGHT = 60;
    }

    public static final class CommandHistory {
        public static final int MAX_ENTRIES = 100;
    }
}
