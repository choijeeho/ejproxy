package com.eastarjet.net.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.connection.AsyncConnection;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class AsyncServiceBinder implements ServiceBinder 
{
	static Logger log = Toolkit.getLogger(AsyncServiceBinder.class);
	Service service;
	int timeout;
	
	AsyncServiceBinder(Service serv)
	{
		service=serv;
		timeout=service.getConfig().getIntServiceProperty(ServiceConfig.BINDER_TIMEOUT);
		if(log.isInfoEnabled()) log.info(service.getServiceName()+" timeout="+timeout);
	}
	
	String [] allows;
	String [] denies;
	String localname;
	
	ServerSocketChannel serverSocketChannel;
	
	/**
	 * binding할  포트, IP주소 초기화
	 */
	@Override
	public void bind() throws IOException 
	{
	
		localname = InetAddress.getLocalHost().getHostName();
		
		allows = service.getConfig().getServiceProperties("allows");
		denies = service.getConfig().getServiceProperties("denies");
		
		NetworkService netService=(NetworkService ) service;
		serverSocketChannel= ServerSocketChannel.open();
		InetSocketAddress addr = new InetSocketAddress(netService.servicePort);
		serverSocketChannel.socket().bind(addr);
 
	}//method


	@Override
	public Connection accept() throws IOException 
	{
		Connection con=null;
		SocketChannel socketChannel = null;
		InetAddress iaddr=null;
 
			
			socketChannel = serverSocketChannel.accept();
			
			if(socketChannel!=null && socketChannel.socket()!=null)
			{
				//socketChannel.socket().setReceiveBufferSize(RelayTask.PACKETSIZE);
				Socket sock=socketChannel.socket();

				iaddr = sock.getInetAddress();
				if(timeout>0) sock.setSoTimeout(timeout);
				
				if((localname!=null && localname.equals(iaddr)))
				{
					log.debug(service.getServiceName()+" local connection will be closed.");
				//	System.out.println("closed");
					socketChannel.close();
					socketChannel=null;
					
				}
			}
			if(socketChannel!=null) con=new AsyncConnection(socketChannel);  
		return con;
	}

	
	
	@Override
	public int getServicePort() {
		
		// TODO Auto-generated method stub
		return ((NetworkService ) service).getServicePort();
	}

	@Override
	public void setServicePort(int port) {
		// TODO Auto-generated method stub
	}

 
}//class
