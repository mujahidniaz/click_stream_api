package Java;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javafx.util.converter.LocalDateTimeStringConverter;

public class JavaHelper {
	DateTimeFormatter jdf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public String getTimeStampFromEpoch(String epoch) {
		try {

			long unix_seconds = Long.parseLong(epoch);
			// convert seconds to milliseconds

			Date timestamp = new Date(unix_seconds);
			// format of the timestamp
			SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String java_timestamp = jdf.format(timestamp);
			return java_timestamp;

		} catch (Exception e) {
			return "";
		}
	}
	
	

	public LocalDateTime DateObjectFromEpoch(String epoch) {
		try {

			long unix_seconds = Long.parseLong(epoch);
			// convert seconds to milliseconds

			LocalDateTime timestamp=Instant.ofEpochMilli(unix_seconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
			return timestamp;

		} catch (Exception e) {
			return null;
		}
	}

	public String[] generateKey(String timestamp, String user, String action) {
		// 2020-01-10 15:25:52

		String key = "";
		String userkey = "";

		key = key + getHourString(timestamp);
		userkey = userkey + getHourString(timestamp) + "-U";
		try {

			if (action.equals("Click")) {
				key = key + "-C";
			} else {
				key = key + "-I";
			}

			return new String[] { key, userkey };

		} catch (Exception e) {

			return new String[] { "", "" };

		}
	}

	public java.util.Date getOldestDate(java.util.Date date, int no_of_hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, no_of_hours);
		return calendar.getTime();
	}

	public String getHourString(String timestamp) {
		try {
			return timestamp.substring(0, 13).replaceAll("[ -]", "");
		} catch (Exception e) {
			return "";
		}

	}

	public long GetHoursDifference(LocalDateTime end) {
		try {

			LocalDateTime start = LocalDateTime.now();

			long numberOfHours = Duration.between(start, end).toHours();

			return numberOfHours;
		} catch (Exception e) {
			return 0;
		}
	}

	public String getValue(String timestamp, String user) {
		return timestamp + user;
	}
	
	public String generateUserCountQuery(LocalDateTime dt)
	{
		try {
			return "select count(*) from (select distinct user from "+Constants.DATABASE_NAME+"."+Constants.TABLE_NAME+" where HOUR(timestamp)="+dt.getHour()+" and date(timestamp)='"+dt.format(jdf2)+") as dusers";
		}
		catch(Exception e)
		{
			return "";
		}
	}
	public String generateClickImpressionCountQuery(LocalDateTime dt)
	{
		try {
			
			return "SELECT type,COUNT(TYPE) FROM "+Constants.DATABASE_NAME+"."+Constants.TABLE_NAME+ " WHERE HOUR(TIMESTAMP)="+dt.getHour()+" and date(timestamp)='"+dt.format(jdf2)+"' GROUP BY TYPE ORDER BY type";
		}
		catch(Exception e)
		{
			return "";
		}
	}
	public String generateQuery(String timestamp, String user, String action) {
		String query = "";
		if (action.toLowerCase().contains("click")) {
			action = "C";
		} else {
			action = "I";
		}
		String params = "'" + timestamp + "','" + user + "','" + action + "'";
		query = "INSERT into Analytics.Raw_Data(timestamp,user,type) VALUES (" + params + ")";
		return query;

	}
}
