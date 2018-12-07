package com.eastarjet.net.service;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class ServiceConfigure 
{
	static Logger log=Toolkit.getLogger(ServiceConfigure.class);
	
	static void configure(Service service, ServiceConfig config )
	{
		String slistener = config.getServiceProperty(ServiceConfig.SERVICE_LISTENER);
		if(slistener!=null)	addServiceListener(service,slistener);
		
		if(service instanceof AbstractService)
		{
			AbstractService aservice=(AbstractService)service;
			aservice.setServiceName(config.getServiceProperty(ServiceConfig.TITLE));
			aservice.setThreadManager(new ThreadManager(service,config));
		}
		
		if(service instanceof NetworkService)
		{
			NetworkService nservice = (NetworkService ) service;
		
			String sbinder = config.getServiceProperty(ServiceConfig.BINDER);
			if(sbinder!=null&& "sync".equals(sbinder)) 
			{
				 nservice.setBinder(new SyncServiceBinder(service));
			}
			else nservice.setBinder( new AsyncServiceBinder(service));
			
			nservice.setServicePort(config.getIntServiceProperty(ServiceConfig.SERVICE_PORT));
			//int poolSize=config.getIntProperty("threadPool");
			//nservice.setWorkingCount(0);
		}//if
		
	}
	
	static void addServiceListener(Service service, String slistener)
	{
		//Toolkit.createInstance(slinsteanr);
		try
		{
			if(log.isInfoEnabled()) log.info("listner :"+slistener);
			
			ServiceListener listener = (ServiceListener)service.getClass()
						.getClassLoader().loadClass(slistener)
						.getConstructor(null).newInstance(null);
			 service.addServiceListener(listener);
		}
		catch(Exception e)
		{
			log.error("addServiceListener",e);
		}
		//service.addServiceListener();
	}
}//class
