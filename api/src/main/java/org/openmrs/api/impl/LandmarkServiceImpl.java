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
package org.openmrs.api.impl;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.LandmarkService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.LandmarkDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;
import org.openmrs.api.LandmarkService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link org.openmrs.api.LandmarkService}
 * <p>
 * This class should not be instantiated alone, get a service class from the Context:
 * Context.getLandmarkService();
 *
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.api.LandmarkService
 * @see org.openmrs.Landmark
 */
public class LandmarkServiceImpl extends BaseOpenmrsService implements LandmarkService {
	
	private LandmarkDAO dao;
	
	/**
	 * @see org.openmrs.api.LandmarkService#setLandmarkDAO(org.openmrs.api.db.LandmarkDAO)
	 */
	public void setLandmarkDAO(LandmarkDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#saveLandmark(org.openmrs.Landmark)
	 */
	public Landmark saveLandmark(Landmark landmark) throws APIException {
		if (landmark.getName() == null) {
			throw new APIException("Landmark name is required");
		}
		
		return dao.saveLandmark(landmark);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getLandmark(Integer)
	 */
	public Landmark getLandmark(Integer landmarkId) throws APIException {
		return dao.getLandmark(landmarkId);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getLandmark(String)
	 */
	public Landmark getLandmark(String name) throws APIException {
		return dao.getLandmark(name);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getLandmarkByUuid(String)
	 */
	public Landmark getLandmarkByUuid(String uuid) throws APIException {
		return dao.getLandmarkByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getAllLandmarks()
	 */
	public List<Landmark> getAllLandmarks() throws APIException {
		return dao.getAllLandmarks(true);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getAllLandmarks(boolean)
	 */
	public List<Landmark> getAllLandmarks(boolean includeRetired) throws APIException {
		return dao.getAllLandmarks(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getLandmarks(String)
	 */
	public List<Landmark> getLandmarks(String nameFragment) throws APIException {
		return getLandmarks(nameFragment, false, null, null);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#retireLandmark(org.openmrs.Landmark, String)
	 */
	public Landmark retireLandmark(Landmark landmark, String reason) throws APIException {
		landmark.setVoided(true);
		landmark.setVoidReason(reason);
		return saveLandmark(landmark);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#unretireLandmark(org.openmrs.Landmark)
	 */
	public Landmark unretireLandmark(Landmark landmark) throws APIException {
		landmark.setVoided(false);
		return saveLandmark(landmark);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#purgeLandmark(org.openmrs.Landmark)
	 */
	public void purgeLandmark(Landmark landmark) throws APIException {
		dao.deleteLandmark(landmark);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getCountOfLandmarks(String, Boolean)
	 */
	@Override
	public Integer getCountOfLandmarks(String nameFragment, Boolean includeRetired) {
		return OpenmrsUtil.convertToInteger(dao.getCountOfLandmarks(nameFragment, includeRetired));
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getLandmarks(String, boolean, Integer, Integer)
	 */
	@Override
	public List<Landmark> getLandmarks(String nameFragment, boolean includeRetired, Integer start, Integer length) {
		
		return dao.getLandmarks(nameFragment, includeRetired, start, length);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getRootLandmarks(boolean)
	 */
	@Override
	public List<Landmark> getRootLandmarks(boolean includeRetired) throws APIException {
		return dao.getRootLandmarks(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#getAddressTemplate()
	 */
	@Override
	public String getAddressTemplate() throws APIException {
		String addressTemplate = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE);
		if (!StringUtils.hasLength(addressTemplate))
			addressTemplate = OpenmrsConstants.DEFAULT_ADDRESS_TEMPLATE;
		
		return addressTemplate;
	}
	
	/**
	 * @see org.openmrs.api.LandmarkService#saveAddressTemplate(String)
	 */
	@Override
	public void saveAddressTemplate(String xml) throws APIException {
		Context.getAdministrationService().setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml);
		
	}
}
