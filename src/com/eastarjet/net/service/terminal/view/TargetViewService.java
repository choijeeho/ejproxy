package com.eastarjet.net.service.terminal.view;

import java.io.IOException;

import com.eastarjet.net.service.Application;
import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.ConnectionManager;
import com.eastarjet.net.service.connection.AsyncConnectionManager;

public class TargetViewService extends ViewService 
{
	ConnectionManager connections;
	
	
	public void initialize(Application app) throws Exception
	{
		super.initialize(app);
		//Target ConnectionManager
		if(log.isInfoEnabled()) log.info(">>>>loading Target ConnectionManager");
		String targetIP = getConfig().getServiceProperty("targetIP"); 
		int targetPort = getConfig().getIntServiceProperty("targetPort");
		connections = new AsyncConnectionManager();
		connections.setTarget(targetIP, targetPort);
		
		//connections.startMonitor();
	}
	
	
	public void finalize(Application application) throws Exception
	{
		super.finalize(application);
		//connections.stopMonitor();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Session createSession()
	{
		Session ret=new TargetSession(this);
		return ret;
	}
	
	/**
	 * 
	 * @return ViewTaskProcessor
	 */
	public ViewTaskProcessor createProcessor(ViewServiceTask task) throws IOException
	{
		return new TargetViewTaskProcessor(task.getSource());
	}
	
	
	public Connection getTargetConnection() throws IOException
	{
		
		return connections.getConnection();
	}
	
	public void releaseTargetConnection(Connection con)throws IOException
	{
		connections.releaseConnection(con);
	}
	
}//class
