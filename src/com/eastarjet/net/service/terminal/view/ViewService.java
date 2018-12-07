package com.eastarjet.net.service.terminal.view;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.net.service.Application;
import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.ConnectionManager;
import com.eastarjet.net.service.NetworkService;
import com.eastarjet.net.service.Service;
import com.eastarjet.net.service.terminal.view.config.ViewConfigure;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * ViewService 
 * 
 * @author clouddrd
 * @since 2010.10
 */
public class ViewService extends NetworkService 
{
	static Logger log= Toolkit.getLogger(ViewService.class);
	

	ViewManager viewManager;
	List<Session> sessions = new LinkedList<Session>();
	
	public ViewService(){}
	

	/**
	 * Service ÃÊ±âÈ­ 
	 * 
	 */
	public void initialize(Application appication) throws Exception
	{
		if(log.isInfoEnabled()) log.info(">>>>>>>> ViewService Starting");
		
		//ViewManager 
		String viewConfig=getConfig().getServiceProperty("viewconfig");
		if(viewConfig==null) viewConfig="/viewconfig.xml";
		viewManager = new ViewManager();
		viewManager.load(viewConfig);
		

	}
	
	public void finalize(Application application) throws Exception
	{
	
		//connections.stopMonitor();
	}
	
	/**
	 * 
	 * @return
	 */
	public Session createSession() 
	{
		Session ret=new Session(this);
		return ret;
	}
	
	/**
	 * 
	 * @return ViewTaskProcessor
	 */
	public ViewTaskProcessor createProcessor(ViewServiceTask task)
	throws IOException
	{
		return new ViewTaskProcessor(task.getSource());
	}
	
	public void removeSession(Session session)
	{
		sessions.remove(session);
	}
	
	public ViewManager getViewManager()
	{return viewManager;}

	
}//class
