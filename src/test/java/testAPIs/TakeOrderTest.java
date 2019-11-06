package testAPIs;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import jsonData.Payload;
import utils.Utils;

public class TakeOrderTest {
	
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
	 * Take order test with valid order id
	 */
	@Test
	public void takeOrderValidIdTest() {
		logger.info("*** Test case: Take order with a valid id ***");
		res = PO.putOrder(id,"take");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 200);
		assertEquals(res.then().extract().path("status"), PO.prop.getProperty("status_ongoing"));
	}
	
	/*
	 * Take order test with on going status (Negative scenario)
	 */
	@Test(dependsOnMethods="takeOrderValidIdTest")
	public void takeOrderWithOngoingStatusTest() {
		logger.info("*** Test case: Take order with status: ONGOING ***");
		res = PO.putOrder(id,"take");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 422);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_422_statusNotOnAssigning"));
	}
	
	/*
	 * Take order test with invalid order id (Negative scenario)
	 */
	@Test
	public void takeOrderInvalidIdTest() {
		logger.info("*** Test case: Take order with invalid order id ***");
		res = PO.putOrder(99999,"take");
		res.prettyPrint();
		assertEquals(res.getStatusCode(), 404);
		assertEquals(res.then().extract().path("message"), PO.prop.getProperty("error_404_orderNotFound"));
	}	

}