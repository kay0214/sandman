package com.sandman.download.repository;

import com.sandman.download.domain.User;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the User entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserName(String userName);
    public List<User> findByEmail(String email);
    public List<User> findByMobile(String mobile);
}
