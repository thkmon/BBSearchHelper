package com.thkmon.textsearch.log;

import com.thkmon.textsearch.util.DateUtil;
import com.thkmon.textsearch.util.FileUtil;

public class FileLogManager {
	
	private String logFileName = "temp.txt";
	
	public FileLogManager(String name) {
		if (name == null) {
			name = "log";
		} else {
			name = name.trim();
		}
		
		String dateTimeStd = DateUtil.getTodayDateTimeStd();
		String dateTime = dateTimeStd.replace("/", "").replace(":", "").replace(" ", "");
		
		logFileName = name + "_" + dateTime + ".txt";
		
		this.debug("----------");
		this.debug("기록 시간 : " + dateTimeStd);
		this.debug("----------");
	}
	
	
	public void debug(String str) {
		FileUtil.writeFile("log/" + logFileName, str, true);
		System.out.println(str);
	}
	
	
	public void debug(Object obj) {
		
		if (obj == null) {
			debug("null");
			return;
		}
		
		String str = "";
		
		try {
			str = String.valueOf(obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		debug(str);
	}
}