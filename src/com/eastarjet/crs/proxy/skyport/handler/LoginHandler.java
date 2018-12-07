package com.eastarjet.crs.proxy.skyport.handler;

import java.util.Hashtable;
import java.util.Map;

import com.eastarjet.crs.proxy.skyport.handler.tools.LoginParser;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class LoginHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(LoginHandler.class);
	
	static byte [] buf="ZE Login:".getBytes();
	ConnectedInputHandler inputHandler=new ConnectedInputHandler();
	
	@Override
	public boolean handleTargetRequest(int target,Session ses, Request req, Response resp) 
	{
		if(log.isDebugEnabled())log.debug("connected ");
		if(target==Session.OUTPUT)
		{
			// TODO Auto-generated method stub
			int pos=req.getPeekPosition()+1;
			req.skipRead(pos);
			
			
			resp.write(buf, 0, buf.length);
			
			ses.setWaitHandler(Session.INPUT,inputHandler);
		}
		
		return false;
	}
	
}//class

class ConnectedInputHandler extends AbstractHandler
{
	static Logger log = Toolkit.getLogger(ConnectedInputHandler.class);
 

	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		return true	;
	}
	
	@Override
	public boolean isHandleable(Session session, Request request){return true;}
	@Override
	public boolean isWorking(Session session, Request request){return true;}
	@Override
	public boolean handleTargetRequest(int target,Session ses, Request req, Response resp)
	{
		String sin=(String)ses.getAttribute(this, "input");
		if(sin==null) sin="";
		
		int ch=req.read();
		

		//log.debug("input : "+(char)ch);
		resp.writeByte(ch);
		if(ch=='\r') return true;
		
		//Escape Mode
		Boolean b=(Boolean)ses.getAttribute("keyEscape");
		if(b!=null && b)
		{
			if(ch=='[')  return true;
			if(ch=='D') { ses.removeAttribute("keyEscape"); return true;}
		}
		
		if(ch==0x1b) 
		{
			ses.setAttribute("keyEscape", new Boolean(true));
			return true;
		}
		
		if(ch=='\n' || ch==0 )
		{
			
			log.debug("input end : "+sin);
			
			ses.removeAttribute(this, "input");
			
			Map<String,String> info=(Map<String,String>)ses.getAttribute("agentInfo");
			if(info==null)
			{
				info=new Hashtable<String, String>();
				ses.setAttribute("agentInfo", info);
			}
				
			LoginParser.parse(info, sin);
			if(info.get(SessionKey.AGENTID) != null)
				ses.setAttribute(SessionKey.AGENTID,info.get(SessionKey.AGENTID));
			if(info.get(SessionKey.DEPARTMENT)!=null)
				ses.setAttribute(SessionKey.DEPARTMENT,info.get(SessionKey.DEPARTMENT));
			return false;
		}
		
		if(ch=='\b')sin=sin.substring(0,sin.length()-1);
		else sin+=(char)ch;
		
		ses.setAttribute(this, "input",sin);
		return true;
	}
}
