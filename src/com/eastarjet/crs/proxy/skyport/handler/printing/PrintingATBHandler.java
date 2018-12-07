package com.eastarjet.crs.proxy.skyport.handler.printing;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.axis.types.UnsignedShort;
import org.json.JSONObject;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;
import com.eastarjet.ejproxy.EJProxyClient;
import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayResponse;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
import com.navitaire.schemas.WrapOfInventoryLegOp;
import com.navitaire.schemas.WrapOfLogonResponse;
import com.navitaire.schemas.ClientServices.Common.SessionManagerClient.SessionManagerClientSoapProxy;
import com.navitaire.schemas.ClientServices.OperationsManager.OperationsManagerClient.OperationsManagerClientSoapProxy;
import com.navitaire.schemas.Common.SessionContext;
import com.navitaire.schemas.Common.Enumerations.OpTimeType;
import com.navitaire.schemas.Messages.Common.InventoryLegKey;
import com.navitaire.schemas.Messages.Operations.InventoryLegOp;
import com.navitaire.schemas.Messages.Session.Response.LogonResponse;

public class PrintingATBHandler extends AbstractPrintingHandler 
{
	static Logger log = Toolkit.getLogger(PrintingATBHandler.class);

	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		try{
			sendATBPrinting( session,request, response);
		}catch(Exception e)
		{
			log.error("Error at PrintingATBHandler"  , e);
		}
		return false;
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy",Locale.ENGLISH);
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	static String gmpkor= new String( new byte[]{-22, -71, 63, -113, -84});
	static byte [] bgmpkor= {-22, -71, 63, -113, -84};
	
	
	 public static String getRPad(String str, int size, String strFillText) {//오른쪽으로 자리수 채우기(tsa바코드)
	        for(int i = (str.getBytes()).length; i < size; i++) {
	            str += strFillText;
	        }
	        return str;
	    }
	 
	 public static String getLPad(String str, int size, String strFillText) {//왼쪽으로 자리수 채우기(tsa바코드)
	        for(int i = (str.getBytes()).length; i < size; i++) {
	            str = strFillText + str;
	        }
	        return str;
	    }
	 
