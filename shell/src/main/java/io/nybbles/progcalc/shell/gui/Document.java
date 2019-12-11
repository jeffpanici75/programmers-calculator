package io.nybbles.progcalc.shell.gui;

import asciiPanel.AsciiCharacterData;
import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import java.awt.*;

public class Document {
    private AsciiCharacterData[][] _document;
    private Color _fgColor = Color.YELLOW;
    private Color _bgColor = Color.BLUE;
    private int _virtualHeight;
    private int _displayHeight;
    private int _displayWidth;
    private int _virtualWidth;
    private AsciiPanel _panel;
    private Caret _caret;
    private int _row;
    private int _col;

    private void clearDocument() {
        for (var row = 0; row < _displayHeight; ++row) {
            for (var col = 0; col < _displayWidth; ++col) {
                var data = _document[row][col];
                if (data == null) {
                    data = new AsciiCharacterData();
                    _document[row][col] = data;
                }
                data.character = ' ';
                data.backgroundColor = _bgColor;
                data.foregroundColor = _fgColor;
            }
        }
    }

    RowItem getRowAtCaret() {
        var x = _caret.getX();
        var y = _caret.getY();
        return new RowItem(y, x, _document[y]);
    }

    CellItem getCellAtCaret() {
        var y = _caret.getY();
        var x = _caret.getX();
        return new CellItem(y, x, _document[y][x]);
    }

    private void updatePanelWithCell(CellItem cell) {
        _panel.write(
                cell.data.character,
                cell.x,
                cell.y,
                cell.data.foregroundColor,
                cell.data.backgroundColor);
    }

    public Document(
            int virtualHeight,
            int virtualWidth,
            int displayHeight,
            int displayWidth) {
        _virtualHeight = virtualHeight;
        _virtualWidth = virtualWidth;
        _displayHeight = displayHeight;
        _displayWidth = displayWidth;

        _document = new AsciiCharacterData[virtualHeight][virtualWidth];
        _panel = new AsciiPanel(displayWidth, displayHeight + 1, AsciiFont.CP437_9x16);
        _panel.setDefaultBackgroundColor(_bgColor);
        _panel.setDefaultForegroundColor(_fgColor);
        _panel.clear(' ');

        clearDocument();
    }

    public void tab() {
    }

    public void clear() {
        clearDocument();
        _panel.clear(' ');
        _caret.moveTo(0, 0);
    }

    public void scrollUp() {
        var width = getDisplayWidth();
        var height = getDisplayHeight();

        for (var y = 1; y < height + 1; ++y) {
            for (var x = 0; x < width + 1; ++x) {
                var dest = _document[y - 1][x];
                var src = _document[y][x];
                dest.character = src.character;
                dest.foregroundColor = src.foregroundColor;
                dest.backgroundColor = src.backgroundColor;
                _panel.write(dest.character, x, y - 1, dest.foregroundColor, dest.backgroundColor);
            }
        }

        for (var x = 0; x < width + 1; ++x) {
            var data = _document[height][x];
            data.character = ' ';
            data.backgroundColor = _bgColor;
            data.foregroundColor = _fgColor;
            _panel.write(data.character, x, height, data.foregroundColor, data.backgroundColor);
        }
    }

    public void carriageReturn() {
        _caret.carriageReturn();
    }

    public Dimension getCharSize() {
        return new Dimension(_panel.getCharWidth(), _panel.getCharHeight());
    }

    public Dimension getDimensions() {
        var charWidth = _panel.getCharWidth();
        var charHeight = _panel.getCharHeight();
        return new Dimension(
                charWidth * _panel.getWidthInCharacters(),
                (charHeight * _panel.getHeightInCharacters()) + (charHeight + 8));
    }

    public void deleteAtCaret() {
        var item = getRowAtCaret();
        System.arraycopy(
                item.row,
                item.x + 1,
                item.row,
                item.x,
                item.row.length - item.x - 1);
        item.row[item.row.length - 1].character = ' ';
        for (var col = item.x; col < item.row.length; ++col) {
            var data = item.row[col];
            _panel.write(
                    data.character,
                    col,
                    item.y,
                    data.foregroundColor,
                    data.backgroundColor);
        }
    }

