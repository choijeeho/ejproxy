package com.eastarjet.net.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.eastarjet.net.service.connection.SyncConnection;

public class SyncServiceBinder implements ServiceBinder 
{
	Service service;
	ServerSocket socket;
	int timeout=0;
	
	SyncServiceBinder(Service serv)
	{
		service=serv;
		
		timeout=service.getConfig().getIntServiceProperty(ServiceConfig.BINDER_TIMEOUT);
	}

	@Override
	public void bind() throws IOException 
	{
		// TODO Auto-generated method stub
		socket=new ServerSocket(((NetworkService)service).getServicePort());
	}
	
	@Override
	public Connection accept() throws IOException 
	{
		Connection con=null;
		Socket sock=socket.accept();
		if(timeout>0) sock.setSoTimeout(timeout);
		
		if(sock!=null) con=new SyncConnection(sock);
		return con;
	}


	@Override
	public int getServicePort() {
		// TODO Auto-generated method stub
		return ((NetworkService)service).getServicePort();
	}

	@Override
	public void setServicePort(int port) {
		// TODO Auto-generated method stub

	}
	
 

}//class
