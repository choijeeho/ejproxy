package com.eastarjet.net.service.task;

import java.io.IOException;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ActiveStandbyTask extends RelayTask 
{
	public ActiveStandbyTask(){}
	boolean switched=false;
	static int activeIndex=0;
	static boolean isInit=false;
	static Logger log = Toolkit.getLogger(ActiveStandbyTask.class);
	static String []ips=new String[2];
	static int [] ports=new int[2];
	
	public void init()
	{
		if(isInit) return;
		ips[0]=service.getConfig().getServiceProperty("targetIP");
		ports[0]=  service.getConfig().getIntServiceProperty("targetPort");
		
		ips[1]=service.getConfig().getServiceProperty("standbyIP");
		ports[1]=  service.getConfig().getIntServiceProperty("standbyPort");
		
		isInit=true;
	}
	
	protected void connect() throws IOException
	{
		
		
		boolean ret=false;
		
		
		if(log.isInfoEnabled()) log.info("try to connect active : "+ips[activeIndex]+":"+ports[activeIndex]);
		try{ret=  targetConnection.connect(ips[activeIndex],ports[activeIndex]);}catch(Exception e)
		{
			if(log.isInfoEnabled()) log.info("fail to connect active  : "+ips[activeIndex]+":"+ports[activeIndex],e);
		}
		
		if(!ret)
		{	
			
			activeIndex=(activeIndex+1)%2;
			if(log.isInfoEnabled()) log.info("try to connect standby : "+ips[activeIndex]+":"+ports[activeIndex]);
				
			targetConnection.connect(ips[activeIndex],ports[activeIndex]);
		}
		
	}

}//class
