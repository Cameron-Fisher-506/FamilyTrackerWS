package za.co.familytrackerws.obj;

import za.co.familytrackerws.utils.DTUtils;

public class Device
{
	private String name;
	private String imei;
	private String createdTime;
	private Coordinate coordinate;
	private Health health;
	
	public Device(String name, String imei, Coordinate coordinate, Health health) {
		this.name = name;
		this.imei = imei;
		this.createdTime = DTUtils.getCurrentDateTime();
		this.coordinate = coordinate;
		this.health = health;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public Health getHealth() {
		return health;
	}
	public void setHealth(Health health) {
		this.health = health;
	}
	
	
}
