package com.eastarjet.crs.proxy;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * Control
 * 	FtpClient (100x1) -------------> (21)FtpRelayTask(300x1) ------------> (21)FTP Server

 * Data	
 * 	FtpClient (100x2) <------------  (300xx) FtpRelayTask(300x2) <------------ (400xx)FTP Server
 * 
 * @author clouddrd
 *
 */
public class FTPRelayTask extends RelayTask 
{
	static Logger log = Toolkit.getLogger(FTPRelayTask.class);
	
	FTPClientDataThread thread;
	public FTPRelayTask(){}
	
 	public void processLoopStart()
	{
 		/*
 		if(log.isDebugEnabled()) log.debug("loopStart");
 		 SocketChannel socket=(SocketChannel) this.source.getSocket();
 		 String clientAddress=socket.socket().getInetAddress().getHostAddress();
 		 int clientPort=socket.socket().getPort();
 		 int localPort=targetSocket.socket().getLocalPort();
 		if(log.isDebugEnabled()) log.debug("local="+localPort+",addr="+clientAddress
 				+",port"+clientPort);
 		thread = new FTPClientDataThread(localPort,clientAddress,clientPort);
 		thread.start();*/
 		//this.processSourceReceived(buf);
	}
 	
 	int tokenCount = 0;
 	String port="";
 	ByteBuffer tbuffer=ByteBuffer.allocate(40960);
 	
 	public void processSourceReceived(ByteBuffer buf)  throws IOException
 	{
	 
 		int sz=buf.position();
 		int startPos=0;
 		tbuffer.clear();
 		buf.flip();
 		
 		for(int i=0;i<sz;i++)
 		{
 			int ch=buf.get();
 			//log.debug("ch1="+(char) ch);
 			
	 		if(tokenCount==0 && ch=='P')
	 		{startPos=i; tokenCount++;  tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount==1 && ch=='O'){ tokenCount++; tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount==2 && ch=='R'){ tokenCount++; tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount==3 && ch=='T') { tokenCount++; tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount >3 && ch=='\r') {  continue;}
	 		else if(tokenCount >3 && ch=='\n')
	 		{
	 			log.debug("port:"+port);
	 			
	 			String tport=" "+ bindPort(port);
	 			
	 			tbuffer.put(tport.getBytes());
	 			
	 			port="";
	 		}
	 		else if(tokenCount > 3 )
	 		{	port+=(char)ch; continue;}
	 		
	 		tokenCount=0;
	 		
	 		tbuffer.put((byte)ch);
	 		
 		}//while
 		
 		
		writeAll(targetSocket,tbuffer);
		
 	}
 	
 	public void processTargetReceived(ByteBuffer buf)  throws IOException
 	{
	 
 		int sz=buf.position();
 		int startPos=0;
 		tbuffer.clear();
 		buf.flip();
 		
 		for(int i=0;i<sz;i++)
 		{
 			int ch=buf.get();
 		//	log.debug("ch2="+(char) ch);
 			
	 		if(tokenCount==0 && ch=='2')
	 		{startPos=i; tokenCount++;  tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount==1 && ch=='2'){ tokenCount++; tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount==2 && ch=='7'){ tokenCount++; tbuffer.put((byte)ch); continue;}
	 	//	else if(tokenCount==3 && ch=='V') { tokenCount++; tbuffer.put((byte)ch); continue;}
	 		else if(tokenCount >2 && ch=='\r') {  continue;}
	 		else if(tokenCount >2 && ch=='\n')
	 		{
	 			
	 			if(log.isDebugEnabled()) log.debug("port:"+port);
	 			
	 			int sp=port.indexOf('(');
	 			int ep=port.indexOf(')');
	 			port=port.substring(sp+1,ep);
	 			if(log.isDebugEnabled()) log.debug("new port:"+port);
	 			
	 			
	 			String tport=" "+ bindPort(port);
	 			
	 			tbuffer.put(tport.getBytes());
	 			
	 			port="";
	 		}
	 		else if(tokenCount > 2 )
	 		{	port+=(char)ch; continue;}
	 		
	 		tokenCount=0;
	 		
	 		tbuffer.put((byte)ch);
	 		
 		}//while
 		
 		SocketChannel sock=(SocketChannel)this.source.getSocket();
		writeAll(sock,tbuffer);
		
 	}
 	
