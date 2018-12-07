package com.eastarjet.crs.proxy.skyport.handler.tools;

 
import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.crs.proxy.skyport.bean.OpPassenger;
import com.eastarjet.crs.proxy.skyport.handler.SessionKey;
import com.eastarjet.crs.proxy.skyport.manager.WatchListManager;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.ByteQueue;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class PaxListParser 
{
	static Logger log = Toolkit.getLogger(PaxListParser.class);
	static WatchListManager watchListManager =new WatchListManager();
	
	byte [] tempBuf=new byte[4096];
	public void readPaxList( Session session, Request request, Response response)
	{
		ByteQueue queue=(ByteQueue)session.getAttribute(SessionKey.PAX_LIST_BUFFER);
		if(queue!=null && queue.size()>0)
		{
			
			int sz=queue.size();
			queue.poll(tempBuf, 0, sz);
			String paxList=null;
			try{
				paxList=new String(tempBuf,0,sz,"utf-8");
			}catch(Exception e){}

			
			if(log.isTraceEnabled())log.trace("paxListBuffer=\r\n"+paxList);
			try{
				parsePaxList(session, paxList);
			}
			catch(Exception e)
			{
				log.error("error at parsePaxList in CheckinCommandHandler",e);
			}
			
			queue.clear();
		}
	}
	
	void parsePaxList( Session ses,String paxListStr) throws Exception
	{
		List<OpPassenger> paxList =(List<OpPassenger>) ses.getAttribute(SessionKey.PAX_LIST);
		if(paxList==null)
		{
			paxList=new LinkedList<OpPassenger>();
			ses.setAttribute(SessionKey.PAX_LIST, paxList);
		}
		ByteArrayInputStream  bin=new ByteArrayInputStream(paxListStr.getBytes());
		
		String line=null,extLine=null;
		
		do
		{
			extLine=null;
			line = Toolkit.readLine(bin);
			if(line==null) break;
			if(line!=null) line=line.replace("[2J[H","");
			if(line!=null) line=line.trim();
			if(line.length()==0) continue;
			if(line.indexOf(")")<0) continue;
			
			//log.debug("pax:["+line+"]");
			if(line.indexOf(")")>0 && line.length() <30 )
			{
				extLine=Toolkit.readLine(bin);
			//	log.debug("pax:["+extLine+"]");
			}
			
			OpPassenger pax=new OpPassenger();
			parsePassenger(pax,line,extLine);
			if(watchListManager.isWatchedPerson(pax)) 
			{
				if(log.isDebugEnabled()) log.debug("watched Person : "+ pax.getLastName()+"/"+pax.getFirstName());
				pax.setWatchedPerson(true);
			}
			paxList.add(pax);
			//if
		}while(line!=null);
	}//method
	
	void parsePassenger(OpPassenger pax,String line,String exLine)
	{
		String sno;
		String fname=null;
		String lname=null;
		String title=null;
		String tl=line;
		String bl=line;
		if(exLine!=null){  bl=exLine;	}
		
		int ix=0;
		int ex=tl.indexOf(")");
		sno=tl.substring(ix,ex);
		ix=ex+1;

		ex=tl.indexOf("/",ix);
		lname=tl.substring(ix,ex);
		ix=ex+1;
		
		ex=tl.indexOf("=",ix);
		if(ex>0)
		{
			
			fname=tl.substring(ix,ex);
			ix=ex+1;
			ex=40;
			if(tl.length()<ex) ex=tl.length()-1;
			title=tl.substring(ix,ex).trim();			
		}
		else 
		{
			ex=23;
			int ttl=tl.length();
			if(ex>ttl) ex=ttl;
			fname=tl.substring(ix,ex);
			ix=ex+1;
		}
		

		
		
		pax.setPaxNo(sno);
		//pax.setSeqNo(seqNo);
		pax.setFirstName(fname);
		pax.setLastName(lname);
		pax.setTitle(title);
		
	}
}//class
