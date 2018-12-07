package com.eastarjet.crs.proxy.skyport;

import java.io.IOException;
import java.nio.Buffer;

import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayHandler;
import com.eastarjet.net.service.relay.RelayRequest;
import com.eastarjet.net.service.relay.RelayResponse;
import com.eastarjet.net.service.relay.RelaySession;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class PrintingHandler implements RelayHandler  
{
	final static int STATUS_TRANSFER		=	0;
	final static int STATUS_PRINTINGSTART	=	1;
	int status=STATUS_TRANSFER;
	
	boolean isActive=false;
	static Logger log = Toolkit.getLogger(PrintingHandler.class);
	
	final static byte [] header = {0x1b,0x5b,0x35,0x69 };
	final static byte [] tail 	= {0x1b,0x5b,0x34,0x69 };

	public PrintingHandler(){}

	
	public int  getValidCount(RelaySession session,RelayBuffer buf)
	{
		
		int ret=-1;
		
		if(buf==null) return -1;
		
		buf.mark();
		ret = buf.compareWith(header);
		buf.rewind();
		if(ret==0) session.setHandler(this);
		
		return ret;
	}
	 
	
	public void setActive(boolean b){isActive=b;}
	public boolean isActive(){return isActive;} 
	
	public boolean hasEnoughData(RelaySession session,RelayBuffer buf) throws IOException
	{
		boolean ret=false;
		buf.mark();
		
		if(buf.startsWith(header))
		{ 
			if(buf.endsWith(tail)) ret=true;
			
//			int spos = buf.find(tail);
//			if(spos >= 0 )
//			{
//				ret=true;
//			
//			}
		}
		buf.rewind();
		
		if(ret && log.isDebugEnabled())	log.debug("enough data: "+ buf.toString());
		return ret;	
	}

	public void handleRequest(RelayRequest request, RelayResponse response) throws IOException
	{
		RelayBuffer buf = request.getBuffer();
		String head=buf.readLine();
		if(log.isDebugEnabled()) log.debug("Head:"+head);
		String line1=buf.readLine();
		if(log.isDebugEnabled()) log.debug("Line1:"+line1);
	/*	while(head!=null && !head.contains("[5i"))
		{
			if(log.isDebugEnabled()) log.debug("Head:"+head);
			head=buf.readLine();
		}//while
		*/
		RelayResponse sres= request.getSession().getSourceReponse();
		if(line1.contains("ATB")) sendATBPrinting(sres, buf);
		
		else if (line1.contains("BTP")) sendBTPPrinting(sres, buf);
		else { sendPrinting(sres,buf); }
		//setActive(false);
	}
	
 
	protected void  sendPrinting(RelayResponse response, RelayBuffer buf) throws IOException 
	{
		buf.setPosition(0);
		response.writeAll(buf);
	}
	
	protected void  sendBTPPrintingOld(RelayResponse response, RelayBuffer buf) throws IOException 
	{
		buf.setPosition(0);
		response.writeAll(buf);
	}
	
	protected void  sendBTPPrinting(RelayResponse response, RelayBuffer buf) throws IOException
	{
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
		String departureCode = "ICN";//airportCheckInPrompt.getMarket().substring(0, 3);
		
		String contentsString = "";
		String line = null,v=null;

		while (null != (line = buf.readLine())) 
		{
			v=null;
			if(line.contains("(BTP IATA INDENT)"))
			{	v= buf.readLine(); iataIndent = v.substring(1);  }
			else if(line.contains("(BTP BAG TAG NUMBER)"))
			{	v= buf.readLine(); 	bagtagNumber = v; }
			else if(line.contains("(BTP AIRLINE CODE)"))
			{	v= buf.readLine(); 	airlineCode = v; }
			else if(line.contains("(BTP PAX NAME)"))
			{	v= buf.readLine(); 	paxName = v; }
			else if(line.contains("(BTP SEQUENCE NUMBER)"))
			{	v= buf.readLine(); 	sequenceNumber = v; }
			else if(line.contains("(BTP BAG TAG SPUR NUMBER)"))
			{	v= buf.readLine(); 	bagtagspurNumber = v; }
			else if(line.contains("(BTP REC LOC)"))
			{	v= buf.readLine(); 	recLoc = v; }
			else if(line.contains("(BTP DESTINATION CODE)"))
			{	v= buf.readLine(); 	destinationCode = v; }		
			else if(line.contains("(BTP FLIGHT NUMBER)"))
			{	v= buf.readLine(); 	flightNumber = v; }
			else if(line.contains("(BTP DATE)"))
			{	v= buf.readLine(); 	date = v; }
			else if(line.contains("(BTP FLIGHT DATE)"))
			{	v= buf.readLine(); 	flightDate = v; }
			else if(line.contains("(BTP DEPARTURE TIME)"))
			{	v= buf.readLine(); 	departureTime = v; }
			
			
			
			
			if(log.isDebugEnabled())
				log.debug("line : "  + line+"="+v);
			
			if(line.equals("[4i")) break;
			
		}
		if(flightNumber==null) return;
		
		flightNumber ="0000" + flightNumber.trim();
		flightNumber = flightNumber.substring(flightNumber.length()-4);
		
		if(log.isDebugEnabled())
		{
			log.debug("iataIndent : "  + iataIndent);
			log.debug("bagtagNumber : "  + bagtagNumber);
			log.debug("airlineCode : "  + airlineCode);
			log.debug("paxName : "  + paxName);
			log.debug("sequenceNumber : "  + sequenceNumber);
			log.debug("bagtagspurNumber : "  + bagtagspurNumber);
			log.debug("recLoc : "  + recLoc);
			log.debug("destinationCode : "  + destinationCode);
			log.debug("flightNumber : "  + flightNumber);
			log.debug("date : "  + date);
			log.debug("flightDate : "  + flightDate);
			log.debug("departureTime : "  + departureTime);
			log.debug("departureCode : "  + departureCode);
		}

		
//		String baggageTag = "[5i" + "BTP2\n" + "BTP189901" + "_" + "01" +
//			iataIndent + bagtagNumber + "_" +
//		"02" + paxName + "_" + "03" + recLoc + "_" + "0D" + " / KG" + "_" +
//		"10" + departureCode + "_" + "11" + date  /*+ flightDate*/ + "_" +
//		"42" + destinationCode + "_" + "43" + " " + "_" + "44" + airlineCode + flightNumber + "_" +
//		"45" + date /*+ flightDate*/ + "_" + "52" + " " + "_" + "53" + " " + "_" + "54" + " " + "_" + 
//		"55" + " " + "_" + "62" + " " + "_" + "63" + " "  + "_" +  "64" + " " + "_" + "65" + " " + "_" + "[4i";
		
		
		flightDate = formatFlightDate(flightDate);
		String baggageTag = "[5i" + "BTP2\n" + "BTP189901" + "_" + "01" + airlineCode + " " + bagtagNumber + "_" +
        "02" + iataIndent + bagtagNumber + "_" + "03" + paxName + "_" + "04" + recLoc + "_" + "05" + departureCode + "_" +
        "06" + flightDate + "_" + "0A" + " / KG" + "_" + "22" + destinationCode + "_" + "23" + " " + "_"  + "24" + airlineCode + flightNumber + "_" +
        "25" + flightDate + "_" + "32" + " " + "_" + "33" + " " + "_" + "34" + " " + "_" + 
        "35" + " " + "_" + "42" + " " + "_" + "43" + " "  + "_" +  "44" + " " + "_" + "45" + " " + "_" + "\n[4i" ;


		
		if(log.isDebugEnabled())log.debug("BTP stream : "  + baggageTag);
		
		byte[] tbuf=baggageTag.getBytes();
		response.writeAll(tbuf,0,tbuf.length);

	}//method
	
	
	protected void sendATBPrinting(RelayResponse response,RelayBuffer buf) throws IOException
	{
		if (log.isDebugEnabled())
		{
			//log.debug("{}.service()", this);
		}
		
		/*Session session = handlerRequest.getSession();
		/////
		String agentName = (String) session.getAttribute("agentName");
		if (log.isDebugEnabled()) 
		{
			log.debug("agentName {}", agentName);
		}
		AirportCheckInPrompt airportCheckInPrompt = (AirportCheckInPrompt) session.getAttribute("airportCheckInPrompt");
		if (log.isDebugEnabled()) 
		{
			log.debug("airportCheckInPrompt {}", airportCheckInPrompt.toString());
		}*/
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
		
		String atbname = null;
		String atbfullfromApo = null;
		String atbfulltoApo = null;
		String atbcityPair = null;
		
		String atbBarcode2d = null;
		String atbpaxType = null;
		
		String btpiataIndent = null;
		String btpbagtagNumber = null;
		String btpairlineCode = null;
		String btppaxName = null;
		String btpsequenceNumber = null;
		String btpbagtagspurNumber = null;
		String btprecLoc = null;
		String btpdestinationCode = null;
		String btpflightNumber = null;
		String btpdate = null;
		String btpyear = null;
		String btpflightDate = null;
		String btpdepartureTime = null;
		String btpdepartureCode = "";//airportCheckInPrompt.getMarket().substring(0, 3);
		
		
		String contentsString = "";
	
		String line = null,v=null;
	
		while (null != (line = buf.readLine())) 
		{
			
			
			v=null;
			if(line.contains("(ATB PASSENGER NAME)"))
			{	v = buf.readLine(); atbfullname = v; }
			else if(line.contains("(ATB INFANT IND)"))
			{
				v = buf.readLine(); atbinfantIND = v;
			}
			else if(line.contains("(ATB INFANT NAME)"))
			{
				v = buf.readLine();  atbinfantName = v;
			}
			else if(line.contains("(ATB ARRIVAL LOCATION)"))
			{	v  = buf.readLine();  atbfulltoApo =v;}
			else if(line.contains("(ATB FLIGHT DATE)"))
			{	v = buf.readLine();
					atbfromDate =v;
				log.debug("fromDate : "  + atbfromDate);
			}
			else if(line.contains("(ATB AIRLINE CODE)"))
			{	v =	 buf.readLine(); atbflightcode = v; }
			else if(line.contains("(ATB FLIGHT NUMBER)"))
			{	v =	 buf.readLine(); 	atbflightNo = v; }
			else if(line.contains("(ATB SEAT NUMBER)"))
			{	v =	 buf.readLine(); 	atbseatNo = v; }
			else if(line.contains("(ATB DEPARTURE TIME COLON)"))
			{	v =	 buf.readLine(); 	atbfromTime = v; }
			else if(line.contains("(ATB SEQUENCE NUMBER)"))
			{	v =	 buf.readLine(); 	atbsequenceNo = v; }		
			else if(line.contains("(ATB GATE NUMBER)"))
			{	v =	 buf.readLine(); 	atbgateNo = v; }
			else if(line.contains("(ATB BOARDING TIME)"))
			{	v =	 buf.readLine(); 	atbbrdTime = v; 
			}
			else if(line.contains("(ATB CITY PAIR)"))
			{	v =	 buf.readLine(); 
				atbcityPair = v;
				atbfromApo = atbcityPair.substring(0, 3);
				atbtoApo = atbcityPair.substring(3, 6);
			}
			else if(line.contains("(ATB REC LOC)"))
			{	v =	 buf.readLine(); 	atbpnr = v; }
			else if(line.contains("(ATB DEPARTURE LOCATION)"))
			{	v =	 buf.readLine(); 	atbfullfromApo = v; }
			else if(line.contains("(ATB ARRIVAL TIME COLON)"))
			{	v =	 buf.readLine(); 	atbtoTime = v; }
			else if(line.contains("(ATB COMMA DELIMITED SSR LIST)"))
			{	v =	 buf.readLine(); 	atbssr = v; }
			else if(line.contains("(ATB BOARDING MESSAGE)"))
			{	v =	 buf.readLine(); 	atbmsg = v; }
			else if(line.contains("(ATB BAG PIECES)"))
			{	v =	 buf.readLine(); 	atbbagPieces = v; }
			else if(line.contains("(ATB BAG WEIGHT)"))
			{	v =	 buf.readLine(); 	atbbagWeight = v; }
			else if(line.contains("(BTP IATA INDENT)"))
			{	v =	 buf.readLine(); 	btpiataIndent = v; }
			else if(line.contains("(BTP BAG TAG NUMBER)"))
			{	v =	 buf.readLine(); 	btpbagtagNumber = v; }
			else if(line.contains("(BTP AIRLINE CODE)"))
			{	v =	 buf.readLine(); 	btpairlineCode = v; }
			else if(line.contains("(BTP PAX NAME)"))
			{	v =	 buf.readLine(); 	btppaxName = v; }
			else if(line.contains("(BTP SEQUENCE NUMBER)"))
			{	v =	 buf.readLine(); 	btpsequenceNumber = v; }
			else if(line.contains("(BTP BAG TAG SPUR NUMBER)"))
			{	v =	 buf.readLine(); 	btpbagtagspurNumber = v; }
			else if(line.contains("(BTP REC LOC)"))
			{	v =	 buf.readLine(); 	btprecLoc = v; }
			else if(line.contains("(BTP DESTINATION CODE)"))
			{	v =	 buf.readLine(); 	btpdestinationCode = v; }		
			else if(line.contains("(BTP FLIGHT NUMBER)"))
			{	v =	 buf.readLine(); 	btpflightNumber = v; }
			else if(line.contains("(BTP DATE)"))
			{	v =	 buf.readLine(); 	btpdate = v; }
			else if(line.contains("(BTP FLIGHT DATE)"))
			{	v =	 buf.readLine(); 	btpflightDate = v; }
			else if(line.contains("(BTP DEPARTURE TIME)"))
			{	v =	 buf.readLine(); 	btpdepartureTime = v; } 
			
			
			if(log.isDebugEnabled())
				log.debug("line : "  + line+"="+v);
			if(line.equals("[4i")) break;
		}
		

		
		
		if(atbbrdTime!=null && atbbrdTime.length()>3)
		{
			atbbrdTime=atbbrdTime.substring(0,2) +":"+atbbrdTime.substring(2,4);
		}

		if (atbfullname.contains("MSTR"))
		{
			atbpaxType = "4";//infant for KAC
			atbseatNo="INF";
		}
//		else if(atbinfantIND!=null && !atbinfantIND.isEmpty() && atbfullname!=null &&atbfullname.contains(atbinfantName))
//		{
//			atbpaxType = "4";//infant for KAC
//			atbseatNo="INF";
//		}
		else 
		{
			atbpaxType = "1"; //adult for KAC
			////atbpaxType = this.paxTypeAdultHasInfant;
		}
		

		
		//Format PaxName
//		String[] atbfullnames = atbfullname.split("/");
//
//		if(atbfullnames!=null && atbfullnames.length >= 3 )
//			atbfullname = atbfullnames[0] + "/" + atbfullnames[1] + " " + atbfullnames[2];

		
		if(atbfullname.length() > 29)
			atbfullname = atbfullname.substring(0, 29);
		

		
		if(atbfullname.length() > 19)
			atbname=atbfullname.substring(0,19);
		else 
			atbname=atbfullname;
			

	
		
		
		if(atbfullfromApo.length() > 18)
			atbfullfromApo=atbfullfromApo.substring(0,18);

		if(atbfulltoApo.length() > 16)
			atbfulltoApo=atbfulltoApo.substring(0,16);
		

		
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
			
			log.debug("btpiataIndent : "  + btpiataIndent);
			log.debug("btpbagtagNumber : "  + btpbagtagNumber);
			log.debug("btpairlineCode : "  + btpairlineCode);
			log.debug("btppaxName : "  + btppaxName);
			log.debug("btpsequenceNumber : "  + btpsequenceNumber);
			log.debug("btpbagtagspurNumber : "  + btpbagtagspurNumber);
			log.debug("btprecLoc : "  + btprecLoc);
			log.debug("btpdestinationCode : "  + btpdestinationCode);
			log.debug("btpflightNumber : "  + btpflightNumber);
			log.debug("btpyear : "  + btpyear);
			log.debug("btpflightDate : "  + btpflightDate);
			log.debug("btpdepartureTime : "  + btpdepartureTime);
			log.debug("btpdepartureCode : "  + btpdepartureCode);
		}//ilog
		
		
		//Gate Number ¿ìÃøÁ¤·Ä 3ÀÚ¸® 
		atbgateNo = ("   "+atbgateNo);
		atbgateNo=atbgateNo.substring(atbgateNo.length()-3);
		
		atbflightNo = atbflightNo.trim();
		atbflightNo = String.format("%04d", Integer.parseInt(atbflightNo));
		String barcodeFlightNo = String.format("%05d", Integer.parseInt(atbflightNo));
		atbsequenceNo = String.format("%03d", Integer.parseInt(atbsequenceNo));
		String barcodeSequenceNo = String.format("%04d", Integer.parseInt(atbsequenceNo));
		
		String year = "20" + atbfromDate.substring(5, 7);
		String month = String.format("%02d", getMonth(atbfromDate.substring(2, 5)));
		String day = atbfromDate.substring(0, 2);

		atbBarcode2d = "I" + atbflightcode +  barcodeFlightNo + 
						barcodeSequenceNo + year + month + day +
						"C" + atbpaxType + atbfromApo;
		
		String boardingPass = "[5i" + "ATB2\n" + "CP" + "|" + "1C01" + "|" + "01E" + "|" + "05" + atbname + "|" + "06" + atbfullname + "|" + "12" + atbfromApo 
								+ "|" + "13" + atbfullfromApo + "|" + "1C" + atbtoApo + "|" + "1F" + atbflightcode + "|" + "21" + atbflightNo 
								+ "|" + "25" + atbfromDate + "|" + "2B" + atbfulltoApo + "|" + "35" + atbfromTime + "|" + "3A" + atbgateNo + "|" + "3B" + atbbrdTime
								+ "|" + "3C" + atbseatNo + "|" + "3D" + atbsequenceNo + "/" + atbpnr + "|" + "EEM1" + atbBarcode2d + "|" + "[4i" + "\n";
		
		
	
		if(log.isDebugEnabled())log.debug("Barcode2d : "  + atbBarcode2d);
		if(log.isDebugEnabled())log.debug("boardingPass : "  + boardingPass);
		
		String bpbt = boardingPass;

		
		byte [] tbuf=bpbt.getBytes();
		response.writeAll(tbuf,0,tbuf.length);
	}
	
	
	
	//2522 10
	public String formatFlightDate(String fdate)
	{
		
		String ret="",y="10";
		fdate=fdate.trim();
		int ch = fdate.charAt(fdate.length()-1)- '1';
		String m="";
		if(ch>0 && ch<12)
		{	
			String d=fdate.substring(0,2);
			m=months[ch];
			ret=d+m+y;
			
		}
		else ret=fdate+y;
		
		return ret;
	}
	
	static  String[] months =       new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	
	public int getMonth(String month) {
		 
        for (int monthcount = 0; monthcount < 12; monthcount++)
        {
            if (months[monthcount].equals(month))
                return monthcount + 1;
        }
        return 0;
	}

	
}//class
