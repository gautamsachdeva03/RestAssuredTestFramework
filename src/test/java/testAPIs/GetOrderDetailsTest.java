package testAPIs;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import jsonData.Payload;
import utils.Utils;

public class GetOrderDetailsTest {

	Utils PO;
	Response res;
	Payload payload;
	int id;
	final static Logger logger = Logger.getLogger(GetOrderDetailsTest.class);

	@BeforeClass
	public void init() {
		PO = new Utils();
	}

	/*
	 * Get order details test with valid order id and validate the status
	 */
	@Test
	public void getOrderDetailsWithAssigningStatusTest() {
		logger.info("*** Test case: Get order details with a valid id ***");
		//Place a order before proceeding to the tests
		payload = new Payload();
		payload.addStops(22.344674, 114.124651);
		payload.addStops(22.375384, 114.182446);
		res = PO.postOrder(payload);
		id = res.then().extract().path("id");
		logger.info("Get request sent for order ID: "+id);
		res = PO.getOrder(id);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 200);
		assertEquals(res.then().extract().path("status"), "ASSIGNING");
	}

	/*
	 * Get order details test with invalid order id
	 */
	@Test
	public void getOrderDetailsWithInvalidIdTest() {
		logger.info("*** Test case: Get order details with invalid id ***");
		res = PO.getOrder(9999);
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 404);
		assertEquals(res.then().extract().path("message"), "ORDER_NOT_FOUND");
	}
}