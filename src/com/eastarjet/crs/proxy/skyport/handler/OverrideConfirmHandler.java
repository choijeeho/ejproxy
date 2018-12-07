package com.eastarjet.crs.proxy.skyport.handler;

import java.util.Iterator;
import java.util.List;

import com.eastarjet.crs.proxy.skyport.bean.WatchPerson;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class OverrideConfirmHandler extends ReadLineHandler 
{
	static Logger log = Toolkit.getLogger(OverrideConfirmHandler.class);
	public OverrideConfirmHandler(){ setInputBuffring(true);}
	@Override
	public boolean handleReadLine(int target, Session session,
			Request request, Response response,String line) 
	{
		if(log.isInfoEnabled()) log.info("OverrideConfirmHandler : "+line);
		// TODO Auto-generated method stub
		Response sresp=session.getResponse(Session.OUTPUT);
		if("y".equals(line)|| "Y".equals(line))
		{
			String cmd=(String)session.getAttribute(SessionKey.LAST_CHECKIN_COMMAND);
			if(log.isInfoEnabled()) log.info("OverrideConfirmHandler : cmd="+cmd);
			if(cmd==null) return false;
			byte [] tbuf=cmd.getBytes();
			sresp.write(tbuf,0,tbuf.length);
			//setNextView("checkIn");
		}
		sresp.writeByte('\r');
		sresp.writeByte('\n');
		sresp.flush();
		return false;
	}

}
