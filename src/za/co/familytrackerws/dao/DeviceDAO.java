package za.co.familytrackerws.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
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
		int code = 0;
		String title = null;
		String message = null;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		
		if(jsonObject != null)
		{
			toReturn = new JSONObject();
			
			String imei = jsonObject.has("imei") ? jsonObject.getString("imei") : null;
			String name = jsonObject.has("name") ? jsonObject.getString("name") : null;
			
			if(imei != null)
			{
				Document result = deviceCollection.find(Filters.eq("imei", imei)).first();
				if(result != null)
				{
					Bson document = Document.parse(jsonObject.toString());
					Bson updateOperation = new Document("$set", document);
					
					Document updatedDocument = deviceCollection.findOneAndUpdate(Filters.eq("imei", imei), updateOperation);
					if(updatedDocument == null)
					{				
						if(name != null)
						{
							title = "Updated " + name;
							message = name + " device has been successfully updated!";
						}else
						{
							title = "Updated " + imei;
							message = imei + " has been successfully updated!";
						}
						
						toReturn.put("code", code);
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
						title = "Created " + imei;
						message = imei + " has been successfully created!";
					}
					
					toReturn.put("code", code);
					toReturn.put("title", title);
					toReturn.put("message", message);
				}
			}else
			{
				code = -1;
				title = "IMEI not found.";
				message = "IMEI must be passed in order to update or create a new device!";
				
				toReturn.put("code", code);
				toReturn.put("title", title);
				toReturn.put("message", message);
			}
			
		}
		
		return toReturn;
	}
	
	public static JSONObject getDeviceCoordinateHealth(String imei)
	{
		JSONObject toReturn = null;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		
		if(imei != null)
		{
			Document device = deviceCollection.find(Filters.eq("imei", imei)).first();
			if(device != null)
			{
				toReturn = new JSONObject();
				if(device.containsKey("coordinate"))
				{
					Document coordinate = (Document) device.get("coordinate");
					if(coordinate != null)
					{
						toReturn.put("coordinate", coordinate.toJson());
					}
				}
				
				if(device.containsKey("health"))
				{
					Document health = (Document)device.get("health");
					if(health != null)
					{
						toReturn.put("health", health.toJson());
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
			if(jsonObject != null && jsonObject.has("imei"))
			{
				String imei = jsonObject.getString("imei");
				if(imei != null)
				{
					if(jsonObject.has("coordinate"))
					{
						JSONObject coordinate = jsonObject.getJSONObject("coordinate");
						if(coordinate != null && coordinate.has("latitude") && coordinate.has("longitude"))
						{	
							Document document = deviceCollection.findOneAndUpdate(Filters.eq("imei", imei), Updates.combine(
									Updates.set("coordinate.latitude", coordinate.getString("latitude")),
									Updates.set("coordinate.longitude", coordinate.getString("longitude")),
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
							Document document = deviceCollection.findOneAndUpdate(Filters.eq("imei", imei), Updates.combine(
									Updates.set("health.batteryLife", health.getString("batteryLife")),
									Updates.set("health.signalStrength", health.getString("signalStrength")),
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
	
	public static boolean linkDevice(String body)
	{
		boolean toReturn = false;
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			if(body != null)
			{
				JSONObject jsonObject = new JSONObject(body);
				if(jsonObject != null && jsonObject.has("imei") && jsonObject.has("imeiToLink"))
				{
					String imei = jsonObject.getString("imei");
					if(imei != null)
					{
						Document device = deviceCollection.find(Filters.eq("imei", imei)).first();
						if(device != null && device.containsKey("monitors"))
						{
							
							List<Document> monitors = (List<Document>) device.get("monitors");
							
							if(monitors != null && monitors.size() > 0)
							{
								boolean isExists = false;
								for(int i = 0; i < monitors.size(); i++)
								{
									Document monitor = monitors.get(i);
									if(monitor != null && monitor.containsKey("imei"))
									{
										if(monitor.getString("imei").equals(imei))
										{
											isExists = true;
										}
									}
								}
								
								if(!isExists)
								{						
									Document toPush = new Document();
									toPush.put("imei", jsonObject.getString("imeiToLink"));
									
									Document document = deviceCollection.findOneAndUpdate(Filters.eq("imei", imei), Updates.push("monitors", toPush));
									if(document != null)
									{
										toReturn = true;
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
	
	public static boolean unlinkDevice(String body)
	{
		boolean toReturn = false;
		
		
		MongoCollection<Document> deviceCollection = MongoUtils.mongoDatabase.getCollection(MONGO_DB_COLLECTION_NAME);
		if(deviceCollection != null)
		{
			if(body != null)
			{
				JSONObject jsonObject = new JSONObject(body);
				if(jsonObject != null && jsonObject.has("imei") && jsonObject.has("imeiToUnlink"))
				{
					String imei = jsonObject.getString("imei");
					if(imei != null)
					{
						Document device = deviceCollection.find(Filters.eq("imei", imei)).first();
						if(device != null && device.containsKey("monitors"))
						{
							
							List<Document> monitors = (List<Document>) device.get("monitors");
							
							if(monitors != null && monitors.size() > 0)
							{
								boolean isExists = false;
								for(int i = 0; i < monitors.size(); i++)
								{
									Document monitor = monitors.get(i);
									if(monitor != null && monitor.containsKey("imei"))
									{
										if(monitor.getString("imei").equals(imei))
										{
											isExists = true;
										}
									}
								}
								
								if(!isExists)
								{						
									Document toRemove = new Document();
									toRemove.put("imei", jsonObject.getString("imeiToUnlink"));
									
									Document document = deviceCollection.findOneAndUpdate(Filters.eq("imei", imei), Updates.pull("monitors", toRemove));
									if(document != null)
									{
										toReturn = true;
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
}
