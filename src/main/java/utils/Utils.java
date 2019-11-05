package utils;

import static io.restassured.RestAssured.given;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jsonData.Payload;

public class Utils {

	Response res;
	public Properties prop;
	FileReader propFile;
	public static boolean lateNightFlag = false;
	
	public Utils() {		
		try {
			propFile = new FileReader(System.getProperty("user.dir")+"//src//main//java//common//common.properties");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		prop = new Properties();
		try {
			prop.load(propFile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		RestAssured.baseURI =  prop.getProperty("baseURI","http://localhost"); 
		RestAssured.port =  Integer.parseInt(prop.getProperty("port","51544")); 
		RestAssured.basePath =  prop.getProperty("basePath","/v1/orders"); 
	}
	
	public Response getOrder(int orderId) {
		res = given().contentType(ContentType.JSON).get("/"+orderId);
		return res;
	}
	
	public Response postOrder(Payload json) {
		res = given().contentType(ContentType.JSON).body(json).post();
		return res;
	}
	
	public Response putOrder(int orderId, String orderStatus) {
		res = given().contentType(ContentType.JSON).put("/"+orderId+"/"+orderStatus);
		return res;
	}
	
	public float calculateFare(List<Integer> distance) {
		float totalDistanceMeters = 0;
	    float totalFare = 0;
	    float extraFare = 0;
		float baseDistanceMeters = Integer.parseInt(prop.getProperty("baseDistanceMeters","2000"));  
	    float metersDividend = Integer.parseInt(prop.getProperty("metersDividend","200"));
	    float baseFare;
	    float priceMultiplier;
	    
	    if (lateNightFlag) {
		    baseFare = Integer.parseInt(prop.getProperty("baseFareLateNight","30"));
		    priceMultiplier = Integer.parseInt(prop.getProperty("priceMultiplierLateNight","8"));	    	
	    }
	    else {
		    baseFare = Integer.parseInt(prop.getProperty("baseFare","20"));
		    priceMultiplier = Integer.parseInt(prop.getProperty("priceMultiplier","5"));
	    }
		
		for (int i: distance) {
	    	totalDistanceMeters += i;
	    }	    
	    
	    if (totalDistanceMeters > 0 ) {
	    	if(totalDistanceMeters >= baseDistanceMeters) {
	    		float remainingDistance = totalDistanceMeters - baseDistanceMeters;
		    	extraFare = (remainingDistance/metersDividend) * priceMultiplier;
	    	}
	    }
	    totalFare = baseFare + extraFare;
	    return totalFare;
	}
	
	public String setOrderTime(int year, int hour) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, hour);
		
        if (hour >= 22 || hour <= 5) {
			lateNightFlag = true;
		}
        
        //returning a date object with specified year and time. 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");
        return formatter.format(cal.getTime());
	}

}