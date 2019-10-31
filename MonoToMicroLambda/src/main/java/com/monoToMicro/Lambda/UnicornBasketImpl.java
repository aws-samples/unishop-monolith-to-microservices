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

package com.monoToMicro.Lambda;

import java.util.List;



import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * 
 * @author niroz
 *
 */
public class UnicornBasketImpl implements RequestHandler <UnicornBasket, String>{

	@Override
	public String handleRequest(UnicornBasket unicornBasket, Context context) {
		return "Unicorn Lives Matter";
	}
	
	/**
	 * 
	 * @param unicornBasket
	 * @param context
	 * @return
	 */
	public String addUnicornToBasket(UnicornBasket unicornBasket, Context context) {
						
		//Build the DDB instance connection
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();				
		DynamoDBMapper mapper = new DynamoDBMapper(client);
				
		//Get current basket
		UnicornBasket currentBasket = mapper.load(UnicornBasket.class, unicornBasket.getUuid());
		
		//if there is no current basket then use the incoming basket as the new basket
		if(currentBasket==null) {
			if(unicornBasket.getUuid()!=null && unicornBasket.getUnicorns()!=null) {
				mapper.save(unicornBasket);
				return "Added Unicorn to basket";
			}
			return "No basket exist and none was created";
		}
		
		//basket already exist, will check if item exist and add if not found
		List<Unicorn> currentUnicorns = currentBasket.getUnicorns();		
		List<Unicorn> unicornsToAdd = unicornBasket.getUnicorns();
		
		//Assuming only one will be added but checking for null or empty values
		if(unicornsToAdd!=null && !unicornsToAdd.isEmpty()) {
		
			Unicorn unicornToAdd = unicornsToAdd.get(0);
			String unicornToAddUuid = unicornToAdd.getUuid();
		
			for (Unicorn currentUnicorn: currentUnicorns) {
				if(currentUnicorn.getUuid().equals(unicornToAddUuid)) {
					//The unicorn already exists, no need to add him.
					return "Unicorn already exists!";
				}							
			}
			
			//Unicorn was not found, need to add and save
			currentUnicorns.add(unicornToAdd);
			currentBasket.setUnicorns(currentUnicorns);
			mapper.save(currentBasket);
			return "Added Unicorn to basket";
		}		
		return "Are you sure you added a Unicorn?";
	}
	
	/**
	 * 
	 * @param unicornBasket
	 * @param context
	 * @return
	 */
	public String removeUnicornFromBasket(UnicornBasket unicornBasket, Context context) {
		
		//Build the DDB instance connection 
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();				
		DynamoDBMapper mapper = new DynamoDBMapper(client);
				
		//Get current basket
		UnicornBasket currentBasket = mapper.load(UnicornBasket.class, unicornBasket.getUuid());
		
		//if no basket exist then return an error
		if(currentBasket==null) {						
			return "No basket exist, nothing to delete";
		}
		
		//basket exist, will check if item exist and will remove
		List<Unicorn> currentUnicorns = currentBasket.getUnicorns();		
		List<Unicorn> unicornsToRemove = unicornBasket.getUnicorns();
		
		//Assuming only one will be added but checking for null or empty values
		if(unicornsToRemove!=null && !unicornsToRemove.isEmpty()) {
		
			Unicorn unicornToRemove = unicornsToRemove.get(0);
		
			String unicornToRemoveUuid = unicornToRemove.getUuid();
		
			for (Unicorn currentUnicorn: currentUnicorns) {		
			
				if(currentUnicorn.getUuid().equals(unicornToRemoveUuid)) {
					currentUnicorns.remove(currentUnicorn);
					if(currentUnicorns.isEmpty()) {
						//no more unicrons in basket, will delete the basket
						mapper.delete(currentBasket);
						return "Unicorn was removed and basket was deleted!";
					}else {
						//keeping basket alive as more unicrons are in it
						currentBasket.setUnicorns(currentUnicorns);
						mapper.save(currentBasket);						
						return "Unicorn was removed! Other unicorns are still in basket";	
					}					
				}							
			}
			
			if(currentBasket.getUnicorns()!=null && currentBasket.getUnicorns().isEmpty()) {
				//no unicorn to remove, will try to remove the basket nonetheless
				mapper.delete(currentBasket);
			}	
			return "Didn't find a unicorn to remove";
		}
		return "Are you sure you asked to remove a Unicron?";		
	}
	
	/**
	 * 
	 * @param unicornBasket
	 * @param context
	 * @return
	 */
	public UnicornBasket getUnicornsBasket(UnicornBasket unicornBasket, Context context) {
		
		//Build the DDB instance connection 
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();				
		DynamoDBMapper mapper = new DynamoDBMapper(client);
						
		if(unicornBasket.getUuid()!=null && !unicornBasket.getUuid().isEmpty()) {
			return mapper.load(UnicornBasket.class, unicornBasket.getUuid());		
		}
		return null;
	}
}


