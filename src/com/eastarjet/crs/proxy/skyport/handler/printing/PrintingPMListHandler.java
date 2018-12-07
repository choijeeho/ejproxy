package com.eastarjet.crs.proxy.skyport.handler.printing;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
import com.navitaire.schemas.ClientServices.BookingManager.BookingManagerClient.BookingManagerClientSoapProxy;
import com.navitaire.schemas.ClientServices.Common.SessionManagerClient.SessionManagerClientSoapProxy;
import com.navitaire.schemas.ClientServices.OperationsManager.OperationsManagerClient.OperationsManagerClientSoapProxy;
import com.navitaire.schemas.Common.Enumerations.AvsCollectionsMode;
import com.navitaire.schemas.Common.Enumerations.FlightType;
import com.navitaire.schemas.Common.Enumerations.SSRCollectionsMode;
import com.navitaire.schemas.Common.SessionContext;
import com.navitaire.schemas.Messages.Booking.PassengerFee;
import com.navitaire.schemas.Messages.Booking.Request.GetBookingRequest;
import com.navitaire.schemas.Messages.Common.InventoryLegKey;
import com.navitaire.schemas.Messages.Common.Name;
import com.navitaire.schemas.Messages.Manifest.Manifest;
import com.navitaire.schemas.Messages.Manifest.ManifestInfant;
import com.navitaire.schemas.Messages.Manifest.ManifestLegSSR;
import com.navitaire.schemas.Messages.Manifest.ManifestPassenger;
import com.navitaire.schemas.Messages.Manifest.ManifestRequest;
import com.navitaire.schemas.Messages.Operations.Reports.MealSSRReport;
import com.navitaire.schemas.Messages.Session.Response.LogonResponse;
import com.navitaire.schemas.WrapOfBooking;
import com.navitaire.schemas.WrapOfLogonResponse;
import com.navitaire.schemas.WrapOfManifest;
import com.navitaire.schemas.WrapOfMsgListOfMealSSRReport;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.apache.axis.types.UnsignedShort;

public class PrintingPMListHandler extends AbstractPrintingHandler
{
  static Logger log = Toolkit.getLogger(PrintingPMListHandler.class);

  static String infantTitle = "\r\n                                           Fare\r\nCnt           Name (i = INF)        PNR    Class   Seq No   Date   Seat No\r\n--- ------------------------------ ------ -------- ------  ------- -------\r\n";
  static String ssrTitle = "\r\n                                           Fare\r\nCnt           Name (i = INF)        PNR    Class   Seq No  Seat No FeeCode\r\n--- ------------------------------ ------ -------- ------  ------- -------\r\n";
  static final byte[] printBegin = { 27, 91, 53, 105 };
  static final byte[] printEnd = { 27, 91, 52, 105 };
  
  static SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
  int infantCnt=0;  // 유아 수 카운트
  public boolean handleTargetRequest(int target, Session session, Request request, Response response)
  {
    try
    {
      sendPMPrinting(request, response);
    }
    catch (Exception e) {
      log.error("Error at PrintingPMListHandler", e);
    }
    return false;
  }

