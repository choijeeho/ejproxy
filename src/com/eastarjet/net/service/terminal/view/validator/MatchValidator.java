package com.eastarjet.net.service.terminal.view.validator;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.Validator;
import com.eastarjet.net.service.terminal.view.ViewTaskProcessor;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class MatchValidator extends AbstractValidator
{
	static Logger log = Toolkit.getLogger(MatchValidator.class);
	byte[] matchPattern;
	public MatchValidator(){}
	
	public void setAttribute(String k,Object v)
	{
		if("pattern".equals(k))
		{
			matchPattern=makePattern((String)v);
			if(log.isDebugEnabled())
			{
				String tv=Toolkit.dumpHex(matchPattern, matchPattern.length);
				log.debug("pattern :\n"+tv);
			}
		}
		else super.setAttribute(k, v);
	}//method
	
	
	
	// "$0x01;$1n;$04n;$$;$4?;$4c;$*;$0x01;"
	protected byte[] makePattern(String v)
	{
		byte[] buf=v.getBytes();
		byte[] dbuf=new byte[buf.length];
		int i=0,ti=0,stat=0;
		int h=0,l=0,prev=0,hv=0;
		while(i<buf.length)
		{
			//log.debug("i="+i+",ti="+ti+",prev="+prev+",ch="+(char)buf[i]);
			if(stat==0 && buf[i]!='$')
			{dbuf[ti++]=buf[i++]; }
			else if(stat==0 && buf[i]=='$')
			{	stat=1; prev=i; i++ ;  }
			else if(stat==1 )
			{
				if( buf[i]=='0'){stat=2; i++ ;}
				else if( buf[i]=='$')
				{stat=0; dbuf[ti++]='$';i++ ;}
				else stat=-1;
			}
			else if(stat==2 )
			{
				if( buf[i]=='x'){stat=3;i++ ; }
				else stat=-1;
			}
			else if(stat==3 ) 
			{
				if(buf[i]==';'){h=0;i++; stat=4;}
				else { stat=4; h=buf[i++];}
			}
			else if(stat==4 )
			{
				
				l=buf[i++];
				h=(h >= 'a')? 10+h-'a':h-'0';
				l=(l >= 'a')? 10+l-'a':l-'0';
				hv = (byte)(h*16+l);
				stat=5;
			}
			else if(stat==5)
			{
				if(buf[i]==';'){dbuf[ti++]=(byte)hv; i++; stat=0;}
				else { stat=-1;}
			}
			else if(stat==-1)
			{
				for(int ix=prev;ix<=i;ix++)
					dbuf[ti++]=buf[ix];
				i++;
				stat=0;
			}

		}//while
		
		byte []b = new byte[ti];
		System.arraycopy(dbuf,0,b,0,ti);
		return b;
	}
	
	
	
	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		
		int index=session.getIntAttribute(this,"index");
		byte ch=(byte)request.peek();
		if(matchPattern[index]==ch)
		{
			session.setIntAttribute(this,"index",index+1);
			if(matchPattern.length==index+1)
			{
 
				session.setAttribute(this,"result","true");
				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isHandleable(Session session, Request request) 
	{
		// TODO Auto-generated method stub
		boolean ret=false;
		if("true".equals(session.getAttribute(this,"result")))
				ret= true;
		return ret;
	}

}//class
