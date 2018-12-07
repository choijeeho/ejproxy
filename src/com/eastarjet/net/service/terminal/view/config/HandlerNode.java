package com.eastarjet.net.service.terminal.view.config;

import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * HandlerNode represent Handler class 
 * 
 * </pre>
 * @author clouddrd
 *
 */
public class HandlerNode extends ConfigNode 
{
	public final static  int LAST=-1;
	public final static  int FIRST=1;
	int index;
	protected ValidatorNode 		validator;
	protected List<HandlerNode> 	handlers=new LinkedList<HandlerNode>();
	protected List<ReferenceNode> 	nexts=new LinkedList<ReferenceNode>();


	public HandlerNode()
	{
		
	}
	

}//class
