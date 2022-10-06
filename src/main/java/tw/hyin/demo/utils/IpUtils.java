package tw.hyin.demo.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import tw.hyin.java.utils.Log;

import java.net.InetAddress;

/**
 * @author YingHan
 * @Description 取得客戶端真實 IP 位址
 * @since 2022-10-05
 */
public class IpUtils {

    public static String getClientIpAddr(ServerHttpRequest request) {
        //for K8s
        String ip = request.getHeaders().getFirst("X-Original-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Forwarded-For");
        }
        //for nginx
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        // 若為本機傳送，則根據網卡取本機配置的IP
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.error(e.getLocalizedMessage());
            }
            ip = inet.getHostAddress();
        }
        // 對於通過多重代理的情況，第一個IP為客戶端真實IP,多個IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
