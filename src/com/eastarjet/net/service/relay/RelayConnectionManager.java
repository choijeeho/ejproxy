package com.eastarjet.net.service.relay;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.net.service.ServiceConfig;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class RelayConnectionManager extends Thread 
{
	static Logger log = Toolkit.getLogger(RelayConnectionManager.class);
	String serviceName;
	String serverIP;
	int targetPort;
	int size;
	int timeout=10000;
	 
	String []sourceIPs;
	List<RelayConnection> connections=new LinkedList<RelayConnection>();
	
	boolean isIdle=true;
	
	
	public RelayConnectionManager(ServiceConfig conf)
	{
		serviceName = conf.getServiceProperty("title");
		 
		this.serverIP= conf.getServiceProperty("targetIP");
		this.targetPort=conf.getIntServiceProperty("targetPort");
		this.size=conf.getIntServiceProperty("connectionPoolSize");
		this.timeout=conf.getIntServiceProperty("connectionCheckTimePeriod");;
		this.sourceIPs=conf.getServiceProperties("sourceIP");
		
		if(log.isInfoEnabled()) log.info(serviceName +  " - ConnectionManager [ target="+serverIP
				+":"+targetPort+",size="+size+", timeout="+ timeout);
	
		for(int i=0;i<size;i++)
		{
			connections.add(createConnection(i));
		}
		
	}
	
	
	public RelayConnectionManager(String key, ServiceConfig conf)
	{
		serviceName = conf.getServiceProperty("title");
		 
		this.serverIP= conf.getServiceProperty(key+".targetIP");
		this.targetPort=conf.getIntServiceProperty(key+".targetPort");
		this.size=conf.getIntServiceProperty("connectionPoolSize");
		this.timeout=conf.getIntServiceProperty("connectionCheckTimePeriod");;
		this.sourceIPs=conf.getServiceProperties("sourceIP");
		
		if(log.isInfoEnabled()) log.info(serviceName +  " - ConnectionManager."+key+" [ target="+serverIP
				+":"+targetPort+",size="+size+", timeout="+ timeout);
	
		for(int i=0;i<size;i++)
		{
			connections.add(createConnection(i));
		}
	}
	
	
	RelayConnection createConnection(int ix)
	{
		String sourceIP=null;
		RelayConnection ret=null;
		if(sourceIPs!=null ) sourceIP=sourceIPs[ix%sourceIPs.length];
		
		if(sourceIP!=null)
			 ret=new RelayConnection(this, serverIP,targetPort,sourceIP);
		else 
			 ret = new RelayConnection(this,serverIP,targetPort);
		
		return ret;
	}
	
	
	public void run()
	{
	//	checkConnections();
		synchronized (this) {notify();	}
		
		while(isIdle)
		{
			synchronized(this)
			{
				try	{	 wait(timeout);	//Thread.sleep(timeout);
				} 	catch(Exception e){}
			}
			
			//checkConnections();
		}//while
	}
	
	public synchronized void release(RelayConnection con) throws IOException
	{
		con.setUsing(false);
		
		synchronized(this){notifyAll();}
	}
	
	void checkConnections()
	{
		int noUsingCount=0;
		for(int i=0;i<size;i++)
		{
			RelayConnection con=connections.get(i);
			synchronized(con)
			{
				if(log.isDebugEnabled())
					log.debug(serviceName +  " : connection["+i+"] - isUsing="+con.isUsing());
				
				if(!con.isUsing()){ con.checkTimeout(); noUsingCount++;}
			}
		}
		
		int using1=size-noUsingCount;
		if(log.isInfoEnabled()) 
			log.info(serviceName +  " check Connections : "+using1+"/"+size);
	}
	
	public void waitInit()
	{
		try{
		synchronized(this){ wait(); }
		}catch(Exception e){}
	}
	public synchronized  RelayConnection getConnection()
	{
		RelayConnection ret=null;
		/*
		for(int i=0;i<size;i++)
		{
			Connection con=connections.get(i);
			if(!con.isUsing())
			{ret=con; break;}
		}*/
		
		if(ret==null)
		{
			ret=createConnection(size);
			size++;
			//connections.add(ret);		
		}
		
		ret.setUsing(true);
		
		return ret;
	}
	
	
}//class

