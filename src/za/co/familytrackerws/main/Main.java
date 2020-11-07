package za.co.familytrackerws.main;

import javax.servlet.http.HttpServlet;

import za.co.familytrackerws.utils.MongoUtils;

public class Main extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public void init()
	{
		MongoUtils.init();
	}

}
