package com.tany.demo.Utils;

public class ResultModel {
    private String code;
    private String msg;
    private Object data;

    public static ResultModel success() {
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.SUCCESS);
        model.setData(ResultCode.SUCCESS.getCode());
        return model;
    }

    public static ResultModel failed(){
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.FAILED);
        model.setData(ResultCode.UNKNOW_ERROR.getCode());
        return model;
    }

    public static ResultModel success(Object obj) {
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.SUCCESS);
        model.setData(obj);
        return model;
    }

    public static ResultModel failed(Object obj){
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.FAILED);
        model.setData(obj);
        return model;
    }

    public static ResultModel operationError() {
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.OPERATION_ERROR);
        return model;
    }

    public static ResultModel paramError() {
        ResultModel model = new ResultModel();
        model.setCode(ResultCode.PARAM_ERROR);
        return model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(ResultCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
