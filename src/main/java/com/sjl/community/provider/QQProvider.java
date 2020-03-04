package com.sjl.community.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sjl.community.config.QQLoginParams;
import com.sjl.community.dto.QQUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author song
 * @create 2020/2/14 16:16
 */
@Component
@Slf4j
public class QQProvider {

    @Autowired
    private QQLoginParams params;

    private OkHttpClient client = new OkHttpClient();

    /**
     * 获取AccessToken
     */
    public String getAccessToken(String code) throws IOException {
        String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="+params.getClient_id()+"&client_secret="+params.getClient_secret()+"&code="+code+"redirect_uri="+params.getRedirect_uri();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String str = response.body().string();
            return str.split("&")[0].split("=")[1];
        }catch (Exception e){
            log.error("获取access_token失败");
        }
        return null;
    }

    /**
     * 获取OpenId
     */
    public String getOpenId(String access_token) throws IOException {
        String url = "https://graph.qq.com/oauth2.0/me?access_token="+access_token;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(string);
            return jsonObject.getString("openid");
        }catch (Exception e){
            log.error("获取OpenId失败");
        }
        return null;
    }

    /**
     * 根据access_token获取用户信息
     */
    public QQUser getQQUser(String access_token, String openId) throws IOException {
        String url = "https://graph.qq.com/user/get_user_info?access_token="+access_token+"&oauth_consumer_key="+params.getClient_id()+"&openid="+getOpenId(openId);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try(Response response = client.newCall(request).execute()) {
            //得到的是json字符串，因此需要转为QQUser对象
            String str = response.body().string();
            return JSON.parseObject(str, QQUser.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取QQUser错误");
        }
        return null;
    }
}
