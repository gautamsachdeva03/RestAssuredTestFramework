package testAPIs;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import jsonData.Payload;
import utils.Utils;

public class CompleteOrderTest {
	
	Utils PO;
	Response res;
	Payload payload;
	int id;
	final static Logger logger = Logger.getLogger(GetOrderDetailsTest.class);
	
	@BeforeClass
	public void init() {
		PO = new Utils();
		
		//Place a order before proceeding to the tests
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		res = PO.postOrder(payload);
		id = res.then().extract().path("id");
	}
	
	/*
	 * Complete order test without taking the order (Negative Scenario)
	 */
	@Test(priority=1)
	public void completeOrderWithoutTakingTest() {
		logger.info("*** Test case: Complete order with status: Assigning ***");
		res = PO.putOrder(id,"complete");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 422);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_422_statusNotOnGoing"));
		
		//Change status to Ongoing for further tests
		res = PO.putOrder(id, "take");
		assertEquals(res.getStatusCode(), 200);
	}
	
	/*
	 * Complete order test with valid order id
	 */
	@Test(dependsOnMethods="completeOrderWithoutTakingTest")
	public void completeOrderValidIdTest() {
		logger.info("*** Test case: Complete order with valid order id and valid status ***");
		res = PO.putOrder(id,"complete");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 200);
		assertEquals(res.then().extract().path("status"), PO.prop.getProperty("status_completed"));
	}
	
	/*
	 * Complete order test with already completed order (Negative scenario)
	 */
	@Test(dependsOnMethods="completeOrderValidIdTest")
	public void completeOrderWithCompletedStatusTest() {
		logger.info("*** Test case: Complete order test with status: Completed ***");
		res = PO.putOrder(id,"complete");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 422);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_422_statusNotOnGoing"));
	}
	
	/*
	 * Complete order test with invalid order id (Negative scenario)
	 */
	@Test
	public void completeOrderInvalidIdTest() {
		logger.info("*** Test case: Complete order test with invalid id ***");
		res = PO.putOrder(9999,"complete");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 404);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_404_orderNotFound"));
	}

}