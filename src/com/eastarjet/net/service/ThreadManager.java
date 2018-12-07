package com.eastarjet.net.service;

import java.util.Iterator;
import java.util.Vector;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ThreadManager  implements ThreadListener
{
	boolean isAutoIncreasable=false;
	int poolSize;
	static Logger log = Toolkit.getLogger(ThreadManager.class);
	
	Service service;
	Vector<TaskThread> workers=new Vector<TaskThread>();
	Vector<TaskThread> threads=new Vector<TaskThread>();
	ServiceConfig config;
	public ThreadManager(Service service,ServiceConfig config)
	{
		this.service=service; 
		this.config=config;
		poolSize=config.getIntServiceProperty(ServiceConfig.THREAD_POOL);
		String v=config.getServiceProperty(ServiceConfig.THREAD_AUTOINCREASABLE);
		
		v=(v!=null)? v.toLowerCase():null;
		isAutoIncreasable="true".equals(v);
	}
	

	public void init()
	{
		for(int i=0;i<poolSize;i++)
		{
			TaskThread thread=	createTaskThread(service,i);
			
			if(!thread.isIdle)
			{
				thread.waitIdle();
			}
			
		}//for
	}
	
	public 	TaskThread getIdleThread()
	{
		TaskThread ret=null;
		
		synchronized (threads)
		{
		
	 
			Iterator<TaskThread> it= threads.iterator();
			while(it.hasNext())
			{
				TaskThread thread=it.next();
				synchronized(thread)
				{
					if(!thread.isWorking)
					{	ret=thread;	break;	}
				}
			}//for
			
			if(ret==null && isAutoIncreasable)
			{
				ret=createTaskThread(service,threads.size());
			}//if
			long id=-1;
			if(ret!=null)
			{
				ret.isWorking=true;
				workers.add(ret);
				id=ret.getThreadNo();
			}
			
			if(log.isInfoEnabled()) log.info(service.getServiceName() +".manager added thread["+id+"] : working="+workers.size()+"/total="+threads.size());
		}//synchronized
		

			return ret;
	}//getIdleThread
	
	TaskThread createTaskThread(Service service,int threadNo)
	{
		TaskThread ret = new TaskThread(service,threadNo);
		ret.addThreadListener(this);
		threads.add(ret);
		ret.start();
		return ret;
	}
	
	public void waken(TaskThread th){}
	public void slept(TaskThread th)
	{
		workers.remove(th);
		long id= (th!=null)?th.getThreadNo():-1;
		if(log.isInfoEnabled()) log.info(service.getServiceName() +".manager removed thread["+id+"] : working="+workers.size()+"/total="+threads.size());
	}
 
	
}//class
