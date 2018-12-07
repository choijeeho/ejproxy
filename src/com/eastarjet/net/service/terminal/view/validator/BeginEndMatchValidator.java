package com.eastarjet.net.service.terminal.view.validator;

import java.util.Iterator;
import java.util.List;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;



public class BeginEndMatchValidator extends MatchValidator 
{
	static Logger log = Toolkit.getLogger(BeginEndMatchValidator.class);
	byte[] beginPattern, endPattern;
	byte[][]endPatterns;

	public void setAttribute(String k,Object v)
	{
		if("beginPattern".equals(k))
		{
			beginPattern=makePattern((String)v);
			
			if(log.isDebugEnabled())
			{
				String tv=Toolkit.dumpHex(beginPattern, beginPattern.length);
				log.debug("beginPattern :\n"+tv);
			}
		}
		else if("endPattern".equals(k))
		{
			if(v instanceof String )
			{
				endPatterns=new byte[1][];
				endPatterns[0]=makePattern((String)v);
			}
			else
			{
				List<String> ps=(List<String>)v;
				Iterator<String> it=ps.iterator();
				endPatterns = new byte[ps.size()][];
				int i=0;
				while(it.hasNext())
				{
					byte [] tp=makePattern(it.next());
					endPatterns[i++]=tp;
				}
			}
			 
			if(log.isDebugEnabled())
			{
				String tv=Toolkit.dumpHex(endPatterns[0], endPatterns[0].length);
				if(log.isDebugEnabled())log.debug("endPattern :\n"+tv);
			}//if
			
		}//else if
		else super.setAttribute(k, v);
	}
	
	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		
		int state=session.getIntAttribute(this,"state");
		
		if(state==0)
		{
			int index=session.getIntAttribute(this,"bindex");
			byte ch=(byte)request.peek();
			if(beginPattern[index]==ch)
			{
				session.setIntAttribute(this,"bindex",index+1);
				if(beginPattern.length==index+1)
				{
					session.setIntAttribute(this,"state",1);
				}
				return true;
			}
		}
		else if(state==1)
		{
			byte ch=(byte)request.peek();
			
			for(int i=0;i<endPatterns.length;i++)
			{
				int index=session.getIntAttribute(this,"eindex"+i);
			
				if(index < endPatterns[i].length &&  endPatterns[i][index]==ch)
				{
					session.setIntAttribute(this,"eindex"+i,index+1);
					if(endPatterns[i].length==index+1)
					{
						session.setIntAttribute(this,"state",0);
						session.setAttribute(this,"result","true");
					}
				}
				
			}//for
			
			return true;
		}	
		return false;
	}

 
}
