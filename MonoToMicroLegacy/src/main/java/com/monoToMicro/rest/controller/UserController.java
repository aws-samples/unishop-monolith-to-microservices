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

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.monoToMicro.core.events.CreateUserEvent;
import com.monoToMicro.core.events.ReadUserEvent;
import com.monoToMicro.core.events.UserCreatedEvent;
import com.monoToMicro.core.events.UserReadEvent;
import com.monoToMicro.core.model.User;
import com.monoToMicro.core.services.UserService;


@Controller
@RequestMapping("/user")
public class UserController extends CoreController {

	@Autowired
	private UserService userService;

	/**
	 * 
	 * @param user
	 * @return
	 */
	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.POST)	
	public ResponseEntity<User> createUser(@RequestBody User user) {

		if (user!=null) {

			UserCreatedEvent userCreatedEvent = userService.create(new CreateUserEvent(user));

			if (userCreatedEvent.isCreated()) {
				user = userCreatedEvent.getUser();
				return new ResponseEntity<User>(user, HttpStatus.CREATED);
			}
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}		
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public ResponseEntity<User> login(@RequestBody User user) {

		if (user!=null) {
			UserReadEvent userReadEvent = userService.getByEmail(new ReadUserEvent(user));
			if(userReadEvent.isReadOK()) {
				user = userReadEvent.getUser();
				return new ResponseEntity<User>(user, HttpStatus.OK);	
			}
			
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}	
}
