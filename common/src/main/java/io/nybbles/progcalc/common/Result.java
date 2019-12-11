package io.nybbles.progcalc.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Result {
    private ArrayList<ResultMessage> _messages = new ArrayList<>();
    private boolean _success = true;

    public Result() {
    }

    public boolean isSuccess() {
        return _success;
    }

    public List<ResultMessage> getMessages() {
        return _messages;
    }

    public Collection<ResultMessage> messagesByType(ResultMessageType type) {
        var messages = new ArrayList<ResultMessage>();
        _messages.stream()
                .filter(x -> x.getType() == type)
                .forEach(messages::add);
        return messages;
    }

    public Collection<ResultMessage> messagesForCode(String code) {
        var messages = new ArrayList<ResultMessage>();
        _messages.stream()
                .filter(x -> x.getCode().equals(code))
                .forEach(messages::add);
        return messages;
    }

    public void addInfo(String code, String message) {
        _messages.add(new ResultMessage(ResultMessageType.Info, code, message));
    }

    public void addInfo(String code, String message, String details) {
        _messages.add(new ResultMessage(ResultMessageType.Info, code, message, details));
    }

    public void addError(String code, String message) {
        _messages.add(new ResultMessage(ResultMessageType.Error, code, message));
        _success = false;
    }

    public void addError(String code, String message, String details) {
        _messages.add(new ResultMessage(ResultMessageType.Error, code, message, details));
        _success = false;
    }

    public void addWarning(String code, String message) {
        _messages.add(new ResultMessage(ResultMessageType.Warning, code, message));
    }

    public void addWarning(String code, String message, String details) {
        _messages.add(new ResultMessage(ResultMessageType.Warning, code, message, details));
    }

}
