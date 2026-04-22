package com.example.takeway.firstspringboot.exception;

import com.example.takeway.firstspringboot.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        // 打印到控制台留给后端自己看
        System.err.println("【业务异常被拦截】: " + e.getMessage());

        // 返回给前端一个 HTTP 200，但带有错误信息
        return Result.error(400, e.getMessage());
    }

    // 兜底方案：捕获那些连我们自己都没预料到的系统死机异常（比如空指针、数据库断开）
    @ExceptionHandler(Exception.class)
    public Result<String> handleSystemException(Exception e) {
        e.printStackTrace(); // 打印极其详细的日志方便排查 Bug
        return Result.error(500, "服务器开小差了，请稍后再试！");
    }
}
