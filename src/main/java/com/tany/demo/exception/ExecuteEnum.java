package com.tany.demo.exception;

public enum ExecuteEnum {


    SUCCESS(0, "success"),
    WARN_UNKNOWN(500, "unknown warn"),
    ERROR_UNKNOWN(1000, "unknown error"),

    ERROR_HBASE_EXECUTE(5300, "hbase execute error"),

    ERROR_ZOOKEEPER_EXECUTE(5400, "zookeeper error"),
    ;

    private int code;
    private String msg;

    private ExecuteEnum(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
    public String getMsg(int code){
        for (ExecuteEnum e : ExecuteEnum.values()) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return null;
    }

    public static ExecuteEnum getExecuteEnum(int code){
        for (ExecuteEnum e : ExecuteEnum.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
