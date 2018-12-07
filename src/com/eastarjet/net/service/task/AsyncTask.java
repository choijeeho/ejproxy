package com.eastarjet.net.service.task;

import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.Connection;

public class AsyncTask extends NetworkTask 
{

	protected Connection source;
	
	@Override
	public void doWork() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setSource(Connection socket) 
	{
		// TODO Auto-generated method stub
		source=socket;
		
		//sourceIP=source.get
		SocketChannel tsocket=(SocketChannel)socket.getSocket();
		try
		{
			sourceIP= tsocket.socket().getInetAddress().getHostAddress();
		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	public Connection getSource()
	{ return source;}
}//class
