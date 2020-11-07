package za.co.familytrackerws.dao;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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
}
