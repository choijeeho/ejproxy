package com.eastarjet.net.service.terminal.view.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;

public class EchoHandler extends AbstractHandler 
{

	@Override
	public boolean hasInterest(Session ses, Request req)
	{
		return true;
	}
	
	@Override
	public boolean isHandleable(Session ses, Request req)
	{ 
		return true;
	}
 
	
	@Override
	public boolean handleTargetRequest(int target,Session ses, Request req, Response resp) 
	{
		// TODO Auto-generated method stub
		byte ch=(byte)req.read(); 
		resp.writeByte(ch); 
		//resp.flush();
		
		return false;
	}

}//EchoHandler
