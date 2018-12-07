package com.eastarjet.crs.proxy.skyport.handler.tools;

import java.util.Hashtable;
import java.util.Map;

import com.eastarjet.crs.proxy.skyport.handler.SessionKey;

public class LoginParser {

	public static void parse(Map<String,String> map, String login)
	{

		//Map<String,String> ret= new Hashtable<String,String>();
		String slogin=login.trim();
		if( slogin.toLowerCase().startsWith("hello") && slogin.length()>6) slogin=slogin.substring(6);
		else return;
		String userId=null;
		String dept=null;
		int pos=slogin.indexOf(',');
		if(pos < 0) pos= slogin.indexOf('.');
		if(pos < 0) userId=slogin.trim();
		
		if(pos >= 0) 
		{ 
			userId=slogin.substring(0,pos);
			dept=slogin.substring(pos+1);
		}
		
		map.put(SessionKey.AGENTID,userId);
		if(dept!=null) map.put(SessionKey.DEPARTMENT,dept);
		//return ret;
	}
}
