package keville.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {

  public String name;
  public String country;
  public String region;
  public String locality;
  public String streetAddress;
  public Double latitude;
  public Double longitude;

}
