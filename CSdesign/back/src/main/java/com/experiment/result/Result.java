package com.experiment.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Boolean success; //true成功，false失败
    private String msg; //信息
    private T data; //数据

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.success = true;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> success(String msg, T object) {
        Result<T> result = new Result<T>();
        result.success = true;
        result.data = object;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.success = false;
        result.msg = msg;
        return result;
    }



    /*public Result(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result(Boolean success, String msg, String url) {
        this.success = success;
        this.msg = msg;
        this.url = url;
    }*/
}
