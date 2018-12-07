package com.eastarjet.net.service;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * Application is a starter <br/><br/>
 * ex) Application application = new Application("/proxy.conf");<br/>
   &nbsp;&nbsp&nbsp;&nbsp;
   application.start();</br>
  <br/>
		&lt;config sample&gt; </br><br/>
		#proxy.conf<br/>
		packetSize=64000<br/>
		<br/>
		services=speed2,payment,speedlb<br/>
		<br/>
		manager.port=10023<br/>
		manager.users=clouddrd/msihangul,root/opqhdks1!<br/>
		manager.allows=10.223.121<br/>
		<br/>
		<br/>
		#,payment<br/>
		#services=speed,payment,event,ods,report<br/>
		<br/>
		speedlb.title=Skyspeed LB<br/>
		speedlb.class=com.eastarjet.sys.repeater.task.ActiveStandbyTask<br/>
		speedlb.servicePort=15734<br/>
		speedlb.targetIP=127.0.0.1<br/>
		speedlb.targetPort=25734<br/>
		speedlb.standbyIP=127.0.0.1<br/>
		speedlb.standbyPort=35734<br/>
		speedlb.threadPool=5<br/>
 * <br/>
 * 
 * @author clouddrd
 *
 */
public class Application 
{
	static Logger log = Toolkit.getLogger(Application.class);
	Properties config=new Properties();
	ServiceConfig [] serviceConfig; 
	Hashtable<String,Service> services=new Hashtable<String,Service>();
	Hashtable <String,Object> attributes=new Hashtable<String,Object>();
	
	public Application(String config)throws Exception 
	{
		loadConfig(config);
	}
	
	public Application(Properties config)throws Exception 
	{
		this.config.putAll(config);
		parseConfig(this.config);
	}
	
	protected Application() {}
	
	public void setAttribute(String key,Object value){attributes.put(key, value);}
	public Object getAttribute(String key){return attributes.get(key);}
	
	public void setConfig(Properties conf)
	{
		this.config.putAll(conf); 
	}
	
	public Properties getConfig(){return config;}
	
	
	void loadConfig(String sconfig) throws Exception
	{
 
			if(sconfig==null) sconfig="/proxy.conf";
			InputStream in  = Application.class.getResourceAsStream(sconfig);
			try{
			config.load(in);
			}catch(Exception e)
			{
				//String dir=System.getProperty("user.dir");
				throw new Exception("can't load config : check classpath if include config('"+sconfig+"')");
			}
			parseConfig(config);
	}
	
	protected void parseConfig(Properties tconfig)  throws Exception
	{
			String sservices = tconfig.getProperty("services");
			String [] services = parseComma(sservices);
			
			serviceConfig= new ServiceConfig[services.length];
			for(int i=0;i<services.length;i++)
			{
				try
				{
					ServiceConfig conf=new ServiceConfig(services[i],tconfig);
					serviceConfig[i]=conf;
				}
				catch(Exception te)
				{ 
					log.error("load config",te);
				}
			}//for
			
			if(log.isInfoEnabled()) log.info("config is loaded.");
 	}//method
	
	/*
	 * parse "adadf,dbbb,ccc"
	 *  
	 **/
	String[] parseComma(String services)
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
	}//method
	
	public Service getService(String name)
	{ return services.get(name);}
	
	public void start()
	{
		try
		{
 			ServiceConfig[] confs=serviceConfig;
			
			Thread thread=null; 
			for(int i=0;i<confs.length;i++)
			{
				Service service = createService(confs[i]);
				services.put(service.getServiceName(),service);
				thread = new Thread(service);
				thread.start(); 
			}
			
			if(thread!=null)		thread.join();
		}
		catch(Exception e)
		{
			log.error("can't start services ", e);
		}
	}//method
	
	public void shutdown()
	{
		Iterator<Service> it=services.values().iterator();
		while(it.hasNext())
		{
			Service svc=it.next();
			svc.shutdown(); 
		}//while
	}
	
	protected Service createService(ServiceConfig conf) throws Exception
	{
		Service ret=null;
		String clazz=conf.getServiceProperty("service.class");
		if(clazz!=null)
		{
			ret= (Service)Toolkit.createInstance(clazz);
		}
		else ret = new NetworkService();
		
		ret.setConfig(conf);
		ret.initConfig(this);
		return ret;
	}
}//class
