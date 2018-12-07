package com.eastarjet.net.service;

public interface ServiceListener 
{
	public void serviceInitialize(Service service);
	public void serviceInitialized(Service service);
	public void serviceFinalize(Service service);
	public void serviceFinalized(Service service);
	public void taskWakeup(Service service,ServiceTask task);
	public void taskSleep(Service service,ServiceTask task);
}
