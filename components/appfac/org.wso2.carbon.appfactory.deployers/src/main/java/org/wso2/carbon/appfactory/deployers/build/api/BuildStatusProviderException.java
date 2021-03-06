/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.appfactory.deployers.build.api;

/**
 * Exception to hanlde erroes when accessing Jenkins APIs
 * 
 * @author shamika
 *
 */
public class BuildStatusProviderException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public static final int INVALID_API = 100;

    public static final int INVALID_RESPONSE = 101;
    
    private int code = 0;
    
    public BuildStatusProviderException(String msg) {
	    super(msg);
    }
    
    public BuildStatusProviderException(String msg, int code) {
    	super(msg);
    	this.code = code;
    }
    
    /**
     * Returns the error code.
     * @return 0 - default
     */
    public int getCode(){
    	return this.code;
    }
    
}
