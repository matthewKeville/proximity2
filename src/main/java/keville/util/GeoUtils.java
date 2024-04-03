package keville.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.geo.Point;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import keville.model.event.location.Location;
import keville.model.event.location.USStateAndTerritoryCodes;

public class GeoUtils {

  private static final int maxGeoFetchAttempts = 3;
  private static final int retryDelayInSeconds = 5;

  private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GeoUtils.class);
  private static LocalDateTime lastGeoFetch = LocalDateTime.now();

  //https://en.wikipedia.org/wiki/Versine#ahav
  private static double hav(double theta) {
    return Math.pow(Math.sin(theta/2.0),2.0);
  }

  //https://en.wikipedia.org/wiki/Versine#ahav
  private static double ahav(double theta) {
    return 2 * Math.asin(Math.sqrt(theta));
  }

  /**
   * @p0 : latitude (x)  and longitude (y) of geospatial coordinate in DD
   * @p1 : latitude (x)  and longitude (y) of geospatial coordinate in DD
   * @return Spherical / Great Circle Distance in km
   *
   */
  public static double sphericalDistance(Point p0,Point p1) {
    double epsilon = hav(p1.getX() - p0.getX()) + ( Math.cos(p0.getX())*Math.cos(p1.getX())*hav(p1.getY()-p0.getY()) );
    double theta =  ahav(epsilon);
    double radius = 6371; // radius of the Earth in km
    //arc length angle should be in radians
    return ( theta * Math.PI / 180 ) * radius;
  }

  //Perhaps this belongs in its own class
  public static Location getLocationFromGeoCoordinates(double latitude, double longitude) throws RemoteException {
    int attempt = 0;
    while ( attempt < maxGeoFetchAttempts ) { 
      try {
        LOG.info("attempting reverse geocode : " + attempt);
        Location location = tryGetLocationFromGeoCoordinates(latitude,longitude);  
        return location;
      } catch (InterruptedException | URISyntaxException | IOException | JsonSyntaxException e) {
        attempt++;
      }
    }
    throw new RemoteException("Unable to reverse geocode (latitude,longitude) : " + latitude + " , " + longitude);
  }

  public static Location tryGetLocationFromGeoCoordinates(double latitude, double longitude) throws InterruptedException, IOException, URISyntaxException, JsonSyntaxException {


    Duration duration = Duration.between(LocalDateTime.now(),lastGeoFetch.plusSeconds(retryDelayInSeconds));
    if ( !duration.isNegative() ) {
      LOG.info("wating " + duration.toSeconds() + " seconds ");
      //wait
      Thread.sleep( duration.toMillis() );
      lastGeoFetch = LocalDateTime.now();
    }

    Location result = null;
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest getRequest;
    String response = "";
    
    LOG.info("Reverse Geocoding : " + latitude + " , " + longitude);

    URI uri = new URI("https://geocode.maps.co/reverse?lat=" + latitude + "&lon=" + longitude ); /*without terminal '/' we get a 301 */
    getRequest = HttpRequest.newBuilder()
      .uri(uri) 
      .GET()
      .build();
    HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
    response = getResponse.body();

    JsonObject json = JsonParser.parseString(response).getAsJsonObject();

    if ( !json.has("address"))  {
      LOG.warn("No address returned");
      return result;
    }

    JsonObject address = json.getAsJsonObject("address");
    
    String state = null;
    if ( address.has("state") ) {
      state = address.get("state").getAsString();
      state = USStateAndTerritoryCodes.getANSILcode(state); //we want "nj" not "New Jersey"
    }

    String locality = null; 
    if ( address.has("village") )  {
      locality = address.get("village").getAsString();
    } else if ( address.has("town") ) {
      locality = address.get("town").getAsString();
    } else if ( address.has("city") ) {
      locality = address.get("city").getAsString();
    } else {
      LOG.warn("Failed to find a town , city or village for the geocoordinate : " + latitude + " , " + longitude);
    }

    result = Location.builder()
      .setLatitude(latitude)
      .setLongitude(longitude)
      .setCountry("us")
      .setRegion(state)
      .setLocality(locality)
    .build();

    return result;
    
  }

}
