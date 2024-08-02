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


package com.ldapauth.constants;

import com.ldapauth.configuration.ApplicationConfig;

public class ConstsDatabase {

    public static String  MYSQL         	= "MySQL";

    public static String  POSTGRESQL    	= "PostgreSQL";

    public static String  ORACLE        	= "Oracle";

    public static String  MSSQLSERVER   	= "SQL Server";

    public static String  DB2           	= "db2";

    public static boolean compare(String databaseProduct) {
        if(databaseProduct.equalsIgnoreCase(ApplicationConfig.databaseProduct)) {
            return true;
        }
        return false;
    }

}
