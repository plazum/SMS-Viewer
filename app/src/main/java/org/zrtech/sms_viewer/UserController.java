package org.zrtech.sms_viewer;

import android.content.Context;

import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.MediaType;

@RestController
public class UserController {

    public static final String result_html = """
        <html lang="zh">
            <head><title>短信查看器</title></head>
            <body style="font-size: large; padding: 0.5em;">
                <pre style="word-wrap: break-word; white-space: pre-wrap;">%s</pre>
            </body>
        </html>
        """;
    public static final String password_error_html = """
        <html lang="zh">
            <head><title>短信查看器</title></head>
            <body style="text-align: center;">
                密码错误！
            </body>
        </html>
        """;
    public static final String method_error_html = """
        <html lang="zh">
            <head><title>短信查看器</title></head>
            <body style="text-align: center;">
                网页请求方法错误！请退回到首页并刷新。
            </body>
        </html>
        """;

    @GetMapping("/")
    public ResponseBody login() {
        String html = """
            <html lang="zh">
                <head><title>短信查看器</title></head>
                <body style="text-align: center;">
                    <form action="sms" method="%s">
                        <label>请输入密码：<input type="password" name="password"></label>
                        <button>登录</button>
                    <form>
                </body>
            </html>
            """.formatted(MainActivity.password.isEmpty() ? "get" : "post");
        return new StringBody(html, MediaType.TEXT_HTML);
    }

    @PostMapping("/sms")
    public ResponseBody sms(Context context, @RequestParam(name = "password", required = false, defaultValue = "") String password) {
        // 如果密码已关闭，那么应该使用GET方法来请求
        if (MainActivity.password.isEmpty()) {
            return new StringBody(method_error_html, MediaType.TEXT_HTML);
        }
        // 如果密码已开启，并且输入密码和设置密码一致，那么密码正确
        if (password.equals(MainActivity.password)) {
            String html = result_html.formatted(SmsGetter.text(context));
            return new StringBody(html, MediaType.TEXT_HTML);
        } else {
            return new StringBody(password_error_html, MediaType.TEXT_HTML);
        }
    }

    @GetMapping("/sms")
    public ResponseBody sms_no_password(Context context, @RequestParam(name = "password", required = false, defaultValue = "") String password) {
        // 如果密码已开启，那么应该使用POST方法来请求
        if (!MainActivity.password.isEmpty()) {
            return new StringBody(method_error_html, MediaType.TEXT_HTML);
        }
        // 如果密码已关闭，并且输入密码为空，那么密码正确
        if (password.isEmpty()) {
            String html = result_html.formatted(SmsGetter.text(context));
            return new StringBody(html, MediaType.TEXT_HTML);
        } else {
            return new StringBody(password_error_html, MediaType.TEXT_HTML);
        }
    }
}
