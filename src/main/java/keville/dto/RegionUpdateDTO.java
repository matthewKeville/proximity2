package keville.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionUpdateDTO {

  public Integer id;
  public String name;
  public double radius;
  public double longitude;
  public double latitude;
  public boolean isDisabled;
  public boolean forceScan;

}
