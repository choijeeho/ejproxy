package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.io.IOException;
import java.util.Stack;

/**
 * 
 * @author clouddrd
 *
 */
public interface PatternSource 
{
	public Operator getCurrent();
	public void setCurrent(Operator op);

	public Stack getStack();
	
	public Variables getVariables(Operator op);
	
	public void setBackOperator(Operator op);
	public Operator getBackOperator();

	public void setResult(boolean r);
	public boolean getResult();
	
	public Reader getReader();
	public boolean 	nextToken() throws IOException;
	public int  	currentToken() ;
}
