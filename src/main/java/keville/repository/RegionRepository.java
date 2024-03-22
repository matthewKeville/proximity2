package keville.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import keville.model.region.Region;

public interface RegionRepository extends CrudRepository<Region, Integer>{

  @Query("""
  SELECT  REGION.* FROM REGION where REGION.owner  = :userId
  """)
  Iterable<Region> findUserRegions(Integer userId);

}
