package com.eastarjet.net.service;

import java.io.IOException;


/**
 * ConnectionManager manage the Connection 
 * Connection is a wrapper class for  Socket, SocketChannel, jdbc
 *
 * this has Monitoring Thread 
 *  if you want to use , setMonitorMode(); 
 * 
 * @author clouddrd
 *
 */
public abstract class ConnectionManager 
{
	protected String targetIP;
	protected int targetPort;
	public ConnectionManager()
	{}
	
	public void setTarget(String ip,int port)
	{
		targetIP=ip;
		targetPort=port;
	}
	
	public abstract Connection getConnection()throws IOException;
	

	public abstract void  releaseConnection(Connection con)throws IOException;
	
	
	public void startMonitor(){}
	
	public void stopMonitor(){}
	
}//class
