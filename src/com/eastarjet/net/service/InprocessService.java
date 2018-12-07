package com.eastarjet.net.service;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * 
 * @author clouddrd
 *
 */
public class InprocessService extends AbstractService 
{
	static Logger log=Toolkit.getLogger(InprocessService.class);
	@Override
	protected void processService() throws Exception 
	{
		// TODO Auto-generated method stub
		int count=getConfig().getIntServiceProperty(ServiceConfig.THREAD_POOL);
		
		if(log.isInfoEnabled()) log.info("count="+count);
		for(int i=0;i<count;i++)
		{
			TaskThread thread 	= getIdleTask();
			ServiceTask task 	= createTask(getServiceName()+".Task-"+(i+1));
			thread.setTask(task);
		}
	}

	@Override
	public void shutdown() 
	{
		// TODO Auto-generated method stub

	}

}
