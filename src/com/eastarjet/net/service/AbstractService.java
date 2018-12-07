package com.eastarjet.net.service;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public abstract class AbstractService implements Service 
{

	String serviceName;
	protected int count ;
	int workingCount;
///	String [] allows;
	//String [] denies;
	Application application;
	
	List<ServiceTask> tasks=new LinkedList<ServiceTask>();   
	
 	static Logger log = Toolkit.getLogger(AbstractService.class);
	//LinkedList<TaskThread> workers=new LinkedList<TaskThread>();
	
	ThreadManager threads; 
 
	ServiceConfig config;
	Hashtable<String, Object> attributes=new Hashtable<String, Object>();

	public ServiceConfig getConfig(){return config;}
	
	public void setConfig(ServiceConfig conf){config=conf;}
	
	public void setThreadManager(ThreadManager manager)
	{ this.threads=manager;}
	
	public ThreadManager getThreadManager()
	{ return threads;}
	
	public void initConfig(Application app)
	{
		application=app;
		ServiceConfigure.configure(this,config);
	}
	
	public void addWorkingTask(ServiceTask task)
	{ 
		synchronized(tasks)
		{
			tasks.add(task);
			notifyListener(TASK_WAKEUP,task);
		}
	}
	
	public void removeWorkingTask(ServiceTask task)
	{
		synchronized(tasks)
		{
			tasks.remove(task);
			notifyListener(TASK_SLEEP,task);
		}
	}
	
	public void setAttribute(String key,Object value){attributes.put(key, value);}
	public Object  getAttribute(String key){return attributes.get(key);}
	
	public Application getApplication(){return application;}
	
 
	long prevCheckTime=0;
	public synchronized void addWorkingCount(int count)
	{
		long tm=System.currentTimeMillis();
		this.workingCount+=count;
		if(tm-prevCheckTime > 60000 )
		{ 
			log.info(serviceName+" working : "+workingCount);
			prevCheckTime=tm;
		}
	}
	
	
	
	public String getServiceName()	{return serviceName; }
	
	public void setServiceName(String name){ serviceName=name;}
	
 
	public void initialize(Application service) throws Exception
	{}
	public void finalize(Application service) throws Exception
	{}

	
	public void run()
	{
		try
		{
			notifyListener(INITIALIZE);
			initialize(application);
			notifyListener(INITIALIZED);
		}catch(Exception e) 
		{
			log.error("service init ",e); return; 
		}
		
		threads.init();
		try
		{
			processService();
		}
		catch(Exception e)
		{
			log.error("error at processService",e);
		}
		
		try
		{
			notifyListener(FINALIZE);
			finalize(application);
			notifyListener(FINALIZED);
		}catch(Exception e) 
		{
			log.error("service finalize",e); return; 
		}
	}//run

	
	abstract protected void processService() throws Exception;
	
	void notifyListener(int type)
	{
		notifyListener(type, null);
	}
	
	void notifyListener(int type,ServiceTask task)
	{
		Iterator<ServiceListener> it= listeners.iterator();
		while(it.hasNext())
		{
			ServiceListener li=it.next();
			if(type==INITIALIZE) li.serviceInitialize(this);
			else if(type==INITIALIZED) li.serviceInitialized(this);
			else if(type==FINALIZE) li.serviceFinalize(this);
			else if(type==FINALIZED) li.serviceFinalized(this);
			else if(type==TASK_WAKEUP) li.taskWakeup(this,task);
			else if(type==TASK_SLEEP) li.taskSleep(this,task);
			
		}//while
	}
	
	final static int INITIALIZE		=1;
	final static int INITIALIZED	=2;
	final static int FINALIZE		=3;
	final static int FINALIZED		=4;
	final static int TASK_WAKEUP	=5;
	final static int TASK_SLEEP		=6;
	
	protected TaskThread getIdleTask() throws Exception
	{
	
		TaskThread thread=null;
		while(thread==null)
		{
			
			thread = threads.getIdleThread();

			if(thread!=null) break; 
			if(log.isDebugEnabled())log.debug("no task");
			try{Thread.sleep(10000);}catch(Exception e){}
			
		}//while
		
		return thread;
	}

	protected ServiceTask createTask(String title)
	{
		ServiceTask ret=null;
		String clazz=getConfig().getServiceProperty("class");
		if(clazz!=null)
		{
				
			try
			{
				ret = (ServiceTask)getClass().getClassLoader().loadClass(clazz).getConstructor(null).newInstance(null);
			}
			catch(Exception e)
			{
				log.error("can't create task",e);
			}
		}
		
		if(ret==null) ret =  new RelayTask();
	
		ret.setService(this);
		ret.setTitle(title);
		count++;
		
		return ret;
		
	}
	
	Vector<ServiceListener> listeners = new Vector<ServiceListener>();
	public void addServiceListener(ServiceListener listener)
	{
		listeners.add(listener);
	}
	public void removeServiceListener(ServiceListener listener)
	{
		listeners.remove(listener);
	}
}//class

