package keville.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import keville.model.user.User;

import org.springframework.data.jdbc.repository.query.Query;

public interface UserRepository extends CrudRepository<User, Integer>{

  @Query("""
  SELECT  USER.* FROM USER where USER.username  = :username
  """)
  Optional<User> findByUsername(String username);

}
