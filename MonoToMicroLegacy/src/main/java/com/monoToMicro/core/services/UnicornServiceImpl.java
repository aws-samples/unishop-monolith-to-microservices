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

import com.monoToMicro.core.events.ReadUnicornsBasketEvent;
import com.monoToMicro.core.events.ReadUnicornsEvent;
import com.monoToMicro.core.events.UnicornsReadBasketEvent;
import com.monoToMicro.core.events.UnicornsReadEvent;
import com.monoToMicro.core.events.UnicornsWriteBasketEvent;
import com.monoToMicro.core.events.WriteUnicornsBasketEvent;
import com.monoToMicro.core.model.Unicorn;
import com.monoToMicro.core.model.UnicornBasket;
import com.monoToMicro.core.repository.UnicornRepository;
import java.util.*;
import org.springframework.stereotype.Service;


/**
 * 
 * @author nirozeri
 * 
 */
@Service
public class UnicornServiceImpl implements UnicornService {

	private final UnicornRepository unicornRepository;

	/**
	 * 
	 * @param itemRepository
	 */
	public UnicornServiceImpl(final UnicornRepository uncironRepository) {
		this.unicornRepository = uncironRepository;
	}
	
	public UnicornsReadEvent getUnicorns(ReadUnicornsEvent readUnicornsEvent) {
		
		List<Unicorn> unicorns = unicornRepository.getUnicorns();
			
		if (unicorns != null) {
			UnicornsReadEvent unicornsEvent = new UnicornsReadEvent(unicorns,
			UnicornsReadEvent.State.SUCCESS);
			return unicornsEvent;
		}
		return new UnicornsReadEvent(UnicornsReadEvent.State.FAILED);		
	}

	@Override
	public UnicornsWriteBasketEvent addUnicornToBasket(WriteUnicornsBasketEvent writeUnicornsBasketEvent) {
		
		String userUuid = writeUnicornsBasketEvent.getUserUuid();
		String unicornUuid = writeUnicornsBasketEvent.getUnicornUuid();
		boolean result = unicornRepository.addUnicornToBasket(userUuid, unicornUuid);
		
		if (result) {			
			return new UnicornsWriteBasketEvent(UnicornsReadEvent.State.SUCCESS);
		}
		return new UnicornsWriteBasketEvent(UnicornsReadEvent.State.FAILED);
	}

	@Override
	public UnicornsWriteBasketEvent removeUnicornFromBasket(WriteUnicornsBasketEvent writeUnicornsBasketEvent) {
		String userUuid = writeUnicornsBasketEvent.getUserUuid();
		String unicornUuid = writeUnicornsBasketEvent.getUnicornUuid();
		boolean result = unicornRepository.removeUnicornFromBasket(userUuid, unicornUuid);
		
		if (result) {			
			return new UnicornsWriteBasketEvent(UnicornsReadEvent.State.SUCCESS);
		}
		return new UnicornsWriteBasketEvent(UnicornsReadEvent.State.FAILED);
	}

	@Override
	public UnicornsReadBasketEvent getUnicornBasket(ReadUnicornsBasketEvent readUnicornsBasketEvent) {
		String userUUID = readUnicornsBasketEvent.getUserUUID();
		
		List<Unicorn> unicorns = unicornRepository.getUnicornBasket(userUUID);
		
		if (unicorns != null) {			
			return new UnicornsReadBasketEvent(unicorns, UnicornsReadEvent.State.SUCCESS);
		}
		return new UnicornsReadBasketEvent(UnicornsReadEvent.State.FAILED);		
	}

	@Override
	public UnicornsReadBasketEvent getAllBaskets() {
		
		List<UnicornBasket> baskets = unicornRepository.getAllBaskets();
		
		if (baskets != null) {			
			UnicornsReadBasketEvent event = new UnicornsReadBasketEvent(UnicornsReadEvent.State.SUCCESS);
			event.setBaskets(baskets);
			return event;
		}
		return new UnicornsReadBasketEvent(UnicornsReadEvent.State.FAILED);
	}		
}
