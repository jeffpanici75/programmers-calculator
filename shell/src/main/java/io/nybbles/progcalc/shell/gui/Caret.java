package io.nybbles.progcalc.shell.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Caret extends JComponent {
    private ActionListener _listener;
    private Dimension _charSize;
    private Document _document;
    private boolean _visible;
    private Bounds _bounds;
    private int _y, _x;

    private void clampToBounds() {
        if (_x < _bounds.left) {
            _x = _bounds.left;
        } else if (_x > _bounds.right) {
            _x = _bounds.right;
        }
        if (_y < _bounds.top) {
            _y = _bounds.top;
        } else if (_y > _bounds.bottom) {
            _y = _bounds.bottom;
        }
    }

    public Caret(Document document, int y, int x, ActionListener listener) {
        setOpaque(false);

        _y = y;
        _x = x;
        _listener = listener;
        _document = document;
        _document.setCaret(this);
        _charSize = _document.getCharSize();
        var timer = new Timer(250, e -> {
            _visible = !_visible;
            _listener.actionPerformed(new ActionEvent(this, 0, "blink"));
        });
        timer.setRepeats(true);
        timer.start();
        setLimit(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (_visible) {
            var g2 = (Graphics2D) g;
            g2.setColor(new Color(0xffffffef, true));
            g2.fillRect(
                    _x * (_charSize.width - 1),
                    _y * (_charSize.height - 1),
                    _charSize.width,
                    _charSize.height);
        }
    }

    public int getY() {
        return _y;
    }

    public int getX() {
        return _x;
    }

    public void moveUp() {
        if (_y > _bounds.top)
            _y--;
        else
            _y = _bounds.top;
    }

    public void moveEnd() {
        _x = _bounds.right;
    }

    public void moveHome() {
        _x = _bounds.left;
    }

    public void moveDown() {
        if (_y < _bounds.bottom) {
            _y++;
        } else {
            _y = _bounds.bottom;
            if (_y == _document.getDisplayHeight())
                _document.scrollUp();
        }
    }

    public void moveLeft() {
        if (_x > _bounds.left) {
            _x--;
        } else {
            if (_y > _bounds.top) {
                _x = _bounds.right;
                _y--;
            } else {
                _x = _bounds.left;
                _y = _bounds.top;
            }
        }
    }

    public void moveRight() {
        if (_x < _bounds.right) {
            _x++;
        } else {
            if (_bounds.bottom > _bounds.top)
                _x = _bounds.left;
            moveDown();
        }
    }

    public Bounds getLimit() {
        return _bounds;
    }

    public void carriageReturn() {
        moveDown();
        moveHome();
    }

    public void moveTo(int y, int x) {
        _y = y;
        _x = x;
        clampToBounds();
    }

    public void setLimit(Bounds bounds) {
        _bounds = bounds;
        if (_bounds == null) {
            _bounds = new Bounds(
                    0,
                    0,
                    _document.getDisplayWidth(),
                    _document.getDisplayHeight());
        } else {
            clampToBounds();
        }
    }
}
