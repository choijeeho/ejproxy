package com.eastarjet.crs.proxy.skyport.bean;

import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public class WatchPerson 
{
	String firstName;
	String middleName;
	String lastName;
	String docmentType;
	String documentNo;
	String dayOfBirth;
	String gender;
	String nationality;
	static  Logger log = Toolkit.getLogger(WatchPerson.class);
	
	public WatchPerson(String fn,String mname,String lastName,String docType,String docNo,String dob,String gender,String nation)
	{
		this.firstName=fn;
		this.middleName=mname;
		this.lastName=lastName;
		this.docmentType=docType;
		this.documentNo=docNo;
		this.dayOfBirth=dob;
		this.nationality=nation;
	}
	
	public boolean isEquals(OpPassenger pax)
	{
		String fname= pax.getFirstName();
		String mname= pax.getMiddleName();
		String lname=pax.getLastName();
		String dob=pax.getDayOFBirth();
		return isEquals(fname,lname);
	}
	
	
	public boolean isEquals(String fname,String lname)
	{
 
		//log.debug("fn"+fname+",lname="+lname+"/fnam="+firstName+",wlname="+lastName);
		boolean b1=true,b2=true;
		
		//f,l compare
			if(fname==null || !fname.equals(firstName)) b1=false;
			if(b1 &&(lname==null || !lname.equals(lastName))) b1= false;
			
			if(fname==null || !fname.equals(lastName)) b2=false;
			if(b2 &&(lname==null || !lname.equals(firstName))) b2= false;
		//l,f compare
		
		//if(dob==null || !dob.equals(this.dayOfBirth)) return false;
		return b1||b2;
	}	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDocmentType() {
		return docmentType;
	}
	public void setDocmentType(String docmentType) {
		this.docmentType = docmentType;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
 
	public String getDayOfBirth() {
		return dayOfBirth;
	}
	public void setDayOfBirth(String dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
}
