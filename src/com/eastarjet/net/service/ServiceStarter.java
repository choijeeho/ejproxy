package com.eastarjet.net.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * Network Service Starter
 * 
 * async, sync 
 * 
 * @author clouddrd
 *
 */
public class ServiceStarter 
{
	static Logger log = Toolkit.getLogger(ServiceStarter.class);
	
	public static void main(String [] args)
	{
		String sconf=null;
		for(int i=0;i<args.length;i++) 
		{
			if("-conf".equals(args[i]) && i+1<args.length)
			{
				sconf=args[i+1]; i++;
				log.info("config : " + sconf);
			}
			else if("-h".equals(args[i]) || "-help".equals(args[i]))
			{
				printUsage(args[0]);
				return;
			}
		}
		
	
		try{
			Application starter = new Application(sconf);
			starter.start();
		}
		catch(Exception e)
		{
			System.out.println("config :"+sconf);
			e.printStackTrace();
		}
		
	}
	
	static void printUsage(String title)
	{
		System.out.println("Usage:");
		System.out.println("\t"+title+" [-h] [-conf config-file] ");
		System.out.println("\t\tdefault config : conf/proxy.conf" );
	}
}//class




 
