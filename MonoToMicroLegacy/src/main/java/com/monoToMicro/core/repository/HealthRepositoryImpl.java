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

package com.monoToMicro.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.monoToMicro.core.repository.mappers.HealthMapper;

/**
 * 
 * @author nirozeri
 * 
 */

@Repository
@Transactional
public class HealthRepositoryImpl implements HealthRepository {
	
	@Autowired
	private HealthMapper healthMapper;

	/**
	 * 
	 * @param items
	 */
	public HealthRepositoryImpl() {

	}

	/**
	 * 
	 * @param messageMapper
	 */
	public void setHealthMapper(HealthMapper healthMapper) {
		this.healthMapper = healthMapper;
	}
	
	@Override
	public boolean isDatabaseReachable() {
		try {
			int res = healthMapper.isDatabaseReachable();			
			return true;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
