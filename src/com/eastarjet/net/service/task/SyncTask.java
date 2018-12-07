package com.eastarjet.net.service.task;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.Service;
import com.eastarjet.net.service.ServiceTask;

public class SyncTask extends NetworkTask
{
	
	protected Socket socket;
	protected InputStream 	in;
	protected OutputStream 	out;

	public SyncTask(){}
	
	@Override
	public void doWork() {
		// TODO Auto-generated method stub
		
	}

	Connection source;

	@Override
	public void setSource(Connection socket) 
	{
		source=socket;
		// TODO Auto-generated method stub
		this.socket=(Socket) socket.getSocket();
		try
		{
			sourceIP = this.socket.getInetAddress().getHostAddress();
			in=this.socket.getInputStream();
			out=this.socket.getOutputStream();
		}
		catch(Exception e)
		{
			
		}
	}//method
	
	@Override
	public Connection getSource()
	{ return source;}


}//class
