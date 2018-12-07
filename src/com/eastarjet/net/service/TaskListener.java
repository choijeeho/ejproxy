package com.eastarjet.net.service;

public interface TaskListener 
{
	public void taskInitialzed(ServiceTask task);
	public void taskFinalized(ServiceTask task);
}
