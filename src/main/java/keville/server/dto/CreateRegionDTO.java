package keville.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRegionDTO {

  public String name;
  public double radius;
  public double longitude;
  public double latitude;

}
