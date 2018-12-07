package com.eastarjet.crs.proxy.skyport.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.bean.OpPassenger;
import com.eastarjet.crs.proxy.skyport.handler.command.PaxListUpdateCommand;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;
import com.eastarjet.crs.proxy.skyport.handler.tools.PaxListParser;
import com.eastarjet.crs.proxy.skyport.handler.tools.WatchListChecker;
import com.eastarjet.dcs.iapp.webservice.client.FlightCloseRequest;
import com.eastarjet.dcs.iapp.webservice.client.IAPPServiceClient;
import com.eastarjet.fms.wab.client.FlightLoadSheetRequest;
import com.eastarjet.fms.wab.client.FlightLoadSheetResponse;
import com.eastarjet.fms.wab.client.WeightAndBalanceClient;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.ReadLineHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
/**
 * 
 * @author clouddrd
 *
 */
public class CheckinCommandHandler extends ReadLineHandler 
{
	static Logger log = Toolkit.getLogger(CheckinPromptHandler.class);
	static PaxListParser parser=new PaxListParser();
	static WatchListChecker watchListChecker=new WatchListChecker();
	static PaxListUpdateCommand updateCommand=new PaxListUpdateCommand();
	static WatchListConfirmHandler confirmHandler= new WatchListConfirmHandler();
	static IAPPConfirmHandler iappConfirmHandler = new IAPPConfirmHandler();
	//static CheckWatchListHandler checkWatchListHandler =new CheckWatchListHandler();
	static String prompt=" ##############################################################\r\n #=     =     =    =     = = =   =    =  ===  =     =   = = = #\r\n # =   = =   =    = =    =   =   = =  =   =   = =   =  =      #\r\n #  = =   = =    = = =   =  =    =  = =   =   =   = =  = = = =#\r\n #   =     =    =     =  =   ==  =    =  ===  =     =   = = = #\r\n ##############################################################\r\nThis passenger is Watchlist person.\r\nPlease call to Incheon airport security room (032-752-0525)!!!\r\nContinue checkin (Y/N)? ";
	static String warning_prompt=" ##############################################################\r\n #=     =     =    =     = = =   =    =  ===  =     =   = = = #\r\n # =   = =   =    = =    =   =   = =  =   =   = =   =  =      #\r\n #  = =   = =    = = =   =  =    =  = =   =   =   = =  = = = =#\r\n #   =     =    =     =  =   ==  =    =  ===  =     =   = = = #\r\n ##############################################################\r\n ";
	public CheckinCommandHandler(){ setInputBuffring(true);}
 
