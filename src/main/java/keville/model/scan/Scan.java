package keville.model.scan;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import keville.model.event.EventTypeEnum;

@Table("SCAN")
public class Scan {

  @Id
  public Integer id;
  public Integer region;
  public EventTypeEnum provider;

  public LocalDateTime start;
  public LocalDateTime end;
  public ScanStatusEnum status;

  public Scan(){};
}
