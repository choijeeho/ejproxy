package com.eastarjet.net.service.test;

import java.io.IOException;
import java.util.Stack;

import com.eastarjet.net.service.terminal.view.validator.PatternValidator;
import com.eastarjet.net.service.terminal.view.validator.pattern.AbstractPatternSource;
import com.eastarjet.net.service.terminal.view.validator.pattern.PatternAnalyzer;
import com.eastarjet.net.service.terminal.view.validator.pattern.PatternParser;
import com.eastarjet.net.service.terminal.view.validator.pattern.Reader;
import com.eastarjet.util.StringToolkit;

import junit.framework.TestCase;

public class TestValidator extends TestCase 
{
	public void testPatternParser()
	{
		try
		{
		// "$0x01;,  %1n, %$, %c, %b, %*, %( %) %|"
		PatternParser parse = new PatternParser();

		PatternAnalyzer analyzer;
		TestPatternSource source;
		//	PatternAnalyzer analyzer=parse.parse("$0x01;abcd%1n%%%c%*%(abc%)%(abc%)%|%(bcd%);abcd");

//		analyzer=parse.parse("%c,%n,%6n,%3c,%*,def,%*,eft");
//		source=new TestPatternSource("a,2,123456,aks,djlkajsdklf,def,242343kjkjdf,eft");
		
		
	//	analyzer=parse.parse("ab%(abc%)%|%(abdd%)12");
		//source=new TestPatternSource("ababdd12eeee");
		
		analyzer=parse.parse("$0x1b;$0x5b;$0x35;$0x69;$0x01;$0x1b;$0x5b;$0x3f;$0x33;$0x6c;%*$0x1b;$0x5b;$0x34;$0x69;");
		byte[] buf={0x1b,0x5b,0x35,0x69,0x01,0x1b,0x5b,0x3f,0x33,0x6c,0x20,0x20,0x45,0x61,
				0x1b,0x5b,0x34,0x69};
		source=new TestPatternSource(new String(buf));
		
		
		//analyzer=parse.parse("%2n:%2n %5c/%6c %4c/%4n:%*>");
		//source=new TestPatternSource("12:58 28Jan/CJJREP 1750/2130:ZE 7671 (4)>");
		

			while(source.nextToken())
			{
			 	if(!analyzer.hasInterest(source))
			 	{
			 		System.out.println("no interest");
			 		break;
			 	}
		 
				if(!analyzer.hasMorePatterns(source))
				{
					System.out.println("no more pattens");
					break;
				
				}
			}//while
			boolean ret=analyzer.isMatch(source);
			System.out.println("Result="+ret);
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	class TestPatternSource extends AbstractPatternSource
	{
		
		TestReader reader;
		TestPatternSource(String str){ reader=new TestReader(str);}
		@Override
		public Reader getReader() {
			// TODO Auto-generated method stub
			return reader;
		}
		
	} 
	
	class TestReader implements Reader
	{
		int position=0;
		String str;
		
		TestReader(String str){this.str=str;}
		public int peek(){ int ret = str.charAt(position-1); return ret;}
		public boolean isEmpty(){ return position+1==str.length();}
		@Override
		public int getPosition() {
			// TODO Auto-generated method stub
			
			return position;
		}

		@Override
		public int read() throws IOException 
		{
			// TODO Auto-generated method stub
			int ret=-1;
			
			if(position<str.length())
			{	ret = str.charAt(position);
				position++;
			}
			return ret;
		}

		@Override
		public void reset(int position) 
		{
			// TODO Auto-generated method stub
			this.position=position;
		}
		
	}

}//class
