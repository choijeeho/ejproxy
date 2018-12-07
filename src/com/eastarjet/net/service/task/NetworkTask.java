package com.eastarjet.net.service.task;

import com.eastarjet.net.service.Connection;

public abstract class NetworkTask extends AbstractTask 
{
	protected String sourceIP;
	public abstract void setSource(Connection connection);
	public abstract Connection getSource();

}