	public boolean handleReadLine(int target,Session ses, Request req, Response resp,String line)
	{
		String agentId=(String)ses.getAttribute(SessionKey.AGENTID);
		if(log.isInfoEnabled()) log.info("Read Command line:"+line+"/"+agentId+"/handler="+ses.currentView().getID()+"."
				+ses.currentHandler());
		line=line.toLowerCase();
		line=trimEscapeChar(line);
		
		parser.readPaxList(ses,req,resp);
		
		if("help".equals(line))
		{
			if(log.isInfoEnabled()) log.info("go to checkinHelpView:"+line);
			 
			sendBufferedInput(ses, req, resp);
			ses.setNextView("checkinHelpView");
		}
		else if("u".equals(line))
		{
			if(log.isInfoEnabled()) log.info("update PaxList and goto paxListView");
		    updateCommand.doCommand(ses,req,resp);
		    
			sendBufferedInput(ses, req, resp);
			ses.setNextView("paxListView");
		}
		else if(line!=null && 
				(
						line.startsWith("f")
						||(!line.startsWith(".b") && line.startsWith("."))
				 )
		)
		{
			 
			if(log.isInfoEnabled()) log.info(" goto paxListView");
		 
			ses.removeAttribute(SessionKey.PAX_LIST);
			ses.removeAttribute(SessionKey.PAX_LIST_BUFFER);
			sendBufferedInput(ses, req, resp);
			ses.setNextView("paxListView");
		}
		else if( line.startsWith("c") && !line.startsWith("cl")&&!line.startsWith("cp") && 
				!line.startsWith("cu") && !line.startsWith("cf") && !line.startsWith("c*") )
		{
			if(log.isInfoEnabled()) log.info(" will check watchlist : '"+line+"'");
			List<OpPassenger> paxz= watchListChecker.check(ses,line);
			if(paxz!=null&& paxz.size()>0)
			{
				ses.setAttribute(SessionKey.LAST_CHECKIN_COMMAND,line);
				
				byte tbuf[] =prompt.getBytes();
				Request treq=ses.getRequest(Session.INPUT);
				Response tresp=ses.getResponse(Session.INPUT);
				tresp.write(tbuf, 0,tbuf.length);
				tresp.flush();

				ses.setWaitHandler(Session.INPUT,confirmHandler);
				
				return false;
			}
			else 
			{
				sendBufferedInput(ses, req, resp);
				ses.setNextView("checkinView");
			}
		}
		else if(line.equals("fco"))
		{
			if(log.isInfoEnabled()) log.info("fco typed.");
			try
			{
				Response tresp=ses.getResponse(Session.INPUT);
				byte[] buf="wait for a few minutes\r\n".getBytes();
				tresp.write(buf,0,buf.length);
				tresp.flush();
				sendFlightClose(ses);
				
				resp.writeByte('\r');
				resp.writeByte('\n');
				resp.flush();
			}
			catch(Exception e)
			{ 
				log.error("can't send IAPP ",e);
				Response tresp=ses.getResponse(Session.INPUT);
				byte [] buf=e.getMessage().getBytes();
				tresp.write(buf, 0, buf.length);
				
				 resp.writeByte('\r');
				 resp.writeByte('\n');
				 resp.flush();

			}//
		}
		else if(line.startsWith("wbp"))
		{
			try
			{
				if(log.isInfoEnabled()) log.info("wbp typed.");
				printWeightBalanceSheet(ses,resp);
				Response tresp=ses.getResponse(Session.INPUT);
				
				 resp.writeByte('\r');
				 resp.writeByte('\n');
				 resp.flush();
			}
			catch(Exception e)
			{
				log.error("can't print W&B ",e);
				Response tresp=ses.getResponse(Session.INPUT);
				byte [] buf=e.getMessage().getBytes();
				tresp.write(buf, 0, buf.length);
				
				 resp.writeByte('\r');
				 resp.writeByte('\n');
				 resp.flush();
			}//catch
		}
		else if(line.startsWith("vv"))
		{
 
			String info="Handler:"+ses.currentView().getID()+"."
			+ses.currentHandler();
			byte[] tbuf= info.getBytes();
		 
			Response tresp=ses.getResponse(Session.INPUT);
			tresp.write(tbuf, 0, tbuf.length);
			
			 resp.writeByte('\r');
			 resp.writeByte('\n');
			 resp.flush();
		}
		else if(line.startsWith("ex"))
		{
			FlightInfo fi=(FlightInfo)ses.getAttribute(SessionKey.FLIGHTINFO);
			if(fi!=null) fi.setFlightNo("noflght");
			 resp.writeByte('e');
			 resp.writeByte('x');
			 resp.writeByte('\r');
			 resp.writeByte('\n');
			 resp.flush();
		} 
		else if (line.startsWith("cp")) {
			
			FlightInfo flightInfo = (FlightInfo)ses.getAttribute(SessionKey.FLIGHTINFO);
			
			if(log.isInfoEnabled()) log.info(" IAPP non-transmission status Checking..");
			
			byte[] buf=" IAPP non-transmission status Checking..\r\n".getBytes();
			Request treq=ses.getRequest(Session.INPUT);
			Response tresp=ses.getResponse(Session.INPUT);
			tresp.write(buf, 0,buf.length);
			tresp.flush();

			ses.setAttribute(SessionKey.LAST_CHECKIN_COMMAND,line);
			
			//전송여부체크
			int failureCount = 0;
			int odsCount = 0;
			int succCount = 0;
			boolean isError = false;
			
			try {
				String departureDate = CheckinTool.translateFlightDate(flightInfo.getDepartureDate());
				
				JSONObject json = readJsonFromUrl("https://eai.eastarjet.com/iapp/nonTransmitCount?departureDate="+departureDate+"&flightNumber="+flightInfo.getFlightNo());
				
				failureCount = json.getInt("failureCount");
				odsCount = json.getInt("odsCount");
				succCount = json.getInt("succCount");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				
				isError = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isError = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isError = true;
			}
			
			if (isError) {
				
				if(log.isInfoEnabled()) log.info(" IAPP Check Failed!");
				String error = "IAPP Check Failed! Please try again later. \r\n";
				
				byte tbuf[] =error.getBytes();
				Request errorReq=ses.getRequest(Session.INPUT);
				Response errorRes=ses.getResponse(Session.INPUT);
				errorRes.write(tbuf, 0,tbuf.length);
				errorRes.flush();				
			}
			
			//미전송 내역 있다면..
			if (failureCount > 0) {
				//1. 미전송 리스트 노출
				
				String failedList = warning_prompt+"\r\nTotal CheckIn passengers "+odsCount+", IAPP transmission failure "+failureCount+". please check!!\r\n\r\n\r\n";
				
				byte tbuf[] =failedList.getBytes();
				Request warningRequest=ses.getRequest(Session.INPUT);
				Response warningResponse=ses.getResponse(Session.INPUT);
				warningResponse.write(tbuf, 0,tbuf.length);
				warningResponse.flush();		
				
				if(log.isInfoEnabled()) log.info("Total CheckIn passengers "+odsCount+", IAPP transmission failure "+failureCount+". please check!");				
				
//				//2. 재 전송 하시겠습니까?
//				
//				byte resendBuf[] ="Do you want to retransmit un-sent passenger data? (y/n)".getBytes();
//				Request resendRequest=ses.getRequest(Session.INPUT);
//				Response resendResponse=ses.getResponse(Session.INPUT);
//				resendResponse.write(resendBuf, 0,resendBuf.length);
//				resendResponse.flush();
//
//				ses.setWaitHandler(Session.INPUT,iappConfirmHandler);
//				
//				//3. 재 전송 결과 노출
//				iappFailedCount = 0;
//				if (iappFailedCount == 0) {
//					byte[] all_clear_buf=" Ok, IAPP is all Cleared\r\n".getBytes();
//					Request acbRequest=ses.getRequest(Session.INPUT);
//					Response acbResponse=ses.getResponse(Session.INPUT);
//					acbResponse.write(all_clear_buf, 0,all_clear_buf.length);
//					acbResponse.flush();
//					
//				} else {
//					byte[] lastConfirm_buf=" 1 IAPP transmission failure, confirmation of administrator\r\n".getBytes();
//					Request lcbRequest=ses.getRequest(Session.INPUT);
//					Response lcbResponse=ses.getResponse(Session.INPUT);
//					lcbResponse.write(lastConfirm_buf, 0,lastConfirm_buf.length);
//					lcbResponse.flush();
//				}
				
				//return false;
			} else {
				if (!isError) {
					//미전송 내역 없다면..
					byte[] all_clear_buf=" Ok, IAPP is all Cleared\r\n".getBytes();
					Request acbRequest=ses.getRequest(Session.INPUT);
					Response acbResponse=ses.getResponse(Session.INPUT);
					acbResponse.write(all_clear_buf, 0,all_clear_buf.length);
					acbResponse.flush();					
					
					if(log.isInfoEnabled()) log.info("Ok, IAPP is all Cleared");		
				}

			}
			
			resp.writeByte('c');
			resp.writeByte('p');
			resp.writeByte('\r');
			resp.writeByte('\n');
			resp.flush();
		}
		
		else 	sendBufferedInput(ses, req, resp);
		 
	
		return false;
	}
	
