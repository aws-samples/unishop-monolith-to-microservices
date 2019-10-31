/**
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.monoToMicro.core.model;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author nirozeri
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CoreModel implements Serializable{

	@JsonIgnore
	private Long id = null;
	@JsonIgnore
	private DateTime creationDate = null;
	@JsonIgnore
	private DateTime lastModifiedDate = null;
	@JsonIgnore
	private Long createdByUserId = null;
	@JsonIgnore
	private Long lastModifiedByUserId = null;
	@JsonIgnore
	private Boolean active = null;
	
	/**
	 * 
	 * @return
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 */
	public DateTime getCreationDate() {
		return creationDate;
	}
	
	/**
	 * 
	 * @param creationDate
	 */
	public void setCreationDate(DateTime creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * 
	 * @return
	 */
	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	/**
	 * 
	 * @param lastModifiedDate
	 */
	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getCreatedByUserId() {
		return createdByUserId;
	}
	
	/**
	 * 
	 * @param createdByUserId
	 */
	public void setCreatedByUserId(Long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getLastModifiedByUserId() {
		return lastModifiedByUserId;
	}
	
	/**
	 * 
	 * @param lastModifiedByUserId
	 */
	public void setLastModifiedByUserId(Long lastModifiedByUserId) {
		this.lastModifiedByUserId = lastModifiedByUserId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean isActive() {
		if(active!=null){
			return active.booleanValue();
		}
		return null;
	}
	
	/**
	 * 
	 * @param isActive
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
		
	@Override
	public String toString(){
		return 	id + " " +
				creationDate + " " +
				lastModifiedDate + " " +
				createdByUserId + " " +
				lastModifiedByUserId + " " +
				active;
	}
}
