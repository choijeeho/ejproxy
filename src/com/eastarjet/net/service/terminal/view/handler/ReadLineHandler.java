package com.eastarjet.net.service.terminal.view.handler;

import com.eastarjet.crs.proxy.skyport.handler.LoginHandler;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ReadLineHandler extends AbstractHandler 
{

	static Logger log = Toolkit.getLogger(ReadLineHandler.class);

	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		return true	;
	}
	boolean isInputBuffering=false;
	protected void setInputBuffring(boolean b){isInputBuffering=b;}
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
		if(!isInputBuffering)	resp.writeByte(ch);
		
		if(ch=='\r') return true;
		if(ch=='\n' || ch==0)
		{
			
			String agentId=(String)ses.getAttribute(SessionKey.AGENTID);
			if(log.isDebugEnabled())log.debug("inputHandler input : "+sin+" / "+agentId);
			boolean ret= handleReadLine(target,ses,req,resp,sin);
			return ret;
		}
		//if(ch==0x1b) set nextskip;
		
		if(ch!='\b') sin+=(char)ch;
		else 
		{ 
			if(sin.length()!=0)
				sin = sin.substring(0,sin.length()-1); 
		}
		 ses.setAttribute(this, "input",sin);
		return true;
	}
	
	public void sendBufferedInput(Session ses,Request req, Response resp) 
	{
		String sin=(String)ses.getAttribute(this, "input");
		if(log.isDebugEnabled())log.debug("send bufferd input:'"+sin+"'");
		if(!isInputBuffering) return;
		if(sin!=null)
		{
		 byte []tbuf=sin.getBytes(); resp.write(tbuf, 0, tbuf.length);
		}
		 resp.writeByte('\r');
		 resp.writeByte('\n');
	}
	
	public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
	{
		return false;
	}
	

}//class
