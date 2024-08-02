package com.ldapauth.authz.cas.endpoint.response;

import java.util.ArrayList;
import java.util.Iterator;

import com.ldapauth.web.HttpResponseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceResponseBuilder extends CasServiceResponse {
	final static Logger _logger = LoggerFactory.getLogger(ServiceResponseBuilder.class);

	@Override
	public String  serviceResponseBuilder() {
		String responseString = "";
		if(format.equalsIgnoreCase(HttpResponseConstants.FORMAT_TYPE.XML)){
			responseString= serviceResponseXmlBuilder();
		}else{
			responseString= serviceResponseJsonBuilder();
		}

		_logger.trace("Response String : "+responseString);
		return responseString;
	}

	public String  serviceResponseXmlBuilder() {
		StringBuffer responseResult=new StringBuffer("");
		responseResult.append("<cas:serviceResponse xmlns:cas=\"http://www.yale.edu/tp/cas\">");
		if(result){
			responseResult.append("<cas:authenticationSuccess>");
			responseResult.append("<cas:user>").append(user).append("</cas:user>");
			if(ticket!=null){
				responseResult.append("<cas:proxyGrantingTicket>").append(ticket).append("</cas:proxyGrantingTicket>");
			}
			if(!casAttributes.isEmpty()){
				responseResult.append("<cas:attributes>");
				 //采用Iterator遍历HashMap
		        Iterator<String> it = casAttributes.keySet().iterator();
		        while(it.hasNext()) {
		            String key = (String)it.next();
		            ArrayList<String> attrList=casAttributes.get(key);
		            //<cas:firstname>John</cas:firstname>
		            for(String value : attrList){
		            	responseResult.append("<cas:").append(key).append(">");
		            	responseResult.append(value);
		            	responseResult.append("</cas:").append(key).append(">\r\n");
		            }
		        }
		        responseResult.append("</cas:attributes>");
			}
			if(!proxies.isEmpty()){
				responseResult.append("<cas:proxies>");
				for(String proxy : proxies){
					responseResult.append("<cas:proxy>").append(proxy).append("</cas:proxy>");
				}
		        responseResult.append("</cas:proxies>");
			}
			responseResult.append("</cas:authenticationSuccess>");
		}else{
			responseResult.append("<cas:authenticationFailure code=\""+code+"\">");
			responseResult.append(this.description);
			responseResult.append("</cas:authenticationFailure>");
		}
		responseResult.append("</cas:serviceResponse>");
		return responseResult.toString();
	}

	public String  serviceResponseJsonBuilder() {
		StringBuffer responseResult=new StringBuffer("");
		responseResult.append("{\"serviceResponse\" :{");
		if(result){
			responseResult.append("\"authenticationSuccess\" : {");
			responseResult.append("\"user\" : \"").append(user).append("\"");
			if(ticket!=null){
				responseResult.append(",\"proxyGrantingTicket\" : \"").append(ticket).append("\"");
			}

			if(!casAttributes.isEmpty()){
				responseResult.append(",\"attributes\" : {");
				 //采用Iterator遍历HashMap
		        Iterator<String> it = casAttributes.keySet().iterator();
		        int attrCount=1;
		        while(it.hasNext()) {
		            String key = (String)it.next();
		            ArrayList<String> attrList=casAttributes.get(key);
		            if(attrCount==1){
		            	responseResult.append("\"").append(key).append("\":");
		            }else{
		            	responseResult.append(",\"").append(key).append("\":");
		            }
		            //<cas:firstname>John</cas:firstname>
		            String valueString="";
		            if(attrList.size()==1){
		            	valueString="\""+attrList.get(0)+"\"";
		            }else{
		            	int valueCount=1;
		            	valueString+="[";
			            for(String value : attrList){
			            	if(valueCount!=1){
			            		valueString+=",";
			            	}
			            	valueString+="\""+value+"\"";
			            	valueCount++;
			            }
			            valueString+="]";
		            }
		            responseResult.append(valueString);
		            attrCount++;
		        }
		        responseResult.append("}");
			}

			if(!proxies.isEmpty()){
				responseResult.append(",\"proxies\" : [ ");
				int proxyCount=1;
				for(String proxy : proxies){
					if(proxyCount!=1){
						responseResult.append(",");
					}
					responseResult.append("\"").append(proxy).append("\"");
					proxyCount++;
				}
		        responseResult.append("]");
			}
			responseResult.append("}");
		}else{
			responseResult.append("\"authenticationFailure\" : {");
			responseResult.append("\"code\" : \"").append(this.code).append("\"");
			responseResult.append(",\"description\" : \"").append(this.description).append("\"");
			responseResult.append("}");
		}
		responseResult.append("}");
		responseResult.append("}");
		return responseResult.toString();
	}

}
