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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author nirozeri
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Unicorn extends CoreModel {
	
	public enum Type{
		REGULAR, SPECIAL, ON_OFFER
	}
	
	public enum Color{
		RAINBOW, BLACK, WHITE, PINK, BLUE
	}
	
	private String uuid = null;	
	private String name = null;
	private String description = null;
	private Double price = null;
	private String image = null;	
	
	/**
	 * 
	 */
	public Unicorn(){
	}
	
	/**
	 * 
	 * @param UUID
	 */
	public Unicorn(String uuid){
		this.uuid = uuid;
	}

	/**
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * 
	 * @param uuid
	 */
	public void setUuid(String Uuid) {
		this.uuid = Uuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * 
	 * @param price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * 
	 * @return
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}
}
