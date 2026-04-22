package com.example.takeway.firstspringboot.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; //状态码
    private String message; // 错误信息
    private T data; // 成功时返回的数据

    //成功的快捷方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200); // 成功状态码
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> result =success(data);
        result.setMessage(msg);
        return result;
    }

    //失败时的快捷方法
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(null);
        return result;
    }
}
