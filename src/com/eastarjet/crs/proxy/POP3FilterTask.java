package com.eastarjet.crs.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
 
import java.util.Vector;

 
import com.eastarjet.net.service.relay.RelayConnection;
import com.eastarjet.net.service.relay.RelayConnectionManager;
import com.eastarjet.net.service.task.AsyncTask;
import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.net.service.task.SyncTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 *  SMTP Filter Ã³¸®¿ë 
 *   
 * @author clouddrd
 *
 */
public class POP3FilterTask extends AsyncTask 
{
	static Logger log = Toolkit.getLogger(POP3FilterTask.class);
	
	public POP3FilterTask(){}
	
 
	public static int PACKETSIZE=40960;


	protected SocketChannel targetSocket;
	
	boolean isWorking=true;
//	InputStream sin, din;
//	OutputStream sout, dout;
	
	//protected Service service;

	//String title;
	//String destIP;
	//int  destPort;
	
	protected RelayConnection targetConnection;
	
	
	public RelayConnection getTarget()
	{ return targetConnection; }
	
	public void init(){}
	
	//public void setService(Service service){this.service=service;}
	//public Service getService(){return service;}
	//public void setTitle(String name){title=name;}
	//public String getTitle(){return  title;}
//	public void setSourceSocket(Object sock)
//	{
//		socket=(SocketChannel)sock;
//		try{
//		sourceIP= socket.socket().getInetAddress().getHostAddress();
//		}catch(Exception e){}
//	}
	
	public void setTargetAddress(String ip, int port)
	{
	}
	
	public void setTargetConnection(RelayConnection con){targetConnection=con;}
	final static int DEFAULT_TARGET=0;
	final static int ASSIGNED_TARGET=1;
	protected RelayConnectionManager getConnectionManager(int type)
	{
		RelayConnectionManager manager=null;
		if(type==ASSIGNED_TARGET)
		{
			manager=(RelayConnectionManager)service.getAttribute("connectionManager2");
			if(manager==null)
			{
				manager=new RelayConnectionManager("assigned",service.getConfig());
				service.setAttribute("connectionManager2", manager);
			}
		}
		else
		{
			manager=(RelayConnectionManager)service.getAttribute("connectionManager");
			if(manager==null)
			{
				manager=new RelayConnectionManager(service.getConfig());
				service.setAttribute("connectionManager", manager);
			}
		}
		
		return manager;
	}
	
