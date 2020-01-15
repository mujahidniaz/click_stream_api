package Java;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaHelper {

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

	public String getHourString(String timestamp) {
		try {
			return timestamp.substring(0, 13).replaceAll("[ -]", "");
		} catch (Exception e) {
			return "";
		}

	}
	
	public String getValue(String timestamp, String user) {
		return timestamp + user;
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
