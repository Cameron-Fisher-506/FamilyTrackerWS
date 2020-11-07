package za.co.familytrackerws.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
}
