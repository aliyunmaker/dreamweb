package cc.landingzone.dreamweb.common.framework;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "*.do", filterName = "addHttpResponseHeaderFilter")
public class MyHttpResponseHeaderFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpResponseHeaderFilter.class.getName());

    private static String HostName;

    static {
        try {
            HostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(
                "Dreamweb-Hostname", HostName);
        filterChain.doFilter(servletRequest, servletResponse);
//        logger.info("MyHttpResponseHeaderFilter Execute cost=" + (System.currentTimeMillis() - start));
    }
}
