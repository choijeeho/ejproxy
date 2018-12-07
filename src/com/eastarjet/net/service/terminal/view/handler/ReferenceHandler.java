package com.eastarjet.net.service.terminal.view.handler;

import com.eastarjet.net.service.terminal.view.Handler;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;

public class ReferenceHandler extends AbstractHandler 
{
	protected Handler refHandler;
	protected boolean isValidatorEnabled;
	protected String referenceID;
	
	public ReferenceHandler()
	{}
	
	public String getReferenceID(){return referenceID;}
	public void setReferenceID(String id){referenceID=id;}
	public void setValidatorEnabled(boolean b)
	{isValidatorEnabled=true;}
	public boolean isValidatorEnabled(){return isValidatorEnabled;}  

	
	@Override
	public boolean hasInterest(Session session,
			Request request)
	{
		if(!isValidatorEnabled) 	return false;
		
		return refHandler.hasInterest(session, request);
	}
	
	@Override 
	public boolean isHandleable(Session session,
			Request request)
	{
		if(!isValidatorEnabled) 	return false;
		
		return refHandler.isHandleable(session, request);
	}
	
	public void setReferenceHandler(Handler refHandler)
	{
		this.refHandler=refHandler;
	}
	
	public Handler getReferenceHandler()	{return refHandler;}
	
	@Override
	public boolean handleTargetRequest(int target,Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		return refHandler.handleRequest(session, request,
				response);
	}

}//class
