package com.eastarjet.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.eastarjet.crs.proxy.skyport.handler.printing.PrintingBTPHandler;
import com.eastarjet.crs.proxy.skyport.handler.tools.CheckinTool;

import junit.framework.TestCase;

public class CalcWorkHour extends TestCase {

	public void testDepartureDate()
	{
		String date=null;
		PrintingBTPHandler handler=new PrintingBTPHandler();
		for(int i=1;i<=12;i++)
		{
			date=CheckinTool.translateFlightDateForPrintingDevice("12"+i);		//7.12
			System.out.println("date="+date+"<= "+"12"+i);
		}

		date=CheckinTool.translateFlightDateForPrintingDevice("12JUN");		//7.12
		System.out.println("date="+date);
	}
 
	public void _testWorkHour()  
	{
		Hashtable users = new Hashtable();
		Vector<WorkUnit> workUnits = new Vector<WorkUnit>();
		try{
			String curDir = System.getProperty("user.dir");
			System.out.println(curDir);
		FileInputStream fin = new FileInputStream("./data/workhours.csv");

		String line="";
			while(line!=null)
			{
				line=readLine(fin);
				if(line==null) break;
				System.out.println(line);
				
				WorkUnit unit=parseWorkUnit(line);
				workUnits.add(unit);
			}
		fin.close();
		System.out.println("parse done.");
		
		}catch(Exception e)
		{ e.printStackTrace();}
		
		System.out.println("Start analysis");
		DateFormat format= new SimpleDateFormat("yyyyMMdd");
		Date tstartDate = null,tendDate=null;
	 
		
		
		try{tstartDate= format.parse("20100301"); }catch(Exception e1)
		{ e1.printStackTrace(); return;}
		
		try{tendDate= format.parse("20101129"); }catch(Exception e2)
		{ e2.printStackTrace(); return;}

		Calendar startDate =  Calendar.getInstance();
		startDate.setTime(tstartDate);
		
		Calendar endDate =  Calendar.getInstance();
		endDate.setTime(tendDate);
		
		int month =  startDate.get(Calendar.MONTH) ;

		try
		{
			FileOutputStream fout = new FileOutputStream("./data/workhours_calc.csv");
			while(startDate.compareTo(endDate)!=0)
			{
				Hashtable<String,DayUnit> days=new Hashtable<String,DayUnit>();
				int day=startDate.get(Calendar.DAY_OF_MONTH);
				//System.out.println("day:"+day);
				for(int i=0;i<workUnits.size();i++)
				{
					WorkUnit unit=workUnits.get(i);
					if(startDate.compareTo(unit.day)!=0) continue;
					
					DayUnit dayUnit= days.get(unit.name);
					if(dayUnit==null)
					{
						dayUnit=new DayUnit(); days.put(unit.name, dayUnit);
						dayUnit.dept=unit.dept;
						dayUnit.name=unit.name;
						dayUnit.regNo=unit.regNo;
						dayUnit.begin=unit.date;
						dayUnit.end=unit.date;
						continue;
					}
					
					if(dayUnit.end.compareTo(unit.date)<0) 
					{
					//	String tm=dayUnit.end.toString();
					//	String tm2=unit.date.toString();
						dayUnit.end=unit.date;
						
					}
				}//for
				startDate.add(Calendar.DAY_OF_YEAR,1);
				
				
				Iterator<DayUnit> it= days.values().iterator();
				while(it.hasNext())
				{
					DayUnit u= it.next();
					u.calc();
					fout.write((u.toString()+"\r\n").getBytes());
					System.out.println(u.toString());
				}
				
				if(startDate.get(Calendar.MONTH)!=month)
				{
	
					month=startDate.get(Calendar.MONTH);
				//	System.out.println("next Month:"+month);
				}//if
			}//while
			fout.close();
		}catch(Exception e){}//catch
	}//method
	
	class WorkUnit { Calendar day; Calendar date; String type; String name; String regNo; String dept;String jobtitle;}
	class DayUnit 
	{
		String name; String regNo; String dept;
		Calendar begin; Calendar end; boolean isHolyday=false; int hour=0; int extra=0;
		void calc()
		{ 
			if( begin.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || begin.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			{
				isHolyday= true;
			}
			
			if(begin.compareTo(end)==0)
			{ 
				end= Calendar.getInstance();
				end.setTime(begin.getTime());
				if(regNo.equals("200302"))
					end.set(Calendar.HOUR_OF_DAY, 20);
				else end.set(Calendar.HOUR_OF_DAY, 18);
			}
			
			int month= begin.get(Calendar.MONTH);
			//long tm= end.getTimeInMillis() - begin.getTimeInMillis() ;
			int bg=begin.get(Calendar.HOUR_OF_DAY)*60 + begin.get(Calendar.MINUTE);
			int ed=end.get(Calendar.HOUR_OF_DAY)*60+ end.get(Calendar.MINUTE);
			extra=0;
			int btl=9*60 - 30;
			int etl=18*60+30;
			if(month>=11)
			{
				btl=8*60+30 - 30;
				etl=17*60+30+30;
			}
			
			if(bg < btl)extra+=btl-bg;
			if(ed > etl)extra+=ed-etl;
			hour=8;
		} //calc
		
		public String toString()
		{
			//=IF(OR(B2393="CRSÆÀ"),F2393,0) + IF(AND(B2393="CRSÆÀ",G2393),E2393,0)
			String tm=format.format(begin.getTime());
			String bm2=dformat.format(begin.getTime());
			String em2=dformat.format(end.getTime());
			return ""+tm+","+dept+","+name+","+regNo+","+hour+","+extra+","+isHolyday+","+bm2+","+em2;
		}
	}
	class MonthUnit {int holyday; int hour; int extra; }
	
	DateFormat format= new SimpleDateFormat("yyyy/MM/dd");
	DateFormat dformat= new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	WorkUnit parseWorkUnit(String line)
	{
		WorkUnit unit=new WorkUnit();

		int spos=0;
		int epos=line.indexOf(',');
		
		String sdate = line.substring(spos,epos); spos=epos+1;
		try{
			Calendar day =  Calendar.getInstance();
			Calendar date =  Calendar.getInstance();
			unit.day= day; day.setTime(format.parse(sdate.substring(0,10)));
			unit.date= date; date.setTime(dformat.parse(sdate));
		 //	System.out.println("date="+unit.date);
		}catch(Exception e){}
		
		
		epos=line.indexOf(',',spos);
		unit.type= line.substring(spos,epos); spos=epos+1;
		
		epos=line.indexOf(',',spos);
		unit.name= line.substring(spos,epos); spos=epos+1;

		epos=line.indexOf(',',spos);
		unit.regNo= line.substring(spos,epos); spos=epos+1;

		epos=line.indexOf(',',spos);
		unit.dept= line.substring(spos,epos); spos=epos+1;

		epos=line.indexOf(',',spos);
		unit.jobtitle= line.substring(spos,epos); spos=epos+1;
		
		return unit;
	}
	
	public String readLine(InputStream in) throws Exception
	{
		String ret=null;
		byte [] buf=new byte[4096];
		int ch=0;
		int count=0;
		while(ch>=0)
		{
			ch=in.read();
			if(ch<0) break;
			if(ch=='\r') continue;
			if(ch=='\n') break;
			buf[count++]= (byte)ch;
		}
		if(count>0) ret=new String(buf,0,count); 
		return ret;
	}
}
