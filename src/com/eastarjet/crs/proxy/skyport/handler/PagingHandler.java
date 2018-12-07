package com.eastarjet.crs.proxy.skyport.handler;

import java.nio.charset.Charset;

import com.eastarjet.crs.proxy.skyport.handler.tools.PaxListParser;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class PagingHandler extends AbstractHandler 
{

	static Logger log = Toolkit.getLogger(PagingHandler.class);
	static PaxListParser parser=new PaxListParser();
	
	public PagingHandler()
	{
		if(log.isDebugEnabled()) log.debug("PagingHandler !!!!!>>>*");
	}
	//byte[]buf=new byte[20480];
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		if(log.isInfoEnabled()) log.info("PagingHandler !!!");
		
		
		parser.readPaxList(session,request,response);
 
		/****************************/
		int pos=request.getPeekPosition()+1;
		for(int i=0;i<pos;i++)
		{
			int ch=request.read();
			response.writeByte(ch);
		}
		response.flush();
		// TODO Auto-generated method stub
		return false;
	}
}//class
