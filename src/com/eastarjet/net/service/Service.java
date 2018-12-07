package com.eastarjet.net.service;


/**
 * 서비스 모델 (AbstractSerivce, NetworkService  참조) 
 *  
 * @author clouddrd
 *
 */
public interface Service extends Runnable
{
	public ServiceConfig getConfig();
	
	public void setConfig(ServiceConfig conf);
	public void initConfig(Application app);
	
	public void addWorkingTask(ServiceTask task);
	
	public void removeWorkingTask(ServiceTask task);
	
	public void setAttribute(String key,Object value);
	public Object  getAttribute(String key);
	
	public Application getApplication();
	public void addWorkingCount(int count);
	public String getServiceName();

	
	public void initialize(Application service) throws Exception;
	public void finalize(Application service) throws Exception;
	public void shutdown();
	public void addServiceListener(ServiceListener listener);
	public void removeServiceListener(ServiceListener listener);
}
 
