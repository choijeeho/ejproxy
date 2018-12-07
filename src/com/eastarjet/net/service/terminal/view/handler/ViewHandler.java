package com.eastarjet.net.service.terminal.view.handler;

import java.util.Iterator;

import com.eastarjet.crs.proxy.skyport.handler.CheckinPromptHandler;
import com.eastarjet.net.service.terminal.view.Handler;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.Validator;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ViewHandler extends AbstractHandler 
{
	String nextView;
	static Logger log = Toolkit.getLogger(ViewHandler.class);

	
	public boolean handleTargetRequest(int target,Session session,Request request,Response response)
	{
		session.setNextView(nextView);
		return false;
	}
}//class
