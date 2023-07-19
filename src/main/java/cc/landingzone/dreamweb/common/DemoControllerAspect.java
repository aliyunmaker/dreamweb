package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.common.utils.JsonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author：珈贺
 * Description：
 */
@Aspect
@Component
public class DemoControllerAspect {

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Before(value = "execution(* cc.landingzone.dreamweb..*Controller.*(..)) and args(request, response) )")
    public void beforeAdvice(JoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response) {
        logger.info("===method: " + joinPoint.getSignature().getName());
        logger.info("===params: " + JsonUtils.toJsonString(request.getParameterMap()));
    }

}

