package com.sandman.download.repository;

import com.sandman.download.domain.ResourceRecord;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ResourceRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceRecordRepository extends JpaRepository<ResourceRecord, Long> {

}
