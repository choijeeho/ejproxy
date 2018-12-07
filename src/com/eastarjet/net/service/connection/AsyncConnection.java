package com.eastarjet.net.service.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.Connection;



public class AsyncConnection implements Connection 
{
	SocketChannel socket;
	String targetIP;
	int targetPort;
	
	public AsyncConnection(String ip,int port) throws IOException
	{
		
		socket= SocketChannel.open();
		targetIP=ip;
		targetPort=port;
	}
	
	public AsyncConnection(SocketChannel socket) throws IOException
	{
		this.socket=socket;
		targetPort=socket.socket().getPort();
		targetIP=socket.socket().getInetAddress().getHostAddress();
	}
	
	
	public Object getSocket()
	{ return socket;}
	
	public boolean isConnect() 
	{
		boolean ret=false;
		if(socket!=null) ret=socket.isConnected();
		return ret;
	}
	
	public void connect()throws IOException
	{
		socket.connect(new InetSocketAddress(targetIP,targetPort));
	}
	
	public String getTargetAddress(){return targetIP;}
	public int getTargetPort(){return targetPort;}	
	public void close() throws IOException
	{
		socket.close();
		socket.socket().close();
	}
	
}//class
