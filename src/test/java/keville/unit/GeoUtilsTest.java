package keville.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

import keville.model.event.location.Location;
import keville.util.GeoUtils;

public class GeoUtilsTest 
{

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GeoUtilsTest.class);

    @Test
    @Disabled
    public void getLocationFromGeoCoordinatesReturnsCorrectLocation() throws RemoteException {
      double lat = 40.171200;
      double lon = -74.071700;

      Location result = GeoUtils.getLocationFromGeoCoordinates(lat,lon);

      String expectedLocality = "Wall Township";
      String expectedRegion = "NJ";

      assertEquals(expectedRegion,result.region);
      assertEquals(expectedLocality,result.locality);
    }

    @Test
    public void spericalDistanceSanityCheck() {

      //Lawrence MA
      double lat = 42.708;
      double lon = -71.16;

      //Philadelhpia PA
      double lat2 = 39.9525;
      double lon2 = -75.1652;

      //according to 
      //https://latlongdata.com/distance-calculator/
      double expected = 453;
      double tolerance = 10;
      double distance = GeoUtils.sphericalDistance(new Point(lat,lon),new Point(lat2,lon2));
      assert( Math.abs(distance - expected ) < tolerance );

    }

}
