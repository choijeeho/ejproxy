package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.crs.proxy.skyport.handler.command.PaxListUpdateCommand;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class FlightInfoHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(FlightInfoHandler.class);
	
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		
		if(log.isInfoEnabled())log.info("FlightInfoHandler handle");
		session.setAttribute(SessionKey.FLIGHTINFO_REQUESTED,new Boolean(true));
		
		int ch=request.read(); 
		response.writeByte(ch);
		// TODO Auto-generated method stub
		return false;
	}

}
