package com.sandman.download.repository;

import com.sandman.download.domain.DownloadRecord;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the DownloadRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long> {

}
