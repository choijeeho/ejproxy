package com.eastarjet.crs.proxy.skyport.handler;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.EchoHandler;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ViewEchoHandler extends EchoHandler 
{
	static Logger log = Toolkit.getLogger(ViewEchoHandler.class);
	
	@Override
	public boolean handleTargetRequest(int target,Session ses, Request req, Response resp) 
	{
		// TODO Auto-generated method stub
		byte ch=(byte)req.read(); 
		if(target==Session.INPUT)
		{
			ByteQueue buf= (ByteQueue)ses.getAttribute("echoInput");
			if(buf==null) 
			{
				buf=new ByteQueue(1024);
				ses.setAttribute("echoInput",buf);
			}
			if(ch=='\n' || ch==0)
			{ 
				String ret= buf.readString();
				if(ret!=null && ret.equals("vv"))
				{
					Response tres= ses.getResponse(Session.INPUT);
					
					String info=("\r\nView:"+ses.currentView().getID()+"."
							+ses.currentHandler().getID()+"\r\n");
					byte[] tbuf= info.getBytes();
					if(log.isDebugEnabled()) log.debug(info);

					tres.write(tbuf, 0, tbuf.length);
				}
			}
			else if(ch!='\r') buf.add(ch);
			
		}
		resp.writeByte(ch); 
		//resp.flush();
		
		return false;
	}
}
