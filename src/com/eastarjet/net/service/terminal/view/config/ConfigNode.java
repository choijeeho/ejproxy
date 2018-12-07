package com.eastarjet.net.service.terminal.view.config;

import java.util.Hashtable;
import java.util.List;

/**
 * Base class 
 * 
 * 
 * @author clouddrd
 * @since 2010.10.
 *
 */
public class ConfigNode 
{
	protected String id;
	protected String target;
	protected String className;
	protected Hashtable<String,Object> attributes=new Hashtable<String,Object>();
}
