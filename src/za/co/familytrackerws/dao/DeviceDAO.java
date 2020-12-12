package za.co.familytrackerws.dao;

import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import za.co.familytrackerws.utils.DTUtils;
import za.co.familytrackerws.utils.MongoUtils;

public class DeviceDAO 
{
	private static final String MONGO_DB_COLLECTION_NAME = "device";
	
	public static JSONObject create(JSONObject jsonObject)
	{
		JSONObject toReturn = null;
		int responseCode = 0;
		String title = null;
		String message = null;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		
		if(jsonObject != null)
		{
			toReturn = new JSONObject();
			
			String code = jsonObject.has("code") ? jsonObject.getString("code") : null;
			String name = jsonObject.has("name") ? jsonObject.getString("name") : null;
			
			if(code != null)
			{
				Document result = deviceCollection.find(Filters.eq("code", code)).first();
				if(result != null)
				{
					Bson document = Document.parse(jsonObject.toString());
					Bson updateOperation = new Document("$set", document);
					
					Document updatedDocument = deviceCollection.findOneAndUpdate(Filters.eq("code", code), updateOperation);
					if(updatedDocument != null)
					{					
						title = "Updated " + code + " !";
						message = "Your device details has been successfully updated!";
						
						toReturn.put("code", code);
						toReturn.put("title", title);
						toReturn.put("message", message);
					}else
					{
						responseCode = -1;
						title = "Failed to update " + code + " !";
						message = "Your device details failed to update!";
						
						toReturn.put("code", responseCode);
						toReturn.put("title", title);
						toReturn.put("message", message);
					}
				}else
				{
					//create new
					Document device = new Document(Document.parse(jsonObject.toString()));
					deviceCollection.insertOne(device);
					
					if(name != null)
					{
						title = "Create " + name + "device.";
						message = name + " device has been successfully created!";
					}else
					{
						title = "Created " + code;
						message = code + " has been successfully created!";
					}
					
					toReturn.put("code", responseCode);
					toReturn.put("title", title);
					toReturn.put("myCode", code);
					toReturn.put("message", message);
				}
			}else
			{
				responseCode = -1;
				title = "Code not found.";
				message = "Code must be passed in order to update or create a new device!";
				
				toReturn.put("code", responseCode);
				toReturn.put("title", title);
				toReturn.put("message", message);
			}
			
		}
		
		return toReturn;
	}
	
	public static JSONObject getAllLinkedDevices(String code)
	{
		JSONObject toReturn = null;
		JSONArray jsonArray = new JSONArray();
		
		int responseCode = 0;
		String title = null;
		String message = null;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		
		if(deviceCollection != null)
		{
			if(code != null)
			{
				Document device = deviceCollection.find(Filters.eq("code", code)).first();
				if(device != null && device.containsKey("monitors"))
				{
					List<Document> monitors = (List<Document>) device.get("monitors");
					
					if(monitors != null && monitors.size() > 0)
					{
						for(int i = 0; i < monitors.size(); i++)
						{
							Document monitor = monitors.get(i);
							if(monitor != null && monitor.containsKey("code"))
							{
								JSONObject jsonObject = getDeviceCoordinateHealth(monitor.getString("code"));
								if(jsonObject != null)
								{
									jsonObject.put("name", monitor.containsKey("name") ? monitor.getString("name") : null);
									jsonArray.put(jsonObject);
								}
								
							}
						}
						
						responseCode = 0;
						title = "Linked devices found!";
						message = "Linked devices found!";
						
						toReturn = new JSONObject();
						toReturn.put("code", responseCode);
						toReturn.put("title", title);
						toReturn.put("message", message);
						toReturn.put("devices", jsonArray);
					}else
					{
						responseCode = 0;
						title = "You dont have any devices linked to track!";
						message = "You dont have any devices linked to track!";
						
						toReturn = new JSONObject();
						toReturn.put("code", responseCode);
						toReturn.put("title", title);
						toReturn.put("message", message);
						toReturn.put("devices", jsonArray);
					}
				}else
				{
					responseCode = 0;
					title = "You dont have any devices linked to track!";
					message = "You dont have any devices linked to track!";
					
					toReturn = new JSONObject();
					toReturn.put("code", responseCode);
					toReturn.put("title", title);
					toReturn.put("message", message);
					toReturn.put("devices", jsonArray);
				}
			}else
			{
				responseCode = -1;
				title = "Code not found.";
				message = "Code must be passed in order to update or create a new device!";
				
				toReturn = new JSONObject();
				toReturn.put("code", responseCode);
				toReturn.put("title", title);
				toReturn.put("message", message);
			}
		}else
		{
			responseCode = -1;
			title = "Database Error!";
			message = "Please contact the developer!";
			
			toReturn = new JSONObject();
			toReturn.put("code", responseCode);
			toReturn.put("title", title);
			toReturn.put("message", message);
		}
		
		
		return toReturn;
	}
	
