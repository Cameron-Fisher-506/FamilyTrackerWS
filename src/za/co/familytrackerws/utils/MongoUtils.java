package za.co.familytrackerws.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoUtils 
{
	public static MongoClient mongoClient;
	public static MongoDatabase mongoDatabase;
	
	public static void init()
	{
		mongoClient = MongoClients.create(StringUtils.DB_URL);
		mongoDatabase = mongoClient.getDatabase(ConstantUtils.DB_NAME);
	}

}
