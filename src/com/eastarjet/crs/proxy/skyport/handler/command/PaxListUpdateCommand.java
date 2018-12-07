package com.eastarjet.crs.proxy.skyport.handler.command;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.axis.types.UnsignedShort;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;
import com.eastarjet.crs.proxy.skyport.bean.Passenger;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
import com.navitaire.schemas.WrapOfLogonResponse;
import com.navitaire.schemas.WrapOfManifest;
import com.navitaire.schemas.WrapOfMsgListOfMealSSRReport;
import com.navitaire.schemas.ClientServices.Common.SessionManagerClient.SessionManagerClientSoapProxy;
import com.navitaire.schemas.ClientServices.OperationsManager.OperationsManagerClient.OperationsManagerClient;
import com.navitaire.schemas.ClientServices.OperationsManager.OperationsManagerClient.OperationsManagerClientSoapProxy;
import com.navitaire.schemas.Common.SessionContext;
import com.navitaire.schemas.Common.Enumerations.AvsCollectionsMode;
import com.navitaire.schemas.Common.Enumerations.ChannelType;
import com.navitaire.schemas.Common.Enumerations.FlightType;
import com.navitaire.schemas.Common.Enumerations.SSRCollectionsMode;
import com.navitaire.schemas.Common.Enumerations.SystemType;
 
import com.navitaire.schemas.Messages.Common.InventoryLegKey;
import com.navitaire.schemas.Messages.Manifest.FlightService;
import com.navitaire.schemas.Messages.Manifest.ManifestLeg;
import com.navitaire.schemas.Messages.Manifest.ManifestPassenger;
import com.navitaire.schemas.Messages.Manifest.ManifestPassengerDoc;
import com.navitaire.schemas.Messages.Manifest.ManifestRequest;
import com.navitaire.schemas.Messages.Manifest.ManifestSegment;
import com.navitaire.schemas.Messages.Operations.Reports.MealSSRReport;
import com.navitaire.schemas.Messages.Session.Request.LogonRequest;
import com.navitaire.schemas.Messages.Session.Response.LogonResponse;

/**
 * 
 * param : session[SessionKey.FlightInfo]
 * return : session[SessionKey.PaxList]
 * 
 * @author clouddrd
 *
 */
public class PaxListUpdateCommand implements Command 
{
	static Logger log= Toolkit.getLogger(PaxListUpdateCommand.class);
	static String operationsManagerClientSoapProxyURL = "http://internal-zeibep-lb-1526732820.ap-northeast-2.elb.amazonaws.com/OperationsManagerClient.asmx";
    static String sessionManagerClientSoapProxyURL = "http://internal-zeibep-lb-1526732820.ap-northeast-2.elb.amazonaws.com/SessionManagerClient.asmx";
	static OperationsManagerClientSoapProxy operationsmanagerclientsoapProxy = new OperationsManagerClientSoapProxy(operationsManagerClientSoapProxyURL) ;
	static SessionManagerClientSoapProxy sessionmanagerclientsoapProxy= new SessionManagerClientSoapProxy(sessionManagerClientSoapProxyURL);
	static SessionContext session=null;
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	
	
