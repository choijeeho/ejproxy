package com.eastarjet.crs.proxy.skyport.handler;

import java.util.Hashtable;
import java.util.Map;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.handler.command.PaxListUpdateCommand;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinPromptParser;
import com.eastarjet.crs.proxy.skyport.handler.tools.PaxListParser;
import com.eastarjet.net.service.terminal.view.Handler;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.net.service.terminal.view.validator.MatchValidator;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class CheckinPromptHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(CheckinPromptHandler.class);
	CheckinCommandHandler cmdHandler;
	static PaxListUpdateCommand paxListUpdateCommand=new PaxListUpdateCommand();
	static PaxListParser parser=new PaxListParser();
	public CheckinPromptHandler()
	{ cmdHandler=new CheckinCommandHandler();}
 
	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		if(log.isInfoEnabled()) log.info("CheckinPromptHandler begin");
		
		parser.readPaxList(session, request, response);
		
		if(target==Session.OUTPUT)
		{
			// TODO Auto-generated method stub
			int pos=request.getPeekPosition()+1;
			String prompt="";
			for(int i=0;i<pos;i++)
			{ 
				int ch=request.read();
				prompt+=(char)ch;
				if(ch=='>')
					response.writeByte(']');
				else response.writeByte(ch);
			}//for
			
			
			FlightInfo finfo = (FlightInfo )session.getAttribute(SessionKey.FLIGHTINFO);
			
			if(finfo==null)
			{
				finfo = new FlightInfo();
				session.setAttribute(SessionKey.FLIGHTINFO,finfo);
			}
			
			String prevFlightNo=finfo.getFlightNo();
			String prevDepartureDate=finfo.getDepartureDate();
			
			CheckinPromptParser.parse(finfo,prompt);
			
			
			String deptDate=finfo.getDepartureDate();
			String departureTime = finfo.getDepartureTime();
			String flightNo = finfo.getFlightNo();
			String departureAirport = finfo.getDepartureStation();
			String arrivalAirport =finfo.getArrivalStation();
			String arrivalTime = finfo.getArrivalTime();

			if(log.isInfoEnabled()) 
			{
				log.info("CheckinPromptHandler : date="+deptDate+", std="+departureTime+", flightNo="+flightNo
						+", deptSt="+departureAirport);
			}
			
			if(!flightNo.equals(prevFlightNo) || !deptDate.equals(prevDepartureDate) )
			{
				if(log.isInfoEnabled())  log.info("change Flight : dept date="+deptDate+", flt no=" +flightNo +" , update paxlist");
				paxListUpdateCommand.doCommand(session,request,response);
			}

			//session.setAttribute("flightInfo",finfo);
			
			session.setWaitHandler(Session.INPUT, cmdHandler);
			//ses.setWaitHandler(Session.INPUT,inputHandler);
		}//if		
		return false;
	}

}//class

