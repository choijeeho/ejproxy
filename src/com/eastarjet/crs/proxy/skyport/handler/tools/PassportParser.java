package com.eastarjet.crs.proxy.skyport.handler.tools;

import java.util.Hashtable;
import java.util.Map;

public class PassportParser 
{
	/*
	Passenger Last Name           : LEE                                         
    Passenger First Name          : HYEONG                                      
    Passenger Middle Name         : KYU                                         
    Date of birth( 01Jan2004 )    : 27DEC1982                                   
    Passenger gender( M/F )       : M                                           
    Passenger nationality         : KR                                          
    Passenger Country of Residence: KR                                          
    Document type code            : P                                           
    Document number               : 392939                                      
    Issuing country code          : KR                                          
    Expiration date( 01Jan2004 )  : 01JAN2015    
 
 
 1)Passenger last name     *:LEE                                                
 2)Passenger first name    *:HYEONG                                             
 3)Passenger middle name    :KYU                                                
 4)Passenger date of birth *:27Dec1982                                          
 5)Passenger gender        *:Male                                               
 6)Passenger nationality   *:KR                                                 
 7)Country of Residence    *:KR                                                 
 8)Document type           *:P                                                  
 9)Document number         *:392939                                             
10)Issuing Country         *:KR
11)Document expiry date    *:01Jan2015      


    10)Issuing Country         *:KR                                                 
11)Document expiry date    *:24Dec2014                                          
                                                                                
Invalid last name.                                                              
Enter 'Y' to save, or line number to change.                                    
Enter valid line number.                                                        
Enter valid line number.y      

	 */
	static String[][] titles=
	{
		{
			"Passenger Last Name           :",
			"Passenger First Name          :",
		},
		{""}
	};
	
	
	
	public static void parse(Map<String,String> map, String prompt)
	{
		//Map<String,String> map= new Hashtable<String,String>();
		String passportNo="";
		String firstName="";
		String middleName="";
		String lastName="";
		String gender="";
		String birthDay="";
		String nationality="";
		String issueCountry="";
		String issueDate="";
		String expireDate="";
		String regidenceCountry="";
		String docType="";
		
		prompt=trimControlChar(prompt);

		prompt=prompt.trim();
		int epos,spos=0,len=29;
		spos=prompt.indexOf(" 1)Passenger last name     *:")+len;
		epos=prompt.indexOf('\r',spos);
		lastName= prompt.substring(spos,epos).trim(); 
		
		spos=prompt.indexOf(" 2)Passenger first name    *:")+len;
		epos=prompt.indexOf('\r',spos);
		firstName = prompt.substring(spos,epos).replace("[0", "").trim();

		spos=prompt.indexOf(" 3)Passenger middle name    :")+len;
		epos=prompt.indexOf('\r',spos);
		middleName = prompt.substring(spos,epos).trim();

		spos=prompt.indexOf(" 4)Passenger date of birth *:")+len;
		epos=prompt.indexOf('\r',spos); int ch='';
		birthDay = prompt.substring(spos,epos).trim();
		
		spos=prompt.indexOf(" 6)Passenger gender        *:")+len;
		epos=prompt.indexOf('\r',spos);
		gender = prompt.substring(spos,epos).trim(); 
		
		spos=prompt.indexOf(" 7)Passenger nationality   *:")+len;
		epos=prompt.indexOf('\r',spos);
		nationality = prompt.substring(spos,epos).trim(); 

		spos=prompt.indexOf(" 8)Country of Residence    *:")+len;
		epos=prompt.indexOf('\r',spos);
		regidenceCountry = prompt.substring(spos,epos).trim(); 
		
		spos=prompt.indexOf(" 9)Document type           *:")+len;
		epos=prompt.indexOf('\r',spos);
		docType = prompt.substring(spos,epos).trim(); 
		
		spos=prompt.indexOf("10)Document number         *:")+len;
		epos=prompt.indexOf('\r',spos);
		passportNo = prompt.substring(spos,epos).trim(); 
		
			
		spos=prompt.indexOf("11)Issuing Country         *:")+len;
		epos=prompt.indexOf('\r',spos);
		issueCountry = prompt.substring(spos,epos).trim(); 

		spos=prompt.indexOf("12)Document expiry date    *:")+len;
		epos=prompt.indexOf('\r',spos);
		expireDate = prompt.substring(spos,epos).trim(); 
		
		map.put("passportNo",passportNo);
		map.put("firstName",firstName);
		map.put("middleName",middleName);
		map.put("lastName",lastName);
		map.put("gender",gender);
		map.put("birthDay",birthDay);						
		map.put("nationality",nationality);
		map.put("issueCountry",issueCountry);
		map.put("issueDate",expireDate);
		map.put("expireDate",expireDate);
		map.put("regidenceCountry",regidenceCountry);
		map.put("type",docType);
		//return map;
	}
	
	static String trimControlChar(String prompt)
	{
		//prompt=prompt.replaceAll("[0m","");
		StringBuffer tbuf=new StringBuffer();
		int state=0;
		for(int i=0;i<prompt.length();i++)
		{
			int ch=prompt.charAt(i);
			if(ch==''){ state++; continue;}
			if(state==1 && ch=='['){ state++; continue;}
			if(state==2 && ch=='0'){ state++; continue;}
			if(state==3 && ch=='m'){ state++; continue;}
			if(state==4 && ch==''){ state=0; continue;}
			tbuf.append((char)ch);
		}
		return tbuf.toString();
	}
}
