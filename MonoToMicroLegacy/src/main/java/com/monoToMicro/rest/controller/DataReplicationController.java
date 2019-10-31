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

package com.monoToMicro.rest.controller;

import com.monoToMicro.core.events.UnicornsReadBasketEvent;
import com.monoToMicro.core.model.UnicornBasket;
import com.monoToMicro.core.services.UnicornService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 
 * @author nirozeri
 * 
 */
@RestController
@RequestMapping("/data")
public class DataReplicationController extends CoreController {

	@Autowired
	private UnicornService unicornService;

	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Void> replicate() {
				
		UnicornsReadBasketEvent unicornsReadEvent = unicornService.getAllBaskets();

		if (unicornsReadEvent.isReadOK()) {
			List<UnicornBasket> baskets = unicornsReadEvent.getBaskets();
			return new ResponseEntity(HttpStatus.OK);
		}
		return null;

		//return new ResponseEntity<Collection<Unicorn>>(HttpStatus.BAD_REQUEST);				
	}
}
