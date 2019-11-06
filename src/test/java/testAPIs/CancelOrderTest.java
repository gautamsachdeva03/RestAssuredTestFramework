package testAPIs;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import jsonData.Payload;
import utils.Utils;

public class CancelOrderTest {
	
	Utils PO;
	Response res;
	Payload payload;
	int id;
	final static Logger logger = Logger.getLogger(GetOrderDetailsTest.class);
	
	@BeforeClass
	public void init() {
		PO = new Utils();
	}
	
	@BeforeMethod
	public void setupNewOrder() {
		//Place a order before proceeding to the tests
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		res = PO.postOrder(payload);
		id = res.then().extract().path("id");
	}
	
	/*
	 * Cancel order test with status Assigning
	 */
	@Test
	public void cancelOrderWithAssigningStatusTest() {
		logger.info("*** Test case: Cancel order with status: Assigning ***");
		res = PO.putOrder(id, "cancel");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 200);
		assertEquals(res.then().extract().path("status"), PO.prop.getProperty("status_canceled"));
	}
	
	/*
	 * Cancel order test with status Ongoing
	 */
	@Test
	public void cancelOrderWithOngoingStatusTest() {
		logger.info("*** Test case: Cancel order with status: Ongoing ***");
		PO.putOrder(id, "take");
		res = PO.putOrder(id, "cancel");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 200);
		assertEquals(res.then().extract().path("status"), PO.prop.getProperty("status_canceled"));
	}

	/*
	 * Cancel order test with status already completed
	 */
	@Test
	public void cancelOrderWithCompletedStatusTest() {
		logger.info("*** Test case: Cancel order with status: Completed ***");
		PO.putOrder(id, "take");
		PO.putOrder(id, "complete");
		res = PO.putOrder(id, "cancel");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 422);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_422_statusAlreadyCompleted"));
	}
	
	/*
	 * Cancel order test with invalid order id (Negative scenario)
	 */
	@Test
	public void cancelOrderInvalidIdTest() {
		logger.info("*** Test case: Cancel order with invalid order id ***");
		res = PO.putOrder(9999, "cancel");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 404);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_404_orderNotFound"));
	}	

}