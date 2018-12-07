package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class StartEnterHandler  extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(LoginHandler.class);
	
	static byte [] buf="ZE Login:".getBytes();
	 
	
	@Override
	public boolean handleTargetRequest(int target,Session ses, Request req, Response resp) 
	{
		if(log.isDebugEnabled())log.debug("start  ");
		if(target==Session.OUTPUT)
		{
			// TODO Auto-generated method stub
			
			resp.writePeekAll(req);
			Response oresp=ses.getResponse(Session.OUTPUT);
			oresp.writeByte('\r');
			oresp.writeByte('\n');
			oresp.flush();
			//resp.writeByte('\r');
			//resp.writeByte('\n');
			//resp.flush();
			//ses.setWaitHandler(Session.INPUT,inputHandler);
		}//if
		
		return false;
	}
	
}//class
