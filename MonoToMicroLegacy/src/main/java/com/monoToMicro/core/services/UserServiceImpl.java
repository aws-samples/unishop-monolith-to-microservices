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

package com.monoToMicro.core.services;

import com.monoToMicro.core.events.CreateUserEvent;


import com.monoToMicro.core.events.CreatedEvent;
import com.monoToMicro.core.events.ReadUserEvent;
import com.monoToMicro.core.events.UserCreatedEvent;
import com.monoToMicro.core.events.UserReadEvent;
import com.monoToMicro.core.model.User;
import com.monoToMicro.core.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 
 * @author nirozeri
 * 
 */
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	

	/**
	 * 
	 * @param userRepository
	 */
	public UserServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository; 
	}

	@Override
	public UserCreatedEvent create(CreateUserEvent createUserEvent) {
		
		User user = createUserEvent.getUser();
		user.setActive(true);
		//user.setCreationDate(DateTime.now());
		user.setCreatedByUserId(1L);
		user.setUuid(UUID.randomUUID().toString());		

		user = userRepository.create(user);

		if (user != null) {			
			return new UserCreatedEvent(user,CreatedEvent.State.SUCCESS);
		} else {
			return new UserCreatedEvent(CreatedEvent.State.FAILED);
		}

	}
	
	@Override
	public UserReadEvent getByEmail(ReadUserEvent readUserEvent) {
				
		
		User user = userRepository.getByEmail(readUserEvent.getUser().getEmail());		
		if (user!=null) {			
			UserReadEvent userReadEvent = new UserReadEvent(user, UserReadEvent.State.SUCCESS);
			return userReadEvent;
		}
		else{
			return new UserReadEvent(UserReadEvent.State.FAILED);
		}
	}
}
