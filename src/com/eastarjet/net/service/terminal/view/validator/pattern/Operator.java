package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.io.IOException;

import com.eastarjet.util.StringToolkit;

public class Operator 
{
		Operator next; //
 
		public final static int DONE=0;
		public final static int POP_NEW=1;
		public final static int POP_DONE=2;
		
		public int doOperate(PatternAnalyzer analyzer, PatternSource source) {return DONE;}
		
		public boolean hasNext(){return next!=null;}
		public Operator next(){return next;}
		public void setNext(Operator next){ this.next=next;}
		public boolean isCompleted(PatternSource source) {return true;}

		public String toTraceString()
		{ return getClass().getSimpleName()+ ", next="+((next!=null)?next.getClass().getSimpleName():null); }
}

class CompareOperator extends Operator
{
	protected int token;
	protected int count;
	public int doOperate(PatternAnalyzer analyzer, PatternSource ipr ) 
	{
		int ch=ipr.currentToken();

		boolean ret= validate(ipr,ch);
		if(ret)ret=isMatch(ipr,ch);
		int tcount= ipr.getVariables(this).getInteger("matchCount");
		tcount++;
		ipr.getVariables(this).setInteger("matchCount", tcount);
		ipr.setResult(ret);
		return DONE;
	}
	
	protected boolean isMatch(PatternSource ipr,int ch)
	{
		return (token==ch);
	}
	
	public boolean isCompleted(PatternSource source) 
	{
		int tcount= source.getVariables(this).getInteger("matchCount");
		if(tcount<count) return false;
		return true;
	}
	
 
	protected boolean validate(PatternSource ipr,int ch){return true;}
	
	public String toTraceString()
	{
		return super.toTraceString()+", tok="+(char)token;
	}
}



class CharOperator extends CompareOperator
{
	CharOperator (int ch){token=ch; }
	protected boolean isMatch(PatternSource ipr,int ch)
	{
		return ch==token;
	}
	
	protected boolean validate(PatternSource ipr,int ch)
	{return true;}
}


class AlphaOperator extends CompareOperator
{
	AlphaOperator(int digit)
	{
		count=digit;
	}
	
	protected boolean isMatch(PatternSource ipr,int ch)
	{
		return true;
	}
	
	protected boolean validate(PatternSource ipr,int ch)
	{
		return true;
	}
}
 
class NumberOperator extends CompareOperator
{
	NumberOperator(int digit)
	{
		count=digit;
	}
	
	protected boolean isMatch(PatternSource ipr,int ch)
	{
		return true;
	}
	
	protected boolean validate(PatternSource ipr,int ch)
	{return StringToolkit.isDigit((char)ch);}
}



class ZeroMoreOperator extends Operator
{
	public  int doOperate(PatternAnalyzer analyzer,PatternSource source) 
	{
		//int ch=source.getReader().peek();
		
		int ret=DONE;
		int count=source.getVariables(this).getInteger("matchCount");
		
		if(count>0)
		{
			//source.getReader().reset(source.getReader().getPosition()+1);
		}
		count++;
		source.getVariables(this).setInteger("matchCount", count);
		//source.push(this);
		source.setBackOperator(this);

		source.setResult(true);//next Token
		return ret;
	}
	
}


class BlockOperator extends  Operator
{
	BlockOperator (){}
	Operator children;
	
	void setChildren(Operator children)
	{this.children=children;}
	
	public int doOperate(PatternAnalyzer analizer, PatternSource source)
	{
		int  ret=DONE;
		if(!source.getVariables(this).getBoolean("isInit"))
		{
			source.getVariables(this).setBoolean("isInit",true);
			analizer.push(source, this);
			analizer.push(source, children);
			
			ret=POP_NEW;
		}
		else ret=POP_DONE;
			
		return ret;
	}
 
}


/**
 * 
 * @author clouddrd
 *
 */
class OrOperator extends Operator
{
    Operator right;
    Operator left;
	
	void setRight(Operator right)
	{
		this.right=right;
	}
	
	void setLeft(Operator left)
	{
		this.left=left;
	}
	
	 
	public int doOperate(PatternAnalyzer analizer, PatternSource source)
	{
		int ret=DONE;
		int stat=source.getVariables(this).getInteger("state");
		boolean result=source.getResult();
		if(stat==0)
		{
			source.getVariables(this).setInteger("state",1);
			analizer.push(source,this);
			analizer.push(source,left);
			ret=POP_NEW;
			//ret=left.doOperate(source);
			//next=left.next;
			//ret=true;
		}
		else if(stat==1 && !result)
		{
			source.getVariables(this).setInteger("state",2);
			analizer.push(source,this);
			analizer.push(source,right);
			source.setResult(true);
			ret=POP_NEW;
		}
		else if(stat==1 && result)
		{
			analizer.push(source,next);
			ret=POP_DONE;
		}
		else  
		{
			analizer.push(source,next);
			ret=POP_DONE;
		}
		
		
		return ret;
	}
	
	Operator next(PatternSource interpret)
	{
		return next;
	}
}