	public void doWork()
	{
		String title=service.getServiceName();
		
//		socket = sock;
		//String targetIP=service.getConfig().getServiceProperty("targetIP");
		//int targetPort= service.getConfig().getServiceIntProperty("targetPort");
		SocketChannel socket=(SocketChannel)source.getSocket();
		String sourceIP=socket.socket().getInetAddress().getHostAddress();
		//Config config=service.getConfig();
		int packetSize = service.getConfig().getIntServiceProperty("packetSize");
		if(packetSize==0) packetSize=PACKETSIZE;
		
		ByteBuffer  buf =  ByteBuffer.allocate(packetSize);
		byte[] bbuf;

		 isWorking=true;
		Selector selector=null;
		try
		{
			socket.socket().setReceiveBufferSize(packetSize);
			socket.socket().setSendBufferSize(packetSize);
 
			
			socket.configureBlocking(false);

			
			String helo="+OK MSMAILP1 POP3 server (JAMES POP3 Server 2.3.1) ready";
			writeLine(socket,buf,helo);
			if(log.isInfoEnabled())log.info("send Helo="+helo);
			String line = readLine(socket,buf);
			//targetSocket= SocketChannel.open();
			boolean isAssigned=false;
			if("RCON2SVR".equals(line))
			{
				targetConnection = getConnectionManager(ASSIGNED_TARGET).getConnection();
				isAssigned=true;
			}
			else 
			{
				targetConnection = getConnectionManager(DEFAULT_TARGET).getConnection();
			}
			 			

			connect();
			
			targetSocket=targetConnection.getSocket();
			targetSocket.socket().setReceiveBufferSize(packetSize);
			targetSocket.socket().setSendBufferSize(packetSize);
			targetSocket.configureBlocking(false);
			
			targetSocket=targetConnection.getSocket();
			
			if(!isAssigned) 
			{ 
				String tl=readLine(targetSocket,buf);
				if(log.isInfoEnabled())log.info("recev Helo="+tl);
				writeLine(targetSocket,buf,line);
			}
			
			selector = Selector.open();
			
			processLoopStart();
			
			while(isWorking)
			{
				//int rsz = selector.select();
				socket.register(selector, SelectionKey.OP_READ,null);
				targetSocket.register(selector, SelectionKey.OP_READ,null);
				
				if(log.isDebugEnabled()) log.debug(this +" wait ..");
				int count = selector.select(0);
				if(log.isDebugEnabled()) log.debug(this +" select="+count);

 
				
				for (SelectionKey key  : selector.selectedKeys())
				{
					SocketChannel channel=(SocketChannel)key.channel();
					
	 
					buf.clear();
					int rsz = channel.read(buf);
					String ch=(channel==socket)?"src":"dest";
					if(log.isTraceEnabled()) log.trace(this + " "+ ch +" recv rsz="+rsz);
					if(rsz < 0 ){isWorking=false; break;}
					
					//if(log.isDebugEnabled())
					
//					log.debug("channel : isOpen="+channel.isOpen()
//							+",isConnected="+channel.isConnected()
//							+",soc.isConnected="+socket.isConnected()
//							+",pending="+channel.isConnectionPending()
//							);
//					if(!channel.isConnected()){ isWorking=false; break;}

					 
					int bbufsz;
					if(channel==socket )
					{
						//log.debug("send port t:"+bbuf[0]+","+bbuf[1]);
						//if(bbuf[0]==-1 && bbuf[1] == -5)
						
						if(log.isTraceEnabled())
						{
							bbuf=buf.array();
							bbufsz=buf.position();
							
							String dump=dumpHex(bbuf,bbufsz);
							log.trace(this +" recv from source:\r\n"+dump);
						}
						
						processSourceReceived(buf);
						
					}
					else if(channel == targetSocket)
					{
						
						
						if(log.isTraceEnabled())
						{
							bbuf=buf.array();
							bbufsz=buf.position(); 
							String dump=dumpHex(bbuf,bbufsz);
								log.trace(this +" recv from target:\r\n"+dump);
						}
						//log.debug(" buf[0]="+bbuf[0]+",buf[1]=:"+bbuf[1]+",buf[3]=:"+bbuf[3]);
						
						processTargetReceived(buf);
					}
					
				}//for
				
				selector.selectedKeys().clear();
				//selector.keys().clear();
			}//while
			
			processLoopStop();
			
			if(log.isInfoEnabled()) log.info(this +" session disconnected");
			isWorking = false;
			
			selector.close();
			socket.close(); socket=null;
			//targetSocket.close(); targetSocket=null;
			targetConnection.close();
			targetConnection.release();
			
		}
		catch(Exception e)
		{

			log.error(this +" client disconnected", e);
 
			try
			{
				if(socket!=null)socket.close();
			}
			catch(Exception e1)
			{
				log.error(this +" client is closing source Socket", e1);
			 
			}
			try
			{
				//if(targetSocket!=null) targetSocket.close();
				if(targetConnection!=null) targetConnection.close();
			}
			catch(Exception e2)
			{
				log.error(this +" client is closing dest Socket", e2);
			 
			}
			
			try
			{
				if(selector!=null) selector.close();
			}
			catch(Exception e3)
			{
				log.error(this +" client is closing Selector", e3);
			 
			}
				
			if(targetConnection!=null) targetConnection.release();
			if(log.isDebugEnabled()) log.debug(this +" session disconnected");
			isWorking = false;
		}//catch
 
	}//run()
	
