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
package org.openmrs.api.db;

import org.hibernate.SessionFactory;
import org.openmrs.Landmark;
import org.openmrs.api.LandmarkService;

import java.util.List;
import java.util.Map;

/**
 * Landmark-related database functions
 */
public interface LandmarkDAO {
	
	/**
	 * Set the Hibernate SessionFactory to connect to the database.
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory);
	
	/**
	 * Create or update a landmark.
	 *
	 * @param landmark <code>Landmark</code> to save
	 * @return the saved <code>Landmark</code>
	 */
	public Landmark saveLandmark(Landmark landmark);
	
	/**
	 * Get a landmark by landmarkId
	 *
	 * @param landmarkId Internal <code>Integer</code> identifier of the <code>Landmark<code> to get
	 * @return the requested <code>Landmark</code>
	 */
	public Landmark getLandmark(Integer landmarkId);
	
	/**
	 * Get a landmark by name
	 *
	 * @param name String name of the <code>Landmark</code> to get
	 * @return the requested <code>Landmark</code>
	 */
	public Landmark getLandmark(String name);
	
	/**
	 * Get all landmarks
	 *
	 * @param includeRetired boolean - include retired landmarks as well?
	 * @return <code>List<Landmark></code> object of all <code>Landmark</code>s, possibly including
	 *         retired landmarks
	 */
	public List<Landmark> getAllLandmarks(boolean includeRetired);
	
	/**
	 * Gets the landmarks matching the specified arguments
	 *
	 * @param nameFragment is the string used to search for landmarks
	 * @param includeRetired specifies if retired landmarks should also be returned
	 * @param start the beginning index
	 * @param length the number of matching landmarks to return
	 * @return the list of landmarks
	 */
	public List<Landmark> getLandmarks(String nameFragment, boolean includeRetired, Integer start, Integer length)
	        throws DAOException;
	
	/**
	 * Completely remove the landmark from the database.
	 *
	 * @param landmark <code>Landmark</code> object to delete
	 */
	public void deleteLandmark(Landmark landmark);
	
	/**
	 * @param uuid the uuid to look for
	 * @return landmark matching uuid
	 */
	public Landmark getLandmarkByUuid(String uuid);
	
	/**
	 * @see org.openmrs.api.LandmarkService#getCountOfLandmarks(String, Boolean)
	 */
	public Long getCountOfLandmarks(String nameFragment, Boolean includeRetired);
	
	/**
	 * @see org.openmrs.api.LandmarkService#getRootLandmarks(boolean)
	 */
	public List<Landmark> getRootLandmarks(boolean includeRetired);
	
}
