package cc.landingzone.dreamweb.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 时间处理工具类
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public class DateUtil {

	private static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 日期转字符串（包含时分秒）
	 * 
	 * @param date 被格式化日期
	 * @return 格式化后字符串
	 */
	public static String dateTime2String(Date date) {
		if (isEmpty(date))
			return null;
		else
			return new SimpleDateFormat(FORMAT_DATE_TIME).format(date);
	}


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


}
