package com.sandman.download.common.repository;

import com.sandman.download.common.Utils.SQLUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public interface AbstractRepository {
	EntityManager getEm();

    /**
     * 使用jpql进行分页查询
     * @param hql jpql
     * @param pageable 分页对象
     * @param queryParams 查询参数
     * @param <T>
     * @return
     */
    default <T> Page<T> query(String hql, Pageable pageable, Map<String, Object> queryParams) {
        EntityManager em = getEm();
        String countQl = " select count(1) " + SQLUtils.removeFetchInCountQl(SQLUtils.removeSelect(SQLUtils.removeOrderBy(hql)));
        Query countQuery = em.createQuery(countQl);
        queryParams.forEach(countQuery::setParameter);
        Sort sort = pageable.getSort();
        if (sort != null) {
        		hql += " ORDER BY ";
        		Iterator<Order> iterator = sort.iterator();
        		while (iterator.hasNext()) {
        			Order order = iterator.next();
        			hql += order.getProperty()+" "+order.getDirection()+",";
			}
        		hql = hql.substring(0,hql.length()-1);
        }
        Query query = em.createQuery(hql);
        queryParams.forEach(query::setParameter);
        long total;
        if(SQLUtils.hasGroupBy(hql)) {
            List resultList = countQuery.getResultList();
            total = resultList.size();
        } else {
            total = (Long)countQuery.getSingleResult();
        }
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<T> results = query.getResultList();
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 使用jpql进行查询
     * @param ql jpql
     * @param queryParams 查询参数
     * @param <T>
     * @return
     */
    default <T> List<T> query(String hql, Map<String, Object> queryParams) {

        EntityManager em = getEm();

        Query query = em.createQuery(hql);
        queryParams.forEach(query::setParameter);

        return query.getResultList();
    }

    /**
     * 使用jpql进行数据更新操作
     * @param ql
     * @param queryParams
     * @return
     */
    default int update(String hql, Map<String, Object> queryParams) {

        Query query = getEm().createQuery(hql);
        queryParams.forEach(query::setParameter);

        return query.executeUpdate();
    }

}
