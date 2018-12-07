package com.eastarjet.crs.proxy.skyport.handler.printing;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
import java.io.IOException;
import java.io.InputStream;

public class PrintingBTPHandler
  extends AbstractHandler
{
  static Logger log = Toolkit.getLogger(PrintingBTPHandler.class);
  
  public boolean handleTargetRequest(int target, Session session, Request request, Response response)
  {
    try
    {
      sendBTPPrinting(session, request, response);
    }
    catch (Exception e)
    {
      log.error("Error at PrintingBTPHandler", e);
    }
    return false;
  }
  
  protected void sendBTPPrinting(Session session, Request request, Response response)
    throws IOException
  {
    FlightInfo finfo = (FlightInfo)session.getAttribute("flightInfo");
    
    String st = null;
    if (finfo != null) {
      st = finfo.getDepartureStation();
    }
    
    if (st.equals("PPS"))
    {
      response.writePeekAll(request);
      return;
    }
    
    InputStream in = request.getInputStream();
    String hd = Toolkit.readLine(in);
    if (log.isDebugEnabled()) {
      log.debug("btp.hd=" + hd);
    }
    String tag = Toolkit.readLine(in);
    if (log.isDebugEnabled()) {
      log.debug("btp.tag=" + tag);
    }
    FlightInfo flightInfo = (FlightInfo)session.getAttribute("flightInfo");
    
    String sfdate = flightInfo.getDepartureDate();
    
    String iataIndent = null;
    String bagtagNumber = null;
    String airlineCode = null;
    String paxName = null;
    String sequenceNumber = null;
    String bagtagspurNumber = null;
    String recLoc = null;
    String destinationCode = null;
    String flightNumber = null;
    String date = null;
    String flightDate = null;
    String departureTime = null;
    String departureCode = flightInfo.getDepartureStation();
    String btpselectee = null;
    String airlineCode2 = null;
    String flightNumber2 = null;
    String destinationCode2 = null;
    String date2 = null;
    String flightDate2 = null;
    String departureTime2 = null;
    String destinationCode3 = null;
    String airlineCode3 = null;
    String flightNumber3 = null;
    String date3 = null;
    String flightDate3 = null;
    String departureTime3 = null;
    String bagweight = null;
    String bagspurnumber = null;
    
    String contentsString = "";
    String line = null;String v = null;
    while ((line = Toolkit.readLine(in)) != null)
    {
      v = null;
      if (line.contains("(BTP IATA INDENT)"))
      {
        v = Toolkit.readLine(in);iataIndent = v.substring(1);
      }
      else if (line.contains("(BTP BAG TAG NUMBER)"))
      {
        v = Toolkit.readLine(in);bagtagNumber = v;
      }
      else if (line.contains("(BTP AIRLINE CODE)"))
      {
        v = Toolkit.readLine(in);airlineCode = v;
      }
      else if (line.contains("(BTP PAX NAME)"))
      {
        v = Toolkit.readLine(in);paxName = v;
      }
      else if (line.contains("(BTP SEQUENCE NUMBER)"))
      {
        v = Toolkit.readLine(in);sequenceNumber = v;
      }
      else if (line.contains("(BTP BAG TAG SPUR NUMBER)"))
      {
        v = Toolkit.readLine(in);bagtagspurNumber = v;
      }
      else if (line.contains("(BTP REC LOC)"))
      {
        v = Toolkit.readLine(in);recLoc = v;
      }
      else if (line.contains("(BTP DESTINATION CODE)"))
      {
        v = Toolkit.readLine(in);destinationCode = v;
      }
      else if (line.contains("(BTP FLIGHT NUMBER)"))
      {
        v = Toolkit.readLine(in);flightNumber = v;
      }
      else if (line.contains("(BTP DATE)"))
      {
        v = Toolkit.readLine(in);date = v;
      }
      else if (line.contains("(BTP FLIGHT DATE)"))
      {
        v = Toolkit.readLine(in);flightDate = v;
      }
      else if (line.contains("(BTP DEPARTURE TIME)"))
      {
        v = Toolkit.readLine(in);departureTime = v;
      }
      else if (line.contains("(BTP SELECTEE)"))
      {
        v = Toolkit.readLine(in);btpselectee = v;
      }
      else if (line.contains("(BTP AIRLINE CODE 2)"))
      {
        v = Toolkit.readLine(in);airlineCode2 = v;
      }
      else if (line.contains("(BTP FLIGHT NUMBER 2)"))
      {
        v = Toolkit.readLine(in);flightNumber2 = v;
      }
      else if (line.contains("(BTP DESTINATION CODE 2)"))
      {
        v = Toolkit.readLine(in);destinationCode2 = v;
      }
      else if (line.contains("(BTP DATE 2)"))
      {
        v = Toolkit.readLine(in);date2 = v;
      }
      else if (line.contains("(BTP FLIGHT DATE 2)"))
      {
        v = Toolkit.readLine(in);flightDate2 = v;
      }
      else if (line.contains("(BTP DEPARTURE TIME 2)"))
      {
        v = Toolkit.readLine(in);departureTime2 = v;
      }
      else if (line.contains("(BTP DESTINATION CODE 3)"))
      {
        v = Toolkit.readLine(in);destinationCode3 = v;
      }
      else if (line.contains("(BTP AIRLINE CODE 3)"))
      {
        v = Toolkit.readLine(in);airlineCode3 = v;
      }
      else if (line.contains("(BTP FLIGHT NUMBER 3)"))
      {
        v = Toolkit.readLine(in);flightNumber3 = v;
      }
      else if (line.contains("(BTP DATE 3)"))
      {
        v = Toolkit.readLine(in);date3 = v;
      }
      else if (line.contains("(BTP FLIGHT DATE 3)"))
      {
        v = Toolkit.readLine(in);flightDate3 = v;
      }
      else if (line.contains("(BTP DEPARTURE TIME 3)"))
      {
        v = Toolkit.readLine(in);departureTime3 = v;
      }
      else if (line.contains("(BTP WEIGHT)"))
      {
        v = Toolkit.readLine(in);bagweight = v;
      }
      else if (line.contains("(BTP SPUR NUMBER)"))
      {
        v = Toolkit.readLine(in);bagspurnumber = v;
      }
      
      if (log.isDebugEnabled()) {
    	  log.debug("line : "  + line+"="+v);
        }
      
      if (line.contains("\033[4i")) {
        break;
      }
    }
    if (flightNumber == null) {
      return;
    }
    flightNumber = flightNumber.replaceAll(" ", "");
    flightNumber = flightNumber.replaceAll("  ", "");
    flightNumber = flightNumber.replaceAll("   ", "");
    
    flightNumber = String.format("%04d", new Object[] { Integer.valueOf(Integer.parseInt(flightNumber)) });
    if (flightNumber.charAt(0) == '0') {
      flightNumber = flightNumber.substring(1) + " ";
    }
    if (bagtagNumber.length() > 6) {
      bagtagNumber = bagtagNumber.substring(4);
    }
    if (log.isDebugEnabled()) {
      log.debug("remove IATA id : " + bagtagNumber);
    }
    flightDate = CheckinTool.translateFlightDateForPrintingDevice(flightDate);
    
    
    String baggageTag = "";
    try{  // ARINC 및 SITA 통합 백택
    	if(destinationCode2 == null){
    		log.debug("------------- this is not connection -----------");
    		baggageTag = "\033[5iBTP2\nBTP189901_01" + airlineCode + " " + bagtagNumber + "_" + 
      		      "02" + iataIndent + bagtagNumber + "_" + "03" + paxName + "_" + "04" + recLoc + "_" + "05" + departureCode + "_" + 
      		      "06" + flightDate + "_" + "0A" + bagweight + "KG" + "_" + "22" + destinationCode + "_" + "23" + " " + "_" + "24" + airlineCode + flightNumber + "_" + 
      		      "25" + flightDate + "_" + "32" + " " + "_" + "33" + " " + "_" + "34" + " " + "_" + 
      		      "35" + " " + "_" + "42" + " " + "_" + "43" + " " + "_" + "44" + " " + "_" + "45" + " " + "_" + "\n\033[4i";    
    	}else{
    		if(destinationCode2.trim().equals("")){
	    		log.debug("------------- this is not connection -----------");
	        	baggageTag = "\033[5iBTP2\nBTP189901_01" + airlineCode + " " + bagtagNumber + "_" + 
	        		      "02" + iataIndent + bagtagNumber + "_" + "03" + paxName + "_" + "04" + recLoc + "_" + "05" + departureCode + "_" + 
	        		      "06" + flightDate + "_" + "0A" + bagweight + "KG" + "_" + "22" + destinationCode + "_" + "23" + " " + "_" + "24" + airlineCode + flightNumber + "_" + 
	        		      "25" + flightDate + "_" + "32" + " " + "_" + "33" + " " + "_" + "34" + " " + "_" + 
	        		      "35" + " " + "_" + "42" + " " + "_" + "43" + " " + "_" + "44" + " " + "_" + "45" + " " + "_" + "\n\033[4i";
	        }else{
	        	log.debug("------------- this is connection -----------");
	        	flightDate2 = CheckinTool.translateFlightDateForPrintingDevice(flightDate2);
	        	baggageTag = "\033[5iBTP2\nBTP189901_01" + airlineCode + " " + bagtagNumber + "_" + 
	        		      "02" + iataIndent + bagtagNumber + "_" + "03" + paxName + "_" + "04" + recLoc + "_" + "05" + departureCode + "_" + 
	        		      "06" + flightDate + "_" + "0A" + bagweight + "KG" + "_" + "22" + destinationCode2 + "_" + "23" + " " + "_" + "24" + airlineCode2 + flightNumber2 + "_" + 
	        		      "25" + flightDate2 + "_" + "32" + destinationCode + "_" + "33" + " " + "_" + "34" + airlineCode + flightNumber + "_" + 
	        		      "35" + flightDate + "_" + "42" + " " + "_" + "43" + " " + "_" + "44" + " " + "_" + "45" + " " + "_" + "\n\033[4i";
	        }
    	}	    	
    }catch(Exception e){
    	log.debug("exception---------> : " + e);
    	return;
    }

    if (log.isDebugEnabled()) {
      log.debug("baggageTag --> : " + baggageTag + "_" + destinationCode2 + "_");
    }
    byte[] tbuf = baggageTag.getBytes();
    response.write(tbuf, 0, tbuf.length);
  }
}
