package com.eastarjet.crs.proxy.skyport.handler;

import java.util.Map;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.dcs.iapp.webservice.client.IAPPRequest;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class UpdateTravelDocHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(UpdateTravelDocHandler.class);
	//static byte []buf = "\r\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\r\n".getBytes();

	
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		
		ByteQueue queue=(ByteQueue)session.getAttribute("passport");
		if(queue==null) 
		{ 
			queue=new ByteQueue(80*24*2); session.setAttribute("passport",queue);
		}
		else
		{ 
			//queue.clear();  화면 전환이 되지 않아 삭제함
			session.removeAttribute("passport"); 
			queue=new ByteQueue(80*24*2); session.setAttribute("passport",queue);
	    }
			
		int len=request.getPeekPosition()+1;
		String dbg="";
		for(int i=0;i<len;i++)
		{
			int ch=request.read();
			queue.add((byte)ch);
			dbg+=(char)ch;
			response.writeByte(ch);
		}
		
		FlightInfo flightInfo = (FlightInfo )session.getAttribute(SessionKey.FLIGHTINFO);
	
		
			String flightDate=  flightInfo.getDepartureDate();
			String departureTime=flightInfo.getDepartureTime();
			String flightNo=flightInfo.getFlightNo();
			String departureAirport = flightInfo.getDepartureStation();
			String arrivalAirport = flightInfo.getArrivalStation();
			String sflightInfo="\r\nIAPP FlightInfo ["+flightDate+" "+flightNo+ " "
						+ departureAirport+arrivalAirport+" "+departureTime+ "]";
			byte[] tbuf=sflightInfo.getBytes();
		response.write(tbuf,0,tbuf.length);
		
		if(log.isDebugEnabled()) log.debug("passport: "+ dbg);
		
		return false;
	}
}//class
