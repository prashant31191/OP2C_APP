package org.itri.icl.x300.op2ca.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
	static SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy年M月d日");
	static SimpleDateFormat sdfmonth = new SimpleDateFormat("M月d日");
	static SimpleDateFormat sdftime = new SimpleDateFormat("h:m:s");
	static SimpleDateFormat sdfweak = new SimpleDateFormat("EEEE");
	static Calendar cale = Calendar.getInstance();

	public static String zh_DateFmter(Long time) {
		long date = System.currentTimeMillis();
		long yesterday = date - 1 * 24 * 60 * 60 * 1000;
		cale.setTimeInMillis(date);
		cale.set(Calendar.DAY_OF_WEEK, 1);
		long lstDayOfWeek = cale.getTimeInMillis();
		cale.set(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		cale.set(Calendar.HOUR, 0);
		cale.set(Calendar.MINUTE, 0);
		cale.set(Calendar.SECOND, 0);
		cale.set(Calendar.MILLISECOND, 0);
		long lstDayOfYear = cale.getTimeInMillis();
		if (sdf.format(date).equals(sdf.format(time))) {
			return sdftime.format(time);
		} else if (sdf.format(yesterday).equals(sdf.format(time))) {
			return "昨日";
		} else if (time > lstDayOfWeek) {
			return sdfweak.format(time);
		} else if (time > lstDayOfYear) {
			return sdfmonth.format(time);
		} else {
			return sdfyear.format(time);
		}
	}
}
