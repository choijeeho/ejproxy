package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.EchoHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class PaxListHandler extends EchoHandler 
{
	
	static Logger log = Toolkit.getLogger(PaxListHandler.class);
	CheckinCommandHandler cmdHandler;
	
	public PaxListHandler()
	{
		cmdHandler=new CheckinCommandHandler();
		if(log.isDebugEnabled()) log.debug("PaxListHandler !!!!!>>>*");
	}
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		ByteQueue queue=(ByteQueue)session.getAttribute(SessionKey.PAX_LIST_BUFFER);
		if(queue==null) 
		{ queue=new ByteQueue(10240); session.setAttribute(SessionKey.PAX_LIST_BUFFER,queue);}
		 
		
		
		/*
		 * for onpass
		 * 
			int ch=request.read();
			if(ch!='\n')
			{
				queue.add((byte)ch);
				return false;
			}
			
			String line=queue.readString();
			line = replaceOnepassInfo( ses[flightInfo,paxList], line);
			byte [] tb=line.getBytes();
			response.write(tb, 0, tb.length);
			response.writeByte(ch);
			session.setWaitHandler(Session.INPUT,cmdHandler);
		*/
		int ch=request.read();
		queue.add((byte)ch);
		response.writeByte(ch);
 
		session.setWaitHandler(Session.INPUT,cmdHandler);
		// TODO Auto-generated method stub
		return false;
	}
	
 
}//class
