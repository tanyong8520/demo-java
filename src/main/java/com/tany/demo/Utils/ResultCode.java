package com.tany.demo.Utils;

public enum ResultCode {
    /**操作成功*/
    SUCCESS("0000","success"),
    /**操作失败*/
    FAILED("0001","failed"),
    /**未知错误*/
    UNKNOW_ERROR("0002","unknow error"),
    /**数据库相关错误*/
    DATABASE_ERROR("0003","database error"),
    /**参数错误*/
    PARAM_ERROR("1000","parameter error"),
    /**不符合启用条件*/
    ACTIVE_CONDITION_ERROR("1001","activeCondition  error"),
    /**不符合停用条件*/
    DISACTIVE_CONDITION_ERROR("1002","disActiveCondition  error"),
    /**不符合删除条件*/
    DELETE_CONDITION_ERROR("1003","deleteCondition  error"),
    /**操作错误*/
    OPERATION_ERROR("9999","operation error")
    ;

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
