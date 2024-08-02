


package com.ldapauth.web;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.ldapauth.crypto.Md5Sum;
import com.ldapauth.util.PathUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.arch.Processor;
import org.joda.time.DateTime;
import com.ldapauth.configuration.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * InitApplicationContext . 初始化应用上下文
 *
 * @author Crystal.Sea
 *
 */
public class InitializeContext extends HttpServlet {
    private static final Logger _logger = LoggerFactory.getLogger(InitializeContext.class);
    private static final long serialVersionUID = -797399138268601444L;
    ApplicationContext applicationContext;


    @Override
    public String getServletInfo() {
        return super.getServletInfo();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        WebContext.applicationContext = applicationContext;


        // List Environment Variables
        listEnvVars();

        listProperties();

        // List DatabaseMetaData Variables
        listDataBaseVariables();

        // Show License
        showLicense();
    }

    /**
    * InitApplicationContext.
    */
    public InitializeContext() {
        this.applicationContext =
                WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    }

    public InitializeContext(ConfigurableApplicationContext applicationContext) {
        if(applicationContext.containsBean("localeResolver") &&
                applicationContext.containsBean("cookieLocaleResolver")) {
            BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)applicationContext.getBeanFactory();
            beanFactory.removeBeanDefinition("localeResolver");
            beanFactory.registerBeanDefinition("localeResolver",
                    beanFactory.getBeanDefinition("cookieLocaleResolver"));
            _logger.debug("cookieLocaleResolver replaced localeResolver.");
        }
        this.applicationContext = applicationContext;
    }

    /**
     * listDataBaseVariables.
     */
    public void listDataBaseVariables() {
        if (applicationContext.containsBean("dataSource")) {
            try {
                _logger.debug("-----------------------------------------------------------");
                _logger.debug("List DatabaseMetaData Variables ");
                Connection connection =
                        ((javax.sql.DataSource) applicationContext.getBean("dataSource"))
                        .getConnection();

                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ApplicationConfig.databaseProduct = databaseMetaData.getDatabaseProductName();

                _logger.debug("DatabaseProductName   :   {}",
                         databaseMetaData.getDatabaseProductName());
                _logger.debug("DatabaseProductVersion:   {}" ,
                         databaseMetaData.getDatabaseProductVersion());
                _logger.trace("DatabaseMajorVersion  :   {}" ,
                         databaseMetaData.getDatabaseMajorVersion());
                _logger.trace("DatabaseMinorVersion  :   {}" ,
                         databaseMetaData.getDatabaseMinorVersion());
                _logger.trace("supportsTransactions  :   {}" ,
                         databaseMetaData.supportsTransactions());
                _logger.trace("DefaultTransaction    :   {}" ,
                         databaseMetaData.getDefaultTransactionIsolation());
                _logger.trace("MaxConnections        :   {}" ,
                         databaseMetaData.getMaxConnections());
                _logger.trace("");
                _logger.trace("JDBCMajorVersion      :   {}" ,
                         databaseMetaData.getJDBCMajorVersion());
                _logger.trace("JDBCMinorVersion      :   {}" ,
                         databaseMetaData.getJDBCMinorVersion());
                _logger.trace("DriverName            :   {}" ,
                         databaseMetaData.getDriverName());
                _logger.trace("DriverVersion         :   {}" ,
                         databaseMetaData.getDriverVersion());
                _logger.debug("");
                _logger.debug("DBMS  URL             :   {}" ,
                         databaseMetaData.getURL());
                _logger.debug("UserName              :   {}" ,
                         databaseMetaData.getUserName());
                _logger.debug("-----------------------------------------------------------");

            } catch (SQLException e) {
                e.printStackTrace();
                _logger.error("DatabaseMetaData Variables Error .",e);
            }
        }
    }

    /**
     * propertySourcesPlaceholderConfigurer.
     */
    public void listProperties() {
        if (applicationContext.containsBean("propertySourcesPlaceholderConfigurer")) {
            _logger.trace("-----------------------------------------------------------");
            _logger.trace("List Properties Variables ");
            PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =
                    ((PropertySourcesPlaceholderConfigurer) applicationContext
                    .getBean("propertySourcesPlaceholderConfigurer"));

            WebContext.properties =  (StandardEnvironment) propertySourcesPlaceholderConfigurer
                    .getAppliedPropertySources()
                    .get(PropertySourcesPlaceholderConfigurer.ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME)
                    .getSource();

            Iterator<PropertySource<?>> it =WebContext.properties.getPropertySources().iterator();
            while(it.hasNext()) {
            	 _logger.debug("propertySource {}" , it.next());
            }
            _logger.trace("-----------------------------------------------------------");
        }
    }

    /**
     * listEnvVars.
     */
    public void listEnvVars() {
        _logger.debug("-----------------------------------------------------------");
        _logger.debug("List Environment Variables ");
        Map<String, String> map = System.getenv();
        SortedSet<String> keyValueSet = new TreeSet<String>();
        for (Iterator<String> itr = map.keySet().iterator(); itr.hasNext();) {
            String key = itr.next();
            keyValueSet.add(key);
        }
        // out
        for (Iterator<String> it = keyValueSet.iterator(); it.hasNext();) {
            String key = (String) it.next();
            _logger.trace(key + "   =   {}" , map.get(key));
        }
        _logger.debug("APP_HOME" + "   =   {}" , PathUtils.getInstance().getAppPath());
        Processor.Type processorType = ArchUtils.getProcessor().getType();
        if (Objects.isNull(processorType)){
        	processorType = Processor.Type.UNKNOWN;
        }
        _logger.debug("OS      : {}({} {}), version {}",
                    SystemUtils.OS_NAME,
                    SystemUtils.OS_ARCH,
                    processorType,
                    SystemUtils.OS_VERSION

                );

        _logger.debug("COMPUTER: {}, USERNAME : {}",
                        map.get("COMPUTERNAME") ,
                        map.get("USERNAME")
                );
        _logger.debug("JAVA    :");
        _logger.debug("{} java version {}, class {}",
                        SystemUtils.JAVA_VENDOR,
                        SystemUtils.JAVA_VERSION,
                        SystemUtils.JAVA_CLASS_VERSION
                    );
        _logger.debug("{} (build {}, {})",
                        SystemUtils.JAVA_VM_NAME,
                        SystemUtils.JAVA_VM_VERSION,
                        SystemUtils.JAVA_VM_INFO
                    );

        _logger.debug("-----------------------------------------------------------");

    }

    /**
     * showLicense.
     */
    public void showLicense() {
        _logger.info("-----------------------------------------------------------");
        _logger.info("+                      ldapauth Enterprise Edition  ");
        _logger.info("+                           Version {}",
                        WebContext.properties.getProperty("application.formatted-version"));
        _logger.info("+");
        _logger.info("+                 {}Copyright 2024 - {} http://ldapauth.com/",
        			    (char)0xA9 , new DateTime().getYear()
        			);
        _logger.info("+                 .         All rights reserved         . ");
        _logger.info("-----------------------------------------------------------");
    }

}
