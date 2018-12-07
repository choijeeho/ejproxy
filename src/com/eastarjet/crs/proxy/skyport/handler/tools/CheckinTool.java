package com.eastarjet.crs.proxy.skyport.handler.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.eastarjet.dcs.iapp.webservice.client.Passport;
import com.eastarjet.util.StringToolkit;

public class CheckinTool 
{
	static 			SimpleDateFormat dateFormat1= new SimpleDateFormat("ddMMM",Locale.ENGLISH);
	static 			SimpleDateFormat dateFormat2= new SimpleDateFormat("yyyyMMdd");
	static 			SimpleDateFormat dateFormat3= new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);

	/**
	 *  ddMMM -> yyyyMMdd 
	 *  ex) 25JAN -> 20100125
	 *  
	 * @param sfdate
	 * @return
	 * @throws Exception
	 */
	static public String translateFlightDate(String sfdate) throws Exception
	{
		
		Date fdate = dateFormat1.parse(sfdate);
		Calendar ddate= Calendar.getInstance();
		int thisMonth=ddate.get(Calendar.MONTH);
		int thisYear=ddate.get(Calendar.YEAR);
		ddate.setTime(fdate);
	
	
		int departureMonth=ddate.get(Calendar.MONTH);
		int diff=departureMonth - thisMonth;
		if(  diff <= -6 )
		{			ddate.set(Calendar.YEAR, thisYear+1);	}
		else if(diff >= 6 )
		{ 	ddate.set(Calendar.YEAR, thisYear-1); }
		else
		{ 	ddate.set(Calendar.YEAR, thisYear); }
		
		return dateFormat2.format(ddate.getTime());
	}
	
	
	
	
	/**
	 * ddMM -> ddMMMyy  
	 *  ex) 151 -> 15JAN10 (year=2010)
	 *      1512 -> 15DEC10 (year=2010) 
	 * @param flghtDate
	 * @return
	 */
	static public String translateFlightDateForPrintingDevice(String flghtDate)
	{
 
		
		
		Calendar cal= Calendar.getInstance();
		 int curYear=cal.get(Calendar.YEAR);
		 int depYear=curYear;
			String y= (""+depYear);
			if(y.length()==4) y=y.substring(2);
		 
			int sz=flghtDate.length();
			
			for(int i=0;i<sz;i++)
			{
				if(StringToolkit.isAlpha( flghtDate.charAt(i)))
				{ 
					String td=flghtDate+y;
					return td.toUpperCase();
				}
			}//for
			

		int curMon=cal.get(Calendar.MONTH);
		String ret="";
		flghtDate=flghtDate.trim();
		int mon = 0;
		String smon="";
		if(flghtDate.length()==3)
			smon=	 flghtDate.substring(flghtDate.length()-1);
		else if(flghtDate.length()==4)
			smon=	 flghtDate.substring(flghtDate.length()-2);
		
		try{mon=Integer.parseInt(smon)-1;}catch(Exception e){}
		
		int diff=curMon-mon;
		
		if( (diff>0&&diff <6) || (diff<0 && diff > -6) )	depYear=curYear;
		else if(diff <= -6)  depYear=curYear-1; //11.1 - 10.12 
		else if(diff >= 6)   depYear=curYear+1; //11.8 - 12.1
		
			
		String m="";
		if(mon>=0 && mon<12)
		{	
			String d=flghtDate.substring(0,2);
			m=months[mon];
			ret=d+m+y;
		}
		else ret=flghtDate+y;
		
		return ret;

	}
	
	public static int getMonth(String month) 
	{
		 
        for (int monthcount = 0; monthcount < 12; monthcount++)
        {
            if (months[monthcount].equals(month))
                return monthcount + 1;
        }
        return 0;
	}	
	
	static  String[] months =       new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	static public  Passport bindPassport(Passport passport, Map<String,String> map) throws Exception
	{
		String v=map.get("passportNo");
		if(v==null || v.equals("") || v.equals("NONE")) throw new Exception("no passport number");
		passport.setPassportNo(v);
		
		v=map.get("firstName");
		if(v==null || v.equals("")) throw new Exception("no first name");
		passport.setFirstName(v);

		passport.setMiddleName(map.get("middleName"));

		v=map.get("lastName");
		if(v==null || v.equals("")) throw new Exception("no last name");
		passport.setLastName(v);

		v=map.get("gender");
		if(v==null || v.equals("")) throw new Exception("no gender");
		passport.setGender(v);
		

		v=map.get("birthDay");
		if(v==null || v.equals("")) throw new Exception("no birthDay");
		String sbirthDay=v;
		Date tm=dateFormat3.parse(sbirthDay);
		sbirthDay=dateFormat2.format(tm);
		passport.setBirthDay(sbirthDay);
		

		v=map.get("nationality");
		if(v==null || v.equals("")) throw new Exception("no nationality");
		passport.setNationality(v);

		v=map.get("issueCountry");
		if(v==null || v.equals("")) throw new Exception("no issueCountry");
		passport.setIssueCountry(v);
		

		v=map.get("issueDate");
		if(v==null || v.equals("")) throw new Exception("no issueDate");
		String sissue= v;
			 tm=dateFormat3.parse(sissue);
			sissue=dateFormat2.format(tm);
		passport.setIssueDate(sissue);
		

		v=map.get("expireDate");
		if(v==null || v.equals("")) throw new Exception("no expireDate");
		String sexpire=v;
		tm=dateFormat3.parse(sexpire);
		sexpire=dateFormat2.format(tm);
		passport.setExpireDate(sexpire);

		v=map.get("regidenceCountry");
		if(v==null || v.equals("")) throw new Exception("no regidenceCountry");
		passport.setResidenceCountry(v);

		v=map.get("type");
		if(v==null || v.equals("")) throw new Exception("no document type");
		passport.setType(v);//P,M
		

		return passport;
	}

}
