package com.eastarjet.crs.proxy.skyport.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
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
public class VerifyPassportHandler extends AbstractHandler 
{
	static Logger log = Toolkit.getLogger(VerifyPassportHandler.class);

	static byte[] IAPPCHECKING = "##############################################################\r\nIAPP checking ....\r\n".getBytes();
	CommandHandler cmdHandler =new CommandHandler();
//	static OverrideConfirmHandler confirmHandler= new OverrideConfirmHandler();
	public boolean handleTargetRequest(int target,Session ses, 
			Request req, Response resp)
	{
		if(log.isDebugEnabled()) log.debug("Verify Passport Handler was invocked (set readline Handler");
		ses.setWaitHandler(Session.INPUT, cmdHandler);
		return false;
	}
	
	public static String StringReplace(String str){       
	      String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, " ");
	      return str;
	   }
	
	class CommandHandler extends ReadLineHandler
	{
		
		public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
		{
			if(log.isDebugEnabled()) log.debug("Verify Passport's ReadLine Handler was invocked :"+line);
			
			line=line.replace("[A", "");
			line=line.replace("[B", "");
			line=line.replace("[C", "");
			line=line.replace("[D", "");
			
			line = StringReplace(line);
			line=line.trim();
			
			if(line.equals("y")||line.equals("Y"))
			{
				Response tresp=ses.getResponse(Session.INPUT);
				tresp.write(IAPPCHECKING, 0, IAPPCHECKING.length);
				
				ByteQueue queue=(ByteQueue)ses.getAttribute("passport");
				
				String spassport= queue.readString();
				String dcsname = "";
				dcsname = spassport.substring(spassport.indexOf(" for ")+5,spassport.indexOf("1)")).trim();
				
				if(log.isDebugEnabled()) log.debug("dcs name : "+dcsname);
				if(log.isDebugEnabled()) log.debug("verifing Passport by iapp : "+spassport);

				// transmit IAPP message
				try
				{
					Map<String,String> map= new Hashtable<String, String>();
					PassportParser.parse(map,spassport);
		 			
					Passport passport= new Passport();
					CheckinTool.bindPassport(passport,map);
					
					String agentId=(String)ses.getAttribute(SessionKey.AGENTID);
					String terminalId=(String)ses.getAttribute(SessionKey.AGENTID);
					log.info("terminalId = "+terminalId);	
					log.info("agentId = "+agentId);
					FlightInfo flightInfo = (FlightInfo)ses.getAttribute(SessionKey.FLIGHTINFO);
					
					String sfdate= flightInfo.getDepartureDate();
					sfdate = CheckinTool.translateFlightDate(sfdate);
					IAPPServiceClient client= new IAPPServiceClient();
					IAPPRequest request = new IAPPRequest();
					
					request.setAgentId(agentId);
					request.setTerminalId(terminalId);
					//request.setFlightDate(year+month+day);
					request.setFlightDate(sfdate);
					request.setCarrierCode("ZE");
					
					
					String dtime=flightInfo.getDepartureTime();
					if(dtime!=null) dtime=dtime.replaceAll(":","");
					request.setDepartureTime(dtime);
					request.setFlightNo(flightInfo.getFlightNo());
					request.setDepartureAirport(flightInfo.getDepartureStation());
					request.setArrivalAirport(flightInfo.getArrivalStation());
					request.setRecordLocator("NONE " + dcsname);
					request.setPassport(passport);
						
					IAPPResponse response =	client.checkPassenger(request);
					String msg= response.getResponseMessage()+"\r\n";
					if(log.isInfoEnabled()) log.info("iapp response:"+msg);
					if(log.isInfoEnabled()) log.info("flightNo:"+request.getFlightNo());
					
					String conmsg = "";
					if(msg.startsWith("THISFLT"))
					{
						conmsg = msg.split(" \n")[0];
					}
					
					//Override Ã³¸®
					if(!("FTX+AAH+++00 Cleared\r\n".equals(msg) || "THISFLT : FTX+AAH+++00 Cleared".equals(conmsg))){
						if (!("FTX+00+++STATUS IS CLEARED").equals(msg) && !("FTX+00+++STATUS IS CLEARED\r\n").equals(msg)) {
							if(!("departure station is not IAPP airport\r\n".equals(msg))){								
								byte [] buf= "##############################################################\r\n#=     =     =    =     = = =   =    =  ===  =     =   = = = #\r\n# =   = =   =    = =    =   =   = =  =   =   = =   =  =      #\r\n#  = =   = =    = = =   =  =    =  = =   =   =   = =  = = = =#\r\n#   =     =    =     =  =   ==  =    =  ===  =     =   = = = #\r\n##############################################################\r\n".getBytes();
								tresp.write(buf, 0, buf.length);
								resp.writeByte('y');
								resp.writeByte('\r');
								resp.writeByte('\n');
								resp.flush();
								 
							}							
						}
					};
					
					byte [] buf= ((msg+"##############################################################\r\n").getBytes());
					tresp.write(buf, 0, buf.length);
				}
				catch(Exception e)
				{
					log.info("iapp error",e);
				
			          if (spassport.equalsIgnoreCase(""))
			          {
			            byte[] buf = "".getBytes();
			            tresp.write(buf, 0, buf.length);
			          }
			          else
			          {
			            byte[] buf = (e.getMessage() + "\r\n##############################################################\r\n#=     =     =    =     = = =   =    =  ===  =     =   = = = #\r\n# =   = =   =    = =    =   =   = =  =   =   = =   =  =      #\r\n#  = =   = =    = = =   =  =    =  = =   =   =   = =  = = = =#\r\n#   =     =    =     =  =   ==  =    =  ===  =     =   = = = #\r\n##############################################################\r\n").getBytes();
			            tresp.write(buf, 0, buf.length);
			          }					
					
				}
				
			}//if
			
//			ses.setWaitHandler(Session.INPUT, cmdHandler);
			return false;
		}
	}//class
}//
