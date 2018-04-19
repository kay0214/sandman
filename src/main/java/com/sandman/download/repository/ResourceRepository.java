package com.sandman.download.repository;

import com.sandman.download.domain.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Resource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    public Page<Resource> findByUserId(Long userId, Pageable pageable);
    @Query("FROM Resource resource where resource.status=1 and resource.id=?1")
    public Resource getOneResourceById(Long id);
    @Modifying
    @Query("update Resource res set res.status=0 where res.id=?1")
    public Integer delResourceById(Long id);

}
