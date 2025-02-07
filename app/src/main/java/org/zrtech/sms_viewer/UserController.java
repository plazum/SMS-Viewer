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

    @GetMapping("/")
    public ResponseBody login() {
        String html = """
<html lang="zh">
    <head><title>短信查看器</title></head>
    <body style="text-align: center;">
        <form action="sms" method="post">
            <label>请输入密码：<input type="password" name="password"></label>
            <button>登录</button>
        <form>
    </body>
</html>
""";
        return new StringBody(html, MediaType.TEXT_HTML);
    }

    @PostMapping("/sms")
    public ResponseBody sms(Context context, @RequestParam("password") String password) {
        if (password.equals(MainActivity.password)) {
            String html = """
<html lang="zh">
    <head><title>短信查看器</title></head>
    <body style="font-size: large; padding: 0.5em;">
        <pre style="word-wrap: break-word; white-space: pre-wrap;">%s</pre>
    </body>
</html>
""".formatted(SmsGetter.text(context));
            return new StringBody(html, MediaType.TEXT_HTML);
        } else {
            String html = """
<html lang="zh">
    <head><title>短信查看器</title></head>
    <body style="text-align: center;">
        密码错误！
    </body>
</html>
""";
            return new StringBody(html, MediaType.TEXT_HTML);
        }
    }
}
