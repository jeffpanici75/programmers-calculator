package io.nybbles.progcalc.shell;

import java.util.ArrayList;

public class CommandHistory  {
    private ArrayList<String> _commands = new ArrayList<>();
    private int _index;

    public CommandHistory() {
    }

    public void clear() {
        _commands.clear();
    }

    public String prevCommand() {
        if (_index >= _commands.size())
            return null;

        var command = _commands.get(_index);
        if (_index > 0)
            --_index;
        return command;
    }

    public String nextCommand() {
        if (_index + 1 >= _commands.size())
            return null;
        ++_index;
        return _commands.get(_index);
    }

    public void addCommand(String command) {
        var existingIndex = _commands.indexOf(command);
        if (existingIndex != -1) {
            if (existingIndex == _commands.size() - 1)
                return;
            _commands.remove(existingIndex);
        }

        _commands.add(command);
        if (_commands.size() > Constants.CommandHistory.MAX_ENTRIES) {
            _commands.remove(0);
        }
        _index = _commands.size() - 1;
    }
}
