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
import org.springframework.web.bind.annotation.*;

import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;
import com.monoToMicro.core.services.HealthService;

@Controller
@RequestMapping("/health")
public class HealthController{

	@Autowired
	private HealthService healthService;
	
	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.GET, value = "/ping")
	public ResponseEntity<String> ping() {
		
		try {
			InstanceInfo instanceInfo = EC2MetadataUtils.getInstanceInfo();
			if(instanceInfo!=null) {
				String info = instanceInfo.toString();		
				String accountId = instanceInfo.getAccountId();
				String az = instanceInfo.getAvailabilityZone();
				String instanceID = instanceInfo.getInstanceId();
				String instanceType = instanceInfo.getInstanceType();
				String region = instanceInfo.getRegion();
				
				String infoStr = "accountId: "+accountId+'\n'+'\r';
				infoStr += "az: "+az+'\n'+'\r';
				infoStr += "instanceID: "+instanceID+'\n'+'\r';
				infoStr += "instanceType: "+instanceType+'\n'+'\r';
				infoStr += "region: "+region+'\n'+'\r';
				System.out.println(infoStr);
				return new ResponseEntity<String>(infoStr,HttpStatus.OK);
			}
		}
		catch(Exception e) {
			System.out.println("No instance found");
		}
		System.out.println("No instance found");
		return new ResponseEntity<String>("No instance found",HttpStatus.OK);
	}
	
	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.GET, value = "/ishealthy")
	public ResponseEntity<String> isHealthy() {
		
		return new ResponseEntity<String>("Developer life matter", HttpStatus.OK);
	}
	
	@PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.GET, value = "/dbping")
	public ResponseEntity<String> databasePing() {
			
		boolean isReachable = healthService.isDatabaseReachable();
				
		if (isReachable) {								
			return new ResponseEntity<String>(HttpStatus.OK);
		}			
			
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}
	
	
}


