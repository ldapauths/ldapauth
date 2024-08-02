


package com.ldapauth.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ldapauth.util.DateUtils;
import com.ldapauth.util.IdGenerator;
import org.apache.commons.logging.LogFactory;
import com.ldapauth.configuration.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Application is common class for Web Application Context.
 *
 * @author Crystal.Sea
 * @since 1.5
 */
/**
 * @author shimi
 *
 */
public final class WebContext {

    final static Logger _logger = LoggerFactory.getLogger(WebContext.class);

    public static StandardEnvironment properties;

    public static ApplicationContext applicationContext;

    public final static String  ipAddressRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    public static ArrayList<String> sessionAttributeNameList = new ArrayList<String>();

    public static ArrayList<String> logoutAttributeNameList = new ArrayList<String>();

    public static IdGenerator idGenerator;

    static {
        sessionAttributeNameList.add(WebConstants.AUTHENTICATION);

        sessionAttributeNameList.add(WebConstants.AUTHORIZE_SIGN_ON_APP);
        sessionAttributeNameList.add(WebConstants.AUTHORIZE_SIGN_ON_APP_SAMLV20_ADAPTER);

        sessionAttributeNameList.add(WebConstants.CURRENT_USER_PASSWORD_SET_TYPE);

        sessionAttributeNameList.add(WebConstants.CURRENT_INST);

        sessionAttributeNameList.add(WebConstants.FIRST_SAVED_REQUEST_PARAMETER);

        //logout
        logoutAttributeNameList.add(WebConstants.AUTHENTICATION);

        logoutAttributeNameList.add(WebConstants.AUTHORIZE_SIGN_ON_APP);
        logoutAttributeNameList.add(WebConstants.AUTHORIZE_SIGN_ON_APP_SAMLV20_ADAPTER);

        logoutAttributeNameList.add(WebConstants.CURRENT_USER_PASSWORD_SET_TYPE);


        logoutAttributeNameList.add(WebConstants.FIRST_SAVED_REQUEST_PARAMETER);

    }

    /**
     * clear session Message ,session id is Constants.MESSAGE
     *
     * @see WebConstants.MESSAGE
     */
    public static void clearMessage() {
        removeAttribute(WebConstants.CURRENT_MESSAGE);
    }



    /**
     * get ApplicationContext from web  ServletContext configuration
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext(){
        return WebApplicationContextUtils.getWebApplicationContext(getSession().getServletContext());
    }

    /**
     * get bean from spring configuration by bean id
     * @param id
     * @return Object
     */
    public static Object getBean(String name){
        if(applicationContext == null) {
            return getApplicationContext().getBean(name);
        }else {
            return applicationContext.getBean(name);
        }
    }

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException{
    	if(applicationContext==null) {
            return getApplicationContext().getBean(name,requiredType);
        }else {
            return applicationContext.getBean(name,requiredType);
        }
    };

    // below method is common HttpServlet method
    /**
     * get Spring HttpServletRequest.
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getResponse();
    }


    /**
     * get Http Context full Path.
     *
     * @return String HttpContextPath
     */
    public static String getContextPath(boolean isContextPath) {
        HttpServletRequest httpServletRequest = WebContext.getRequest();
        return getContextPath(httpServletRequest,isContextPath);
    }

    /**
     * get Http Context full Path,if port equals 80 or 443 is omitted.
     *
     * @return String eg:http://192.168.1.20:9080/webcontext or
     *         http://www.website.com/webcontext
     */
    public static String getContextPath(HttpServletRequest request,boolean isContextPath) {
    	String fullRequestUrl = UrlUtils.buildFullRequestUrl(request);

        StringBuilder url = new StringBuilder(fullRequestUrl.substring(0, fullRequestUrl.indexOf(request.getContextPath())));

        if(isContextPath) {
        	url.append(request.getContextPath());
        }
        _logger.trace("http ContextPath {}" , url);
        return url.toString();

    }

