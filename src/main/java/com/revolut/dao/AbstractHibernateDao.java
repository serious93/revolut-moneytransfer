/**
 *          ***************************************************************
 *          *                                                             *
 *          *                           NOTICE                            *
 *          *                                                             *
 *          *   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
 *          *   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
 *          *   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
 *          *   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
 *          *   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
 *          *   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
 *          *   ALL OTHER RIGHTS RESERVED.                                *
 *          *                                                             *
 *          *   (c) COPYRIGHT 2017 INFOR.  ALL RIGHTS RESERVED.           *
 *          *   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
 *          *   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
 *          *   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
 *          *   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
 *          *   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
 *          *                                                             *
 *          ***************************************************************
 */
package com.revolut.dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public abstract class AbstractHibernateDao implements DataSourceOperationDao {

    protected Logger log = Logger.getLogger(getClass());

    protected SessionFactory sessionFactory;

    protected Class<? extends Serializable> entityClass;

    public AbstractHibernateDao(SessionFactory sessionFactory) {
       this.sessionFactory=sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T findById(Serializable id, Class<T> entityClass) {

        Transaction trans = getCurrentSession().beginTransaction();
        T entity = getCurrentSession().get(entityClass, id);
        trans.commit();

        return entity;
    }

    public <T extends Serializable> void save(final T entity) {
        log.debug("Save: persisting instance " + entity.toString());
        try {
            Transaction trans = getCurrentSession().beginTransaction();
            getCurrentSession().persist(entity);
            trans.commit();
        } catch (RuntimeException re) {
            log.error("Persist failed for " + entity.toString(), re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T update(final T entity) {
        log.debug("update: instance " + entity.toString());

        T mergedEntity;
        try {
            Transaction trans = getCurrentSession().beginTransaction();
            mergedEntity = (T) getCurrentSession().merge(entity);
            trans.commit();
        } catch (RuntimeException re) {
            log.error("Update: failed for " + entity.toString(), re);
            throw re;
        }

        return mergedEntity;
    }

    public <T extends Serializable> void delete(final T entity) {
        log.debug("Delete: instance " + entity.toString());
        try {
            Transaction trans = getCurrentSession().beginTransaction();
            getCurrentSession().delete(entity);
            trans.commit();
        } catch (RuntimeException re) {
            log.error("Remove: failed for " + entity.toString(), re);
            throw re;
        }
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
