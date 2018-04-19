package com.sandman.download.repository.myRepository.impl;

import com.google.common.collect.Maps;
import com.sandman.download.domain.Resource;
import com.sandman.download.repository.myRepository.ResourceRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

/**
 * Created by wangj on 2018/4/19.
 */
@Service
public class ResourceRepoImpl implements ResourceRepo {
    @PersistenceContext
    private EntityManager em;
    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Page<Resource> findByUserId(Long userId, Pageable pageable) {
        Map<String,Object> queryParams = Maps.newHashMap();
        String sql = " from Resource res where res.status = 1 ";
        String sql_query = new String();
        sql_query += " AND res.userId =:userId " ;
        queryParams.put("userId", userId);
        sql += sql_query;
        Page<Resource> resourcePage = query(sql,pageable,queryParams);
        return resourcePage;
    }
}
