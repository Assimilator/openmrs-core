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
package org.openmrs.web.controller.visit;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.Logger;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.controller.PortletControllerUtil;
import org.openmrs.web.controller.bean.DatatableRequest;
import org.openmrs.web.controller.bean.DatatableResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Lists visits.
 */
@Controller
public class VisitListController {
	
	protected final Logger log = Logger.getLogger(getClass());
	
	public static final String VISITS_PATH = "/admin/visits/datatable";
	
	public static final String PATIENT = "patient";
	
	/**
	 * It handles calls from DataTables.
	 * 
	 * @param patient
	 * @param request
	 * @return {@link DatatableResponse}
	 */
	@RequestMapping(VISITS_PATH)
	public @ResponseBody
	DatatableResponse getVisits(@ModelAttribute Patient patient, HttpServletRequest request) {
		DatatableRequest datatable = DatatableRequest.parseRequest(request);
		
		DatatableResponse response = new DatatableResponse(datatable);
		
		Integer totalVisitsCount = Context.getEncounterService().getEncountersByVisitsAndPatientCount(patient, false, null);
		response.setiTotalRecords(totalVisitsCount);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("person", patient);
		PortletControllerUtil.addFormToEditAndViewUrlMaps(model);
		@SuppressWarnings("unchecked")
		Map<Form, String> formToViewUrlMap = (Map<Form, String>) model.get("formToViewUrlMap");
		
		@SuppressWarnings("unchecked")
		Map<Form, String> formToEditUrlMap = (Map<Form, String>) model.get("formToEditUrlMap");
		
		if (!StringUtils.isBlank(datatable.getsSearch())) {
			Integer filteredVisitsCount = Context.getEncounterService().getEncountersByVisitsAndPatientCount(patient, false,
			    datatable.getsSearch());
			response.setiTotalDisplayRecords(filteredVisitsCount);
		} else {
			response.setiTotalDisplayRecords(totalVisitsCount);
		}
		
		List<Encounter> encounters = Context.getEncounterService().getEncountersByVisitsAndPatient(patient, false,
		    datatable.getsSearch(), datatable.getiDisplayStart(), datatable.getiDisplayLength());
		
		response.setsColumns("visitId", "visitActive", "visitType", "visitLocation", "visitFrom", "visitTo",
		    "visitIndication", "firstInVisit", "lastInVisit", "encounterId", "encounterDate", "encounterType",
		    "encounterProviders", "visitReferredFrom", "encounterLocation", "visitReferredTo", "encounterEnterer",
		    "formViewURL");
		
		int oldVisitID = 0;
		int newVisitID = 0;
		Location userLoc = Context.getLocationService().getLocation(
		    Integer.parseInt(Context.getAuthenticatedUser().getUserProperties().get(
		        OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION)));
		
		for (Encounter encounter : encounters) {
			Map<String, String> row = new HashMap<String, String>();
			oldVisitID = newVisitID;
			
			if (encounter.getVisit() != null) {
				Visit visit = encounter.getVisit();
				newVisitID = visit.getVisitId();
				Set<VisitAttribute> attributeSet = visit.getAttributes();
				if (!attributeSet.isEmpty()) {
					for (VisitAttribute object : attributeSet) {
						if (object.getAttributeType().getId() == 2 && oldVisitID != newVisitID && !encounter.isVoided()) {//Checking if TypeId belongs to the referral TypeId, also making sure that only the first row in a visit has the referral in displayed
							row.put("visitReferredFrom", object.getValue().toString()); //Here we add visit ReferredFrom data
						}
					}
				} else {
					row.put("visitReferredFrom", ""); //Here we add empty visit ReferredFrom data
				}
				row.put("visitId", visit.getId().toString());
				row.put("visitActive", Boolean.toString(isActive(visit.getStartDatetime(), visit.getStopDatetime())));
				row.put("visitType", visit.getVisitType().getName());
				row.put("visitLocation", (visit.getLocation() != null) ? visit.getLocation().getName() : "");
				row.put("visitFrom", Context.getDateFormat().format(visit.getStartDatetime()));
				
				if (visit.getStopDatetime() != null) {
					row.put("visitTo", Context.getDateFormat().format(visit.getStopDatetime()));
				}
				
				if (visit.getIndication() != null && visit.getIndication().getName() != null) {
					row.put("visitIndication", visit.getIndication().getName().getName());
				}
				
				//Originally this code was supposed to handle the drawing of encounters
				//Now, however, since we do not display voided encounters,
				//We have to decrement encounter counter by the amount of voided encounters and then
				// display the non-voided encounter set accordingly
				Set<Encounter> prepVisitEncounters = visit.getEncounters();
				
				//Here we count non-voided visits
				int counter = 0;
				for (Encounter enc : prepVisitEncounters) {
					if (!enc.isVoided()) {
						counter++;
					}
				}
				//Initializing right-size array
				Object[] visitEncounters = new Object[counter];
				counter = 0;
				//Here we will use a for loop to get rid of the voided visits in the array.
				for (Encounter enc : prepVisitEncounters) {
					if (!enc.isVoided()) {
						visitEncounters[counter] = enc;
						counter++;
					}
				}
				
				if (visitEncounters.length > 0) {
					if (encounter.equals(visitEncounters[0])) {
						row.put("firstInVisit", Boolean.TRUE.toString());
					}
					if (encounter.equals(visitEncounters[visitEncounters.length - 1])) {
						row.put("lastInVisit", Boolean.TRUE.toString());
					}
				} else {
					row.put("firstInVisit", Boolean.TRUE.toString());
					row.put("lastInVisit", Boolean.TRUE.toString());
				}
			}
			
			if (encounter.getId() != null && !encounter.isVoided()) { //If it is not mocked encounter and not voided
				row.put("encounterId", encounter.getId().toString());
				row.put("encounterDate", Context.getDateFormat().format(encounter.getEncounterDatetime()));
				row.put("encounterType", encounter.getEncounterType().getName());
				row.put("encounterProviders", getProviders(encounter));
				row.put("encounterLocation", (encounter.getLocation() != null) ? encounter.getLocation().getName() : "");
				row.put("encounterEnterer", (encounter.getCreator() != null) ? encounter.getCreator().getPersonName()
				        .toString() : "");
				
				boolean flag = false;
				if (encounter.getLocation() != null) {
					if (!encounter.getLocation().equals(userLoc)) {
						flag = true;
					}
				} else {
					flag = true;
				}
				
				//Here we check whether the encounter has happened in the same location as the user or whether it is a SuperUser
				if (!flag || Context.getAuthenticatedUser().isSuperUser()) {
					row.put("formViewURL", getViewFormURL(request, formToViewUrlMap, formToEditUrlMap, encounter));
				} else {
					row.put("formViewURL", "False");
				}
				
				//Getting referrals to some other clinics
				Set<Obs> observationSet = encounter.getAllObs();
				if (!observationSet.isEmpty()) {
					String spacer = " ";
					String referralsOut = "";
					for (Obs obj : observationSet) {
						if (obj.getConcept().getId() == 1272
						        && Context.getConceptService().getConcept(obj.getValueCoded().getConceptId())
						                .getConceptClass().getId() == 30) {//Checking if TypeId belongs to the referral TypeId
							referralsOut += spacer
							        + Context.getConceptService().getConcept(obj.getValueCoded().getConceptId()).getName();//adding each stored referral to the display string
							spacer = ", ";
						}
					}
					row.put("visitReferredTo", referralsOut); //Here we add empty visit ReferredFrom data
				} else {
					row.put("visitReferredTo", ""); //Here we add empty visit ReferredFrom data
				}
			}
			
			response.addRow(row);
		}
		
		return response;
	}
	