  protected void sendPMPrinting(Request request, Response response)
    throws Exception
  {
    if (log.isDebugEnabled()) log.debug("PM LIST START");

    InputStream in = request.getInputStream();

    String line = ""; String eline = null;
    int size = 1;
    byte[] lbuf = (byte[])null;

    while (line != null)
    {
      line = Toolkit.readLine(in);
      if (size < 0)
        break;
      lbuf = line.getBytes();

      if (log.isDebugEnabled()) log.debug("line: " + line);
      response.write(lbuf, 0, lbuf.length);
      response.writeByte(13);
      response.writeByte(10);
      if (line.indexOf("Flight:") >= 0) break;
      response.flush();
    }

    String tline = line.replaceAll(" ", "");
    String[] keys = tline.split(":");

    for (int i = 0; i < keys.length; i++)
    {
      if (!log.isDebugEnabled()) continue; log.debug("keys " + i + " : " + keys[i]);
    }
    infantCnt=0; 
    String flightNumber = keys[1].length() == 13 ? keys[1].substring(0, 3) : keys[1].substring(0, 4);
    String departureStation = keys[1].length() == 13 ? keys[1].substring(3, 6) : keys[1].substring(4, 7);
    String arrivalStation = keys[1].length() == 13 ? keys[1].substring(6, 9) : keys[1].substring(7, 10);
    String departureDate = keys[2].substring(0, 7);

    Map ret = getPMSummary(departureDate, flightNumber, departureStation, arrivalStation);
    Vector infants = (Vector)ret.get("infants");
    Vector feeCodes = (Vector)ret.get("inFeecodes");
    
    int countOfInfant = infantCnt;
   
    int feecodeSize = feeCodes.size();
    
    int totalCheckedin = ((Integer)ret.get("totalOnBoard2")).intValue();

    int pos = 0;
    int lineCount = 0;
    while ((line = Toolkit.readLine(in)) != null)
    {
      lineCount++;

      if ((pos = line.indexOf("Checked-in/Boarded           ")) > 0)
      {
        int checkedinBoardeds = totalCheckedin;
        line = line.substring(0, pos + 25) + String.format("%3d", new Object[] { Integer.valueOf(checkedinBoardeds) });
      }
      else if ((pos = line.indexOf("Total Checked-in             ")) > 0)
      {
        line = line.substring(0, pos + 25) + String.format("%3d", new Object[] { Integer.valueOf(totalCheckedin) });
      }
      else if ((pos = line.indexOf("Inf. Checked-in/Boarded      ")) > 0)
      {
        line = line.substring(0, pos + 25) + String.format("%3d", new Object[] { Integer.valueOf(countOfInfant) });
      }
      else if ((pos = line.indexOf("  Totals:           ")) == 0)
      {
        String totals = " " + String.format("%3d", new Object[] { Integer.valueOf(totalCheckedin) }) + " " + String.format("%3d", new Object[] { Integer.valueOf(countOfInfant) });
        line = line.substring(0, pos + 32) + totals;
      }
      else if (line.contains("\001\033[4i"))
      {
        eline = line;
        break;
      }

      lbuf = line.getBytes();
      response.write(lbuf, 0, lbuf.length);
      response.writeByte(13);
      response.writeByte(10);

      if ((lineCount + 1) % 47 == 0)
     {
        if (log.isDebugEnabled()) log.debug("page: " + lineCount);
        response.write(printEnd, 0, printEnd.length);
        response.flush();
        try { Thread.sleep(1000L);
        } catch (Exception localException) {
        }
        response.write(printBegin, 0, printBegin.length);
      }
      response.flush();
      if (!log.isDebugEnabled()) continue; log.debug("line: " + line);
    }

    if (countOfInfant > 0)
    {
      lbuf = infantTitle.getBytes();
      response.write(lbuf, 0, lbuf.length);

      for (int i = 0; i < countOfInfant; i++)
      {
        line = (String)infants.get(i);

        lbuf = line.getBytes();
        response.write(lbuf, 0, lbuf.length);
        response.writeByte(13);
        response.writeByte(10);
        response.flush();
        if (!log.isDebugEnabled()) continue; log.debug("line: " + line);
      }

    }
    // FeeCode 양식 추가
    if (feecodeSize > 0)
    {
      lbuf = ssrTitle.getBytes();
      response.write(lbuf, 0, lbuf.length);

      for (int i = 0; i < feecodeSize; i++)
      {
        line = (String)feeCodes.get(i);

        lbuf = line.getBytes();
        response.write(lbuf, 0, lbuf.length);
        response.writeByte(13);
        response.writeByte(10);
        response.flush();
        if (!log.isDebugEnabled()) continue; log.debug("line: " + line);
      }

    }
    
    
    
    

    line = eline;
    if (log.isDebugEnabled()) log.debug("line : " + eline);
    if (line != null) {
        lbuf = line.getBytes();
        response.write(lbuf, 0, lbuf.length);
        response.writeByte(13);
        response.writeByte(10);

        response.write(printEnd, 0, printEnd.length);
        response.flush();    	
    }

  }

