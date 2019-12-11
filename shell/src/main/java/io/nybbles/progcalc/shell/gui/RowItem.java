package io.nybbles.progcalc.shell.gui;

import asciiPanel.AsciiCharacterData;

public final class RowItem {
    public RowItem(int y, int x, AsciiCharacterData[] row) {
        this.x = x;
        this.y = y;
        this.row = row;
    }
    public int x, y;
    public AsciiCharacterData[] row;
}
