package com.motorfans.test;

import com.motorfans.common.Context;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.util.DigestUtils;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.UUID;

public class test {

    public static void main(String[] args) throws IOException {
        /*String a = Context.getProp(Context.VIDEO_STORE_PATH);
        System.out.println(a);*/
        /*String url = "http://www.youtube.com";
        String html = Jsoup.connect(url)
                .header("user-agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36")
                .header("x-youtube-client-name", "2")
                .header("x-youtube-client-version", "2.20181103")
                .get().html();
        System.out.println(html);*/
        /*while (true) {
            byte[] bytes = Jsoup.connect("http://mp.weixin.qq.com/mp/verifycode?cert=1541595073136.0247").ignoreContentType(true).execute().bodyAsBytes();
            File dir = new File("");
            if(!dir.exists() && dir.isDirectory()){
                dir.mkdir();
            }
            File file = new File("D:\\Test\\" + UUID.randomUUID().toString() + ".png");
            FileOutputStream fp = new FileOutputStream(file);
            fp.write(bytes);
            fp.close();
        }*/
        Connection connection = getJsoupConn("http://httpbin.org/ip").header("Connection", "keep-alive").ignoreContentType(true);
        for(int i=0; i<15; i++) {
            String ip = connection.get().text();
            System.out.println(ip);
        }
    }

    public static Connection getJsoupConn(String url) {
        Authenticator.setDefault(new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("HUG5E4399990WM4D", "D44E8D54B2FD5431".toCharArray());
            }
        });
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020));
        Connection conn = Jsoup.connect(url).header("Proxy-Switch-Ip", "no").proxy(proxy);
        return conn;
    }
}
