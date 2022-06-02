package cc.landingzone.dreamweb.utils;

//import boot.deer.component.enumm.GlobalEnum;
//import boot.deer.model.user.UserModel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** 
 * @ClassName: Util.java 
 * @Author: Mr_Deer
 * @Date: 2019年4月2日 上午9:43:40 
 * @Description: 全局通用工具类
 */
@SuppressWarnings("rawtypes")
public class GlobalUtil {

	// 全局上下文
	private static ApplicationContext applicationContext;

	/**
	 * 判断对象是否为空
	 * 其中包括 集合、对象、字符串
	 *
	 * @param object 需要判断的对象
	 * @return 空返回 true / 非空返回 false
	 */

	public static boolean isEmpty(Object object) {
		boolean result = false;
		if (object == null)
			result = true;
		if (object instanceof String)
			result = strEmpty(object.toString());
		if (object instanceof List)
			result = ((List) object).isEmpty();
		if (object instanceof Map)
			result = ((Map) object).isEmpty();
		return result;
	}

	/**
	 * 判断对象是否为空
	 *
	 * @param obj       需要判断的对象
	 * @param predicate 函数式接口，匹配规则
	 * @param <T>       type
	 * @return 根据调用者规则返回
	 */
	public static <T> boolean isEmpty(T obj, Predicate<T> predicate) {
		return predicate.test(obj);
	}

	/**
	 * 判断字符串是否为空
	 *
	 * @param str 需要判断的字符串
	 * @return 空：true/ 非空：false
	 */
	private static boolean strEmpty(String str) {
		if (str == null)
			return true;
		str = str.trim();
		if (str.length() == 0)
			return true;
		if (str.equals(" "))
			return true;
		return false;
	}

	/* = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = HttpServlet Start = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */

	/**
	 * 获取 request
	 * 
	 * @return request
	 */
	public static HttpServletRequest getHttpServletRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return request;
	}

	/**
	 * 获取 session
	 * 
	 * @return session
	 */
	public static HttpSession getHttpSession() {
		HttpSession session = getHttpServletRequest().getSession();
		return session;
	}

//	/**
//	 * 获取当前 登陆用户
//	 *
//	 * @return user
//	 */
//	public static UserModel getCurrentUser() {
//		UserModel userModel = (UserModel) getHttpSession().getAttribute(GlobalEnum.SESSION_USER.getCode());
//		System.out.println(userModel);
//		return userModel;
//	}

	/* = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = HttpServlet End = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */

	/* = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ApplicationContext Start = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */

	/**
	 * 设置当前上下文
	 * 
	 * @param applicationContext 上下文
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		if (isEmpty(GlobalUtil.applicationContext))
			GlobalUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取当前上下文
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 根据名称返回 Bean
	 * @param beanName 名称
	 * @return Bean
	 */
	public static Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}

	/**
	 * 根据 Class 返回 Bean
	 * 
	 * @param clazz 类型
	 * @return Bean
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	/**
	 * 通过 Name,以及 Clazz 返回指定的 Bean
	 * 
	 * @param beanName 名称
	 * @param clazz 类型
	 * @return Bean
	 */
	public static <T> T getBean(String beanName, Class<T> clazz) {
		return getApplicationContext().getBean(beanName, clazz);
	}
	/* = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ApplicationContext End = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */

}