	//EJCRSClient client;
	public int  doCommand(Session ses,Request req, Response resp)  
	{
		
		/*FlightInfo flightInfo=(FlightInfo )ses.getAttribute(SessionKey.FLIGHTINFO);
		if(flightInfo==null) return 0;
		
		String departureStation = flightInfo.getDepartureStation();
		String departureDate = flightInfo.getDepartureDate();
		String arrivalStation = flightInfo.getArrivalStation();
		String flightNo=flightInfo.getFlightNo();
		String departureTime=flightInfo.getDepartureTime();
		try{
		departureDate = CheckinTool.translateFlightDate(departureDate);
		}catch(Exception e){log.debug("",e);}
		
		if(log.isDebugEnabled()) log.debug("update paxlist : deptSt="+departureStation+",deptDate="+departureDate+",flightNo="+flightNo);
		 List<Passenger>  passengers = new LinkedList<Passenger>();

		//client.getPassengerList();
		 if(session==null)
		 {
			 WrapOfLogonResponse wraplogonResponse = logOnWebService(sessionmanagerclientsoapProxy);
			 LogonResponse logonResponse =  wraplogonResponse.getResult();
			 session = logonResponse.getSessionContext();
		 }
		 
		 ManifestRequest manifestRequest = new ManifestRequest();
		 InventoryLegKey inventoryLegKey = new InventoryLegKey();
		 Calendar navitaireCal = new GregorianCalendar();      
			
		 inventoryLegKey.setCarrierCode("ZE");
		 inventoryLegKey.setDepartureStation(departureStation);
		 inventoryLegKey.setArrivalStation(arrivalStation);
		 inventoryLegKey.setFlightNumber(flightNo);
		   
		 navitaireCal.set(Calendar.YEAR, Integer.parseInt(departureDate.substring(0, 4)));
		 navitaireCal.set(Calendar.MONTH, Integer.parseInt(departureDate.substring(4, 6)) - 1);
		 navitaireCal.set(Calendar.DATE, Integer.parseInt(departureDate.substring(6, 8)));
		 
		 navitaireCal.add(Calendar.HOUR, 9); //시차 적용
		 
		 inventoryLegKey.setDepartureDate(navitaireCal);
		 inventoryLegKey.setOpSuffix(new UnsignedShort("32"));

		 manifestRequest.setInventoryLegKey(inventoryLegKey);
		 manifestRequest.setFlightType(FlightType.All);
		 manifestRequest.setAvsCollectionsMode(AvsCollectionsMode.All);
		 manifestRequest.setSSRCollectionsMode(SSRCollectionsMode.All);
		 
		 try
		 {
		//	 WrapOfManifest wrap =  operationsmanagerclientsoapProxy.getManifest(session, manifestRequest);
			 OperationsManagerClientSoapProxy xx = new OperationsManagerClientSoapProxy();
			 WrapOfManifest wrap =  xx.getManifest(session, manifestRequest);
			 
			 
			 String liftStatus                 = "";
			 short seatRow                     = 0;
			 UnsignedShort seatcolumnAscii     = null;
			 String name                       = "";
			 String seatColumn                 = "";
			 String paxType					   = "";
			 
			 
			 ManifestPassenger [] paxs= null;
			 if(wrap.getResult()!=null) paxs= wrap.getResult().getPassengerList();
			
			 for(int i=0;paxs!=null && i<paxs.length;i++)
			 {
				 ManifestPassenger manifestpassenger=paxs[i];
				 Passenger pax= new Passenger();
				 passengers.add(pax);
				 
				 
				 String fname=manifestpassenger.getName().getFirstName();
				 String mname=manifestpassenger.getName().getMiddleName();
				 String lname=manifestpassenger.getName().getLastName();
				 pax.setFirstName(fname);
				 pax.setMiddleName(mname);
				 pax.setLastName(lname);
				 
				 
				 
				 if(log.isTraceEnabled()) log.trace("name="+fname+","+mname+","+lname);
				 //name = manifestpassenger.getName().getLastName() + manifestpassenger.getName().getMiddleName() + manifestpassenger.getName().getFirstName();
				 ManifestPassengerDoc [] docs =manifestpassenger.getPassengerTravelDocs();
				 int ix=0;
				 if(docs!=null &&docs.length>1)
				 {
					 String docType=docs[ix].getDocTypeCode();
					 String docNo=docs[ix].getDocNumber();
					 String docGender= docs[ix].getGender().getValue();
					 String docNation=docs[ix].getNationality();
					 String docDob=sdf.format(docs[ix].getDOB());
					 pax.setDocType(docType);
					 pax.setDocNumber(docNo);
					 pax.setGender(docGender);
					 pax.setNationality(docNation);
					 
					 pax.setDayOFBirth(docDob);
					 if(log.isDebugEnabled()) log.debug("doc="+fname+","+mname+","+lname
							 +",type="+docType+",docNo="+docNo+",gender="+docGender+",nation="+docNation+",docDob="+docDob);
				 }
				 
				 for(FlightService flightservice: manifestpassenger.getServices())
				 {
					 for(ManifestSegment manifestsegment: flightservice.getSegments())
					 {
						 for(ManifestLeg manifestleg: manifestsegment.getLegs())
						 {
							 liftStatus        = manifestleg.getLiftStatus().toString();
							 seatRow           = manifestleg.getSeatRow();
							 seatcolumnAscii   = manifestleg.getSeatColumn();
							 Date DOB          = manifestpassenger.getDOB();
							 paxType = manifestpassenger.getPaxType();
							 
							 pax.setSeat(""+seatcolumnAscii+seatRow);
							 pax.setDayOFBirth(sdf.format(DOB));
							 
						//	 if(log.isDebugEnabled()) log.debug("liftStatus="+liftStatus+","+seatColumn+seatRow+","+paxType);
						 }//for(ManifestLeg manifestleg: 
					 }//for ManifestSegment
				 }//for FlightService
			 }//for ManifestPassenger
			 
			 ses.setAttribute(SessionKey.FLIGHT_PAX_LIST,passengers);
		 }catch(Exception e)
		 {
			 log.error("error at PaxListUpdateCommand",e);
		 }*/
		 
		return 0;
	}
	 public WrapOfLogonResponse logOnWebService(SessionManagerClientSoapProxy smcsp)
	 {	
		 WrapOfLogonResponse wraplogonResponse = null;
		 try
		 {
			 Properties pmconf = null;
			 String domain =  "DEF";//config.getProperty("crs.soap.login.domain");
			 String agent = "ejonepass";
			 String password = "godhsvotm12";
			 
			 if(domain  != null && agent != null && password != null)
			 {
				 LogonRequest logonRequest = new LogonRequest();
				 logonRequest.setDomainCode(domain);
				 logonRequest.setAgentName(agent);
				 logonRequest.setPassword(password);
				 logonRequest.setSystemType(SystemType.WebRez);
				 logonRequest.setChannelType(ChannelType.Web);

				 wraplogonResponse = smcsp.logon0(logonRequest); 
			 }
				
		 }
		 catch (RemoteException e)
		 {
			 log.error("",e);
			 //e.printStackTrace();
		 }
		 return wraplogonResponse;
	 }
}