	static final byte [] printBegin={ 0x1b,0x5b,0x35,0x69};
	static final byte [] printEnd={ 0x1b,0x5b,0x34,0x69};

	
	void printWeightBalanceSheet(Session ses,Response resp) throws Exception
	{	
		if(log.isDebugEnabled()) log.debug("printWeightBalance");
		
		String agentId=(String)ses.getAttribute(SessionKey.AGENTID);
		byte[] tbuf="\r\nPrinting Weight&Balance ...\r\n".getBytes();
		Response tresp=ses.getResponse(Session.INPUT);
		tresp.write(tbuf, 0, tbuf.length);
		 //ses.setNextView("weightBalanceView");
		 
			FlightInfo flightInfo = (FlightInfo)ses.getAttribute(SessionKey.FLIGHTINFO);
			
			String sfdate= flightInfo.getDepartureDate();
			
			sfdate = CheckinTool.translateFlightDate(sfdate);
			/* dateformat 29Feb => 01Mar error
			String sfdate= (flightInfo.getDepartureDate()+"16");
			sfdate = sfdate.toUpperCase();
			
			String year = "20" + sfdate.substring(5, 7);
			String month = String.format("%02d", CheckinTool.getMonth(sfdate.substring(2, 5)));
			String day = sfdate.substring(0, 2);
			*/
			WeightAndBalanceClient client = new WeightAndBalanceClient();
			FlightLoadSheetRequest request= new FlightLoadSheetRequest();
				request.setDepartureDate(sfdate);
				//request.setFlightDate(year+month+day);
				String dtime=flightInfo.getDepartureTime();
				if(dtime!=null) dtime=dtime.replaceAll(":","");
				request.setDepartureTime(dtime);
				request.setFlightNumber(flightInfo.getFlightNo());
				request.setDepartureStation(flightInfo.getDepartureStation());
				request.setAgentId(agentId);
				
				FlightLoadSheetResponse fresp=null;
				
				try{
					fresp=client.getFlightLoadSheet(request);
				
				}catch(Exception ee)
				{ log.error("error at getFlightLoadSheet", ee);}
				
				if(fresp!=null && "0".equals(fresp.getResponseCode()))
				{
					String msg=fresp.getLoadSheet().getMessage();
					msg=msg.replaceAll("\n", "\r\n");
					byte [] ttbuf=msg.getBytes();
					 tresp.write(printBegin, 0, printBegin.length);
					 tresp.write(ttbuf, 0, ttbuf.length);
					 tresp.write(printEnd, 0, printEnd.length);
					 tresp.writeByte('\r');
					 tresp.writeByte('\n');
					 resp.writeByte('\r');
					 resp.writeByte('\n');
					 resp.flush();
					 //System.out.println(msg);
				}
				else if(fresp!=null) 
				{
					String msg=fresp.getResponseMessage();
					msg=msg.replaceAll("\n", "\r\n")+"\r\n";
					byte [] ttbuf=msg.getBytes();
					tresp.write(ttbuf, 0, ttbuf.length);
					 resp.writeByte('\r');
					 resp.writeByte('\n');
					 resp.flush();
				}
				else
				{
					String msg="No Response from W&B Service\r\n";
					byte [] ttbuf=msg.getBytes();
					tresp.write(ttbuf, 0, ttbuf.length);
					 resp.writeByte('\r');
					 resp.writeByte('\n');resp.flush();
				}//else
				
				if(log.isDebugEnabled()) log.debug("printWeightBalance End");

	}//method
	
