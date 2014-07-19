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
package org.openmrs.web.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

public class DWROrderService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public Boolean createDrugOrderByObject(DrugOrderListItem drugOrderItem) throws Exception {
		
		DrugOrder drugOrder = new DrugOrder();
		Boolean ret = true;
		
		if (drugOrderItem == null)
			throw new DWRException("Drug item cannot be null");
		
		if (drugOrderItem.getPatientId() == null)
			throw new DWRException("Patient cannot be null");
		
		Patient patient = Context.getPatientService().getPatient(drugOrderItem.getPatientId());
		
		if (patient == null)
			throw new DWRException("The specified patient was not found.");
		
		drugOrder.setPatient(patient);
		
		Drug drug = Context.getConceptService().getDrug(drugOrderItem.getDrugId());
		
		if (drug == null)
			throw new DWRException("Invalid drug.");
		
		drugOrder.setDrug(drug);
		drugOrder.setConcept(drug.getConcept());
		
		OrderType orderType = Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG);
		if (orderType == null)
			throw new DWRException(
			        "There is no 'Drug' Order Type in the system.  This must be an Order Type with orderTypeId = "
			                + OpenmrsConstants.ORDERTYPE_DRUG);
		
		drugOrder.setOrderType(orderType);
		
		drugOrder.setDose(drugOrderItem.getDose());
		drugOrder.setUnits(drugOrderItem.getUnits());
		drugOrder.setFrequency(drugOrderItem.getFrequency());
		drugOrder.setInstructions(drugOrderItem.getInstructions());
		drugOrder.setQuantity(drugOrderItem.getQuantity());
		drugOrder.setOrderer(Context.getAuthenticatedUser());
		
		Date date = null;
		
		SimpleDateFormat sdf = Context.getDateFormat();
		
		try {
			date = sdf.parse(drugOrderItem.getStartDate());
		}
		catch (ParseException e) {
			throw new DWRException(e.getMessage());
		}
		
		drugOrder.setStartDate(date);
		
		if (drugOrderItem.getAutoExpireDate() != null) {
			try {
				date = sdf.parse(drugOrderItem.getAutoExpireDate());
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
			drugOrder.setAutoExpireDate(date);
			drugOrder.setDuration(date.toString());
		}
		
		drugOrder.setVoided(new Boolean(false));
		
		try {
			Context.getOrderService().saveOrder(drugOrder);
		}
		catch (APIException e) {
			throw new DWRException(e.getMessage());
		}
		
		log.debug("Finished creating new drug order");
		
		return ret;
	}
	
	/**
	 * This method would normally be return type void, but DWR requires a callback, so we know when
	 * to refresh view
	 */
	public boolean createDrugOrder(Integer patientId, String drugId, Double dose, String units, String frequency,
	        String startDate, String instructions, Integer quantity) throws Exception {
		log.debug("PatientId is " + patientId + " and drugId is " + drugId + " and dose is " + dose + " and units are "
		        + units + " and frequency is " + frequency + " and startDate is " + startDate + " and instructions are "
		        + instructions);
		
		boolean ret = true;
		
		DrugOrder drugOrder = new DrugOrder();
		Patient patient = Context.getPatientService().getPatient(patientId);
		Drug drug = Context.getConceptService().getDrugByNameOrId(drugId);
		if (drug == null)
			throw new DWRException("There is no drug with the name or drugId of: " + drugId);
		
		OrderType orderType = Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG);
		if (orderType == null)
			throw new DWRException(
			        "There is no 'Drug' Order Type in the system.  This must be an Order Type with orderTypeId = "
			                + OpenmrsConstants.ORDERTYPE_DRUG);
		
		drugOrder.setDrug(drug);
		Concept concept = drug.getConcept();
		drugOrder.setConcept(concept);
		drugOrder.setOrderType(orderType);
		drugOrder.setPatient(patient);
		drugOrder.setDose(dose);
		drugOrder.setUnits(units);
		drugOrder.setFrequency(frequency);
		drugOrder.setInstructions(instructions);
		drugOrder.setQuantity(quantity);
		
		Date dStartDate = null;
		if (startDate != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				dStartDate = sdf.parse(startDate);
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
		}
		drugOrder.setStartDate(dStartDate);
		
		/*
		 * Old unused code, when endDate was the end date and not instructions
		 * 
		 * Date dEndDate = null;
		if (endDate != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				dEndDate = sdf.parse(endDate);
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
		}
		*/
		//endDate is currently being used as duration
		//drugOrder.setDuration(endDate);
		drugOrder.setVoided(new Boolean(false));
		
		try {
			Context.getOrderService().saveOrder(drugOrder);
		}
		catch (APIException e) {
			throw new DWRException(e.getMessage());
		}
		
		log.debug("Finished creating new drug order");
		return ret;
	}
	
	public void voidOrder(Integer orderId, String voidReason) {
		Order o = Context.getOrderService().getOrder(orderId);
		Context.getOrderService().voidOrder(o, voidReason);
	}
	
	public void discontinueOrder(Integer orderId, String discontinueReason, String discontinueDate) throws DWRException {
		Date dDiscDate = null;
		if (discontinueDate != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				dDiscDate = sdf.parse(discontinueDate);
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
		}
		
		Order o = Context.getOrderService().getOrder(orderId);
		try {
			Context.getOrderService().discontinueOrder(o, Context.getConceptService().getConcept(discontinueReason),
			    dDiscDate);
		}
		catch (APIException e) {
			throw new DWRException(e.getMessage());
		}
	}
	
	public Vector<DrugOrderListItem> getDrugOrdersByPatientId(Integer patientId, int whatToShow) {
		Patient p = Context.getPatientService().getPatient(patientId);
		Vector<DrugOrderListItem> ret = new Vector<DrugOrderListItem>();
		List<DrugOrder> drugOrders = Context.getOrderService().getDrugOrdersByPatient(p, whatToShow);
		if (drugOrders != null) {
			if (drugOrders.size() > 0) {
				for (DrugOrder drugOrder : drugOrders) {
					ret.add(new DrugOrderListItem(drugOrder));
				}
			}
		}
		return ret;
	}
	
	public Vector<DrugOrderListItem> getCurrentDrugOrdersByPatientId(Integer patientId) {
		return getDrugOrdersByPatientId(patientId, OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public Vector<DrugOrderListItem> getCompletedDrugOrdersByPatientId(Integer patientId) {
		return getDrugOrdersByPatientId(patientId, OrderService.SHOW_COMPLETE);
	}
	
	public String getUnitsByDrugId(Integer drugId) {
		String ret = "";
		
		Drug drug = Context.getConceptService().getDrug(drugId);
		if (drug != null) {
			String drugUnits = drug.getUnits();
			if (drugUnits != null)
				ret = drugUnits;
		}
		
		return ret;
	}
	
	public Vector<DrugSetItem> getCurrentDrugSet(Integer patientId, String drugSetId) {
		return getDrugSet(patientId, drugSetId, OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public Vector<DrugSetItem> getCompletedDrugSet(Integer patientId, String drugSetId) {
		return getDrugSet(patientId, drugSetId, OrderService.SHOW_COMPLETE);
	}
	
	public Vector<DrugSetItem> getDrugSet(Integer patientId, String drugSetId, int whatToShow) {
		log.debug("In getDrugSet() method");
		
		Vector<DrugSetItem> dsiList = null;
		
		Map<String, List<DrugOrder>> orders = this.getOrdersByDrugSet(patientId, drugSetId, ",", whatToShow);
		DrugSetItem dsi = new DrugSetItem();
		Concept c = OpenmrsUtil.getConceptByIdOrName(drugSetId);
		dsi.setDrugSetId(c.getConceptId());
		dsi.setDrugSetLabel(drugSetId.replace(" ", "_"));
		dsi.setName(c.getName(Context.getLocale()).getName());
		if (orders != null) {
			List<DrugOrder> currList = orders.get(drugSetId);
			if (currList != null) {
				dsi.setDrugCount(currList.size());
			} else {
				dsi.setDrugCount(0);
			}
		} else
			dsi.setDrugCount(0);
		dsiList = new Vector<DrugSetItem>();
		dsiList.add(dsi);
		
		return dsiList;
	}
	
	public Vector<DrugSetItem> getCurrentOtherDrugSet(Integer patientId, String displayDrugSetIds) {
		return getOtherDrugSet(patientId, displayDrugSetIds, OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public Vector<DrugSetItem> getCompletedOtherDrugSet(Integer patientId, String displayDrugSetIds) {
		return getOtherDrugSet(patientId, displayDrugSetIds, OrderService.SHOW_COMPLETE);
	}
	
	public Vector<DrugSetItem> getOtherDrugSet(Integer patientId, String displayDrugSetIds, int whatToShow) {
		DrugSetItem dsi = new DrugSetItem();
		
		dsi.setDrugSetLabel("__other__");
		dsi.setName("*");
		Vector<DrugOrderListItem> otherItems = getOtherDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds,
		    whatToShow);
		if (otherItems != null) {
			dsi.setDrugCount(otherItems.size());
		} else
			dsi.setDrugCount(0);
		
		Vector<DrugSetItem> dsiList = new Vector<DrugSetItem>();
		dsiList.add(dsi);
		
		return dsiList;
	}
	
	public Vector<DrugOrderListItem> getCurrentOtherDrugOrdersByPatientIdDrugSetId(Integer patientId,
	        String displayDrugSetIds) {
		log.debug("in getCurrentOtherDrugOrdersBy...() method");
		
		return this.getOtherDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds,
		    OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public Vector<DrugOrderListItem> getCompletedOtherDrugOrdersByPatientIdDrugSetId(Integer patientId,
	        String displayDrugSetIds) {
		log.debug("in getCompletedOtherDrugOrdersBy...() method");
		
		return this.getOtherDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds, OrderService.SHOW_COMPLETE);
	}
	
	public Vector<DrugOrderListItem> getOtherDrugOrdersByPatientIdDrugSetId(Integer patientId, String displayDrugSetIds,
	        int whatToShow) {
		log.debug("in getOtherDrugOrdersBy...() method");
		
		Vector<DrugOrderListItem> ret = null;
		
		Map<String, List<DrugOrder>> ordersBySetId = this.getOrdersByDrugSet(patientId, displayDrugSetIds, ",", whatToShow);
		if (ordersBySetId != null) {
			List<DrugOrder> orders = ordersBySetId.get("*");
			if (orders != null) {
				for (DrugOrder order : orders) {
					if (ret == null)
						ret = new Vector<DrugOrderListItem>();
					DrugOrderListItem drugOrderItem = new DrugOrderListItem(order);
					drugOrderItem.setDrugSetLabel("__other__");
					ret.add(drugOrderItem);
				}
			}
		}
		
		return ret;
	}
	
	public MappedDrugOrders getMappedDrugOrdersByPatientIdDrugSetId(Integer patientId, String drugSetId, int whatToShow)
	        throws Exception {
		
		log.debug("Entering getMappedDrugOrdersByPatientIdDrugSetId method with drugSetId: " + drugSetId);
		
		MappedDrugOrders mret = new MappedDrugOrders();
		Map<String, List<DrugOrderListItem>> ret = null;
		Map<String, List<DrugOrder>> ordersBySetId = this.getOrdersByDrugSet(patientId, drugSetId, ",", whatToShow);
		List<String> l = new ArrayList<String>();
		
		if (ordersBySetId != null) {
			if (ret == null)
				ret = new HashMap<String, List<DrugOrderListItem>>();
			
			for (String key : ordersBySetId.keySet()) {
				
				List<DrugOrder> orders = ordersBySetId.get(key);
				ArrayList<DrugOrderListItem> orderitems = null;
				l.add(key);
				
				if (orders != null) {
					
					for (DrugOrder order : orders) {
						if (orderitems == null)
							orderitems = new ArrayList<DrugOrderListItem>(orders.size());
						
						DrugOrderListItem drugOrderItem = new DrugOrderListItem(order);
						
						if (!key.equals("*"))
							drugOrderItem.setDrugSetId(OpenmrsUtil.getConceptByIdOrName(key).getConceptId());
						
						drugOrderItem.setDrugSetLabel(key.replace(" ", "_"));
						orderitems.add(drugOrderItem);
					}
					
				}
				
				ret.put(key, orderitems);
				
			}
		}
		
		mret.setDrugsMap(ret);
		mret.setDrugSets(l);
		
		return mret;
	}
	
	public Vector<DrugOrderListItem> getDrugOrdersByPatientIdDrugSetId(Integer patientId, String drugSetId, int whatToShow) {
		log.debug("Entering getCurrentDrugOrdersByPatientIdDrugSetId method with drugSetId: " + drugSetId);
		
		Vector<DrugOrderListItem> ret = null;
		
		Map<String, List<DrugOrder>> ordersBySetId = this.getOrdersByDrugSet(patientId, drugSetId, ",", whatToShow);
		if (ordersBySetId != null) {
			List<DrugOrder> orders = ordersBySetId.get(drugSetId);
			if (orders != null) {
				for (DrugOrder order : orders) {
					if (ret == null)
						ret = new Vector<DrugOrderListItem>();
					DrugOrderListItem drugOrderItem = new DrugOrderListItem(order);
					drugOrderItem.setDrugSetId(OpenmrsUtil.getConceptByIdOrName(drugSetId).getConceptId());
					drugOrderItem.setDrugSetLabel(drugSetId.replace(" ", "_"));
					ret.add(drugOrderItem);
				}
			}
		}
		
		return ret;
	}
	
	public Vector<DrugOrderListItem> getCurrentDrugOrdersByPatientIdDrugSetId(Integer patientId, String drugSetId) {
		return getDrugOrdersByPatientIdDrugSetId(patientId, drugSetId, OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public Vector<DrugOrderListItem> getCompletedDrugOrdersByPatientIdDrugSetId(Integer patientId, String drugSetId) {
		return getDrugOrdersByPatientIdDrugSetId(patientId, drugSetId, OrderService.SHOW_COMPLETE);
	}
	
	public void voidCurrentDrugSet(Integer patientId, String drugSetId, String voidReason) {
		log.debug("in voidDrugSet() method");
		
		Patient p = Context.getPatientService().getPatient(patientId);
		
		Context.getOrderService().voidDrugSet(p, drugSetId, voidReason, OrderService.SHOW_CURRENT_AND_FUTURE);
	}
	
	public void voidCompletedDrugSet(Integer patientId, String drugSetId, String voidReason) {
		log.debug("in voidDrugSet() method");
		
		Patient p = Context.getPatientService().getPatient(patientId);
		
		Context.getOrderService().voidDrugSet(p, drugSetId, voidReason, OrderService.SHOW_COMPLETE);
	}
	
	public void discontinueDrugSet(Integer patientId, String drugSetId, String discontinueReason, String discontinueDate)
	        throws DWRException {
		log.debug("in discontinueDrugSet() method");
		
		Patient p = Context.getPatientService().getPatient(patientId);
		
		Date discDate = null;
		if (discontinueDate != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				discDate = sdf.parse(discontinueDate);
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
		}
		
		try {
			Context.getOrderService().discontinueDrugSet(p, drugSetId,
			    Context.getConceptService().getConceptByIdOrName(discontinueReason), discDate);
		}
		catch (APIException e) {
			throw new DWRException(e.getMessage());
		}
		
	}
	
	/*
	 * This method would normally have a return type of void, but DWR requires a callback 
	 */
	public boolean voidCurrentDrugOrders(Integer patientId, String voidReason) {
		log.debug("beginning method");
		
		boolean ret = true;
		
		Patient p = Context.getPatientService().getPatient(patientId);
		
		List<DrugOrder> currentOrders = Context.getOrderService().getDrugOrdersByPatient(p,
		    OrderService.SHOW_CURRENT_AND_FUTURE);
		
		for (DrugOrder o : currentOrders) {
			Context.getOrderService().voidOrder(o, voidReason);
		}
		
		return ret;
	}
	
	/*
	 * This method would normally have a return type of void, but DWR requires a callback 
	 */
	public boolean discontinueCurrentDrugOrders(Integer patientId, String discontinueReason, String discontinueDate)
	        throws DWRException {
		log.debug("beginning method");
		
		boolean ret = true;
		
		Date discDate = null;
		if (discontinueDate != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				discDate = sdf.parse(discontinueDate);
			}
			catch (ParseException e) {
				throw new DWRException(e.getMessage());
			}
		}
		
		Patient p = Context.getPatientService().getPatient(patientId);
		
		List<DrugOrder> currentOrders = Context.getOrderService().getDrugOrdersByPatient(p,
		    OrderService.SHOW_CURRENT_AND_FUTURE);
		
		for (DrugOrder o : currentOrders) {
			try {
				Context.getOrderService().discontinueOrder(o, Context.getConceptService().getConcept(discontinueReason),
				    discDate);
			}
			catch (APIException e) {
				throw new DWRException(e.getMessage());
			}
		}
		
		return ret;
	}
	
	private Map<String, List<DrugOrder>> getOrdersByDrugSet(Integer patientId, String drugSetIds, String delimiter,
	        int whatToShow) {
		Map<String, List<DrugOrder>> ret = null;
		
		if (patientId != null && drugSetIds != null) {
			Patient p = Context.getPatientService().getPatient(patientId);
			if (p != null) {
				List<DrugOrder> ordersToWorkWith = Context.getOrderService().getDrugOrdersByPatient(p, whatToShow);
				ret = Context.getOrderService().getDrugSetsByDrugSetIdList(ordersToWorkWith, drugSetIds, delimiter);
			}
		}
		
		return ret;
	}
	
}