	protected void sendATBPrinting(Session session,Request request,Response response) throws IOException
	{
		
 
		FlightInfo finfo= (FlightInfo)session.getAttribute(SessionKey.FLIGHTINFO);
		
		String st=null;
		
		if(finfo!=null) st=finfo.getDepartureStation();
		
		if(st.equals("PPS"))
		{
			response.writePeekAll(request);
			return;
		}
		
		InputStream in=request.getInputStream();
		String hd=Toolkit.readLine(in); //header
		if(log.isDebugEnabled()) log.debug("atb.hd="+hd);
		
		
		String tag=Toolkit.readLine(in); //ATB tag
		if(log.isDebugEnabled()) log.debug("atb.tag="+tag);
		
		String atbfullname = null;
		String atbinfantName = null;
		String atbinfantIND = null;
		String atbfromApo = null;
		String atbtoApo = null;
		String atbflightcode = null;
		String atbflightNo = null;
		String atbfromDate = null;
		String atbfromTime = null;
		String atbbrdTime = null;
		String atbgateNo = null;
		String atbseatNo = null;
		String atbsequenceNo = null;
		String atbpnr = null;
		String atbtoTime = null;
		String atbssr = null;
		String atbmsg = null;
		String atbbagPieces = null;
		String atbbagWeight = null;
		String atbselectee = null;
		
		String atbname = null;
		String atbnametsa = null;
		String atbnametsa2 = null;
		String atbfullfromApo = null;
		String atbfulltoApo = null;
		String atbcityPair = null;
		
		String atbBarcode2d = null;
		String atbpaxType = null;
		

		String namePre = null;
		String nameOk = null; 
		
		
		String contentsString = "";
	
		String line = null,v=null;
	
	 
		while (null != (line = Toolkit.readLine(in))) 
		{
			
			
			v=null;
			if(line.contains("(ATB PASSENGER NAME)"))
			{	v = Toolkit.readLine(in); atbfullname = v; }
			else if(line.contains("(ATB INFANT IND)"))
			{
				v =Toolkit.readLine(in); atbinfantIND = v;
			}
			else if(line.contains("(ATB INFANT NAME)"))
			{
				v = Toolkit.readLine(in);  atbinfantName = v;
			}
			else if(line.contains("(ATB ARRIVAL LOCATION)"))
			{	
				v  =Toolkit.readLine(in);  atbfulltoApo =v;
				byte [] bp =	atbfulltoApo.getBytes();
				boolean eq=true;
				for(int i=0;i<bp.length;i++)
				{
					if(bp[i]!=bgmpkor[i]){eq=false;} break;
				}
			
 
				if(eq ) atbfulltoApo="GIMPO";
				if(eq ) atbfulltoApo="JEJU";

			}
			else if(line.contains("(ATB FLIGHT DATE)"))
			{	v = Toolkit.readLine(in);
					atbfromDate =v;

			}
			else if(line.contains("(ATB AIRLINE CODE)"))
			{	v =	 Toolkit.readLine(in); atbflightcode = v; }
			else if(line.contains("(ATB FLIGHT NUMBER)"))
			{	v =	 Toolkit.readLine(in); 	atbflightNo = v; }
			else if(line.contains("(ATB SEAT NUMBER)"))
			{	v =	 Toolkit.readLine(in); 	atbseatNo = v; }
			else if(line.contains("(ATB DEPARTURE TIME COLON)"))
			{	v =	 Toolkit.readLine(in); 	atbfromTime = v; }
			else if(line.contains("(ATB SEQUENCE NUMBER)"))
			{	v =	 Toolkit.readLine(in); 	atbsequenceNo = v; }		
			else if(line.contains("(ATB GATE NUMBER)"))
			{	v =	Toolkit.readLine(in); 	atbgateNo = v; }
			else if(line.contains("(ATB BOARDING TIME)"))
			{	v =	 Toolkit.readLine(in); 	atbbrdTime = v; 
			}
			else if(line.contains("(ATB CITY PAIR)"))
			{	v =	 Toolkit.readLine(in); 
				atbcityPair = v;
				atbfromApo = atbcityPair.substring(0, 3);
				atbtoApo = atbcityPair.substring(3, 6);
			}
			else if(line.contains("(ATB REC LOC)"))
			{	v =	 Toolkit.readLine(in); 	atbpnr = v; }
			else if(line.contains("(ATB DEPARTURE LOCATION)"))
			{
				v =	 Toolkit.readLine(in); 	atbfullfromApo = v;



				byte [] bp =	atbfullfromApo.getBytes();
				boolean eq=true;
				for(int i=0;i<bp.length;i++)
				{
					if(bp[i]!=bgmpkor[i]){eq=false;} break;
				}
				//gmp-tsa codeshare
				if(" 887".equals(atbflightNo)) atbfullfromApo="GIMPO(TW8667)";	
				if(" 888".equals(atbflightNo)) atbfullfromApo="Songshan(TW8668)";
				//atbfullfromApo= new String(atbfullfromApo.getBytes("euc-kr"),"utf-8");
//				log.debug("utf-8:"+new String(atbfullfromApo.getBytes("utf-16"),"utf-8"));
//				log.debug("utf-8:"+new String(atbfullfromApo.getBytes("euc-kr"),"utf-8"));
//				log.debug("euc-kr:"+new String(atbfullfromApo.getBytes("utf-8"),"euc-kr"));
				//if(eq ) atbfullfromApo="GIMPO";
				
			}
			else if(line.contains("(ATB ARRIVAL TIME COLON)"))
			{	v =	 Toolkit.readLine(in); 	atbtoTime = v; }
			else if(line.contains("(ATB COMMA DELIMITED SSR LIST)"))
			{	v =	 Toolkit.readLine(in); 	atbssr = v; }
			else if(line.contains("(ATB BOARDING MESSAGE)"))
			{	v =	 Toolkit.readLine(in); 	atbmsg = v; }
			else if(line.contains("(ATB BAG PIECES)"))
			{	v =	 Toolkit.readLine(in); 	atbbagPieces = v; }
			else if(line.contains("(ATB BAG WEIGHT)"))
			{	v =	 Toolkit.readLine(in); 	atbbagWeight = v; }
			else if(line.contains("(ATB SELECTEE)"))
			{	v =	 Toolkit.readLine(in); 	atbselectee = v; }
			
			if(log.isDebugEnabled())
				log.debug("line : "  + line+"="+v);
			if(line.equals("\033[4i")) break;
		}
		
		if(log.isDebugEnabled()) log.debug("atbssr="+atbssr+",atbinfantIND="+atbinfantIND);
		if ( (atbssr!=null && atbssr.contains("INFT")) || (atbinfantIND!=null && atbinfantIND.contains("INF")) || (atbssr!=null && atbssr.contains("IINF"))) 
		{
			if(atbfullname.contains(atbinfantName))
			{
				atbpaxType = "4";//infant for KAC
				atbseatNo="INF";
			}
			else
			{
				atbpaxType = "1";
			}
		}
		else 
		{
			atbpaxType = "1"; //adult for KAC
		}
		
		String[] atbfullnames = null;
		String atbfullnameData = null;
		
		try
		{
			atbfullnames = atbfullname.split("/");
			//atbfullnameData = atbfullnames[0] + "/" + atbfullnames[1] + " " + atbfullnames[2];
			atbfullnameData = atbfullnames[0] + "/" + atbfullnames[1] + atbfullnames[2];//TSA bacodetest
			
			namePre = atbfullnames[0] + "/" + atbfullnames[1];
			
			nameOk =  atbfullnames[2];
			
			if(namePre.length()>17){
				namePre = namePre.substring(0,18);
				//nameOk = nameOk.substring(0, 2);
				if(nameOk.equals("MISS")){
					nameOk="MS";
				}else if(nameOk.equals("MSTR")){
					nameOk="MR";
				}else if(nameOk.equals("MRS")){
					nameOk="MS";
				}
				atbfullname = namePre+nameOk;
				atbname = namePre; 
				atbnametsa = getRPad(atbfullname, 20, " ");
			}else{
				if(nameOk.length()>=4){
					nameOk = nameOk.substring(0, 4);
				}
				
				if(nameOk.equals("MISS")){
					nameOk="MS";
				}else if(nameOk.equals("MSTR")){
					nameOk="MR";
				}else if(nameOk.equals("MRS")){
					nameOk="MS";
				}
				
				
				atbfullname = namePre+nameOk;
				atbname = namePre;
				atbnametsa = getRPad(atbfullname, 20, " ");
			}
		}
		catch(Exception e)
		{
		}

		if(atbname.length() > 16){
			System.out.println("atbname1="+atbname);
			atbname = atbname.substring(0, 16);
			atbname = atbname+nameOk;
		}else{
			atbname = atbfullname;
			atbnametsa2 = getRPad(atbname, 18, " ");
			atbname = atbnametsa2;
		}
		
		if(atbfullname.length() > 28)
			atbfullname = atbfullname.substring(0, 28);
				
		if(atbfullfromApo.length() > 18)
		{
			atbfullfromApo = atbfullfromApo.substring(0, 18);
			if("GIMPO".equals(atbfullfromApo)) atbfullfromApo="GIMPO(TW9887)";
			
		}
		
		if(atbfulltoApo.length() > 15)
		{
			atbfulltoApo = atbfulltoApo.substring(0, 15);
			//if("김포".equals(atbfulltoApo) ) atbfulltoApo="Gimpo";
			
		}
/*
		if(log.isDebugEnabled())
		{
			log.debug("fullname : "  + atbfullname);
			log.debug("infantName : "  + atbinfantName);
			log.debug("fromApo : "  + atbfromApo);
			log.debug("toApo : "  + atbtoApo);
			log.debug("flightcode : "  + atbflightcode);
			log.debug("flightNo : "  + atbflightNo);
			log.debug("fromDate : "  + atbfromDate);
			log.debug("fromTime : "  + atbfromTime);
			log.debug("brdTime : "  + atbbrdTime);
			log.debug("gateNo : "  + atbgateNo);
			log.debug("seatNo : "  + atbseatNo);
			log.debug("sequenceNo : "  + atbsequenceNo);
			log.debug("pnr : "  + atbpnr);
			log.debug("toTime : "  + atbtoTime);
			log.debug("ssr : "  + atbssr);
			log.debug("msg : "  + atbmsg);
			log.debug("bagPieces : "  + atbbagPieces);
			log.debug("bagWeight : "  + atbbagWeight);
			log.debug("name : "  + atbname);
			log.debug("fullfromApo : "  + atbfullfromApo);
			log.debug("fulltoApo : "  + atbfulltoApo);
			log.debug("cityPair : "  + atbcityPair);
			log.debug("paxType : "  + atbpaxType);
		}//ilog
*/
		//Gate Number 우측정렬 3자리 
		atbgateNo = ("   "+atbgateNo);
		atbgateNo=atbgateNo.substring(atbgateNo.length()-3);
		
//		atbflightNo = atbflightNo.trim();
		atbflightNo = atbflightNo.replaceAll(" ", "");
		//atbflightNo = atbflightNo.replaceAll("  ", "");
		//atbflightNo = atbflightNo.replaceAll("   ", "");
		
		atbflightNo = String.format("%04d", Integer.parseInt(atbflightNo));
		String barcodeFlightNo = String.format("%05d", Integer.parseInt(atbflightNo));
		
		atbsequenceNo = String.format("%03d", Integer.parseInt(atbsequenceNo));
		String barcodeSequenceNo = String.format("%04d", Integer.parseInt(atbsequenceNo));
		
		//TSA  Bordingpass Barcode 2013/06/24
		//atbseatNo = String.format("%04d", Integer.parseInt(atbseatNo));
		//atbseatNo = String.format(atbseatNo, args)
		//String barcodeSeatNo = String.format("%4c", atbseatNo);
		
		atbfromDate = atbfromDate.toUpperCase();
		String year = "20" + atbfromDate.substring(5, 7);
		String month = String.format("%02d", CheckinTool.getMonth(atbfromDate.substring(2, 5)));
		String day = atbfromDate.substring(0, 2);
		
		//TSA Bordingpass Barcode julian Date 2013/07/09
		int day2 = Integer.parseInt(day);
		int month2 = (Integer.parseInt(month)-1);
		int year2 = Integer.parseInt(year);
		
		    GregorianCalendar gc = new GregorianCalendar(); 
		    gc.set(GregorianCalendar.DAY_OF_MONTH, day2); 
		    gc.set(GregorianCalendar.MONTH, month2); 
		    gc.set(GregorianCalendar.YEAR, year2); 
		    
		String julian = String.format("%03d", gc.get(GregorianCalendar.DAY_OF_YEAR));
		    
		 //   System.out.println(gc.get(GregorianCalendar.DAY_OF_YEAR));
		
		//TSA Bordingpass Barcode 2013/06/24
		    
		String atbseatNotsa = getLPad(atbseatNo, 4, "0");
		
		if(atbpaxType.equals("1"))
		{
			if(atbfromApo.equals("REP")||atbfromApo.equals("HAN") || atbfromApo.equals("TSA")||atbfromApo.equals("HKG")||atbfromApo.equals("ICN")||atbfromApo.equals("PUS")||atbfromApo.equals("TPE")||atbfromApo.equals("FUK")||atbfromApo.equals("CJJ")||atbfromApo.equals("GMP") || atbfromApo.equals("DAD"))
			{
				atbBarcode2d = atbnametsa +"E" + atbpnr + " "+ atbfromApo + atbtoApo + atbflightcode + " " + atbflightNo + " " + 
				    julian + "C" +  atbseatNotsa + barcodeSequenceNo + " "+ atbpaxType  + "00";	
			}
			else 
			{
				atbBarcode2d = "I" + atbflightcode +  barcodeFlightNo + barcodeSequenceNo + year + month + day + "C" + atbpaxType + atbfromApo;
			}
		} else if (atbpaxType.equals("4")) {
			if(atbfromApo.equals("HAN") || atbfromApo.equals("CJJ") || atbfromApo.equals("PUS"))
			{
				atbBarcode2d = atbnametsa +"E" + atbpnr + " "+ atbfromApo + atbtoApo + atbflightcode + " " + atbflightNo + " " + 
				    julian + "C" +  atbseatNotsa + barcodeSequenceNo + " "+ atbpaxType  + "00";	
			}
		}
		/*atbBarcode2d = atbnametsa +"E"+ atbpnr + " "+ atbfromApo + atbtoApo + atbflightcode + " " + atbflightNo + " " + 
			     "C" +  atbseatNotsa + barcodeSequenceNo + "00";*/
			/*atbBarcode2d =   atbfromApo + atbtoApo+"E" + atbflightcode + atbflightNo + 
			    "C" + barcodeSequenceNo + "000";*/
		
			/*
			if(atbfromApo.equals("TSA"))
			{
				atbBarcode2d = atbfullname +"E"+ atbpnr + " "+ atbfromApo + atbtoApo + atbflightcode + " " + atbflightNo + " " + 
			    gc.get(GregorianCalendar.DAY_OF_YEAR) + "C" +  atbseatNo + barcodeSequenceNo + " "+ atbpaxType  + "00";
			}
		 
			{
				atbBarcode2d = "I" + atbflightcode +  barcodeFlightNo + barcodeSequenceNo + year + month + day + "C" + atbpaxType + atbfromApo;
			}*/
		
	
		
	
		//국제선만 됨 
		//국내선일 경우엔 앞에 공백 추가 
		//String inventoryLegKey = year + month + day + " " + "ZE" + atbflightNo.trim() + " " + atbfromApo + atbtoApo;
	//	log.debug("inventoryLegOp : "  + inventoryLegKey);
		
	//	inventoryLegKey = "20101001 ZE 203 GMPCJU";
		
		/*String etd=getETDfromCRS(inventoryLegKey);
		if(etd!=null) atbfromTime=etd;*/

		/*String dDate = year + "-" + month + "-" + day;
		String etd=getETDfromMiddleWare(atbflightNo.trim(),atbfromApo,atbtoApo,dDate);
		if(!etd.equals("")) atbfromTime=etd;*/
		
		atbbrdTime=calcBoardingTime(atbfromApo,atbfromTime);
		
		
		if(atbflightNo.charAt(0) == '0')                       // 나리타 3자리 편명 때문에 
			atbflightNo = atbflightNo.substring(1) + " ";
		
		String boardingPass = "";
		
		if (atbselectee == null) {
			boardingPass = "\033[5i" + "ATB2\n" + "CP" + "|" + "1C01" + "|" + "01E" + "|" + "05" + atbname 
					+ "|" + "06" + atbfullname						
					+ "|" + "12" + atbfromApo //us sf selectee
					+ "|" + "13" + atbfullfromApo + "|" + "1C" + atbtoApo 
					+ "|" + "1F" + atbflightcode + "|" + "21" + atbflightNo 
					+ "|" + "25" + atbfromDate + "|" + "2B" + atbfulltoApo 
					+ "|" + "35" + atbfromTime + "|" + "3A" + atbgateNo + "|" + "3B" + atbbrdTime
					+ "|" + "3C" + atbseatNo + "|" + "3D" + atbsequenceNo 
					+ "/" + atbpnr + "|" + "EEM1" + atbBarcode2d + "|" + "\033[4i" + "\n";
		} else {
			boardingPass = "\033[5i" + "ATB2\n" + "CP" + "|" + "1C01" + "|" + "01E" + "|" + "05" + atbname 
					+ "|" + "06" + atbfullname						
					+ "|" + "07" + atbselectee 
					+ "|" + "12" + atbfromApo //us sf selectee
					+ "|" + "13" + atbfullfromApo + "|" + "1C" + atbtoApo 
					+ "|" + "1F" + atbflightcode + "|" + "21" + atbflightNo 
					+ "|" + "25" + atbfromDate + "|" + "2B" + atbfulltoApo 
					+ "|" + "35" + atbfromTime + "|" + "3A" + atbgateNo + "|" + "3B" + atbbrdTime
					+ "|" + "3C" + atbseatNo + "|" + "3D" + atbsequenceNo 
					+ "/" + atbpnr + "|" + "EEM1" + atbBarcode2d + "|" + "\033[4i" + "\n";
		}

		if(log.isDebugEnabled())log.debug("Barcode2d : "  + atbBarcode2d);
		if(log.isDebugEnabled())log.debug("boardingPass : "  + boardingPass);
		
		String bpbt = boardingPass;

		byte [] tbuf=bpbt.getBytes();
		response.write(tbuf, 0, tbuf.length);
		//response.writeAll(tbuf,0,tbuf.length);
	}
	
	
	String getETDfromCRS(String inventoryLegKey)
	{
		String ret=null;
		try 
		{
			String operationsManagerClientSoapProxy = (String)getAttribute("soap.operationManager"); 
			String sessionManagerClientSoapProxy = (String) getAttribute("soap.sessionManager") ; 


			OperationsManagerClientSoapProxy operationsmanagerclientsoapProxy = new OperationsManagerClientSoapProxy(operationsManagerClientSoapProxy);
			SessionManagerClientSoapProxy sessionmanagerclientsoapProxy = new SessionManagerClientSoapProxy(sessionManagerClientSoapProxy);

		    //_ejProxy = new EJProxyClient();
			WrapOfLogonResponse wraplogonResponse = logOnWebService(sessionmanagerclientsoapProxy);
			if(wraplogonResponse.getException() != null) return  null;

		//    InventoryLegOp inventoryLegOp = o _ejProxy.getInventoryLegOp(inventoryLegKey);
			LogonResponse logonResponse =  wraplogonResponse.getResult();
			SessionContext session = logonResponse.getSessionContext();
		
			/*
			InventoryLegKey inventoryLegKey = new InventoryLegKey();
			
			inventoryLegKey.setCarrierCode("ZE");
			inventoryLegKey.setDepartureStation(atbfromApo);
			inventoryLegKey.setArrivalStation(atbtoApo);
			
			Calendar daptartue = new GregorianCalendar();      

			daptartue.setTime(sdf.parse(atbfromDate));
			daptartue.add(Calendar.HOUR, 9);

			inventoryLegKey.setDepartureDate(daptartue);

			inventoryLegKey.setFlightNumber(atbflightNo.trim());
			inventoryLegKey.setOpSuffix(new UnsignedShort("32"));
			*/
			WrapOfInventoryLegOp winventoryLegOp=operationsmanagerclientsoapProxy.getInventoryLegOp(session,inventoryLegKey);
			InventoryLegOp inventoryLegOp =  winventoryLegOp.getResult();
			
				
			if(inventoryLegOp != null)
		    {
		    	if (log.isDebugEnabled()) 
		    		log.debug("inventoryLegOp.getInventoryLegOpTimes().length : "  + inventoryLegOp.getInventoryLegOpTimes().length);
		    	
		    	for(int i = 0; i < inventoryLegOp.getInventoryLegOpTimes().length; i++)
		    	{
		    		OpTimeType optimeType = inventoryLegOp.getInventoryLegOpTimes()[i].getOpTimeType();
		    		
		    		
		    		if(optimeType.getValue() == OpTimeType.EstimatedDepartureTime.toString())
		    		{
		    			if (log.isDebugEnabled()) 
		    				log.debug("get a optimeType : "  + inventoryLegKey);
		  		    			
		    			Calendar cal = new GregorianCalendar();      
		    			//SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		    			
		    			cal = inventoryLegOp.getInventoryLegOpTimes()[i].getTime();
		    			cal.add(Calendar.HOUR, 15);
		    			String atbETDTimeData = timeFormat.format(cal.getTime());
		    			ret = atbETDTimeData;
		    			
		    			if (log.isDebugEnabled()) 
		    				log.debug("ETDTime : "  + ret);
		    		}
		    		else
		    		{
		    			if (log.isDebugEnabled()) 
		    				log.debug("ETD is not exist : "  + optimeType.getValue() + " " + i);
		    		}
		    	}
		    }
		    else
		    	log.error("inventoryLegKey  is null: "  + inventoryLegKey);
		}
		catch (Exception ex) 
		{
			log.error("Failed Web Service : "  + inventoryLegKey, ex);
			//ex.printStackTrace();
		}
		return ret;
	}

