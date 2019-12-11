package io.nybbles.progcalc.common;

public class ResultMessage {
    private String _code;
    private String _message;
    private String _details;
    private ResultMessageType _type = ResultMessageType.Info;

    public ResultMessage() {
    }

    public ResultMessage(ResultMessageType type, String code, String message) {
        _type = type;
        _code = code;
        _message = message;
    }

    public ResultMessage(ResultMessageType type, String code, String message, String details) {
        this(type, code, message);
        _details = details;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        this._code = code;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getDetails() {
        return _details;
    }

    public void setDetails(String details) {
        _details = details;
    }

    public ResultMessageType getType() {
        return _type;
    }
}
