package za.co.familytrackerws.obj;

import za.co.familytrackerws.utils.DTUtils;

public class Coordinate 
{
	private String latitude;
	private String longitude;
	private String createdTime;
	
	public Coordinate(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.createdTime = DTUtils.getCurrentDateTime();
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	
}
