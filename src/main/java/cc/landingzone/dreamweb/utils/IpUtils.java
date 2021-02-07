package cc.landingzone.dreamweb.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
public class IpUtils {

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String clientIpAddr;
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xff)){
            // X-Forwarded-For: 用户真实IP, 代理服务器1-IP， 代理服务器2-IP，...
            // 文档: https://help.aliyun.com/document_detail/54007.html
            clientIpAddr = xff.split("[,，]")[0];
        } else {
            clientIpAddr = request.getRemoteAddr();
        }
        return clientIpAddr;
    }
}
