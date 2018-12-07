package com.eastarjet.net.service;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class TaskThread extends Thread 
{
	static Logger log = Toolkit.getLogger(TaskThread.class);
	
	ServiceTask task;
	boolean isWorking=false;
	String serviceName;
	
 
	boolean isIdle=false;
	int threadNo;
	static int count=0;
	Service service;
	String waitIdleLock;
	
	List<ThreadListener> listeners = new Vector<ThreadListener>(); 
	 
	public TaskThread(Service service,int no)
	{
		this.service = service;
		this.serviceName= service.getServiceName();
		threadNo=no;
		waitIdleLock="waitIdleLock-"+serviceName+threadNo;
	}
	public int getThreadNo()
	{
		return threadNo;
	}
	public void run()
	{
		isIdle=true;
		synchronized (waitIdleLock) {
			waitIdleLock.notifyAll();
		}
		
		
	//	threadNo=count++;
	//	waitLock="wait"+threadNo;
		
		while(isIdle)
		{
			
			if(task!=null)
			{
				
				//getIdleTask already set isWorking=true
				synchronized(this){ isWorking=true;}
				service.addWorkingCount(1);
				try
				{	
					task.initialize(service);
					notifyListener(WAKEN);
					task.doWork();
				}catch(Exception e1)
				{log.error(this+" Task can't be initialized", e1);	}
			
				try
				{
					task.finalize(service);
					notifyListener(SLEPT);
				}
				catch(Exception e2)
				{	log.error(this+" Task can't be finalized", e2);}
				synchronized(this){isWorking=false;	task=null;}
				service.addWorkingCount(-1);
			}//if
			
			try
			{
				if(log.isInfoEnabled()) log.info(this+" wait ...");
				synchronized(this)	{wait();}
				if(log.isInfoEnabled()) log.info(this+" wakeup ... !!");
			}
			catch(Exception e){}
		}//while
		
	}//
	
	public synchronized void setTask(ServiceTask task)
	{
		this.task=task;  
		if(log.isDebugEnabled()) log.debug(this+" notify");
		synchronized(this)	{ notifyAll();}
	}
	
	public String toString()
	{return serviceName+".Thread["+threadNo+"]";}
	
	public void exit(){ isIdle=false;}
	
	public void waitIdle()
	{
		synchronized(waitIdleLock)
		{
			try{
			waitIdleLock.wait();}catch(Exception e){}
		}
	}
	final static int WAKEN=1;
	final static int SLEPT=2;
	
	void notifyListener(int type)
	{
		Iterator<ThreadListener> it= listeners.iterator();
		while(it.hasNext())
		{
			ThreadListener li=it.next();
			if(type==WAKEN) li.waken(this);
			else if(type==SLEPT) li.slept(this);
		}
	}
	
	public void addThreadListener(ThreadListener li)
	{ listeners.add(li); }
	
	public void removeThreadListener(ThreadListener li)
	{ listeners.remove(li); }
	
}
