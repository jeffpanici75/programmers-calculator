package io.nybbles.progcalc.shell.gui;

import asciiPanel.AsciiCharacterData;

public final class CellItem {
    public CellItem(int y, int x, AsciiCharacterData data) {
        this.x = x;
        this.y = y;
        this.data = data;
    }
    public int x, y;
    public AsciiCharacterData data;
}
