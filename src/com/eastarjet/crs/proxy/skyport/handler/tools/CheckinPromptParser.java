package com.eastarjet.crs.proxy.skyport.handler.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.eastarjet.crs.proxy.skyport.bean.FlightInfo;

public class CheckinPromptParser 
{
 
	/*
	 * 
	 * 10:55 03Nov/CJJREP 1820/2200:ZE  7671 ()]
	 */
	/**
	 * 	map.put("flightDate", flightDate);
		map.put("departureTime", departureTime);
		map.put("flightNo", flightNo);
		map.put("departureAirport", departureAirport);
		map.put("arrivalAirport", arrivalAirport);
		map.put("arrivalTime", arrivalTime);
	 */
	public static void parse(FlightInfo map, String prompt)
	{

	//	Map<String,String> m=new Hashtable<String,String>();
		String flightDate="";
		String departureTime="";
		String flightNo="";
		String departureAirport="";
		String arrivalAirport="";
		
		prompt=prompt.trim();
		int epos,spos=0;
		epos=prompt.indexOf(' ');
		String curTime= prompt.substring(spos,epos); 
		spos=epos+1;
		
		epos=prompt.indexOf('/', spos);
		String curDate = prompt.substring(spos,epos);
		spos=epos+1;

		epos=prompt.indexOf(' ', spos);
		String seg= prompt.substring(spos,epos);
		spos=epos+1;

		departureAirport= seg.substring(0, 3);
		arrivalAirport=seg.substring(3,6);
		
		epos=prompt.indexOf('/', spos);
		departureTime = prompt.substring(spos,epos);
		spos=epos+1;
		
		epos=prompt.indexOf(':', spos);
		String arrivalTime = prompt.substring(spos,epos);
		spos=epos+1;
		
		
		epos=prompt.indexOf(' ', spos);
		String carrierCode = prompt.substring(spos,epos);
		spos=epos+1;
		

		epos+=6;
		flightNo = prompt.substring(spos,epos).trim();
		spos=epos+1;
		
		
		flightDate=curDate;
	/*	try{
		flightDate=CheckinTool.translateFlightDate(flightDate);
		}catch(Exception e){}*/
		map.setFlightNo(flightNo);
		map.setDepartureDate(flightDate);
		map.setDepartureTime(departureTime);
		map.setDepartureStation(departureAirport);
		
		map.setArrivalTime(arrivalTime);
		map.setArrivalStation(arrivalAirport);
		//return m;
	}
	
	public static String translateFlightDate(String sdate)
	{
		return null;
	}
}//class
