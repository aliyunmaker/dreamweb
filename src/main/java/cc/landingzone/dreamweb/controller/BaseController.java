package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {

    public Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 输出json数据
     *
     * @param response
     * @param result
     */
    public void outputToJSON(HttpServletResponse response, Object result) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            if (null != result) {
                String resultString = JsonUtils.toJsonStringWithDatetime(result);
                response.setContentLength(resultString.getBytes("UTF-8").length);
                response.getWriter().write(resultString);
            }
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 输出text
     *
     * @param response
     * @param result
     */
    public void outputToString(HttpServletResponse response, String result) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            if (null != result) {
                response.getWriter().write(result);
            }
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 输出字节数组
     *
     * @param response
     * @param result
     * @param contentType
     */
    public void outputToByte(HttpServletResponse response, byte[] result, String contentType) {
        response.setContentType(contentType);
        try {
            if (null != result) {
                response.getOutputStream().write(result);
            }
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
