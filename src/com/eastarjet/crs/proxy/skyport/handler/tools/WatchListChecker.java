package com.eastarjet.crs.proxy.skyport.handler.tools;

import java.util.LinkedList;
import java.util.List;
import com.eastarjet.crs.proxy.skyport.bean.OpPassenger;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.crs.proxy.skyport.manager.WatchListManager;
import com.eastarjet.net.service.terminal.view.Session;

/**
 * 
 * @author clouddrd
 *
 */
public class WatchListChecker 
{
	WatchListManager manager =new WatchListManager();
	
	public List<OpPassenger> check(Session ses, String checkinCmd)
	{
		//Map <String,OpPassenger> paxs = ses.getAttribute(SessionKey.PAX_LIST);

		int ix=checkinCmd.indexOf('/');
		String range=null;
		if(ix>=0)
		{
			range = checkinCmd.substring(1,ix);
		}
		else 
			range = checkinCmd.substring(1);
		
		
		ix=range.indexOf('-');
		String sst=null,sed=null;
		int st=0,ed=-1;
		if(ix>0)
		{
			sst=range.substring(0,ix);
			sed=range.substring(ix+1);
		}
		else
		{ sst=range; sed=range;}
		
		try{st=Integer.parseInt(sst); }catch(Exception e){}
		try{ed=Integer.parseInt(sed);}catch(Exception e){}
		
		List<OpPassenger> list=(List<OpPassenger>)ses.getAttribute(SessionKey.PAX_LIST);;
		List<OpPassenger> ret=null;
		
		
		for(int i=st;i<=ed&&i<=list.size() ;i++)
		{
			if(list==null) break;
			OpPassenger pax=list.get(i-1);
			if(pax==null) continue;
			
			String fname=pax.getFirstName();
			String lname=pax.getLastName().trim();
			
			if(manager.isWatchedPerson(fname,lname))
			{ 
				OpPassenger opp=new OpPassenger();
				
				opp.setFirstName(fname);
				opp.setLastName(lname);
				if(ret==null) ret=new LinkedList<OpPassenger>();  
				ret.add(pax); 
			}
			
			//if(pax.isWatchedPerson())
			//{  if(ret==null) ret=new LinkedList<OpPassenger>();  ret.add(pax); }
		}
		
		return ret;
	}
}
