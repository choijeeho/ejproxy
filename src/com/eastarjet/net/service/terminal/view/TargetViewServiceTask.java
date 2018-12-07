package com.eastarjet.net.service.terminal.view;

import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.Service;

/**
 * Target connection
 * 
 * @author clouddrd
 *
 */
public class TargetViewServiceTask extends ViewServiceTask 
{
	public TargetViewServiceTask(){}
	

	protected void initializeTask(Service service) throws Exception
	{
		super.initializeTask(service);
		TargetViewService svc=(TargetViewService)service;
		Connection con=svc.getTargetConnection();
		((TargetViewTaskProcessor)processor).setTarget(con);
	}
	
	protected void finalizeTask(Service service)throws Exception
	{
		super.finalizeTask(service);
		TargetViewService svc=(TargetViewService)service;
		Connection con=((TargetViewTaskProcessor)processor).getTarget();
		svc.releaseTargetConnection(con);
	}

}//class
