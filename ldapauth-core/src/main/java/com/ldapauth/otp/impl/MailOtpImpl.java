/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
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


package com.ldapauth.otp.impl;

import com.ldapauth.otp.AbstractOtp;
import com.ldapauth.otp.code.AbstractOtpCodeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("mailOtp")
public class MailOtpImpl extends AbstractOtp {
    private static final Logger _logger = LoggerFactory.getLogger(MailOtpImpl.class);

    public MailOtpImpl(AbstractOtpCodeStore optTokenStore) {
        otpType = OtpTypes.EMAIL;
        this.optTokenStore = optTokenStore;
    }

	@Override
    public String produce(String subject,String otpMsgType) {
		String code = this.genCode(subject);
        this.optTokenStore.store(
        		subject,
        		code,
                subject,
                otpType,
                otpMsgType);
        _logger.debug("Mail Otp Authn {}" , code);
        return code;
    }

    @Override
    public boolean validate(String subject,String code, String otpMsgType) {
    	if(AbstractOtp.ENABLE_PASS && AbstractOtp.PASS_CODE.equals(code)) {
        	return true;
        }
        return this.optTokenStore.validate(subject, code,otpType,otpMsgType);
    }
}
