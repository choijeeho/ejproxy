package com.eastarjet.net.service;

import java.util.Properties;

public class ServiceConfig extends Properties 
{
	String serviceID;
	
	public final static String SERVICE_CLASS="service.class";
	public final static String TASK_CLASS="class";
	public final static String SERVICE_LISTENER="service.listener";
	public final static String THREAD_POOL="threadPool";
	public final static String TITLE="title";
	public final static String BINDER="binder";
	public final static String BINDER_TIMEOUT="binder.timeout";
	public final static String SERVICE_PORT="servicePort";
	public final static String THREAD_AUTOINCREASABLE="threadAutoIncreasable";
	
	
//	Properties config;
	public ServiceConfig(String serviceID,Properties config)
	{
		this.serviceID=serviceID;
		putAll(config);
	}

	public String getServiceID(){return serviceID;}
	
	public String getApplicationProperty(String key)
	{return getProperty(key);}
	
	public String getServiceProperty(String key)
	{return getProperty(serviceID+"."+key);}
	
	public String[] getServiceProperties(String key)
	{
		String []ret =(String []) get(serviceID+"."+key+"[]");
		if(ret==null)
		{
			String v = getProperty(serviceID+"."+key);
			
			ret =parse(v);
			if(ret!=null) put(serviceID+"."+key+"[]",ret);
		}
		return ret;
	}
	
	String[] parse(String services)
	{
		if(services==null) return null;
			
			int six=0,eix,count=0;
			String[]ss=new String[16];
			eix=services.indexOf(',');
			String service;
			while(eix>=0)
			{
				service=services.substring(six,eix);
				ss[count]=service;
				count++;
				six=eix+1;
				eix=services.indexOf(',',six);
			}
			
			if(eix < services.length())
			{ ss[count++]=services.substring(six);}
			
			if(count==0) return null;
			
			String[]ret= new String[count];
			for(int i=0;i<count;i++)
			{ret[i]=ss[i];}
			
			return ret;
	}	
	
	public int getIntServiceProperty(String key)
	{
		int ret =0;
		 try{
			String v = getProperty(serviceID+"."+key);
			ret= Integer.parseInt(v);
		 }catch(Exception e){}
		return ret;
	}
}
