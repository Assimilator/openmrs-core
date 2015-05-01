package org.openmrs;

import org.openmrs.annotation.Independent;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Landmark extends BaseOpenmrsData implements java.io.Serializable, Attributable<Landmark> {
	
	public static final long serialVersionUID = 455634L;
	
	public static final int LOCATION_UNKNOWN = 1;
	
	// Fields
	
	private Integer landmarkId;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private String name;
	
	private String latitude;
	
	private String longitude;
	
	// Constructors
	
	/** default constructor */
	public Landmark() {
	}
	
	/** constructor with id */
	public Landmark(Integer landmarkId) {
		this.landmarkId = landmarkId;
	}
	
	// Property accessors
	
	@Override
	public Integer getId() {
		return landmarkId;
	}
	
	@Override
	public void setId(Integer landmarkId) {
		this.landmarkId = landmarkId;
	}
	
	/**
	 * @return Returns the latitude.
	 */
	public String getLatitude() {
		return latitude;
	}
	
	/**
	 * @param latitude The latitude to set.
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * @return Returns the landmarkId.
	 */
	public Integer getlandmarkId() {
		return landmarkId;
	}
	
	/**
	 * @param landmarkId The landmarkId to set.
	 */
	public void setlandmarkId(Integer landmarkId) {
		this.landmarkId = landmarkId;
	}
	
	/**
	 * @return Returns the longitude.
	 */
	public String getLongitude() {
		return longitude;
	}
	
	/**
	 * @param longitude The longitude to set.
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		if (getName() != null)
			return getName();
		if (getId() != null)
			return getId().toString();
		return "";
	}
	
	/**
	 * @see Attributable#findPossibleValues(String)
	 */
	public List<Landmark> findPossibleValues(String searchText) {
		try {
			return Context.getLandmarkService().getLandmarks(searchText);
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	/**
	 * @see Attributable#getPossibleValues()
	 */
	public List<Landmark> getPossibleValues() {
		try {
			return Context.getLandmarkService().getAllLandmarks();
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	/**
	 * @see Attributable#hydrate(String)
	 */
	public Landmark hydrate(String landmarkId) {
		try {
			return Context.getLandmarkService().getLandmark(Integer.valueOf(landmarkId));
		}
		catch (Exception e) {
			return new Landmark();
		}
	}
	
	/**
	 * @see Attributable#serialize()
	 */
	public String serialize() {
		if (getlandmarkId() != null)
			return "" + getlandmarkId();
		else
			return "";
	}
	
	/**
	 * @see Attributable#getDisplayString()
	 */
	public String getDisplayString() {
		return getName();
	}
	
}
