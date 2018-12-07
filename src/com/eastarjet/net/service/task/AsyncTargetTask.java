package com.eastarjet.net.service.task;

import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.ConnectionManager;
 

/**
 * AsyncTargetTask는 타켓 연결이 필요한경우 사용.
 * 
 * @author clouddrd
 *
 */
public class AsyncTargetTask extends AsyncTask 
{
	protected Connection target;
	
	public void setTarget(Connection con)
	{target=con;}
	
	public Connection getTarget()
	{return target;}
 
	
}//class
