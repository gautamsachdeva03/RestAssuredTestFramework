package testAPIs;

import static org.testng.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;	

import io.restassured.response.Response;
import jsonData.Payload;
import utils.Utils;

public class PlaceOrderTest {
	
	Utils PO;
	Response res;
	Payload payload;
	final static Logger logger = Logger.getLogger(PlaceOrderTest.class);
	
	@BeforeClass
	public void init() {
		PO = new Utils();
	}
	
	@BeforeMethod
	public void setup() {
		Utils.lateNightFlag = false;
	}
	
	/*	
	 * Posting a new order with single stop (Negative scenario)
	 */	
	@Test
	public void postOrderWithOneStopTest() {
		logger.info("*** Test Case: Post order test with one stop should show an error ***");
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 400);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_400_errorInStopsField"));		
	}
	
	/*	
	 * Posting a new order with two stops
	 */	
	@Test
	public void postOrderWithTwoStopsTest() {
		logger.info("*** Test Case: Post order test with two stops ***");
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.395394, 114.182446);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201); 
		
		int x = res.then().extract().path("id");
		res = PO.getOrder(x);
		
		//Status verification
		assertEquals(res.then().extract().path("status"), PO.prop.getProperty("status_assigning"));
		
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = res.then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}

	/*	
	 * Posting a new order with three stops
	 */	
	@Test
	public void postOrderWithThreeStopsTest() {
		logger.info("*** Test Case: Post order test with three stops ***");
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		payload.addStops(22.385669, 114.186962);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201); 
		
		int x = res.then().extract().path("id");
		assertEquals(PO.getOrder(x).then().extract().path("status"), PO.prop.getProperty("status_assigning"));
				
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = PO.getOrder(x).then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}

	/*	
	 * Posting a new order with four stops
	 */	
	@Test
	public void postOrderWithFourStopsTest() {
		logger.info("*** Test Case: Post order test with four stops ***");
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		payload.addStops(22.385669, 114.186962);
		payload.addStops(22.394649, 114.193952);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201); 
		
		int x = res.then().extract().path("id");
		assertEquals(PO.getOrder(x).then().extract().path("status"), PO.prop.getProperty("status_assigning"));
		
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = PO.getOrder(x).then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}
	
	/*	
	 * Posting a new order with future date without late hours
	 */	
	@Test
	public void postOrderWithFutureDateTwoStopsTest() {
		logger.info("*** Test Case: Post order test with future date but wihout late hours ***");
		payload = new Payload(PO.setOrderTime(ZonedDateTime.now().getYear()+1, 20));
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201);
		
		int x = res.then().extract().path("id");
		assertEquals(PO.getOrder(x).then().extract().path("status"), PO.prop.getProperty("status_assigning"));
		
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = PO.getOrder(x).then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}

	/*	
	 * Posting a new order with future date with late hours
	 */	
	@Test
	public void postOrderWithFutureDateThreeStopsTest() {
		logger.info("*** Test Case: Post order test with future date wih late hours ***");
		payload = new Payload(PO.setOrderTime(ZonedDateTime.now().getYear()+1, 23));
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		payload.addStops(22.385669, 114.186962);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201);
		
		int x = res.then().extract().path("id");
		assertEquals(PO.getOrder(x).then().extract().path("status"), PO.prop.getProperty("status_assigning"));
		
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = PO.getOrder(x).then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}
	
	
	/*	
	 * Posting a new order with current year with late hours
	 */	
	@Test
	public void postOrderWithFutureDateFourStopsTest() {
		logger.info("*** Test Case: Post order test with same date but wih late hours ***");
		payload = new Payload(PO.setOrderTime(ZonedDateTime.now().getYear()+1, 23));
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		payload.addStops(22.385669, 114.186962);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 201);
		int x = res.then().extract().path("id");
		assertEquals(PO.getOrder(x).then().extract().path("status"), PO.prop.getProperty("status_assigning"));
		
		//Distance according to stops 
		int numberOfStops = payload.getStops().size();
		List<Integer> distance = PO.getOrder(x).then().extract().path("drivingDistancesInMeters");
		logger.info("Number of stops: "+numberOfStops);
		
		//No. of distances calculated should be less than number of stops 
		assertEquals(distance.size(), numberOfStops-1);
		
		//Currency should be HKD
		assertEquals(res.then().extract().path("fare.currency"), PO.prop.getProperty("currencyUnit"));
		
		//Calculate fare
		float expectedFare = PO.calculateFare(distance);
		logger.info("Expected fare according to distance: "+expectedFare);
		
		String fareFromResponse = res.then().extract().path("fare.amount");
		float actualfare = Float.valueOf(fareFromResponse);
		logger.info("Actual fare in response: "+actualfare);
		
		//Validation for actual and expected calculated fare
	    assertEquals(actualfare, expectedFare);
	}

	/*	
	 * Posting a new order with past date (Negative scenario
	 */	
	@Test
	public void postOrderWithPastDateTest() {
		logger.info("*** Test Case: Post order test with past date ***");
		payload = new Payload(PO.setOrderTime(ZonedDateTime.now().getYear()-1, 23));
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		payload.addStops(22.385669, 114.186962);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 400);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_400_pastOrder"));	
	}
	
	/*	
	 * Posting a new order with invalid lat lng details
	 */	
	@Test
	public void postOrderWithInvalidLatLngTest() {
		logger.info("*** Test Case: Post order test with invalid lat lng details ***");
		payload = new Payload();
		payload.addStops(1.2, 0);
		payload.addStops(122.375384, 14.182446);
		payload.addStops(0, 0.186962);
		
		res = PO.postOrder(payload);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 400);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_400_errorInLatLngField"));	
	}
	
}