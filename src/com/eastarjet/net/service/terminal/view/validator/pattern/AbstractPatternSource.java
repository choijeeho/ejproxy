package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import org.hamcrest.core.Is;

import com.eastarjet.net.service.terminal.view.validator.pattern.Operator;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public abstract class AbstractPatternSource implements PatternSource 
{
	 
	Operator root;
	Operator current;
	Operator backOperator;
	//Reader reader;
	boolean result;
	Stack stack=new Stack();
	
	Hashtable<Operator, Variables> variablesMap=new Hashtable<Operator, Variables>();
	static Logger log= Toolkit.getLogger(AbstractPatternSource.class);
	
	public void setCurrent(Operator op){ current=op;}
	
	@Override
	public Operator getCurrent() {
		// TODO Auto-generated method stub
		return current;
	}
	
	
	
	public Stack getStack(){return stack;}
	public Operator getBackOperator(){return backOperator;}
	public void setBackOperator(Operator back){ backOperator=back;}
	

	@Override
	public Variables getVariables(Operator op) 
	{
		// TODO Auto-generated method stub
		Variables ret= variablesMap.get(op);
		if(ret==null)
		{
			ret=new Variables();
			variablesMap.put(op,ret);
		}
		
		return ret;
	}

	
	int token;
 
	@Override 
	public boolean nextToken() throws IOException
	{
		token=getReader().read();
		if(token==-1) return false;
		return true;
	}
	
	public int currentToken() 
	{
		return getReader().peek();
	}
	


 
	
	@Override
	public void setResult(boolean r) {
		// TODO Auto-generated method stub
		result=r;
	}

	public boolean getResult()
	{
		return result;
	}
	

}//class
