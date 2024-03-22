package keville.repository;

import org.springframework.data.repository.CrudRepository;

import keville.model.scan.Scan;

public interface ScanRepository extends CrudRepository<Scan, Integer>{}