	private String getViewFormURL(HttpServletRequest request, Map<Form, String> formToViewUrlMap,
	        Map<Form, String> formToEditUrlMap, Encounter encounter) {
		String viewFormURL = formToViewUrlMap.get(encounter.getForm());
		if (viewFormURL == null) {
			viewFormURL = formToEditUrlMap.get(encounter.getForm());
		}
		if (viewFormURL != null) {
			viewFormURL = request.getContextPath() + "/" + viewFormURL + "?encounterId=" + encounter.getId();
		} else {
			viewFormURL = request.getContextPath() + "/admin/encounters/encounter.form?encounterId=" + encounter.getId();
		}
		return viewFormURL;
	}
	
	private String getProviders(Encounter encounter) {
		StringBuilder providersBuilder = new StringBuilder();
		for (Set<Provider> providers : encounter.getProvidersByRoles().values()) {
			for (Provider provider : providers) {
				if (provider.getPerson() != null) {
					providersBuilder.append(provider.getPerson().getPersonName().getFullName());
				} else {
					providersBuilder.append(provider.getIdentifier());
				}
				providersBuilder.append(", ");
			}
		}
		
		if (providersBuilder.length() > 1) {
			return providersBuilder.substring(0, providersBuilder.length() - 2);
		} else {
			return "";
		}
	}
	
	@ModelAttribute
	public Patient getPatient(@RequestParam(PATIENT) Integer patientId) {
		return Context.getPatientService().getPatient(patientId);
	}
	
	private boolean isActive(Date start, Date end) {
		Date now = new Date();
		if (OpenmrsUtil.compare(now, start) >= 0) {
			if (OpenmrsUtil.compareWithNullAsLatest(now, end) < 0) {
				return true;
			}
		}
		return false;
	}
}
