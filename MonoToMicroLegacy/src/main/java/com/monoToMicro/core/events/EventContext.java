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

import com.monoToMicro.core.model.User;

/**
 * 
 * @author nirozeri
 * 
 */
public class EventContext {

	public enum State {
		NOT_FOUND, FAILED, SUCCESS, PENDING, IN_PROGRESS
	}

	protected State state = null;
	protected User requestingUser = null;

	/**
	 * 
	 * @return
	 */
	public User getRequestingUser() {
		return requestingUser;
	}

	/**
	 * 
	 * @param requestingUser
	 */
	public void setRequestingUser(User requestingUser) {
		this.requestingUser = requestingUser;
	}

	/**
	 * 
	 * @return
	 */
	public State getState() {
		return state;
	}

	/**
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSetRequestingUser(){
		return requestingUser!=null?true:false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isStateSuccess() {
		return state == State.SUCCESS;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStateNotFound() {
		return state == State.NOT_FOUND;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStateFailed() {
		return state == State.FAILED;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStatePending() {
		return state == State.PENDING;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStateInProgress() {
		return state == State.IN_PROGRESS;
	}
}
