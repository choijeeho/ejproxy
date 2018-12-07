package com.eastarjet.net.service.task;

import java.util.List;

import com.eastarjet.net.service.Service;
import com.eastarjet.net.service.ServiceTask;

public abstract class AbstractTask implements ServiceTask 
{
	protected Service service;
	protected String title;

	
	


	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return service;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public void initialize(Service service) throws Exception
	{
		// TODO Auto-generated method stub
		initializeTask(service);
		service.addWorkingTask(this);
	}
	
	protected void initializeTask(Service service) throws Exception
	{}

	
	@Override
	public void finalize(Service service) throws Exception
	{
		// TODO Auto-generated method stub
		service.removeWorkingTask(this);
		finalizeTask(service);
	}
	
	protected void finalizeTask(Service service)throws Exception
	{}
	
	@Override
	public void setService(Service service) {
		// TODO Auto-generated method stub
		this.service=service;
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		this.title=title;
	}

	public String toString(){return title;}
}
