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
import za.co.familytrackerws.utils.GeneralUtils;

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
			String code = "";
			if(jsonObject.has("code"))
			{
				code = jsonObject.getString("code");
			}else
			{
				code = GeneralUtils.generateCode();
				
				while(DeviceDAO.isCodeExists(code))
				{
					code = GeneralUtils.generateCode();
				}
			}
			
			jsonObject.put("code", code);
			toReturn = DeviceDAO.create(jsonObject);
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("getDeviceCoordinateHealth/{code}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceCoordinateHealth(@PathParam("code") String code)
	{
		JSONObject toReturn = null;
		
		toReturn = DeviceDAO.getDeviceCoordinateHealth(code);
		if(toReturn == null)
		{
			return Response.status(404).entity(-1).build();
		}
		
		return Response.status(200).entity(toReturn.toString()).build();
	}
	
	@Path("getAllLinkedDevices/{code}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllLinkedDevices(@PathParam("code") String code)
	{
		JSONObject toReturn = null;
		int responseCode = 0;
		String title = null;
		String message = null;
		
		toReturn = DeviceDAO.getAllLinkedDevices(code);
		if(toReturn == null)
		{
			responseCode = -1;
			title = "Server Error!";
			message = "Please contact the developer!";
			
			toReturn = new JSONObject();
			toReturn.put("code", responseCode);
			toReturn.put("title", title);
			toReturn.put("message", message);
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
		JSONObject toReturn = DeviceDAO.linkDevice(body);
		
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
