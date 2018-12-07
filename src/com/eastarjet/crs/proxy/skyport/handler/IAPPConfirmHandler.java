package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class IAPPConfirmHandler extends ReadLineHandler {
	static Logger log = Toolkit.getLogger(IAPPConfirmHandler.class);
	public IAPPConfirmHandler(){ setInputBuffring(true);}
	@Override
	public boolean handleReadLine(int target, Session session,
			Request request, Response response,String line) 
	{
		if(log.isInfoEnabled()) log.info("IAPPConfirmHandler : "+line);
		// TODO Auto-generated method stub
		Response sresp=session.getResponse(Session.OUTPUT);
		if("y".equals(line)|| "Y".equals(line))
		{
			//IAPP 재전송 처리
			
			
			
			
//			String cmd=(String)session.getAttribute(SessionKey.LAST_CHECKIN_COMMAND);
//			if(log.isInfoEnabled()) log.info("IAPPConfirmHandler : cmd="+cmd);
//			if(cmd==null) return false;
//			byte [] tbuf=cmd.getBytes();
//			sresp.write(tbuf,0,tbuf.length);
//			//setNextView("checkIn");
		}
		sresp.writeByte('\r');
		sresp.writeByte('\n');
		sresp.flush();
		return false;
	}
}
