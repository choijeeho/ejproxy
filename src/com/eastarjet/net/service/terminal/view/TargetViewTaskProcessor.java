package com.eastarjet.net.service.terminal.view;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.eastarjet.net.service.Connection;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class TargetViewTaskProcessor extends ViewTaskProcessor 
{
	static Logger log= Toolkit.getLogger(TargetViewTaskProcessor.class);
	
	
	ByteBuffer buf;
	byte []dbg;

	SocketChannel sourceSocket;
	SocketChannel targetSocket;
	protected Request targetRequest;
	protected Response targetResponse;
	protected Connection target;
	Selector selector;
	int packetSize=4096;
	int bufferSize=40960;
	
	public TargetViewTaskProcessor(Connection source)
	{
		super(source);
	}
	
	public Connection getTarget(){return target;}
	public void setTarget(Connection con){target=con;}
	
	public void initialize(Session session) throws IOException
	{
		if(log.isTraceEnabled()) log.trace("initializing session ...");
		int tpacketSize=session.getService().getConfig().getIntServiceProperty("packetSize");
		int tbufSize=session.getService().getConfig().getIntServiceProperty("bufferSize");
		if(tpacketSize!=0) packetSize=tpacketSize;
		if(tbufSize!=0) bufferSize=tbufSize;
		if(log.isTraceEnabled())
		{		dbg=new byte[bufferSize];		}
		
		
		buf=ByteBuffer.allocate(bufferSize);
		
		selector = Selector.open();
		sourceSocket= (SocketChannel)source.getSocket();
		targetSocket= (SocketChannel)target.getSocket();

		sourceSocket.socket().setReceiveBufferSize(packetSize);
		sourceSocket.socket().setSendBufferSize(packetSize);
		sourceSocket.configureBlocking(false);
		
		targetSocket.socket().setReceiveBufferSize(packetSize);
		targetSocket.socket().setSendBufferSize(packetSize);
		targetSocket.configureBlocking(false);
		
		sourceRequest =  new Request(bufferSize);
		sourceResponse = new Response(bufferSize);
		sourceResponse.setSocket(sourceSocket);
		
		targetRequest =  new Request(bufferSize);
		targetResponse = new Response(bufferSize);
		targetResponse.setSocket(targetSocket);
		
		session.setRequestResponse(session.INPUT, sourceRequest, sourceResponse);
		session.setRequestResponse(session.OUTPUT, targetRequest, targetResponse);
		
		if(log.isDebugEnabled()) log.debug("initialized session.");
	}
	
	public Iterator  select(Session session) throws IOException
	{
			sourceSocket.register(selector, SelectionKey.OP_READ,null);
			targetSocket.register(selector, SelectionKey.OP_READ,null);
			
			if(log.isTraceEnabled()) log.trace(" select ..");
			
			// 대기 시작 이후 10분마다 idle time out 여부 확인 
//			int count = selector.select(0);
			int count = selector.select(1000*60*30);
			
			Set<SelectionKey> set= selector.selectedKeys();
			
			SetIterator ret= new SetIterator(set.iterator()); 
		
		return (Iterator)ret;
	}
	
	class SetIterator implements Iterator 
	{
		Iterator<SelectionKey> it;
		SetIterator(Iterator<SelectionKey>pit){it=pit;}
		public boolean hasNext()
		{return it.hasNext();}
		public Object next()
		{
			return it.next();
		}
		public void  remove(){}
	}
	
	
	public int readRequest(Session session, Object key) throws IOException
	{
		SocketChannel channel=(SocketChannel)((SelectionKey)key).channel();
		int type=Session.INPUT;
		buf.clear();
		int rsz = channel.read(buf);
		buf.flip();
		
		if(channel==sourceSocket)	type=Session.INPUT;
		else type=Session.OUTPUT;

		if(log.isTraceEnabled())
		{
			
			buf.get(dbg,0,rsz);
			log.trace("input ["+rsz+"] from "+((type==Session.INPUT)?"INPUT":"OUTPUT")+"\r\n"+Toolkit.dumpHex(dbg,rsz ));
			buf.flip();
		}

		session.getRequest(type).add(buf, rsz);
		return type;
	}
	
	
	public void clearSelector() throws IOException
	{
		selector.selectedKeys().clear();
	}

	
	public boolean processRequest(Session session, int type)
	{
		boolean ret=true; 
		session.setTarget(type);
		if(type==Session.INPUT)
		{
			processRequest(session,sourceRequest,targetResponse);
		}
		else
		{
			processRequest(session,targetRequest,sourceResponse);
		}
		return ret;
	}
	
}//class
