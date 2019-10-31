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

import org.springframework.stereotype.Service;


import com.monoToMicro.core.events.ReadUnicornsBasketEvent;
import com.monoToMicro.core.events.ReadUnicornsEvent;
import com.monoToMicro.core.events.UnicornsReadBasketEvent;
import com.monoToMicro.core.events.UnicornsReadEvent;
import com.monoToMicro.core.events.UnicornsWriteBasketEvent;
import com.monoToMicro.core.events.WriteUnicornsBasketEvent;

/**
 * 
 * @author nirozeri
 * 
 */
@Service
public interface UnicornService {

	
	/**
	 * 
	 * @param readItemSetEvent
	 * @return
	 */
	public UnicornsReadEvent getUnicorns(ReadUnicornsEvent readUnicornsEvent);

	/**
	 * 
	 * @param writeUnicornsBasketEvent
	 * @return
	 */
	public UnicornsWriteBasketEvent addUnicornToBasket(WriteUnicornsBasketEvent writeUnicornsBasketEvent);
		
	/**
	 * 
	 * @param writeUnicornsBasketEvent
	 * @return
	 */
	public UnicornsWriteBasketEvent removeUnicornFromBasket(WriteUnicornsBasketEvent writeUnicornsBasketEvent);
	
	/**
	 * 
	 * @param readUnicornsBasketEvent
	 * @return
	 */
	public UnicornsReadBasketEvent getUnicornBasket(ReadUnicornsBasketEvent readUnicornsBasketEvent);
	
	/**
	 * 
	 * @param readUnicornsBasketEvent
	 * @return
	 */
	public UnicornsReadBasketEvent getAllBaskets();		
}
