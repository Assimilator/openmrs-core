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
package org.openmrs.api;

import org.openmrs.*;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.LandmarkDAO;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * API methods for managing Landmarks <br/>
 * <br/>
 * Example Usage: <br/>
 * <code>
 *   List<Landmark> landmarks = Context.getLandmarkService().getAllLandmarks();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.Landmark
 */
@Transactional()
public interface LandmarkService extends OpenmrsService {
	
	/**
	 * Set the data access object that the service will use to interact with the database. This is
	 * set by spring in the applicationContext-service.xml file
	 *
	 * @param dao
	 */
	public void setLandmarkDAO(LandmarkDAO dao);
	
	/**
	 * Save landmark to database (create if new or update if changed)
	 *
	 * @param landmark is the landmark to be saved to the database
	 * @should throw APIException if landmark has no name
	 * @should overwrite transient tag if tag with same name exists
	 * @should throw APIException if transient tag is not found
	 * @should return saved object
	 * @should remove landmark tag from landmark
	 * @should add landmark tag to landmark
	 * @should remove child landmark from landmark
	 * @should cascade save to child landmark from landmark
	 * @should update landmark successfully
	 * @should create landmark successfully
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LANDMARKS })
	public Landmark saveLandmark(Landmark landmark) throws APIException;
	
	/**
	 * Returns a landmark given that landmarks primary key <code>landmarkId</code> A null value is
	 * returned if no landmark exists with this landmark.
	 *
	 * @param landmarkId integer primary key of the landmark to find
	 * @return Landmark object that has landmark.landmarkId = <code>landmarkId</code> passed in.
	 * @should return null when no landmark match given landmark id
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public Landmark getLandmark(Integer landmarkId) throws APIException;
	
	/**
	 * Returns a landmark given the landmark's exact <code>name</code> A null value is returned if
	 * there is no landmark with this name
	 *
	 * @param name the exact name of the landmark to match on
	 * @return Landmark matching the <code>name</code> to Landmark.name
	 * @should return null when no landmark match given landmark name
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public Landmark getLandmark(String name) throws APIException;
	
	/**
	 * Returns a landmark by uuid
	 *
	 * @param uuid is the uuid of the desired landmark
	 * @return landmark with the given uuid
	 * @should find object given valid uuid
	 * @should return null if no object found with given uuid
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public Landmark getLandmarkByUuid(String uuid) throws APIException;
	
	/**
	 * Returns all landmarks, includes retired landmarks. This method delegates to the
	 * #getAllLandmarks(boolean) method
	 *
	 * @return landmarks that are in the database
	 * @should return all landmarks including retired
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public List<Landmark> getAllLandmarks() throws APIException;
	
	/**
	 * Returns all landmarks.
	 *
	 * @param includeRetired whether or not to include retired landmarks
	 * @should return all landmarks when includeRetired is true
	 * @should return only unretired landmarks when includeRetires is false
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public List<Landmark> getAllLandmarks(boolean includeRetired) throws APIException;
	
	/**
	 * Returns landmarks that match the beginning of the given string. A null list will never be
	 * returned. An empty list will be returned if there are no landmarks. Search is case
	 * insensitive. matching this <code>nameFragment</code>
	 *
	 * @param nameFragment is the string used to search for landmarks
	 * @should return empty list when no landmark match the name fragment
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public List<Landmark> getLandmarks(String nameFragment) throws APIException;
	
	/**
	 * Returns a specific number landmarks from the specified starting position that match the
	 * beginning of the given string. A null list will never be returned. An empty list will be
	 * returned if there are no landmarks. Search is case insensitive. matching this
	 * <code>nameFragment</code>. If start and length are not specified, then all matches are
	 * returned
	 *
	 * @deprecated replaced by {@link org.openmrs.api.LandmarkService#getLandmarks(String, boolean, Integer, Integer)}
	 *
	 * @param nameFragment   is the string used to search for landmarks
	 * @param includeRetired Specifies if retired landmarks should be returned
	 * @param start          the beginning index
	 * @param length         the number of matching landmarks to return
	 * @since 1.8
	 */
	@Deprecated
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public List<Landmark> getLandmarks(String nameFragment, boolean includeRetired, Integer start, Integer length)
	        throws APIException;
	
	/**
	 * Retires the given landmark. This effectively removes the landmark from circulation or use.
	 *
	 * @param landmark landmark to be retired
	 * @param reason   is the reason why the landmark is being retired
	 * @should retire landmark successfully
	 * @should throw IllegalArgumentException when no reason is given
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LANDMARKS })
	public Landmark retireLandmark(Landmark landmark, String reason) throws APIException;
	
	/**
	 * Unretire the given landmark. This restores a previously retired landmark back into
	 * circulation and use.
	 *
	 * @param landmark
	 * @return the newly unretired landmark
	 * @throws org.openmrs.api.APIException
	 * @should unretire retired landmark
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LANDMARKS })
	public Landmark unretireLandmark(Landmark landmark) throws APIException;
	
	/**
	 * Completely remove a landmark from the database (not reversible) This method delegates to
	 * #purgeLandmark(landmark, boolean) method
	 *
	 * @param landmark the Landmark to clean out of the database.
	 * @should delete landmark successfully
	 */
	@Authorized( { PrivilegeConstants.PURGE_LANDMARKS })
	public void purgeLandmark(Landmark landmark) throws APIException;
	
	/**
	 * Return the number of all landmarks that start with the given name fragment, if the name
	 * fragment is null or an empty string, then the number of all landmarks will be returned
	 *
	 * @param nameFragment   is the string used to search for landmarks
	 * @param includeRetired Specifies if retired landmarks should be counted or ignored
	 * @return the number of all landmarks starting with the given nameFragment
	 * @since 1.8
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public Integer getCountOfLandmarks(String nameFragment, Boolean includeRetired);
	
	/**
	 * Returns all root landmarks (i.e. those who have no parentLandmark), optionally including retired ones.
	 *
	 * @param includeRetired
	 * @return return all root landmarks depends on includeRetired
	 * @should return all root landmarks when includeRetired is true
	 * @should return only unretired root landmarks when includeRetired is false
	 * @since 1.9
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public List<Landmark> getRootLandmarks(boolean includeRetired);
	
	/**
	 * Returns the xml of default address template.
	 *
	 * @return a string value of the default address template. If the GP is
	 *         empty, the default template is returned
	 * @see org.openmrs.util.OpenmrsConstants#GLOBAL_PROPERTY_ADDRESS_TEMPLATE
	 * @see org.openmrs.util.OpenmrsConstants#DEFAULT_ADDRESS_TEMPLATE
	 * @since 1.9
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LANDMARKS })
	public String getAddressTemplate() throws APIException;
	
	/**
	 * Saved default address template to global properties
	 *
	 * @param xml is a string to be saved as address template
	 * @should throw APIException if the string is empty
	 * @should update default address template successfully
	 * @should create default address template successfully
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.MANAGE_ADDRESS_TEMPLATES })
	public void saveAddressTemplate(String xml) throws APIException;
	
}
