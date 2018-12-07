package com.eastarjet.crs.proxy.skyport.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.crs.proxy.skyport.bean.OpPassenger;
import com.eastarjet.crs.proxy.skyport.bean.WatchPerson;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class WatchListManager 
{
	static Logger log = Toolkit.getLogger(WatchListManager.class);
	
	static List<WatchPerson> watchPersonList=null;
	/*
	static WatchPerson [] watchPersonList = 
	{
		new WatchPerson("AHN",null,"YOUNGJAE","P","P123456","20111111","M","KR" ),
		new WatchPerson("CHOI",null,"JINWOO","P","P123456","20111111","M","KR" ),
		new WatchPerson("HWANG",null,"GUEMHEE","P","P123456","20111111","M","KR" )
	};*/
	
	Connection con =null;
	public WatchListManager()
	{
		loadWatchList();
	}
	
	static void loadWatchList()
	{
		watchPersonList=new LinkedList<WatchPerson>();
	       if(log.isDebugEnabled()) log.debug("select * from WATCHLIST");
		try{
			   Class.forName ("oracle.jdbc.OracleDriver");

			   Connection conn = DriverManager.getConnection
			     ("jdbc:oracle:thin:@//10.222.16.137:1521/EJD", "EJWATCHLIST", "ejwatch084!");
			                        // @//machineName:port/SID,   userid,  password
			   try {
			     Statement stmt = conn.createStatement();
			     try {
			       ResultSet rset = stmt.executeQuery("select * from WATCHLIST");
			       if(log.isDebugEnabled()) log.debug("select * from WATCHLIST");
			       
			       try 
			       {
			         while (rset.next())
			         {
			        	 //ABD AL RAUF ABD AL BASIM,19650610
			        	 String sv1=rset.getString("FIRSTNAME");
			        	 String sv2=rset.getString("MIDDLENAME");
			        	 String sv3=rset.getString("LASTNAME");
			        	 String sv4=rset.getString("BIRTHDAY");
			        	 String sv5=rset.getString("DOCUMENTTYPE");
			        	 String sv6=rset.getString("DOCUMENTNUMBER");
			        	 String sv7=rset.getString("NATIONALITY");
			        	 
			        	// log.debug("sv1:"+sv1+","+sv2+","+sv3);
			        	 WatchPerson wp=new WatchPerson(sv1, sv2, sv3, sv5, sv6, sv4, null,sv7);
			        	 watchPersonList.add(wp);
			        	// System.out.println("sv1-"+sv1);
			           //System.out.println (rset.getString(1));   // Print col 1
			         }//while
			       } //while
			       finally {
			          try { rset.close(); } catch (Exception e) {
			        	  log.debug("",e);
			        	  
			          }
			       }
			     } 
			     finally {
			       try { stmt.close(); } catch (Exception e) 
			       {
			    	   log.debug("",e);
			       }
			     }
			   } 
			   finally {
			     try { conn.close(); } catch (Exception e) {
			    	  log.debug("",e);
			     }
			   }
			}catch(Exception e){
				  log.debug("",e);
			}
	}
	
	static long previousTime=0;
	public boolean isWatchedPerson(OpPassenger pax)
	{
		long currentTime=System.currentTimeMillis();
		if(currentTime-previousTime > 60000)
		{
			previousTime=currentTime;
			loadWatchList();
		}
		/*
		if(watchPersonList==null)
		{ 
			loadWatchList(); 
		}*/
		//loadWatchList();
		if(log.isInfoEnabled()); //log.debug("(Pax Seach)WatchList inspected:");
		for(int i=0;i<watchPersonList.size();i++)
		{
			if(watchPersonList.get(i).isEquals(pax)) return true;
		}
		return false;
	}
	
	public boolean isWatchedPerson(String fname,String lname)
	{
		long currentTime=System.currentTimeMillis();
		if(currentTime-previousTime > 3600000)//1hour
		{
			previousTime=currentTime;
			loadWatchList();
		}
		/*
		if(watchPersonList==null)
		{ 
			loadWatchList(); 
		}*/
		//loadWatchList();
		if(log.isInfoEnabled()); //log.debug("(Checkin)WatchList inspected:");
		for(int i=0;i<watchPersonList.size();i++)
		{
			if(watchPersonList.get(i).isEquals(fname,lname)) return true;
		}
		return false;
	}	
}