 	String bindPort(String port) throws IOException
 	{
 		String []toks= port.split(",");
 		
 		if(toks.length<6) return null;
 		String snum1=toks[4];
 		String snum2=toks[5];
 		int num1=0;
 		int num2=0;
 		String targetAddress=toks[0]+"."+toks[1]+"."+toks[2]+"."+toks[3];
 		try{num1=Integer.parseInt(snum1) ; }catch(Exception e){}
 		try{num2=Integer.parseInt(snum2); }catch(Exception e){}
 		int targetPort=num1*256+num2;
 		
 		ServerSocket ssocket=new ServerSocket(0);
 		int sport=ssocket.getLocalPort();
 		String iaddr=targetSocket.socket().getLocalAddress().getHostAddress();
 		String taddr=iaddr.replace('.', ',');
 		int h=sport/256;
 		int l=sport%256;
 		log.debug("Bind org="+port+" > Addr:"+taddr+", port:"+sport+"("+h+","+l+")");
 		
 		thread = new FTPClientDataThread(ssocket,targetAddress,targetPort);
 		thread.start();
 		
 		return taddr+","+h+","+l;
 	}
	
	public void processLoopEnd()
	{
	
		tokenCount=0;
		port="";
		 /*
		thread.close();
		try
		{
			thread.join();
		}catch(Exception e){}*/
	}
}

class FTPClientDataThread extends Thread
{
	String clientAddress;
	int clientPort;
	int localPort;
	boolean isLoop=true;
	ServerSocket serverSocket;
	Socket ftpServerSocket;
	Socket clientSocket;
	
	public FTPClientDataThread(ServerSocket serverSocket,String clientAddress, int clientPort)
	{
		//this.localPort=localPort;
		this.serverSocket=serverSocket;
		this.clientAddress=clientAddress.trim();
		this.clientPort=clientPort;
	}
	
	public void run()
	{
 		if(FTPRelayTask.log.isDebugEnabled()) FTPRelayTask.log.debug("running");
		
		try
		{
			//serverSocket=new ServerSocket(localPort);
	 		if(FTPRelayTask.log.isDebugEnabled()) FTPRelayTask.log.debug("serverSocket");
			
	 			serverSocket.setSoTimeout(300000);
				ftpServerSocket=serverSocket.accept();
		 		if(FTPRelayTask.log.isDebugEnabled()) FTPRelayTask.log.debug("accept ftp client");

		 		clientSocket=new Socket(clientAddress,clientPort);
		 		
		 		FTPClientPumpThread rthread=new FTPClientPumpThread(ftpServerSocket,clientSocket,FTPClientPumpThread.RECEIVE);
				FTPClientPumpThread sthread=new FTPClientPumpThread(ftpServerSocket,clientSocket,FTPClientPumpThread.SEND);
				rthread.start();
				//sthread.start();
				sthread.run();
			
			
			closeSocket();
		}catch(Exception e)
		{
			FTPRelayTask.log.debug("serverSocket",e);
		}
		
	}
	
	void closeSocket() throws IOException
	{
		try{
		if(serverSocket!=null) serverSocket.close();
		}catch(Exception e1){}
		try
		{
			if(ftpServerSocket!=null) ftpServerSocket.close();
		}catch(Exception e2){}
		try{
			if(clientSocket!=null) clientSocket.close();
		}catch(Exception e3){}
	}
	
	public void close()
	{
		isLoop=false;
		try{
			closeSocket();
		}catch(Exception e){}
	}
}

class FTPClientPumpThread extends Thread
{
	public final static int RECEIVE=0;
	public final static int SEND=1;
	InputStream in ;
	OutputStream out;
	byte[] buf=new byte[40960];
	boolean isLoop=true;
	Socket source;
	Socket target;
	
	FTPClientPumpThread(Socket source,Socket target, int direction) throws IOException
	{
		this.source=source;this.target=target;
		if(direction==RECEIVE)
		{
			in=source.getInputStream();
			out=target.getOutputStream();
		}
		else 
		{
			in=target.getInputStream();
			out=source.getOutputStream();
		}
	}
	public void run()
	{
		while(isLoop)
		{
			try{
				int sz=in.read(buf);
				if(sz<0) break;
				out.write(buf,0,sz);
				out.flush();
			}catch(Exception e)
			{ break;}
		}//while
		
		try{source.close();}catch(Exception e1){}
		try{target.close();}catch(Exception e2){}
		
	}//run
}