    /**
     * isTraceEnabled print request headers and parameters<br>
     * see WebInstRequestFilter
     * @param request
     */
    public static void printRequest(final HttpServletRequest request) {
		_logger.info("getContextPath : {}"  , request.getContextPath());
    	_logger.info("getRequestURL : {} " , request.getRequestURL());
		_logger.info("URL : {}" , request.getRequestURI().substring(request.getContextPath().length()));
    	_logger.info("getMethod : {} " , request.getMethod());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
          String key = (String) headerNames.nextElement();
          String value = request.getHeader(key);
          _logger.info("Header key {} , value {}" , key, value);
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
          String key = (String) parameterNames.nextElement();
          String value = request.getParameter(key);
          _logger.info("Parameter {} , value {}",key , value);
        }
    }

    /**
     * get current Session.
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * get current Session,if no session ,new Session created.
     *
     * @return HttpSession
     */
    public static HttpSession getSession(boolean create) {
        System.out.println("new Session created");
        return getRequest().getSession(create);
    }

    /**
     * set Attribute to session ,Attribute name is name,value is value.
     *
     * @param name String
     * @param value String
     */
    public static void setAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    /**
     * get Attribute from session by name.
     *
     * @param name String
     * @return
     */
    public static Object getAttribute(String name) {
        return getSession().getAttribute(name);
    }

    /**
     * remove Attribute from session by name.
     *
     * @param name String
     */
    public static void removeAttribute(String name) {
        getSession().removeAttribute(name);
    }

    /**
     * get Request Parameter by name.
     *
     * @param name String
     * @return String
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }


    /**
     * encoding encodingString by ApplicationConfig.
     *
     * @param encodingString String
     * @return encoded String
     */
    public static String encoding(String encodingString) {
        ApplicationConfig applicationConfig = getBean("applicationConfig",ApplicationConfig.class);
        return applicationConfig.getCharacterEncodingConfig().encoding(encodingString);
    }

    /**
     * get locale from Spring Resolver,if locale is null,get locale from Spring.
     * SessionLocaleResolver this is from internationalization
     *
     * @return Locale
     */
    public static Locale getLocale() {
        Locale locale = null;
        try {
            CookieLocaleResolver cookieLocaleResolver =
            			getBean("localeResolver",CookieLocaleResolver.class);
            locale = cookieLocaleResolver.resolveLocale(getRequest());

        } catch (Exception e) {
            LogFactory.getLog(WebContext.class).debug("getLocale() error . ");
            e.printStackTrace();
            locale = RequestContextUtils.getLocale(getRequest());
        }

        return locale;
    }

    public static Map<String, String> getRequestParameterMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String[]> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            map.put(key, values != null && values.length > 0 ? values[0] : null);
        }
        return map;
    }

    /**
     * 根据名字获取cookie.
     *
     * @param request HttpServletRequest
     * @param name  cookie名字
     * @return Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = getCookieAll(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面.
     *
     * @param request HttpServletRequest
     * @return Map
     */
    private static Map<String, Cookie> getCookieAll(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 保存Cookies.
     *
     * @param response response响应
     * @param name cookie的名字
     * @param value cookie的值
     * @param time cookie的存在时间
     */
    public static HttpServletResponse setCookie(
            HttpServletResponse response, String domain ,String name, String value, int time) {
        // new一个Cookie对象,键值对为参数
        Cookie cookie = new Cookie(name, value);
        // tomcat下多应用共享
        cookie.setPath("/");
        if(domain != null) {
            cookie.setDomain(domain);
        }
        // 如果cookie的值中含有中文时，需要对cookie进行编码，不然会产生乱码
        try {
            URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 单位：秒
        if(time >= 0) {
            cookie.setMaxAge(time);
        }
        // 将Cookie添加到Response中,使之生效
        response.addCookie(cookie); // addCookie后，如果已经存在相同名字的cookie，则最新的覆盖旧的cookie
        return response;
    }

    public static HttpServletResponse expiryCookie(
            HttpServletResponse response, String domain ,String name, String value) {
        WebContext.setCookie(response,domain,name, value,0);
        return response;
    }

    public static HttpServletResponse setCookie(
            HttpServletResponse response, String domain ,String name, String value) {
        WebContext.setCookie(response,domain,name, value,-1);
        return response;
    }

    /**
     * get Current Date,eg 2012-07-10.
     *
     * @return String
     */
    public static String getCurrentDate() {
        return DateUtils.getCurrentDateAsString(DateUtils.FORMAT_DATE_YYYY_MM_DD);
    }

    /**
     * get System Menu RootId,root id is constant.
     *
     * @return String
     */
    public static String getSystemNavRootId() {
        return "100000000000";
    }

    /**
     * get Request IpAddress,for current Request.
     *
     * @return String,100.167.216.100
     */
    public static final String getRequestIpAddress() {
        return getRequestIpAddress(getRequest());
    }

    /**
     * get Request IpAddress by request.
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static final String getRequestIpAddress(HttpServletRequest request) {
        String requestIpAddress = request.getHeader("x-forwarded-for");
        if (requestIpAddress == null || requestIpAddress.length() == 0 || "unknown".equalsIgnoreCase(requestIpAddress)) {
        	requestIpAddress = request.getHeader("Proxy-Client-IP");
        }
        if (requestIpAddress == null || requestIpAddress.length() == 0 || "unknown".equalsIgnoreCase(requestIpAddress)) {
        	requestIpAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (requestIpAddress == null || requestIpAddress.length() == 0 || "unknown".equalsIgnoreCase(requestIpAddress)) {
        	requestIpAddress = request.getRemoteAddr();
        }
        if(requestIpAddress.indexOf(",")>-1) {
        	String[] ipAddress = requestIpAddress.split(",");
        	requestIpAddress = ipAddress[0];
        }
        _logger.trace("getRequestIpAddress() RequestIpAddress: {}" , requestIpAddress);
        return requestIpAddress;
    }

    /**
     * captchaValid.
     * @param captcha String
     * @return
     */
    public static boolean captchaValid(String captcha) {
        if (captcha == null || !captcha
                .equals(WebContext.getSession().getAttribute(
                        WebConstants.KAPTCHA_SESSION_KEY).toString())) {
            return false;
        }
        return true;
    }

    /**
     * getI18nValue.
     *  @param code String
     * @return
     */
    public static String getI18nValue(String code) {
        String message = code;
        try {
            message = getApplicationContext().getMessage(
                code.toString(),
                null,
                getLocale());
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
        return message;
    }

    /**
     * getI18nValue.
     * @param code String
     * @param filedValues Object
     * @return
     */
    public static String getI18nValue(String code, Object[] filedValues) {
        String message = code;
        try {
            message = getApplicationContext().getMessage(
                code.toString(),
                filedValues,
                getLocale());
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
        return message;
    }

    /**
     * getRequestLocale.
     * @return
     */
    public static String getRequestLocale() {
        return "";
    }

    /**
     * generate random Universally Unique Identifier,delete -.
     *
     * @return String
     */
    public static String genId() {
    	if(idGenerator == null) {
    		idGenerator = new IdGenerator();
    	}
        return idGenerator.generate();
    }

    public static ModelAndView redirect(String redirectUrl) {
        return new ModelAndView("redirect:" + redirectUrl);
    }

    public static ModelAndView forward(String forwardUrl) {
        return new ModelAndView("forward:" + forwardUrl);
    }
}
