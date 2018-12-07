package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;

public class MainMenuHandler extends AbstractHandler {

	static byte [] buf="24. General Reference Ex       *(ZE)".getBytes();

	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		if(target==Session.OUTPUT )
		{
			int skip=request.getPeekPosition()+1;
			request.skipRead(skip);
			
			response.write(buf, 0, buf.length);
			
		// TODO Auto-generated method stub
		}
		return false;
	}

}
