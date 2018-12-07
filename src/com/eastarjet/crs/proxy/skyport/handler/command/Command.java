package com.eastarjet.crs.proxy.skyport.handler.command;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;

/**
 * 
 * @author clouddrd
 *
 */
public interface Command 
{
	public int doCommand(Session ses, Request req,Response resp);
}
