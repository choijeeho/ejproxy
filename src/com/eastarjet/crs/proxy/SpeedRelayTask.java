package com.eastarjet.crs.proxy;

 
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import com.eastarjet.net.service.task.RelayTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class SpeedRelayTask extends RelayTask 
{
	static Logger log = Toolkit.getLogger(SpeedRelayTask.class);
	
	static int headerType=-1; //0=test, 1=prod
	static boolean  isDump= false;
	static boolean isInit=false;

	
	public SpeedRelayTask()
	{	}

	public void init()
	{
		if(isInit) return;
		isInit=true;

		//filter 헤터 타입 선택
		String stype=service.getConfig().getServiceProperty("headerType");
		if("prod".equals(stype)) headerType=1;
		else headerType=0;
		
		//덤프여부 선택 
		String dump=service.getConfig().getServiceProperty("dump");
		if("1".equals(dump))isDump=true;
		
	}
	
	
	public boolean isFilterRejected(SocketChannel sock,ByteBuffer buf)
	{
		if(service==null) return true;
		String [] allows = service.getConfig().getServiceProperties("allows");
		
		Socket socket=sock.socket();
		if(socket==null ) return true;
		
		if(socket.getInetAddress()==null ) return true;
		String addr = socket.getInetAddress().getHostAddress();
		if(log.isDebugEnabled()) log.debug("filter check HostName : "+ addr);
		for(int i=0;allows!=null && i<allows.length;i++)
		{
			if(addr.startsWith(allows[i]))	return false;
		}
		
		if(getHeaderType(buf)==0) return true;
		return false;
	}
	
	static int [][] headers =
	{	
		 {0x2e,0x4e,0x45,0x54,-1,-1,-1,-1,   
			 -1,-1,-1,0x00,-1,-1,-1,-1,
			 0x01,0x01,-1,0x00,0x00,0x00,0x74,0x63,
			 0x70,0x3a,0x2f,0x2f,0x45,0x4a,0x54,0x53, /* cp://EJT */
			 0x50,0x45,0x45,0x44,0x50,0x52,0x4f,0x58,
			 0x59,0x3a,0x31,0x35,0x37,0x33,0x34,0x2f,
			 0x4e,0x65,0x77,0x53,0x6b,0x69,0x65,0x73 },
			
			 {0x2e,0x4e,0x45,0x54,-1,-1,-1,-1,   
				 -1,-1,-1,0x00,-1,-1,-1,-1,
				 0x01,0x01,-1,0x00,0x00,0x00,0x74,0x63,
				 0x70,0x3a,0x2f,0x2f,0x45,0x4a,0x52,0x53, // "cp://EJR" 
				 0x50,0x45,0x45,0x44,0x50,0x52,0x4f,0x58,
				 0x59,0x3a,0x31,0x35,0x37,0x33,0x34,0x2f,
				 0x4e,0x65,0x77,0x53,0x6b,0x69,0x65,0x73 },			 
			 
		 {0x2e,0x4e,0x45,0x54,0x01,0x00,0x00,0x00,   
				 0x00,0x00,0x3f,0x01,0x00,0x00,0x04,0x00,
				 0x01,0x01,0x36,0x00,0x00,0x00,0x74,0x63,
				 0x70,0x3a,0x2f,0x2f,0x45,0x4a,0x52,0x53,
				 0x4b,0x59,0x53,0x50,0x45,0x45,0x44,0x50,
				 0x52,0x4f,0x58,0x59,0x3a,0x31,0x35,0x37,
				 0x33,0x34,0x2f,0x4e,0x65,0x77,0x53,0x6b }
	};
	
	
	/* 
	 * if buf is Login Message,  return  > 0 ,
	 * if buf is a incorrect login message , return 0,
	 * else return < 0  
	 * */
	int getHeaderType(ByteBuffer  buf)
	{

		
		int ret = -1;
		int count=0;
		int [] header = headers[headerType]; 
		int len = header.length;
		int sz= buf.position();
		String debug1="",debug2="";
		
		for(int i=0;i<sz;i++)
		{	
			count=0;
			debug1="";
			debug2="";
			for(int j=0;j<len && i+j<sz;j++)
			{
				int ch =header[j];
				byte sch=buf.get(i+j);
				if(log.isInfoEnabled())
				{
					debug1+= ch+", ";
					debug2+=sch+", ";
				}
				if(ch!=-1 && ch!=sch) break;
				count++;
			}//for
			if(count >= 4 ) break;
			
		}//for
		
		if(count==len)   ret=count;
		else if(count > 24 ) ret = 0;
		
		
		//if(log.isDebugEnabled())log.debug("return = "+ret);
		if(log.isInfoEnabled()) log.info(this+" : getHeaderType="+ret);
		if(log.isInfoEnabled() && isDump)
		{
			log.info("getHeaderType header :["+debug1+"] == ["+debug2+"]");
		}
		return ret;
	}
	
	
	/*
	 * 
	 *  2e,4e,45,54,01,00,00,00,|.NET   
		00,00,3f,01,00,00,04,00,|      
		01,01,36,00,00,00,74,63,| 6   t
		70,3a,2f,2f,45,4a,52,53,|cp://EJR
		4b,59,53,50,45,45,44,50,|SKYSPEED
		52,4f,58,59,3a,31,35,37,|PROXY:15
		33,34,2f,4e,65,77,53,6b,|734/
		
		
	 */

}
