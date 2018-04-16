package com.tany.demo.httpUtils;

public class HttpResponseModel {
    private int code;
    private String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpResponseModel{" +
                "code=" + code +
                ", content='" + content + '\'' +
                '}';
    }
}
