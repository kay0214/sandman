package com.sandman.download.repository.myRepository;

import com.sandman.download.common.repository.AbstractRepository;
import com.sandman.download.domain.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by wangj on 2018/4/19.
 */
public interface ResourceRepo extends AbstractRepository {
    public Page<Resource> findByUserId(Long userId, Pageable pageable);
    public Page<Resource> findManyResourcesByFuzzy(String searchContent, Pageable pageable);
}
