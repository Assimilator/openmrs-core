package org.openmrs.web.dwr;

import java.util.List;
import java.util.Map;

public class MappedDrugOrders implements java.io.Serializable {
	
	public static final long serialVersionUID = 73332L;
	
	private List<String> drugSets;
	
	private Map<String, List<DrugOrderListItem>> drugsMap;
	
	public MappedDrugOrders() {
	}
	
	public List<String> getDrugSets() {
		return drugSets;
	}
	
	public void setDrugSets(List<String> drugSets) {
		this.drugSets = drugSets;
	}
	
	public Map<String, List<DrugOrderListItem>> getDrugsMap() {
		return drugsMap;
	}
	
	public void setDrugsMap(Map<String, List<DrugOrderListItem>> drugsMap) {
		this.drugsMap = drugsMap;
	}
	
}
