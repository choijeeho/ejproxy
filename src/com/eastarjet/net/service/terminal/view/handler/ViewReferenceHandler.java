package com.eastarjet.net.service.terminal.view.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.View;

public class ViewReferenceHandler extends ReferenceHandler 
{
	public ViewReferenceHandler(){}
	
	@Override
	public boolean handleTargetRequest(int target, 
			Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		
		View v=(View)getReferenceHandler();
		session.setNextView(v.getID());
		return false;
	}	

}//class