	public static JSONObject getDeviceCoordinateHealth(String code)
	{
		JSONObject toReturn = null;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		
		if(code != null)
		{
			Document device = deviceCollection.find(Filters.eq("code", code)).first();
			if(device != null)
			{
				toReturn = new JSONObject();
				toReturn.put("code", code);
				toReturn.put("name", device.containsKey("name") ? device.getString("name") : null);
				toReturn.put("createdTime", device.containsKey("createdTime") ? device.getString("createdTime") : null);
				
				if(device.containsKey("coordinate"))
				{
					Document coordinate = (Document) device.get("coordinate");
					if(coordinate != null)
					{
						toReturn.put("coordinate", new JSONObject(coordinate.toJson()));
					}
				}
				
				if(device.containsKey("health"))
				{
					Document health = (Document)device.get("health");
					if(health != null)
					{
						toReturn.put("health", new JSONObject(health.toJson()));
					}
				}
				
			}
		}
		
		return toReturn;
	}
	
	public static boolean updateDeviceCoordinateHealth(String body)
	{
		boolean toReturn = false;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			JSONObject jsonObject = new JSONObject(body);
			if(jsonObject != null && jsonObject.has("code"))
			{
				String code = jsonObject.getString("code");
				if(code != null)
				{
					if(jsonObject.has("coordinate"))
					{
						JSONObject coordinate = jsonObject.getJSONObject("coordinate");
						if(coordinate != null)
						{	
							Document document = deviceCollection.findOneAndUpdate(Filters.eq("code", code), Updates.combine(
									Updates.set("coordinate.latitude", coordinate.has("latitude") ? coordinate.getString("latitude") : null),
									Updates.set("coordinate.longitude", coordinate.has("longitude") ? coordinate.getString("longitude") : null),
									Updates.set("coordinate.speed", coordinate.has("speed") ? coordinate.getString("speed") : null),
									Updates.set("coordinate.accuracy", coordinate.has("accuracy") ? coordinate.getString("accuracy") : null),
									Updates.set("coordinate.bearing", coordinate.has("bearing") ? coordinate.getString("bearing") : null),
									Updates.set("coordinate.createdTime", DTUtils.getCurrentDateTime())
									));
							
							if(document != null)
							{
								toReturn = true;
							}else
							{
								toReturn = false;
							}
						}
					}
					
					if(jsonObject.has("health"))
					{
						JSONObject health = jsonObject.getJSONObject("health");
						if(health != null && health.has("batteryLife") && health.has("signalStrength"))
						{
							Document document = deviceCollection.findOneAndUpdate(Filters.eq("code", code), Updates.combine(
									Updates.set("health.batteryLife", health.has("batteryLife") ? health.getString("batteryLife") : null),
									Updates.set("health.signalStrength", health.has("signalStrength") ? health.getString("signalStrength") : null),
									Updates.set("health.isRoaming", health.has("isRoaming") ? health.getBoolean("isRoaming") : null),
									Updates.set("health.createdTime", DTUtils.getCurrentDateTime())
									));
							
							if(document != null)
							{
								toReturn = true;
							}else
							{
								toReturn = false;
							}
						}
					}
				}
			}
		}
		return toReturn;
	}
	
	public static JSONObject linkDevice(String body)
	{
		JSONObject toReturn = new JSONObject();
		int responseCode = 0;
		String message = "";
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			if(body != null)
			{
				JSONObject jsonObject = new JSONObject(body);
				if(jsonObject != null && jsonObject.has("code") && jsonObject.has("codeToLink") && jsonObject.has("name"))
				{
					String code = jsonObject.getString("code");
					String name = jsonObject.getString("name");
					String codeToLink = jsonObject.getString("codeToLink");
					if(code != null)
					{
						Document device = deviceCollection.find(Filters.eq("code", code)).first();
						if(device != null && device.containsKey("monitors"))
						{
							
							List<Document> monitors = (List<Document>) device.get("monitors");
							
							boolean isExists = false;
							if(monitors != null && monitors.size() > 0)
							{
								for(int i = 0; i < monitors.size(); i++)
								{
									Document monitor = monitors.get(i);
									if(monitor != null && monitor.containsKey("code"))
									{
										if(monitor.getString("code").equals(codeToLink))
										{
											isExists = true;
											responseCode = 1;
											message = codeToLink + " is already linked!";
										}
									}
								}
							}
							
							if(!isExists)
							{						
								Document toPush = new Document();
								toPush.put("code", codeToLink);
								toPush.put("name", name);
								
								Document document = deviceCollection.findOneAndUpdate(Filters.eq("code", code), Updates.push("monitors", toPush));
								if(document != null)
								{
									responseCode = 0;
									message = "You have successfully linked " + codeToLink + " !";
								}
							}
						}else
						{
							Document toPush = new Document();
							toPush.put("code", codeToLink);
							toPush.put("name", name);
							
							Document document = deviceCollection.findOneAndUpdate(Filters.eq("code", code), Updates.push("monitors", toPush));
							if(document != null)
							{
								responseCode = 0;
								message = "You have successfully linked " + codeToLink + " !";
							}
						}
						
					}
					
				}else
				{
					responseCode = -1;
					message = "Your device Code, partner  and name is required to start monitoring!";
				}
			}
		}
		
		toReturn.put("code", responseCode);
		toReturn.put("message", message);
		
		return toReturn;
	}
	
	public static boolean unlinkDevice(String body)
	{
		boolean toReturn = false;
		
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			if(body != null)
			{
				JSONObject jsonObject = new JSONObject(body);
				if(jsonObject != null && jsonObject.has("code") && jsonObject.has("codeToUnlink"))
				{
					String code = jsonObject.getString("code");
					String codeToUnlink = jsonObject.getString("codeToUnlink");
					if(code != null)
					{
						Document device = deviceCollection.find(Filters.eq("code", code)).first();
						if(device != null && device.containsKey("monitors"))
						{
							
							List<Document> monitors = (List<Document>) device.get("monitors");
							
							if(monitors != null && monitors.size() > 0)
							{
								for(int i = 0; i < monitors.size(); i++)
								{
									Document monitor = monitors.get(i);
									if(monitor != null && monitor.containsKey("code"))
									{
										if(monitor.getString("code").equals(codeToUnlink))
										{
											Document toRemove = new Document();
											toRemove.put("code", codeToUnlink);
											
											Document document = deviceCollection.findOneAndUpdate(Filters.eq("code", code), Updates.pull("monitors", toRemove));
											if(document != null)
											{
												return true;
											}
										}
									}
								}

							}
						}
						
					}
					
				}
			}
		}
		
		return toReturn;
	}
	
	public static boolean isCodeExists(String code)
	{
		boolean toReturn = false;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			Document device = deviceCollection.find(Filters.eq("code", code)).first();
			if(device != null)
			{
				toReturn = true;
			}
		}
		
		return toReturn;
	}
}
