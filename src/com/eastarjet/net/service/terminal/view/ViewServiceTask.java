package com.eastarjet.net.service.terminal.view;

import java.util.Iterator;

import com.eastarjet.net.service.Service;
import com.eastarjet.net.service.task.AsyncTargetTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * ViewServiceTask support  screen view and state
 * 
 * @author clouddrd
 *
 */
public class ViewServiceTask extends AsyncTargetTask 
{
	
	static Logger log= Toolkit.getLogger(ViewServiceTask.class);
	
	Session session;
	ViewTaskProcessor processor;
	long lastAccess; // For timeout

	public void doWork()
	{
		try
		{
			processor.initialize(session);
		}
		catch(Exception e)
		{
			log.error(this+" Task processor can't be initialized",e);
		}
		
		try
		{
			boolean isContinue=true;
			while(isContinue)
			{
				Iterator it=processor.select(session);
				while(it.hasNext())
				{
					Object key=it.next();
					
				   int type=processor.readRequest(session,key);
				   isContinue=processor.processRequest(session,type);
				   if(!isContinue) break;
				   lastAccess = System.currentTimeMillis(); // For idle time out
				}
				processor.clearSelector();
				
				//idle time Out ¼³Á¤ 30ºÐ (1000*60*30) 
				if( System.currentTimeMillis() - lastAccess > 1000*60*29) isContinue = false;
			}//while
		}
		catch(Exception e1)
		{
			log.error(this+" Task will be terminated",e1);
		}
		
	}//method
	
 
	
	protected void initializeTask(Service service) throws Exception
	{
		if(log.isDebugEnabled()) log.debug(this+" initializing ...");
			
		ViewService vservice=(ViewService)service;
		session=vservice.createSession();
		
		ViewManager mng=vservice.getViewManager();
		session.setViewManager(mng);

		String view=mng.getStartView().getID();
		session.setCurrentView(view);
		
		processor=vservice.createProcessor(this);
	}
	
	protected void finalizeTask(Service service) throws Exception
	{
		if(log.isDebugEnabled()) log.debug(this+" finalizing ...");
		
		ViewService vservice=(ViewService)service;
		vservice.removeSession(session);
		
		try{

			((TargetViewTaskProcessor)processor).selector.close();
			source.close();
			
		}catch(Exception e1){}
		try{		target.close();}catch(Exception e2){}
	}
 
}//class
