package model;


import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

@Entity(value = "UserData", noClassnameStored = true)
public class UserData {
	@Id
	private ObjectId id;
	@Expose
	@Indexed(options = @IndexOptions(unique = true))
	private String instrumentID;
	@Expose
	private String timestamp;
	@Expose
	private double speed;
	@Expose
	private double temp;
	@Expose
	private double lat;
	@Expose
	private double lon;
	@Expose
	private double heading;
	@Expose
	private double windSpeed;
	@Expose
	private double windDirection;

	public UserData() {
		//required empty constructor
	}


	public UserData(ObjectId id, String instrumentID, String timestamp, double speed, double temp, double lat, double lon, double heading, double windSpeed, double windDirection) {
		this.id = id;
		this.instrumentID = instrumentID;
		this.timestamp = timestamp;
		this.speed = speed;
		this.temp = temp;
		this.lat = lat;
		this.lon = lon;
		this.heading = heading;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getInstrumentID() {
		return instrumentID;
	}

	public void setInstrumentID(String instrumentID) {
		this.instrumentID = instrumentID;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public double getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
	}

	//Generated toString() method
	@Override
	public String toString() {
		return "UserData{" +
				"instrumentID='" + instrumentID + '\'' +
				", timestamp='" + timestamp + '\'' +
				", speed=" + speed +
				", temp=" + temp +
				", lat=" + lat +
				", lon=" + lon +
				", heading=" + heading +
				", windSpeed=" + windSpeed +
				", windDirection=" + windDirection +
				'}';
	}
}