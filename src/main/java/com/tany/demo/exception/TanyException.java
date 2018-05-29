package com.tany.demo.exception;

import java.io.Serializable;

public class TanyException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 错误码
     */
    private Integer errCode;
    /**
     * 错误信息
     */
    private String errMsg;

    public int getErrorCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public TanyException(Throwable e) {
        super(e);
        this.errCode = ExecuteEnum.ERROR_UNKNOWN.getCode();
        this.errMsg = ExecuteEnum.ERROR_UNKNOWN.getMsg();
    }


    public TanyException(ExecuteEnum errorConst, String errInfo,Throwable e) {
        super(e);
        this.errCode = errorConst.getCode();
        this.errMsg = errInfo;
    }

    public TanyException(ExecuteEnum errorConst,Throwable e) {
        super(e);
        this.errCode = errorConst.getCode();
        this.errMsg = errorConst.getMsg();
    }

    public TanyException(ExecuteEnum errorConst, String errInfo) {
        super();
        this.errCode = errorConst.getCode();
        this.errMsg = errInfo;
    }

    public TanyException(ExecuteEnum errorConst) {
        super();
        this.errCode = errorConst.getCode();
        this.errMsg = errorConst.getMsg();
    }

    @Override
    public String toString() {
        return this.errCode + ":" + this.errMsg;
    }
}
