package com.eastarjet.crs.proxy.skyport.handler.printing;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.eastarjet.ejproxy.EJProxyClient;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.util.Toolkit;
import com.navitaire.schemas.WrapOfInventoryLegOp;
import com.navitaire.schemas.WrapOfLogonResponse;
import com.navitaire.schemas.ClientServices.Common.SessionManagerClient.SessionManagerClientSoapProxy;
import com.navitaire.schemas.ClientServices.OperationsManager.OperationsManagerClient.OperationsManagerClientSoapProxy;
import com.navitaire.schemas.Common.SessionContext;
import com.navitaire.schemas.Common.Enumerations.OpTimeType;
import com.navitaire.schemas.Messages.Operations.InventoryLegOp;
import com.navitaire.schemas.Messages.Session.Response.LogonResponse;

public class BGRHandler extends AbstractPrintingHandler {

	@Override
	public boolean handleTargetRequest(int target, Session session,
			Request request, Response response) 
	{
		// TODO Auto-generated method stub
		//send queue. pattern
		//read server message until carrige return
		//send [message + (print tag + message)] to te
		try{
			sendErrorBGR(request, response);
			}catch(Exception e)
			{
				log.error("Error at BGRHandler"  , e);
			}
			return false;
	
	}
	
	
	protected void sendErrorBGR(Request request,Response response) throws IOException
	{
		InputStream in=request.getInputStream();
		String brgerrormessage=Toolkit.readLine(in); //header

		String command = Toolkit.readLine(in); //header
		
		if(log.isDebugEnabled()) log.debug("BGR ERROR = " + brgerrormessage);
		
		String bgr = "\033[5i" + "BGR2ERROR" + "\033[4i";

		byte [] tbuf=bgr.getBytes();
		response.write(tbuf, 0, tbuf.length);
	
		byte [] errormessage = brgerrormessage.getBytes();
		response.write(errormessage, 0, errormessage.length);
		response.write("\n".getBytes(), 0, "\n".length());
		
		byte [] commandBGR = command.getBytes();
		response.write(commandBGR, 0, commandBGR.length);
		//response.writeAll(tbuf,0,tbuf.length);
	}
	

}
