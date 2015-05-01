/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.openmrs.Landmark;
import org.openmrs.Location;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.LandmarkDAO;

import java.util.List;
import java.util.Map;

/**
 * Hibernate landmark-related database functions
 */
public class HibernateLandmarkDAO implements LandmarkDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#saveLandmark(org.openmrs.Landmark)
	 */
	public Landmark saveLandmark(Landmark landmark) {
		sessionFactory.getCurrentSession().saveOrUpdate(landmark);
		return landmark;
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#getLandmark(Integer)
	 */
	public Landmark getLandmark(Integer landmarkId) {
		return (Landmark) sessionFactory.getCurrentSession().get(Landmark.class, landmarkId);
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#getLandmark(String)
	 */
	@SuppressWarnings("unchecked")
	public Landmark getLandmark(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Landmark.class).add(
		    Expression.eq("name", name));
		
		List<Landmark> landmarks = criteria.list();
		if (null == landmarks || landmarks.isEmpty()) {
			return null;
		}
		return landmarks.get(0);
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#getAllLandmarks(boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<Landmark> getAllLandmarks(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Landmark.class);
		if (!includeRetired) {
			criteria.add(Expression.eq("retired", false));
		} else {
			//push retired landmarks to the end of the returned list
			criteria.addOrder(Order.asc("retired"));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	@Override
	public List<Landmark> getLandmarks(String nameFragment, boolean includeRetired, Integer start, Integer length)
	        throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		
		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}
		
		if (!includeRetired)
			criteria.add(Restrictions.eq("retired", false));
		
		criteria.addOrder(Order.asc("name"));
		if (start != null)
			criteria.setFirstResult(start);
		if (length != null && length > 0)
			criteria.setMaxResults(length);
		
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#deleteLandmark(org.openmrs.Landmark)
	 */
	public void deleteLandmark(Landmark landmark) {
		sessionFactory.getCurrentSession().delete(landmark);
	}
	
	@Override
	public Landmark getLandmarkByUuid(String uuid) {
		return (Landmark) sessionFactory.getCurrentSession().createQuery("from Landmark l where l.uuid = :uuid").setString(
		    "uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#getCountOfLandmarks(String, Boolean)
	 */
	@Override
	public Long getCountOfLandmarks(String nameFragment, Boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Landmark.class);
		if (!includeRetired)
			criteria.add(Expression.eq("retired", false));
		
		if (StringUtils.isNotBlank(nameFragment))
			criteria.add(Expression.ilike("name", nameFragment, MatchMode.START));
		
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.LandmarkDAO#getRootLandmarks(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Landmark> getRootLandmarks(boolean includeRetired) throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Landmark.class);
		if (!includeRetired)
			criteria.add(Expression.eq("retired", false));
		
		criteria.add(Expression.isNull("parentLandmark"));
		
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
}