    public void backSpaceAtCaret() {
        var item = getRowAtCaret();
        var start = Math.max(0, item.x - 1);
        System.arraycopy(
                item.row,
                item.x,
                item.row,
                start,
                item.row.length - start - 1);
        item.row[item.row.length - 1].character = ' ';
        for (var col = start; col < item.row.length; ++col) {
            var data = item.row[col];
            _panel.write(
                    data.character,
                    col,
                    item.y,
                    data.foregroundColor,
                    data.backgroundColor);
        }
        _caret.moveLeft();
    }

    public AsciiPanel getPanel() {
        return _panel;
    }

    public int getDisplayWidth() {
        return _displayWidth - 1;
    }

    public int getDisplayHeight() {
        return _displayHeight - 1;
    }

    public void setCaret(Caret caret) {
        _caret = caret;
    }

    public void insertAtCaret(char ch) {
        var item = getRowAtCaret();
        for (var col = item.row.length - 2; col >= item.x; --col) {
            var dest = item.row[col + 1];
            var src = item.row[col];
            dest.character = src.character;
            dest.foregroundColor = src.foregroundColor;
            dest.backgroundColor = src.backgroundColor;
        }
        item.row[item.x].character = ch;
        for (var col = item.x; col < item.row.length; ++col) {
            var data = item.row[col];
            _panel.write(
                    data.character,
                    col,
                    item.y,
                    data.foregroundColor,
                    data.backgroundColor);
        }
        _caret.moveRight();
    }

    public void writeAtCaret(char ch) {
        var cell = getCellAtCaret();
        cell.data.character = ch;
        _panel.write(
                cell.data.character,
                cell.x,
                cell.y,
                cell.data.foregroundColor,
                cell.data.backgroundColor);
        _caret.moveRight();
    }

    public void clearRegion(Bounds rect) {
        for (var y = rect.top; y <= rect.bottom; ++y) {
            for (var x = rect.left; x < rect.right; ++x) {
                var data = _document[y][x];
                data.character = ' ';
                _panel.write(
                        data.character,
                        x,
                        y,
                        data.foregroundColor,
                        data.backgroundColor);
            }
        }
    }

    public String getString(Bounds rect) {
        var builder = new StringBuilder();
        for (var y = rect.top; y <= rect.bottom; ++y) {
            for (var x = rect.left; x < rect.right; ++x) {
                var data = _document[y][x];
                builder.append(data.character);
            }
        }
        return builder.toString().trim();
    }

    public void writeAtCaret(String value) {
        for (var i = 0; i < value.length(); ++i) {
            var cell = getCellAtCaret();
            var ch = value.charAt(i);
            if (ch == '\n') {
                _caret.moveDown();
                _caret.moveHome();
                continue;
            }
            cell.data.character = ch;
            updatePanelWithCell(cell);
            _caret.moveRight();
        }
    }

    public Color getDefaultForegroundColor() {
        return _fgColor;
    }

    public Color getDefaultBackgroundColor() {
        return _bgColor;
    }

    public void setDefaultBackgroundColor(Color color) {
        _bgColor = color;
    }

    public void setDefaultForegroundColor(Color color) {
        _fgColor = color;
    }

    public Color setForegroundColor(int y, int x, Color color) {
        var data = _document[y][x];
        var fgColor = data.foregroundColor;
        data.foregroundColor = color;
        _panel.write(data.character, x, y, data.foregroundColor, data.backgroundColor);
        return fgColor;
    }

    public Color setBackgroundColor(int y, int x, Color color) {
        var data = _document[y][x];
        var bgColor = data.backgroundColor;
        data.backgroundColor = color;
        _panel.write(data.character, x, y, data.foregroundColor, data.backgroundColor);
        return bgColor;
    }

    public void writeAtCaret(String value, Color bgColor, Color fgColor) {
        for (var i = 0; i < value.length(); ++i) {
            var cell = getCellAtCaret();
            var ch = value.charAt(i);
            if (ch == '\n') {
                _caret.moveDown();
                _caret.moveHome();
                continue;
            }
            cell.data.character = ch;
            cell.data.backgroundColor = bgColor;
            cell.data.foregroundColor = fgColor;
            updatePanelWithCell(cell);
            _caret.moveRight();
        }
    }
}
