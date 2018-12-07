package com.eastarjet.net.service;

import java.io.IOException;

/**
 * 
 * @author clouddrd
 *
 */
public interface ServiceBinder 
{
	public void setServicePort(int port);
	public int getServicePort();
	
	public void bind() throws IOException;
	public Connection accept() throws IOException;
}
