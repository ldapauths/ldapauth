/*
 * Copyright [2020] [ldapauth of copyright http://www.ldapauth.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ldapauth.authn.jwt;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * congress 内存缓存服务
 *
 * @author Crystal.Sea
 *
 */
public class InMemoryCongressService implements CongressService{
    private static final Logger _logger = LoggerFactory.getLogger(InMemoryCongressService.class);

    /**
     * 缓存3分钟
     */
	protected  static  Cache<String, AuthJwt> congressStore =
        	        Caffeine.newBuilder()
        	            .expireAfterWrite(3, TimeUnit.MINUTES)
        	            .maximumSize(200000)
        	            .build();

	public InMemoryCongressService() {
        super();
    }

	/**
	 * 存储
	 */
    @Override
	public void store(String congress, AuthJwt authJwt) {
    	congressStore.put(congress, authJwt);
    	_logger.debug("store congress {} , {}",congress,authJwt);
	}

    /**
     * 删除
     */
	@Override
	public AuthJwt remove(String congress) {
		AuthJwt authJwt = congressStore.getIfPresent(congress);
		congressStore.invalidate(congress);
		_logger.debug("remove congress {}",congress);
		return authJwt;
	}

	/**
	 * 读取
	 */
    @Override
    public AuthJwt get(String congress) {
    	AuthJwt authJwt = congressStore.getIfPresent(congress);
        return authJwt;
    }

    /**
     * 消费
     */
	@Override
	public AuthJwt consume(String congress) {
		AuthJwt authJwt = congressStore.getIfPresent(congress);
		congressStore.invalidate(congress);
		_logger.debug("consume congress {}",congress);
		return authJwt;
	}

}
