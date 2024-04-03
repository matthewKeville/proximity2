package keville.model.region;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("REGION")
public class Region {

  @Id
  public Integer id;
  public String name;
  public Integer owner; //agr
  public double radius;
  public double longitude;
  public double latitude;
  public boolean isDisabled;
  /* will cause region to be scanned immediately regardless of disability or lastScan*/
  @Column(value = "FORCE_SCAN")
  public boolean forceScan;
  @Column(value = "LAST_SCAN")
  public LocalDateTime lastScan;

  public Region(){};

  public String toString() { 
    return 
      " name    : " + name +
      " (r,x,y) : (" + radius + " , " + latitude + " , " + longitude + ")";
  }

}

