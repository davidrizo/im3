package es.ua.dlsi.grfia.muretweb.repository;

import es.ua.dlsi.grfia.muretweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
