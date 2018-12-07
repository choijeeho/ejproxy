package com.eastarjet.net.service.terminal.view.validator.pattern;

 
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import com.eastarjet.util.StringToolkit;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;


/*
 * 
 *
 **/

public class PatternAnalyzer  
{
	Operator rootOperator;
	static Logger log= Toolkit.getLogger(PatternAnalyzer.class);
	public void setRootOperator(Operator oper){rootOperator=oper;}

 
	boolean hasNextOperator(PatternSource source) 
	{
		// TODO Auto-generated method stub
		boolean ret=false;
		Operator current=source.getCurrent();
		if(current==null) current=rootOperator;
		
		Stack<StackNode> stack=(Stack<StackNode>)source.getStack();
		
		if(current==null) ret = false;
		else 
		{
			if(current.isCompleted(source))
			{
				ret=current.next()!=null;
				if(!ret) ret= !stack.isEmpty();
			}
			else 
				ret = true; 
		}
		return ret;
	}

 
	public Operator nextOperator(PatternSource source ) 
	{
		// TODO Auto-generated method stub
		Operator current=source.getCurrent();
		if(current==null)
		{
			current=rootOperator;
			source.setCurrent(current);
			return current;
		}
		
		Stack<StackNode> stack= (Stack<StackNode>) source.getStack(); 

		if(current!=null && current.isCompleted(source))
		{
			current=current.next();
			if(current==null && !stack.isEmpty())
			{
				 current=pop(source,Operator.POP_DONE);
			}
		}
		source.setCurrent(current);
		return current;
	}
	
 
	public boolean hasInterest(PatternSource source)  
	{
		boolean ret=false;
		
		Operator op= null;
		if(log.isTraceEnabled())	{ log.trace("Interest >>>>>> ");}

		if(!hasNextOperator(source)) return ret;
		
		op=nextOperator(source);
	
		while(op!=null)
		{
			if(log.isTraceEnabled())
			{ log.trace("    op: ["+op.toTraceString() +"], ch="+ (char)source.currentToken());}
			int iret=op.doOperate(this,source);
			boolean bret= source.getResult();
			if(iret==Operator.DONE)
			{
				Operator bop =  source.getBackOperator();
				
				if(!bret && bop!=null )
				{
					op=bop;	source.setCurrent(op);
					continue;
				}
			}//
			
			
			if(iret==Operator.POP_DONE || !bret)
			{
				op=pop(source,Operator.POP_DONE);
			}
			else if(iret==Operator.POP_NEW)
			{
				op=pop(source,Operator.POP_NEW);
			}
			else break;
		} //while
		
		ret= source.getResult();
		if(log.isTraceEnabled())	{ log.trace("Intereset result="+ret+" >>>>>");}

		return ret;
	}
	
 
	  Operator pop(PatternSource source,int type) 
	{
		// TODO Auto-generated method stub
		Stack<StackNode> stack=(Stack<StackNode>)source.getStack();
		StackNode node=null;
		
		if(!stack.isEmpty()) node=stack.pop();
		Operator current=null;
		if(node!=null)
		{
			current=node.operator;
			source.setCurrent(current);
			source.setBackOperator(node.back);
			boolean result=source.getResult();
			int npos=0;
			if(type==Operator.POP_DONE && !result)
			{
				npos=node.position; source.getReader().reset(npos);
			}
			
			if(log.isTraceEnabled()) 
				log.trace("    pop: ["+current.toTraceString()+"],result="+result
					+",type="+type+",pos="+npos);
		}
		return current;
	}


	public void push(PatternSource source, Operator op) 
	{
		// TODO Auto-generated method stub
		StackNode node=new StackNode(op,
					source.getReader().getPosition(),
					source.getBackOperator());
		
		Stack<StackNode> stack=(Stack<StackNode>)source.getStack();
		
		if(log.isTraceEnabled()) log.trace("    push:"+op.toTraceString()+",pos="+node.position);
		stack.push(node);
	}

	
	
	public boolean hasMorePatterns(PatternSource source)
	{
		return hasNextOperator(source) ; 	
	}
	
 
	
	public boolean isMatch(PatternSource source)
	{
		boolean ret=source.getResult() ;

		ret =ret & !hasNextOperator(source);
		return  ret;
	}
	

 
	
	
	class StackNode
	{
		Operator operator;
		int position;
		Operator back;
		StackNode(Operator op, int pos,Operator back)
		{operator=op; position=pos;this.back=back;}
	}
}//class
 




