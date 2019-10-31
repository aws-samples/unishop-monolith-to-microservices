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

package com.monoToMicro.core.events;

import java.util.List;

import com.monoToMicro.core.model.Unicorn;
import com.monoToMicro.core.model.UnicornBasket;

/**
 * 
 * @author nirozeri
 * 
 */
public class UnicornsReadBasketEvent extends ReadCompleteEvent {

	private List<Unicorn> unicorns = null;
	private List<UnicornBasket> baskets = null;

	/**
	 * 
	 * @param state
	 */
	public UnicornsReadBasketEvent(State state) {
		this.setState(state);
	}

	/**
	 * 
	 * @param items
	 * @param state
	 */
	public UnicornsReadBasketEvent(List<Unicorn> unicorns, State state) {
		this.setState(state);
		this.unicorns = unicorns;
	}

	/**
	 * 
	 * @param items
	 */
	public UnicornsReadBasketEvent(List<Unicorn> unicorns) {
		this.unicorns = unicorns;
	}

	/**
	 * 
	 * @return
	 */
	public List<Unicorn> getUnicorns() {
		return unicorns;
	}
	
	/**
	 * 
	 * @param baskets
	 */
	public void setBaskets(List<UnicornBasket> baskets) {
		this.baskets = baskets;
	}
	
	/**
	 * *
	 * @return
	 */
	public List<UnicornBasket> getBaskets() {
		return baskets;
	}
	
}
