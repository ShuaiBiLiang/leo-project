package com.leo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	public static final String DATA_FORMAT = "yyyy-MM-dd";
	public static final String DATA_FORMAT_WITHOUT_YEAR = "MM-dd";

	public static final String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATA_TIME_FORMAT2 = "yyyyMMddHHmmss";

	private static final TimeZone beijingTimeZone;

	static {
		int newTime=(int)(8 * 60 * 60 * 1000);
		//TimeZone timeZone;
		String[] ids = TimeZone.getAvailableIDs(newTime);
		if (ids.length == 0) {
			beijingTimeZone = TimeZone.getDefault();
		} else {
			beijingTimeZone = new SimpleTimeZone(newTime, ids[0]);
		}
	}

	public static Calendar getCalendarInstance(){
		TimeZone.setDefault(beijingTimeZone);
		Calendar calendar = Calendar.getInstance(beijingTimeZone);
		return calendar;
	}

	public static String getCurrentTime() {
		return dateToStr(System.currentTimeMillis(), null);
	}

//	public static TimeZone getBeijing() {
//		int newTime=(int)(8 * 60 * 60 * 1000);
//		TimeZone timeZone;
//		String[] ids = TimeZone.getAvailableIDs(newTime);
//		if (ids.length == 0) {
//			timeZone = TimeZone.getDefault();
//		} else {
//			timeZone = new SimpleTimeZone(newTime, ids[0]);
//		}
//		return timeZone;
//	}

	/**
	 * 时间戳转换成日期格式字符串
	 * @param timestamp 精确到毫秒
	 * @return
	*/
	public static String dateToStr(long timestamp) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_FORMAT);
			sdf.setTimeZone(beijingTimeZone);
			return sdf.format(new Date(timestamp));
		} catch (Exception e) {
			return null;
		}
	}

	public static String dateToStr(Date date) {
		try {
			//Date date = new Date();
	        SimpleDateFormat sf = new SimpleDateFormat(DATA_TIME_FORMAT);
			sf.setTimeZone(beijingTimeZone);
	        return sf.format(date);
		} catch(Exception e) {
			return "";
		}
	}

	/**
	 * 时间戳转换成日期格式字符串
	 * @param timestamp 精确到毫秒
	 * @param format 格式
	 * @return
	*/
	public static String dateToStr(long timestamp, String format) {
		try {

			if(format == null || format.isEmpty()){
				format = DATA_TIME_FORMAT;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setTimeZone(beijingTimeZone);
			return sdf.format(new Date(timestamp));
		} catch (Exception e) {
			return null;
		}
	}

    /**
	 * 日期格式字符串转换成时间戳
	 * @param str 字符串日期
	 * @param format 如：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static long strToDate(String str,String format){
		if (str == null || str.isEmpty()) {
			return 0;
		}
		if(format == null || format.isEmpty()){
			format = DATA_TIME_FORMAT;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setTimeZone(beijingTimeZone);
			Date date = sdf.parse(str);
			return date.getTime();
		} catch (Exception e) {
			logger.error("strToDate str=" + str + ", format=" + format, e);
			return 0;
		}
	}

	/**
	 * 一天开始时间戮（日期格式字符串转换成时间戳 ）
	 * @param str 字符串日期
	 * @return
	 */
	public static long strToStartDate(String str){
		if (str == null || str.isEmpty()) {
			return 0;
		}
		if(str.length() == 10){
			str += " 00:00:00";
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_FORMAT);
			sdf.setTimeZone(beijingTimeZone);
			return sdf.parse(str).getTime();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 一天结束时间戮（日期格式字符串转换成时间戳 ）
	 * @param str 字符串日期
	 * @return
	 */
	public static long strToEndDate(String str){
		if (str == null || str.isEmpty()) {
			return 0;
		}
		if(str.length() == 10){
			str += " 23:59:59";
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_FORMAT);
			sdf.setTimeZone(beijingTimeZone);
			return sdf.parse(str).getTime() + 999;
		} catch (Exception e) {
			return 0;
		}
	}


    /**
     * 根据生日时间戳获取年龄
     * @param timestamp
     * @return
     */
//    public static int getAgeByBirth(long timestamp) {
//        Date birthday = new Date(timestamp);
//        int age = 0;
//        try {
//            Calendar now = Calendar.getInstance();
//            now.setTime(new Date());// 当前时间
//
//            Calendar birth = Calendar.getInstance();
//            birth.setTime(birthday);
//
//            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
//                age = 0;
//            } else {
//                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
//                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
//                    age += 1;
//                }
//            }
//            return age;
//        } catch (Exception e) {//兼容性更强,异常后返回数据
//            return 0;
//        }
//    }

    /**
     * 根据生日时间戳获取年龄描述（大于24个月显示岁，大于2个月显示月，大于0天显示天）
	 * eg:3个月
     * @param timestamp
     * @return
     */
    public static String getAgeStringByBirth(long timestamp) {
        Date birthday = new Date(timestamp);
        String result = "0天";
        try {
            Calendar now = getCalendarInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = getCalendarInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                result = "0天";
            } else {

				Calendar d2m = getCalendarInstance();
				d2m.setTime(birth.getTime());
				d2m.set(Calendar.MONTH, birth.get(Calendar.MONTH) + 2);
				Calendar d2y = getCalendarInstance();
				d2y.setTime(birth.getTime());
				d2y.set(Calendar.YEAR, birth.get(Calendar.YEAR) + 2);


				int ageInterval = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
				int monthInterval = (now.get(Calendar.YEAR)*12+now.get(Calendar.MONTH))-(birth.get(Calendar.YEAR)*12+birth.get(Calendar.MONTH));
                Long dayInterval = (now.getTimeInMillis()-birth.getTimeInMillis())/ (1000 * 60 * 60 * 24);
                if(now.compareTo(d2m)>0){
                	if(now.compareTo(d2y)>0){
						result = ageInterval+"岁";
					}else{
						result = monthInterval+"月";
					}
				}else {
                	result = dayInterval+"天";
				}
            }
            return result;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return result;
        }
    }

	/**
	 * 取得当前时间戳（精确到秒）
	 * @return
	 */
	public static String timeStamp(){
		long time = System.currentTimeMillis();
		String t = String.valueOf(time/1000);
		return t;
	}

	public static void main(String[] args) throws ParseException {

//	    String s = "2018-09-01 00:00:00";
//        SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_FORMAT);
//		Date birth = null;
//		try {
//			birth = sdf.parse(s);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		System.out.println(getIntervalMonths(new Date(),new Date(1559003422000l)));

//		long t = getTimeBeforeDays(0);
//		System.out.println(dateToStr(t));
	}

	public static long getTimeByMonth(int month) {
		return getTimeByDay(month*30);
	}

	public static long getTimeByDay(int day) {
		Calendar calendar = getCalendarInstance();

//		calendar.set(Calendar.HOUR_OF_DAY, 23);
//		calendar.set(Calendar.MINUTE, 59);
//		calendar.set(Calendar.SECOND, 59);
//		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return calendar.getTime().getTime();
	}

	public static long getTimeBeginByDay(int day) {
		Calendar now = getCalendarInstance();
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		now.set(Calendar.HOUR_OF_DAY,0);
		now.set(Calendar.MINUTE,0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MILLISECOND,0);
		return now.getTime().getTime();
	}

	public static long getTodayBeginTime() {
		Calendar now = getCalendarInstance();
		now.set(Calendar.HOUR_OF_DAY,0);
		now.set(Calendar.MINUTE,0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MILLISECOND,0);
		return now.getTime().getTime();
	}

	/**
	 * 剩余时间（单位/天）
	 * @param timestamp
	 * @return
	 */
	public static int getIntervalDays(long timestamp) {
		long currentTime = System.currentTimeMillis();
		if(timestamp <= 0){
			return -1;
		}
		Calendar calst = getCalendarInstance();
		Calendar caled = getCalendarInstance();
		calst.setTimeInMillis(currentTime);
		caled.setTimeInMillis(timestamp);
		//设置时间为0时
		calst.set(Calendar.HOUR_OF_DAY, 0);
		calst.set(Calendar.MINUTE, 0);
		calst.set(Calendar.SECOND, 0);
		caled.set(Calendar.HOUR_OF_DAY, 0);
		caled.set(Calendar.MINUTE, 0);
		caled.set(Calendar.SECOND, 0);
		//得到两个日期相差的天数
		BigDecimal days = new BigDecimal(caled.getTime().getTime() -calst
				.getTime().getTime() ).divide(new BigDecimal(1000 * 3600 * 24),0, BigDecimal.ROUND_HALF_UP);

		return days.intValue();
	}

	/**
	 * 剩余时间（单位/小时）
	 * @param timestamp
	 * @return
	 */
	public static int getIntervalHours(long timestamp) {
		long currentTime = System.currentTimeMillis();
		if(timestamp <= 0){
			return -1;
		}
		Calendar calst = getCalendarInstance();
		Calendar caled = getCalendarInstance();
		calst.setTimeInMillis(currentTime);
		caled.setTimeInMillis(timestamp);

		//得到两个日期相差的小时数
		BigDecimal hours = new BigDecimal(caled.getTime().getTime() -calst
				.getTime().getTime() ).divide(new BigDecimal(1000 * 3600),0, BigDecimal.ROUND_HALF_UP);

		int hourInt = hours.intValue()%24;
		if(hourInt==0){
			hourInt++;
		}
		return hourInt;
	}

	/**
	 * 返回几天前0点0分0秒的时间戳
	 * @param days
	 * @return
	 */
	public static long getTimeBeforeDays(int days) {
		long currentTime = System.currentTimeMillis();
		if(days < 0){
			return 0L;
		}
		if(days==0){
			Calendar calst = getCalendarInstance();
			calst.setTimeInMillis(currentTime);
			calst.set(Calendar.DAY_OF_YEAR, calst.get(Calendar.DAY_OF_YEAR)-days);
			//设置时间为0时
			calst.set(Calendar.HOUR_OF_DAY, 0);
			calst.set(Calendar.MINUTE, 0);
			calst.set(Calendar.SECOND, 0);
			calst.set(Calendar.MILLISECOND,0);
			return calst.getTimeInMillis();
		}else {
			return currentTime-new BigDecimal(24*60*60*1000).multiply(new BigDecimal(days)).longValue();
		}
	}



	public static long dateToLong(Date date) {
		try {
			return date.getTime();
		} catch(Exception e) {
			return 0;
		}
	}

	public static List<String> lastDaysArray(Integer days,String dataFormat) {
		List<String> result = new ArrayList<>();
		for(int i=days-1;i>=0;i--){
			Calendar calst = getCalendarInstance();
			calst.set(Calendar.DAY_OF_YEAR, calst.get(Calendar.DAY_OF_YEAR)-i);
			result.add(dateToStr(calst.getTimeInMillis(),dataFormat));
		}
		return result;
	}


	public static List<String> getDaysArray(long start, long end, String dataFormat) {
		List<String> result = new ArrayList<>();
		Calendar calst = getCalendarInstance();
		calst.setTimeInMillis(start);
		calst.set(Calendar.HOUR_OF_DAY, 0);
		calst.set(Calendar.MINUTE, 0);
		calst.set(Calendar.SECOND, 0);
		result.add(dateToStr(calst.getTimeInMillis(),dataFormat));
		for(;calst.getTimeInMillis()<end;){
			calst.set(Calendar.DAY_OF_YEAR, calst.get(Calendar.DAY_OF_YEAR)+1);
			if(calst.getTimeInMillis()<end){
				result.add(dateToStr(calst.getTimeInMillis(),dataFormat));
			}
		}
		return result;
	}

	/**
	 * 获取指定时间几个月后的时间
	 * @param beginTime
	 * @param month
	 * @return
	 */
	public static long getTimeByIntervalMonths(Long beginTime, int month){
		Calendar calendar = getCalendarInstance();
		if(beginTime!=null){
			// 设置开始时间
			calendar.setTimeInMillis(beginTime);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND,0);
		}
		// 设置为几个月后
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
		return (calendar.getTimeInMillis());
	}

	/**
	 * 获取指定时间几年后的时间
	 * @param beginTime
	 * @param year
	 * @return
	 */
	public static long getTimeByIntervalYears(Long beginTime, int year){
		Calendar calendar = getCalendarInstance();
		if(beginTime!=null){
			// 设置开始时间
			calendar.setTimeInMillis(beginTime);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND,0);
		}
		// 设置为几个月后
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);
		return (calendar.getTimeInMillis());
	}

	/**     
	 * 获取两个日期相差的月数     
	 **/
	public static int getIntervalMonths(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		int year1 = c1.get(Calendar.YEAR);int year2 = c2.get(Calendar.YEAR);
		int month1 = c1.get(Calendar.MONTH);int month2 = c2.get(Calendar.MONTH);
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH); // 获取年的差值 
		int yearInterval = year1 - year2;
		// 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
		if (month1 < month2 || month1 == month2 && day1 < day2){
			yearInterval--;
		}
		// 获取月数差值
		int monthInterval = (month1 + 12) - month2;
		if (day1 < day2){
			monthInterval--;
		}
		monthInterval %= 12;
		int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
		return monthsDiff;
	}


}
