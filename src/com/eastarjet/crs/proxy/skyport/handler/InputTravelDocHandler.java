package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.crs.proxy.skyport.handler.GeneralRefHandler.CommandHandler;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class InputTravelDocHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(InputTravelDocHandler.class);

	CommandHandler cmdHandler=new CommandHandler();

	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		
		response.writePeekAll(request);
		session.setWaitHandler(Session.INPUT, cmdHandler);
		
		return false;
	}
	
	//CommandHandler cmdHandler=new CommandHandler();
	class CommandHandler extends ReadLineHandler
	{
		
		public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
		{
			if(log.isDebugEnabled()) log.debug("Read PassportInfo :"+line);
			int count= ses.getIntAttribute(this, "count");
			if(count< 12) ses.setWaitHandler(Session.INPUT, this);
			ses.setIntAttribute(this,"count",count+1);
			return false;
		}
	}//class
}//class
