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

import com.monoToMicro.core.model.Unicorn;
import com.monoToMicro.core.model.UnicornBasket;
import com.monoToMicro.core.repository.mappers.UnicornMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 
 * @author nirozeri
 * 
 */

@Repository
@Transactional
public class UnicornRepositoryImpl implements UnicornRepository {

	@Autowired
	private UnicornMapper unicornMapper;

	/**
	 * 
	 * @param items
	 */
	public UnicornRepositoryImpl() {

	}
	
	public List<Unicorn> getUnicorns(){
		try {
			return unicornMapper.getUnicorns();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean addUnicornToBasket(String userUUID, String unicornUUID) {
		try {
			return unicornMapper.addUnicornToBasket(userUUID, unicornUUID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeUnicornFromBasket(String userUUID, String unicornUUID) {
		try {
			return unicornMapper.removeUnicornFromBasket(userUUID, unicornUUID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Unicorn> getUnicornBasket(String userUUID){
		try {
			return unicornMapper.getUnicornBasket(userUUID);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UnicornBasket> getAllBaskets() {
		try {
			return unicornMapper.getAllBaskets();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
