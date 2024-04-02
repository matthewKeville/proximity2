package keville.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;

import keville.model.event.location.Location;
import keville.util.GeoUtils;

import org.junit.jupiter.api.Test;

public class GeoUtilsTest 
{

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GeoUtilsTest.class);

    @Test
    public void getLocationFromGeoCoordinatesReturnsCorrectLocation() throws RemoteException {
      double lat = 40.171200;
      double lon = -74.071700;

      Location result = GeoUtils.getLocationFromGeoCoordinates(lat,lon);

      String expectedLocality = "Wall Township";
      String expectedRegion = "NJ";

      assertEquals(expectedRegion,result.region);
      assertEquals(expectedLocality,result.locality);
    }
}
