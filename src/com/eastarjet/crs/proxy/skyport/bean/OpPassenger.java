package com.eastarjet.crs.proxy.skyport.bean;

/**
 * 
 * @author clouddrd
 *
 */
public class OpPassenger 
{
	Passenger passenger;
	private String paxNo;
	private String title;
	private String firstName;
	private String lastName;
	private String middleName;
	private String seqNo;
	private String checkinStatus;
	private String boardingStatus;
	private String seat;
	private String pnr;
	private String tdate;
	private String bookingStatus;
	private String dayOFBirth;
	
	private boolean isWatchedPerson;
	
	
	public String getPaxNo() {
		return paxNo;
	}


	public void setPaxNo(String paxNo) {
		this.paxNo = paxNo;
	}


	public Passenger getPassenger() {
		return passenger;
	}


	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getSeqNo() {
		return seqNo;
	}


	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}


	public String getCheckinStatus() {
		return checkinStatus;
	}


	public void setCheckinStatus(String checkinStatus) {
		this.checkinStatus = checkinStatus;
	}


	public String getBoardingStatus() {
		return boardingStatus;
	}


	public void setBoardingStatus(String boardingStatus) {
		this.boardingStatus = boardingStatus;
	}


	public String getSeat() {
		return seat;
	}


	public void setSeat(String seat) {
		this.seat = seat;
	}


	public String getPnr() {
		return pnr;
	}


	public void setPnr(String pnr) {
		this.pnr = pnr;
	}


	public String getTdate() {
		return tdate;
	}


	public void setTdate(String tdate) {
		this.tdate = tdate;
	}


	public String getBookingStatus() {
		return bookingStatus;
	}


	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}


	public String getDayOFBirth() {
		return dayOFBirth;
	}


	public void setDayOFBirth(String dayOFBirth) {
		this.dayOFBirth = dayOFBirth;
	}


	public boolean isWatchedPerson(){return isWatchedPerson;}
	public void setWatchedPerson(boolean b){isWatchedPerson=b;}
}

