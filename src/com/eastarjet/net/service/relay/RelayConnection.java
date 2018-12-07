package com.eastarjet.net.service.relay;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class RelayConnection
{
	static Logger log = Toolkit.getLogger(RelayConnection.class);
	SocketChannel socket;
	boolean isUsing=false;
	String serverIP;
	String sourceIP;
	int port;
	RelayConnectionManager manager;
	
	public RelayConnection(RelayConnectionManager manager, String serverIP,int port)
	{
		this(manager, serverIP,port,null);
		
	}
	
	public RelayConnection(RelayConnectionManager manager, String serverIP,int port,String sourceIP)
	{
		this.serverIP=serverIP;
		this.port=port;
		this.sourceIP=sourceIP;
		this.manager=manager;
	}
	
	public synchronized boolean isConnected()
	{
		if(socket==null) return false;
		
		return socket.isConnected();
	}
	
	public synchronized boolean  isUsing(){return isUsing;}
	public synchronized void setUsing(boolean v){isUsing=v;}
	
	public SocketChannel getSocket()
	{return socket;}
	
	public synchronized  void checkTimeout()
	{
		try
		{
			
			if(socket==null){ connect(); return;}
			if(log.isDebugEnabled()) log.debug(" socket : isConnected="+socket.isConnected()
					+",isClosed="+socket.socket().isClosed()
					
					);
			if(!socket.isConnected()|| socket.socket().isClosed()) 
			{ socket.close(); connect();}
		}
		catch(Exception e)
		{}
	}
	
	
	public synchronized boolean connect() throws IOException
	{
		return this.connect(serverIP,port);
	}
	
	public synchronized boolean connect(String targetIP,int targetPort) throws IOException
	{
		if(socket!=null)
		{
			try{	socket.close();}catch(Exception e){};
			socket=null;
		}
		
		socket= SocketChannel.open();
		if(log.isDebugEnabled()) log.debug(" Connecting!!! ["+serverIP+":"+targetPort+"]... ");
		
		boolean ret= false;
		if(sourceIP!=null)
		{
			SocketAddress addr= new InetSocketAddress(sourceIP, 0);
			socket.socket().bind(addr);
		}
			
		socket.connect(new InetSocketAddress(targetIP,targetPort));
		ret= true;
		return ret;
	}
	
	public synchronized void release() 
	{
		try
		{
			manager.release(this);
		}
		catch(Exception e)
		{}
	}
	
	public synchronized void close() throws IOException
	{
		if(socket!=null) 
		{
			socket.finishConnect(); socket.close();
			socket=null;
		}
	}
	
}//class
