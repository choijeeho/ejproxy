package com.eastarjet.test;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import junit.*;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;
import com.eastarjet.crs.proxy.skyport.handler.tools.PassportParser;
import com.eastarjet.dcs.iapp.webservice.client.IAPPRequest;
import com.eastarjet.dcs.iapp.webservice.client.IAPPResponse;
import com.eastarjet.dcs.iapp.webservice.client.IAPPServiceClient;
import com.eastarjet.dcs.iapp.webservice.client.Passport;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * 
 * @author clouddrd
 *
 */
public abstract class iapp_test extends AbstractHandler 
{
	
	//
	
	class CommandHandler extends ReadLineHandler
	{
		public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
		{
			try{			
			
					
					
					IAPPServiceClient client= new IAPPServiceClient();
					IAPPRequest request = new IAPPRequest();
					
						request.setAgentId("kdmz7");
						request.setTerminalId("kdmz7");
						request.setFlightDate("kdmz7");
						request.setCarrierCode("ZE");		
						request.setDepartureTime("kdmz7");
						request.setFlightNo("kdmz7");
						request.setDepartureAirport("kdmz7");
						request.setArrivalAirport("kdmz7");
						request.setRecordLocator("NONE");
					
						
					IAPPResponse response =	client.checkPassenger(request);
					String msg= response.getResponseMessage()+"\r\n";
					
					byte [] buf= msg.getBytes();
					
				}
				catch(Exception e)
				{
				
					
				}
				
			
			//ses.setWaitHandler(Session.INPUT, cmdHandler);
			return false;
		}
	}//class
}//
