package za.co.familytrackerws.utils;

import java.util.Random;

public class GeneralUtils {
	public static String generateCode()
	{
		StringBuilder toReturn = new StringBuilder();
		
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		Random rand = new Random();
		
		for(int i = 0; i < 10; i++)
		{
			toReturn.append(characters.charAt(rand.nextInt(characters.length())));
		}
		
		return toReturn.toString();
	}
}
