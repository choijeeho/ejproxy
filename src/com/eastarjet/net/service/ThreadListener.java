package com.eastarjet.net.service;

/**
 * notify when thread is working or sleep
 * 
 * @author clouddrd
 *
 */
public interface ThreadListener 
{
	public void waken(TaskThread thread);
	public void slept(TaskThread thread);
}
