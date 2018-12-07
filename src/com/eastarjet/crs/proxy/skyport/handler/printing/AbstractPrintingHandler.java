package com.eastarjet.crs.proxy.skyport.handler.printing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.JSONObject;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
import com.navitaire.schemas.WrapOfLogonResponse;
import com.navitaire.schemas.ClientServices.Common.SessionManagerClient.SessionManagerClientSoapProxy;
import com.navitaire.schemas.Common.Enumerations.ChannelType;
import com.navitaire.schemas.Common.Enumerations.SystemType;
import com.navitaire.schemas.Messages.Session.Request.LogonRequest;

public abstract class AbstractPrintingHandler extends AbstractHandler {
	static Logger log = Toolkit.getLogger(AbstractPrintingHandler.class);
	protected WrapOfLogonResponse logOnWebService(SessionManagerClientSoapProxy smcsp) throws Exception
	{	
		WrapOfLogonResponse wraplogonResponse = null;
		//if(log.isDebugEnabled())log.debug("wraplogonResponse.getException()222 : " + wraplogonResponse.getException());
			Properties pmconf = null;
			String domain =(String)getAttribute("domain");
			String agent =(String)getAttribute("agent");//"ejonepass";
			String password = (String)getAttribute("password");
			
 
			
			if(domain  != null && agent != null && password != null)
			{
				if(log.isDebugEnabled())log.debug("logon");
				LogonRequest logonRequest = new LogonRequest();
				logonRequest.setDomainCode(domain);
				logonRequest.setAgentName(agent);
				logonRequest.setPassword(password);
				logonRequest.setSystemType(SystemType.WebRez);
				logonRequest.setChannelType(ChannelType.Web);

				wraplogonResponse = smcsp.logon0(logonRequest); 
			}
				
		return wraplogonResponse;
	}
	
	protected String logonFromMiddleware() throws Exception {

		String domain = (String)getAttribute("domain");
		String agent = (String)getAttribute("agent");
		String password = (String)getAttribute("password");
		JSONObject responsejson = null;
		
	    JSONObject requestData = new JSONObject();
	    JSONObject requestSubData = new JSONObject();
	    requestData.put("ContractVersion", 0);
	    requestSubData.put("DomainCode", domain);
	    requestSubData.put("AgentName", agent);
	    requestSubData.put("Password", password);
	    requestSubData.put("LocationCode", "");
	    requestSubData.put("RoleCode", "");
	    requestSubData.put("TerminalInfo", "");
	    requestData.put("logonRequestData", requestSubData);

	    String query = (String)getAttribute("soap.sessionManager");

	    responsejson = sendPost(query, requestData);
	    
	    return responsejson.getString("Signature");
	}
	
	protected JSONObject sendPost(String _url, JSONObject _param) throws Exception {

		JSONObject responsejson = null;
        URL url = new URL(_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");

        if(log.isDebugEnabled())log.debug("params : " + _param.toString());  
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(_param.toString());
        wr.flush();
     
        //display what returns the POST request

        StringBuilder sb = new StringBuilder();  
        int HttpResult = conn.getResponseCode(); 
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }
            br.close();                    
        } else {
            System.out.println(conn.getResponseMessage());  
        }  
        
        if(log.isDebugEnabled())log.debug("response : " + sb.toString());  
        responsejson = new JSONObject(sb.toString());
        return responsejson;
	}
	
	

}
