package com.eastarjet.net.service.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.Connection;
import com.eastarjet.net.service.ConnectionManager;

/**
 * 
 * 
 * @author clouddrd
 *
 */
public class AsyncConnectionManager extends ConnectionManager 
{

	@Override
	public Connection getConnection() throws IOException 
	{
		Connection con=new AsyncConnection(targetIP,targetPort);
			con.connect();
		return con;
	}

	@Override
	public void releaseConnection(Connection con) throws IOException
	{
		// TODO Auto-generated method stub
		AsyncConnection acon=(AsyncConnection)con;
		con.close();
	}

}
