package jsonData;

import java.util.ArrayList;
import java.util.List;

public class Payload {
	
	public String orderAt;
	public List<Stops> stops;

	public Payload() {
		this.stops = new ArrayList<Stops>();
	}
	
	public Payload(String orderTime) {
		this.orderAt = orderTime;
		this.stops = new ArrayList<Stops>();
	}

	public String getOrderAt() {
		return orderAt;
	}

	public void setOrderAt(String orderAt) {
		this.orderAt = orderAt;
	}
	
	public List<Stops> getStops() {
		return stops;
	}

	public void setStops(List<Stops> stops) {
		this.stops = stops;
	}
	
	public void addStops(double lat, double lng) {
		stops.add(new Stops(lat,lng));
	}

}
