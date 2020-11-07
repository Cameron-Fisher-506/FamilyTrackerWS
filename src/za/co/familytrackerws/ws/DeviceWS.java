package za.co.familytrackerws.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import za.co.familytrackerws.dao.DeviceDAO;

@Path("device")
public class DeviceWS 
{
	@Path("create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String body)
	{
		JSONObject toReturn = null;
		
		JSONObject jsonObject = new JSONObject(body);
		if(jsonObject != null)
		{
			toReturn = DeviceDAO.create(jsonObject);
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("getDeviceCoordinateHealth/{imei}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceCoordinateHealth(@PathParam("imei") String imei)
	{
		JSONObject toReturn = null;
		
		toReturn = DeviceDAO.getDeviceCoordinateHealth(imei);
		if(toReturn == null)
		{
			return Response.status(404).entity(-1).build();
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("updateDeviceCoordinateHealth")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCoordinateHealth(String body)
	{
		JSONObject toReturn = new JSONObject();
		
		if(DeviceDAO.updateDeviceCoordinateHealth(body))
		{
			toReturn.put("code", 0);
		}else {
			toReturn.put("code", -1);
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("linkDevice")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response linkDevice(String body)
	{
		JSONObject toReturn = new JSONObject();
		
		boolean isLinked = DeviceDAO.linkDevice(body);
		if(isLinked)
		{
			toReturn.put("code", 0);
		}else {
			toReturn.put("code", -1);
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("unlinkDevice")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unlinkDevice(String body)
	{
		JSONObject toReturn = new JSONObject();
		
		boolean isLinked = DeviceDAO.unlinkDevice(body);
		if(isLinked)
		{
			toReturn.put("code", 0);
		}else {
			toReturn.put("code", -1);
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
}