	void sendFlightClose(Session ses) throws Exception
	{	
		 sendFlightCloseToIAPP( ses);
		 
	}
	
	void sendFlightCloseToIAPP(Session ses) throws Exception
	{	
		String agentId=(String)ses.getAttribute(SessionKey.AGENTID);
		String terminalId=(String)ses.getAttribute(SessionKey.AGENTID);
	 
		FlightInfo flightInfo = (FlightInfo)ses.getAttribute(SessionKey.FLIGHTINFO);
	
		String sfdate= flightInfo.getDepartureDate();
		sfdate = CheckinTool.translateFlightDate(sfdate);
	

		IAPPServiceClient client= new IAPPServiceClient();
		FlightCloseRequest request= new FlightCloseRequest();
			request.setAgentId(agentId);
			request.setTerminalId(terminalId);
			request.setDepartureDate(sfdate);
			
			String dtime=flightInfo.getDepartureTime();
			if(dtime!=null) dtime=dtime.replaceAll(":","");
			request.setDepartureTime(dtime);
			request.setFlightNumber(flightInfo.getFlightNo());
			request.setCarrierCode("ZE");
			
			request.setDepartureStation(flightInfo.getDepartureStation());
			request.setArrivalStation(flightInfo.getArrivalStation());
			//request.setArrivalTime(flightInfo.get("arrivalTime"));
			//request.setRecordLocator("NONE");
			//request.setArrivalAirport();
			//request.setA
			client.closeFlight(request);
 
	}
	/*
	 * 
	 	1b,5b,41,                                          |.[A
		1b,5b,42,                                          |.[B
		1b,5b,44,                                          |.[D
		1b,5b,43,                                          |.[C
	 * 
	 * */
	String trimEscapeChar(String str)
	{
		StringBuffer buf=new StringBuffer();
		int len=str.length();
		for(int i=0;i<len;i++)
		{
			char ch=str.charAt(i);
			if(ch==0x1b) 
			{  i++; ch=str.charAt(i);
				if(ch==0x5b && i<len ) 
				{
					i++; ch=str.charAt(i);
				}
				continue;
			}
			buf.append(ch);
		}
		return buf.toString();
	}
	

	  private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

		  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
		    } finally {
		      is.close();
		    }
		  }
	
}//class


