package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class GeneralRefHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(GeneralRefHandler.class);

	CommandHandler cmdHandler=new CommandHandler();
	static byte [] buf="\r\n 3. Weight&Balance\r\n 4. PM list".getBytes(); 
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		int len=request.getPeekPosition()+1;
		for(int i=0;i<len;i++)
		{
			byte ch=(byte)request.read();
			response.writeByte(ch);
		}//for
		
		response.write(buf, 0, buf.length);
		
		session.setWaitHandler(Session.INPUT, cmdHandler);
		return false;
	}
	
	
	class CommandHandler extends ReadLineHandler
	{
		public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
		{
			if(log.isDebugEnabled()) log.debug("Read Command line:"+line);
			if("3".equals(line))
			{
				if(log.isDebugEnabled()) log.debug("go to Weight&Balance View  ");
				ses.setNextView("weightAndBalanceView");
			}
			else if("4".equals(line))
			{
				if(log.isDebugEnabled()) log.debug("go to PMListView ");
				ses.setNextView("pmlistView");
			}
			
			
			return false;
		}
	}//class

}//class

