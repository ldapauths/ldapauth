


package com.ldapauth.authn.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 未认证接口返回/auth/entrypoint
 * <p>
 * {
 * 	"status" :401 ,
 *  "error" :"Unauthorized"
 *  "message": "Unauthorized",
 *  "path" : "/"
 * }
 * </p>
 *
 * @author Crystal.Sea
 *
 */
@Controller
@RequestMapping(value = "/auth")
public class UnauthorizedEntryPoint {
	private static final Logger _logger = LoggerFactory.getLogger(UnauthorizedEntryPoint.class);

 	@RequestMapping(value={"/entrypoint"})
	public void entryPoint(
			HttpServletRequest request, HttpServletResponse response)
					throws StreamWriteException, DatabindException, IOException {
 		_logger.trace("UnauthorizedEntryPoint /entrypoint.");
 		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
 	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
 	    final Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("success", false);
 	    responseBody.put("code", HttpServletResponse.SC_UNAUTHORIZED);
 	    responseBody.put("message", "Unauthorized");
 	    responseBody.put("path", request.getServletPath());
 	    final ObjectMapper mapper = new ObjectMapper();
 	    mapper.writeValue(response.getOutputStream(), responseBody);
 	}
}
