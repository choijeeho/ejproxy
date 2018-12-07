package com.eastarjet.crs.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
 
import java.util.Vector;

 
import com.eastarjet.net.service.task.SyncTask;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 *  SMTP Filter Ã³¸®¿ë 
 *   
 * @author clouddrd
 *
 */
public class SMTPFilterTask extends SyncTask 
{
	static Logger log = Toolkit.getLogger(SMTPFilterTask.class);
	
	public SMTPFilterTask(){}
	
	String sender=null;
	Vector<String> receivers=new Vector<String>();
	boolean isReport=false;
	
	@Override
	public void doWork() 
	{
		// TODO Auto-generated method stub
		

		//String data=null;
		try
		{
			socket.setSoTimeout(60000);
			
			writeAll(out,"220 ejwmlp1.eastarjetmail.com"+
					" Microsoft ESMTP MAIL Service,"+
					" Version: 6.0.3790.4675 ready "+
					"at Thu, 14 Oct 2010 07:25:40 +0900\r\n"
				);
		}catch(Exception e){}
		int dataStatus=0;
		receivers.clear();
		while(true)
		{
			try
			{
				String line=readLine(in);
				if( log.isTraceEnabled()) log.trace("read :"+line);
				if(line==null) break;
				
				if(dataStatus > 0)
				{
				//	data+=line+"\r\n";
				
					try{
					if(isReport) writeAll(tout,line+"\r\n");
					}catch(Exception te){};
					
					if(dataStatus==1 && line.isEmpty()) 
					{
						dataStatus++;
						if(log.isTraceEnabled()) log.trace("start END .......");
					}
					else if (dataStatus==2)
					{ 
						if(".".equals(line))
						{ 
							dataStatus=0;
						//	log.info(data);
							if(isReport)
							{
								try 
								{
									//writeAll(tout,"\r\n.\r\n");
									if(log.isTraceEnabled())log.trace("Target Data : "+readLine(tin));
								}
								catch(Exception ee)
								{
									if(log.isInfoEnabled()) log.info("Error at Target Read ",ee);
								}
							}//if
							isReport= false;
							writeAll(out,"250 DATA RECEVIED\r\n");
							break;
							
						}
						else if(line.isEmpty())
						{
							if(log.isTraceEnabled())log.trace("restart END ......."); 
							dataStatus=2;
						}
						else dataStatus=1; 
					}
					else if (dataStatus==3)
					{
						if( line.isEmpty())
						{
							dataStatus=0;
						//	log.info(data);
							writeAll(out,"250 DATA RECEVIED\r\n");
						}else dataStatus=1;
					}//
				}
				else if(line.startsWith("HELO")||line.startsWith("EHLO"))  
				{
					isReport=false;
					dataStatus=0;
					writeAll(out,"250 ejwmlp1.eastarjetmail.com Hello\r\n");
				}
				else if(line.startsWith("MAIL FROM:"))
				{
					sender=line.substring(10).trim();
					sender=sender.substring(1,sender.length()-1);
					log.info("Sender : '"+ sender+"'");
					if("support@navitaire.com".equals(sender))
					{
						if(log.isInfoEnabled()) log.info("Will Transfer : "+ sender);
						isReport= true;
						
					}
					//else
						writeAll(out,"250 2.1.0 "+sender+"....Sender OK\r\n");
				}
				else if(line.startsWith("RCPT TO:"))
				{
					
					String receiver=line.substring(8).trim();
					receiver=receiver.substring(1,receiver.length()-1);
					receivers.add(receiver);
					log.info("Receiver : "+ receiver);
					writeAll(out,"250 2.1.5 "+receiver+"\r\n");
				}
				else if(line.startsWith("DATA"))  
				{
					writeAll(out,"354 Start mail input; end with <CRLF>.<CRLF>\r\n");
					
					dataStatus=1;
					if(isReport) connectTarget();
				}
				else if(line.startsWith("QUIT"))  
				{
					writeAll(out,"250 Will QUIT\r\n");
					break;
				}
			}catch(Exception e){ log.error("Error",e);break;}

		}//while

		try{socket.close(); }catch(Exception e){}
		
		try{if(tsocket!=null) tsocket.close(); }catch(Exception e2){}
		//close();
	}
	
 
	Socket tsocket=null;
	InputStream tin=null;
	OutputStream tout=null;

	void connectTarget()
	{
		try
		{
			if(tsocket!=null) tsocket.close();
		}
		catch(Exception te){ log.info("Error at closing Target Socket",te);}
		
		try
		{
			 
			String ip=getService().getConfig().getServiceProperty("targetIP");
			int port=getService().getConfig().getIntServiceProperty("targetPort");
			tsocket=new Socket(ip,port);
			tin=tsocket.getInputStream();
			tout=tsocket.getOutputStream();
			
			String  line=readLine(tin);
			if(log.isDebugEnabled()) log.debug(line);

			writeAll(tout,"HELO PROXY\r\n");
			line=readLine(tin);
			if(log.isDebugEnabled()) log.debug(line);
			
			writeAll(tout,"MAIL FROM: <"+sender+">\r\n");
			line=readLine(tin);
			if(log.isDebugEnabled()) log.debug(line);
			
			for(int i=0;i<receivers.size();i++)
			{
				writeAll(tout,"RCPT TO: <"+receivers.get(i)+">\r\n");
				line=readLine(tin);
				if(log.isDebugEnabled()) log.debug(line);
			}

			writeAll(tout,"DATA\r\n");
			line=readLine(tin);
			if(log.isDebugEnabled()) log.debug(line);
 			
		}catch(Exception e){log.error("Error Connect Target",e);}
	}
	
	byte []buf=new byte[40960];
	
	String readLine(InputStream in) throws IOException 
	{
		int ch = 0;
		int count=0;
		
		while(ch >= 0)
		{
			ch=in.read();
			
		
			if(ch<0) break;
			if(ch=='\r') continue;
			if(ch=='\n') break;
				
			buf[count++]=(byte)ch;
		}//while
		if(ch>=0 && count==0) return "";
		if(ch<0) return null;
		return new String(buf,0,count);
	}//method
	
	void writeAll(OutputStream out,String data) throws IOException
	{
		if(log.isTraceEnabled()) log.debug("send :"+data);
		
		byte []tbuf=data.getBytes();
		int count=0;
 
			out.write(tbuf,count,tbuf.length-count);
 
	}//method
}//class