  Map<String, Object> getPMSummary(String departureDate, String flightNumber, String departureStation, String arrivalStation)
    throws Exception
  {
    String operationsManagerClientSoapProxy = (String)getAttribute("soap.operationManager");
    String sessionManagerClientSoapProxy = (String)getAttribute("soap.sessionManager");

    OperationsManagerClientSoapProxy operationsmanagerclientsoapProxy = new OperationsManagerClientSoapProxy(operationsManagerClientSoapProxy);
    SessionManagerClientSoapProxy sessionmanagerclientsoapProxy = new SessionManagerClientSoapProxy(sessionManagerClientSoapProxy);
 
    WrapOfLogonResponse wraplogonResponse = logOnWebService(sessionmanagerclientsoapProxy);

    if (wraplogonResponse.getException() != null) return null;

    if (log.isDebugEnabled()) log.debug("flightNumber     : " + flightNumber);
    if (log.isDebugEnabled()) log.debug("departureStation : " + departureStation);
    if (log.isDebugEnabled()) log.debug("arrivalStation : " + arrivalStation);
    if (log.isDebugEnabled()) log.debug("departureDate : " + departureDate);

    LogonResponse logonResponse = wraplogonResponse.getResult();
    SessionContext session = logonResponse.getSessionContext();

    ManifestRequest manifestRequest = new ManifestRequest();
    InventoryLegKey inventoryLegKey = new InventoryLegKey();

    inventoryLegKey.setCarrierCode("ZE");
    inventoryLegKey.setDepartureStation(departureStation);
    inventoryLegKey.setArrivalStation(arrivalStation);

    Calendar daptartue = new GregorianCalendar();

    daptartue.setTime(sdf.parse(departureDate));
    daptartue.add(10, 9);

    inventoryLegKey.setDepartureDate(daptartue);

    inventoryLegKey.setFlightNumber(flightNumber);
    inventoryLegKey.setOpSuffix(new UnsignedShort("32"));

    manifestRequest.setInventoryLegKey(inventoryLegKey);
    manifestRequest.setFlightType(FlightType.All);
    manifestRequest.setAvsCollectionsMode(AvsCollectionsMode.All);
    manifestRequest.setSSRCollectionsMode(SSRCollectionsMode.All);

    WrapOfManifest wrapofmanifest = operationsmanagerclientsoapProxy.getManifest(session, manifestRequest);
    
   
    System.out.println("=============flightNumber==========="+flightNumber);
    
    if (wrapofmanifest.getException() != null) return null;
    if (log.isDebugEnabled()) log.debug("wrapofmanifest.getException() is not null");
     Manifest ff = wrapofmanifest.getResult();
    int totalOnBoard = wrapofmanifest.getResult().getManifested();
    int totalOnBoard2 = wrapofmanifest.getResult().getTotalCheckedIn();
    if (log.isDebugEnabled()) log.debug("totalOnBoard : " + totalOnBoard);

    Calendar birthDay = Calendar.getInstance();
    GregorianCalendar today = new GregorianCalendar();
    Vector infants = new Vector();
    Vector inFeecodes = new Vector();
   
    int mealCnt=0;    // 기내식 수 카운트

    for (int i = 0; i < totalOnBoard; i++)
    {
      ManifestPassenger manifestpassenger = wrapofmanifest.getResult().getPassengerList()[i];

      String recordLocator = manifestpassenger.getRecordLocator();
    
      Date DOB = manifestpassenger.getDOB();

      String passengerName = manifestpassenger.getName().getLastName() + ", " + manifestpassenger.getName().getMiddleName() + manifestpassenger.getName().getFirstName();
      
      // 좌석 가지고 오기
       short seatRow                     = 0;
       char seatcolumnAscii     		= 0;
	
	  seatRow = manifestpassenger.getServices()[0].getSegments()[0].getLegs()[0].getSeatRow();
	  // short -> char 형변환
      seatcolumnAscii =(char) manifestpassenger.getServices()[0].getSegments()[0].getLegs()[0].getSeatColumn().longValue();
      
      ManifestLegSSR[] MSSR = manifestpassenger.getServices()[0].getSegments()[0].getLegs()[0].getSSRs();
      // inft는 밑에서 어차피 뽑아주므로 제외하고 삽입한다 유아도 제외
      Vector vt = new Vector();
    
   
    
      for(int cnt=0; cnt < MSSR.length; cnt++){
    	  System.out.println("****MEAL="+MSSR[cnt].getSSRCode());
    	  if(MSSR[cnt].getSSRCode().equals("MEAL") || MSSR[cnt].getSSRCode().equals("IBR1") || MSSR[cnt].getSSRCode().equals("IBR2") || MSSR[cnt].getSSRCode().equals("ICC1") || MSSR[cnt].getSSRCode().equals("ICC2") || MSSR[cnt].getSSRCode().equals("ICR1") || MSSR[cnt].getSSRCode().equals("ICR2") || MSSR[cnt].getSSRCode().equals("ICS1") || MSSR[cnt].getSSRCode().equals("ICS2")  || MSSR[cnt].getSSRCode().equals("IPS1") || MSSR[cnt].getSSRCode().equals("IPS2") || MSSR[cnt].getSSRCode().equals("ISS1") || MSSR[cnt].getSSRCode().equals("ISS2") ){
    		  String ssrCodes = MSSR[cnt].getSSRCode();
        	  vt.add(ssrCodes);  
    	  }
    	  System.out.println("ssrcode="+MSSR[cnt].getSSRCode());
    	  if(MSSR[cnt].getSSRCode().equals("IINF") || MSSR[cnt].getSSRCode().equals("INF") || MSSR[cnt].getSSRCode().equals("INFT")){
    		
    		  infantCnt++;
    	  }
    	  
    	 
      }
      
     
      if(vt.size()>0){
    	  System.out.println("MealCode가 있을 시");
      // Meal 코드가 있는 사람과 없는 사람에 따라 자리 조정구문 추가
      String feecodedata = String.format("%3d", new Object[] { Integer.valueOf(mealCnt + 1) }) + "   " + passengerName;
      mealCnt++;
      for (int k = feecodedata.length(); k < 35; k++)
      {
    	  feecodedata = feecodedata + " ";
      }
      if(vt.size()>0){
    	  for(int j=0; j<vt.size(); j++){
    		  if(j==0){
    			  feecodedata = feecodedata + recordLocator + "                    " +seatRow + seatcolumnAscii + "    "+vt.get(j)+"\r\n";
    		  }else{
    			  feecodedata += "                                                                                                      "+vt.get(j)+"\r\n";  
    		  }
    		  
    	  }
    	 	  
      }
      inFeecodes.add(feecodedata);
      
      }
      
      if (log.isDebugEnabled()) log.debug("manifestpassenger.isInfant() : " + manifestpassenger.isInfant());
      if (!manifestpassenger.isInfant())
        continue;
     
      String infantname = manifestpassenger.getManifestInfant().getName().getLastName() + ", " + manifestpassenger.getManifestInfant().getName().getMiddleName() + manifestpassenger.getManifestInfant().getName().getFirstName();
      String infantDOB = manifestpassenger.getManifestInfant().getDOB().toString();

   //   String infantdata = String.format("%3d", new Object[] { Integer.valueOf(infantCnt + 1) }) + "   " + passengerName;
      String infantdata = String.format("%3d", new Object[] { Integer.valueOf(infantCnt) }) + "   " + passengerName;
      System.out.println(infantdata.length());
  
      for (int k = infantdata.length(); k < 35; k++)
      {
        infantdata = infantdata + " ";
      }
 
      
      infantdata = infantdata + recordLocator + "                            " +seatRow + seatcolumnAscii +"\r\n" + "      " + infantname + " (INF)";
      
      if (log.isDebugEnabled()) log.debug("infant : " + infantdata);

      infants.add(infantdata);
    }

    Hashtable ret = new Hashtable();

    ret.put("totalOnBoard", new Integer(totalOnBoard));
    ret.put("totalOnBoard2", new Integer(totalOnBoard2));
    ret.put("infants", infants);
    ret.put("inFeecodes", inFeecodes);
    return ret;
  }
}