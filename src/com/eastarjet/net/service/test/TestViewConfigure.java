package com.eastarjet.net.service.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.eastarjet.net.service.terminal.view.config.ViewConfigure;
import com.eastarjet.net.service.terminal.view.validator.PatternValidator;

import junit.framework.TestCase;

public class TestViewConfigure extends TestCase 
{
	

	
	public void tes1tDateFormat()
	{
		try{
//			Date and Time Pattern  Result  
//			"yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT  
//			"EEE, MMM d, ''yy"  Wed, Jul 4, '01  
//			"h:mm a"  12:08 PM  
//			"hh 'o''clock' a, zzzz"  12 o'clock PM, Pacific Daylight Time  
//			"K:mm a, z"  0:08 PM, PDT  
//			"yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM  
//			"EEE, d MMM yyyy HH:mm:ss Z"  Wed, 4 Jul 2001 12:08:56 -0700  

		SimpleDateFormat format= new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
		 System.out.println(format.toPattern());
		Date date =format.parse("10-Jan-10");
		
		
		System.out.println("date:"+date);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void tes2t()
	{
		loadConfig();
	}
	
	protected void loadConfig()
	{
		System.out.println("start loading config");
		ViewConfigure configure = new ViewConfigure();
		
		try
		{
			configure.load();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}//method
}