	protected void processSourceReceived(ByteBuffer buf) throws IOException
	{
		SocketChannel socket=(SocketChannel)source.getSocket();
		if(isFilterRejected(socket,buf))
		 {
			if(log.isDebugEnabled()) 
				log.debug(this +"filter exit loop");
			isWorking=false; 
			return;
		 }
		// int type = getHeaderType(bbuf,bbufsz);
		 //if(type==0 ) { isWorking=false; break; }
		writeAll(targetSocket,buf);
		
	}
	
	
	protected String readLine(SocketChannel socket,ByteBuffer buf) throws Exception
	{
		String ret="";
		Selector tselector = Selector.open();
		boolean tisWorking=true;
		while(tisWorking)
		{
			//int rsz = selector.select();
			socket.register(tselector, SelectionKey.OP_READ,null);
			int count = tselector.select(0);
			
			for (SelectionKey key  : tselector.selectedKeys())
			{
				SocketChannel channel=(SocketChannel)key.channel();
				
 
				buf.clear();
				int rsz = channel.read(buf);
				buf.flip();
				for(int i=0;i<rsz;i++)
				{
					int ch=buf.get();
					if(ch=='\r') continue;
					if(ch=='\n'){tisWorking=false; break;}
					
					ret+=(char)ch;
				}
			}
			tselector.selectedKeys().clear();
			
		}
		buf.clear();
		tselector.close();
		return ret;
	}
	
	protected int writeLine(SocketChannel socket,ByteBuffer buf,String line) throws Exception
	{
		byte[]sbuf=(line+"\r\n").getBytes();
		
		buf.put(sbuf);
		buf.flip();
		int sz=socket.write(buf);
		buf.clear();
		return sz;
	}	
	
	protected void processLoopStart(){}
	protected void processLoopStop(){}
	
	protected void processTargetReceived(ByteBuffer buf)  throws IOException
	{
		SocketChannel socket=(SocketChannel)source.getSocket();
		writeAll(socket,buf);
	}
	
	public String toString()
	{return ""+title+"("+sourceIP+")";}
	
	protected void connect() throws IOException
	{
			if(!targetConnection.isConnected())
			{
				//if(log.isInfoEnabled()) log.info(this+" connecting ["+targetIP+"]... ");
				targetConnection.connect();
				//ret = destSocket.connect(new InetSocketAddress(destIP,destPort));
			}
	}
	
	public  boolean isFilterRejected(SocketChannel socket,ByteBuffer buf)
	{
		return false;
	}
	
	protected void writeAll(SocketChannel sock, ByteBuffer buf) throws IOException
	{
		int sz=buf.position();
		int count=0;
	 
		buf.flip();
		//buf.position(0);
		while(count<sz)
		{
			count+=sock.write(buf);
			//sock.w
		}//while
		//sz=buf.capacity();
	}
	
	static int DUMPSIZE=16;
	protected String dumpHex(byte[] buf,int sz)
	{
		
		//log.debug(tag);
			 
			String form = "";
			String form2 = "";
			int i=0,prev=0;
			for(i =0;i< sz;i++)
			{
				
				form+=String.format("%02x", (buf[i]&0xff))+",";
				char ch=(char) buf[i];
				form2+= (
							Character.isLetterOrDigit(ch)
							||!Character.isISOControl(ch)
						)
				? ch:'.';
				if((i+1)%DUMPSIZE == 0 )
				{	
					form+="|"+ form2+"\r\n";
					form2="";
					prev=i;
				}
			}//for
		
		if(i > prev && i-prev  <= DUMPSIZE)
		{
			for(int ti=0;ti<=DUMPSIZE -(i-prev);ti++) form+="   "; 
			form+= "|"+ form2;
		}
		
		return form;
		//log.debug();
	}
	
 


	
	void replacePort(ByteBuffer buf, int sz, int pos)
	{
		if(
				buf.get(pos+42)=='1' &&
				buf.get(pos+42)=='2' &&
				buf.get(pos+42)=='1' &&
				buf.get(pos+42)=='0'
			)
		{
			buf.put(pos+42,(byte)'1');
			buf.put(pos+43,(byte)'2');
			buf.put(pos+44,(byte)'0');
			buf.put(pos+45,(byte)'9');
		}
	}
	
}//class
