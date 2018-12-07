package com.eastarjet.net.service;

import com.eastarjet.net.service.task.NetworkTask;
import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 네트워크 서비스용 
 * 
 * @author clouddrd
 *
 */
public class NetworkService extends AbstractService 
{

	int servicePort;
	ServiceBinder binder;
	 
 	static Logger log = Toolkit.getLogger(NetworkService.class);
	
	public int getServicePort(){return servicePort;}
	public void setServicePort(int port){servicePort=port;}
	
	public ServiceBinder getBinder(){return binder;}
	public void setBinder(ServiceBinder binder ){this.binder=binder;}
	
	
	boolean isWorking=true;
	
	protected void processService() throws Exception
	{

		 
		if(log.isInfoEnabled()) log.info(""+serviceName+" Binder [port="+servicePort+"]: Accepting  ....");
		binder.bind();
		
		while(isWorking)
		{
			Connection socket=null;
			try
			{
				socket= binder.accept();
				if(socket==null)
				{
					if(log.isInfoEnabled()) log.info("Socket is Null");
					continue;
				}//if
			}
			catch(Exception ae)
			{
				log.error("can't accept ",ae);
				 
				break;
			}
			
			
			try
			{
				log.info("["+serviceName+"-"+count+"] starting ...");
				TaskThread thread = getIdleTask();
				ServiceTask task = createTask(getServiceName()+".Task-"+(count+1));
				((NetworkTask)task).setSource(socket);
				thread.setTask(task);
			}
			catch(Exception e)
			{
				log.error("error at new task",e);
				//e.printStackTrace();
			}
		}//while
		
 
	}//run

	
 
	
	public void shutdown()
	{
		//binder.close();
		isWorking=false;
	}
}//class
