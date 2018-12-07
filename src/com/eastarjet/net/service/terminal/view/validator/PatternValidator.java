package com.eastarjet.net.service.terminal.view.validator;

import java.io.IOException;
import java.util.Map;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.validator.pattern.AbstractPatternSource;
import com.eastarjet.net.service.terminal.view.validator.pattern.Operator;
import com.eastarjet.net.service.terminal.view.validator.pattern.PatternAnalyzer;
import com.eastarjet.net.service.terminal.view.validator.pattern.PatternParser;
import com.eastarjet.net.service.terminal.view.validator.pattern.PatternSource;
import com.eastarjet.net.service.terminal.view.validator.pattern.Reader;
import com.eastarjet.util.StringToolkit;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class PatternValidator extends MatchValidator 
{

	static Logger log = Toolkit.getLogger(PatternValidator.class);
	//Token [] matchPattern;
	PatternAnalyzer analyzer;
	
	public PatternValidator(){}
	
	public void setAttribute(String k,Object v)
	{
		if("pattern".equals(k))
		{
			//byte [] pattern=makePattern((String)v);
		//	matchPattern= parseToken(pattern);
			PatternParser parser=new PatternParser();
			analyzer = parser.parse((String)v);
			if(log.isDebugEnabled())
			{
			//	String tv=Toolkit.dumpHex(matchPattern, matchPattern.length);
				//log.debug("pattern :\n"+tv);
			}
		}
		else super.setAttribute(k, v);
	}//method
	
	
	void parse(Map map, int ch)
	{
		
	}
	
	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		
		PatternSource source = (PatternSource)session.getAttribute(this,"patternSource");
		if(source==null)
		{
			source=new ValidatorPatternSource(session,request);
		//	analyzer.initSource(source);
			session.setAttribute(this,"patternSource",source);
		}
		
		//int index=session.getIntAttribute(this,"index");
		//byte ch=(byte)request.peek();
		//Token p=matchPattern[index];
		
		boolean ret = false;
		try
		{
			int ch= source.getReader().read();
			ret=analyzer.hasInterest(source);
			if(!analyzer.hasMorePatterns(source))
			{
				boolean isMatch= analyzer.isMatch(source);
				if(isMatch)
				{
					if(log.isDebugEnabled()) log.debug("Pattern success!!!!");
					session.setAttribute(this,"result","true");
				}
			}
			
		}catch(Exception e)
		{
			log.error("check Match",e);
		}
		return ret;
	}

	
	class ValidatorPatternSource extends AbstractPatternSource
	{
		Session ses;
		Operator root;
		VReader reader;
		ValidatorPatternSource(Session ses,Request request)
		{this.ses=ses; reader=new VReader(request);}
		
		@Override
		public Reader getReader() {
			// TODO Auto-generated method stub
			return reader;
		}
 	}
	
	class VReader implements Reader
	{
		Request request;
		int value;
		VReader(Request request)
		{
			this.request=request;
 
		}
		
		@Override
		public int getPosition() {
			// TODO Auto-generated method stub
			 return request.getPeekPosition();
			//return 0;
		}

		@Override
		public int read() throws IOException {
			// TODO Auto-generated method stub
			value= request.peek();
			return value;
		}
		
		@Override
		public int peek() {
			// TODO Auto-generated method stub
			return value;
		}		

		@Override
		public void reset(int position) 
		{
			// TODO Auto-generated method stub
			request.setPeekPosition(position);
			value=request.peek();
			request.setPeekPosition(position);
		}
		
	}
}//class
