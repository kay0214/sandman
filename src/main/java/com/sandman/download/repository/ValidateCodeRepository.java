package com.sandman.download.repository;

import com.sandman.download.domain.ValidateCode;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ValidateCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidateCodeRepository extends JpaRepository<ValidateCode, Long> {
    @Query(" FROM ValidateCode WHERE isValid = 1 AND sended = 1 AND contact = ?1 ")
    public ValidateCode findByContact(String contact);
    public void deleteByContact(String contact);
}