	public String getETDfromMiddleWare(String _fltNo, String _dStation, String _aStation, String _dDate)
	{
		String sign = "";
		String ETD = "";
		String fltNo = _fltNo;
		String dStation = _dStation;
		String aStation = _aStation;
		String dDate = _dDate;
		JSONObject paramjson = new JSONObject();
		
		//setFlightNumber 4자리편명으로 설정
		if(_fltNo.length() != 4){
			fltNo = "0" + _fltNo;
		}
		    
		paramjson.put("CarrierCode", "ZE");
		paramjson.put("FlightNumber", fltNo);
		paramjson.put("DepartureStation", dStation);
		paramjson.put("ArrivalStation", aStation);
		paramjson.put("StationDateTime", dDate);
		
		try {
			sign = logonFromMiddleware();
			ETD = getETDString(sign,paramjson);
			if(ETD.startsWith("9999")){
				ETD = "";
			}else{
				ETD = ETD.split("T")[1];
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ETD;
	}
	
	public String getETDString(String _sign, JSONObject _json) throws Exception{
		
		String sign = _sign;
		JSONObject requestparam = new JSONObject();
		requestparam = _json;
		JSONObject requestData = new JSONObject();
	    JSONObject requestSubData = new JSONObject();
	    JSONObject responsejson = new JSONObject();
	    requestData.put("ContractVersion", 0);
	    requestData.put("Signature", sign);
	    requestSubData.put("CarrierCode", requestparam.get("CarrierCode"));
	    requestSubData.put("FlightNumber", requestparam.get("FlightNumber"));
	    requestSubData.put("DepartureStation", requestparam.get("DepartureStation"));
	    requestSubData.put("ArrivalStation", requestparam.get("ArrivalStation"));
	    requestSubData.put("StationDateTime", requestparam.get("StationDateTime"));
	    requestData.put("flightSummarizedRequest", requestSubData);

	    String query = (String)getAttribute("soap.operationManager");

	    responsejson = sendPost(query, requestData);
	    
	    responsejson = (JSONObject)responsejson.get("FlightSummary");
	    
	    return responsejson.getString("EstimatedDepartureTime");
	}

	String calcBoardingTime(String fromApo,String atbfromTime)
	{
		Calendar cal = new GregorianCalendar();      
		int brdtimeDiffer = 30;
		String ret=atbfromTime; 
		try {
			Date d = timeFormat.parse(atbfromTime, new ParsePosition(0));
			if("NRT".equals(fromApo))
				brdtimeDiffer = 25; 
			
			cal.setTime(d);
			cal.add(Calendar.MINUTE, -brdtimeDiffer);
			String atbbrdTimeData = timeFormat.format(cal.getTime());
			ret = atbbrdTimeData;
		} catch(Exception e) {
		}
		
		return ret;
	}
}//class