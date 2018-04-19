package com.sandman.download.repository;

import com.sandman.download.domain.UploadRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the UploadRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {
    public Page<UploadRecord> findAllByUserId(Long userId, Pageable pageable);
}
