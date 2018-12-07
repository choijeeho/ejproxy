package com.eastarjet.net.service.connection;

import java.io.IOException;
import java.net.Socket;

import com.eastarjet.net.service.Connection;

public class SyncConnection implements Connection 
{
	protected String targetIP;
	protected  int targetPort;
	Socket	socket;
	
	public SyncConnection(String targetIP,int port) throws IOException
	{
		this.targetIP=targetIP; targetPort=port;
		socket=new Socket(targetIP,port);
	}
	
	public SyncConnection(Socket socket) throws IOException
	{
		targetIP=socket.getInetAddress().getHostAddress();
		targetPort=socket.getPort();
		this.socket=socket;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connect() throws IOException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getSocket() {
		// TODO Auto-generated method stub
		return socket;
	}

	@Override
	public String getTargetAddress() {
		// TODO Auto-generated method stub
		return targetIP;
	}

	@Override
	public int getTargetPort() {
		// TODO Auto-generated method stub
		return targetPort;
	}

	@Override
	public boolean isConnect() {
		// TODO Auto-generated method stub
		boolean ret= false;
		if(socket!=null )ret=socket.isConnected();
		return ret;
	}

}//class
