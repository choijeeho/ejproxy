package com.eastarjet.net.service.terminal.view.config;

/**
 * type represent View or Handler
 * validatorState is whether is Validator enable or not
 * index = first, last or default
 * refernceID =  id of another handler or view 
 *  
 * @author clouddrd
 *
 */
public class ReferenceNode extends ConfigNode 
{
	final static int ENABLE=0;
	final static int DISABLE=1;
	final static int TYPE_VIEW=0;
	final static int TYPE_HANDLER=1;

	int type;
	int validatorState ;
	int index;
	String referenceID;
}//class
