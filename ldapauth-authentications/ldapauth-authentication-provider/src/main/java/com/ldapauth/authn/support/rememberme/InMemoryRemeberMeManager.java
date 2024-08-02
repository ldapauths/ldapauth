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


package com.ldapauth.authn.support.rememberme;

import java.util.concurrent.TimeUnit;

import com.ldapauth.constants.ConstsTimeInterval;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class InMemoryRemeberMeManager   extends AbstractRemeberMeManager {

    protected static final Cache<String, RemeberMe> remeberMeStore =
            Caffeine.newBuilder()
                .expireAfterWrite(ConstsTimeInterval.TWO_WEEK, TimeUnit.SECONDS)
                .build();

    @Override
    public void save(RemeberMe remeberMe) {
        remeberMeStore.put(remeberMe.getUsername(), remeberMe);
    }

    @Override
    public void update(RemeberMe remeberMe) {
        remeberMeStore.put(remeberMe.getUsername(), remeberMe);
    }

    @Override
    public RemeberMe read(RemeberMe remeberMe) {
        return remeberMeStore.getIfPresent(remeberMe.getUsername());
    }

    @Override
    public void remove(String username) {
        remeberMeStore.invalidate(username);
    }

}
