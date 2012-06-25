/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jaggeryjs.jaggery.app.mgt;


import org.wso2.carbon.CarbonException;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  A holder for deployed & faulty webapps
 */
@SuppressWarnings("unused")
public class JaggeryApplicationsHolder {

    /**
     * The directory containing the webapps
     */
    private File webappsDir;

    /**
     * All successfully deployed webapps
     */
    private Map<String, JaggeryApplication> startedWebapps =
            new ConcurrentHashMap<String, JaggeryApplication>();

    /**
     * All undeployed webapps
     */
    private Map<String, JaggeryApplication> stoppedWebapps =
            new ConcurrentHashMap<String, JaggeryApplication>();

    /**
     * All faulty webapps
     */
    private Map<String, JaggeryApplication> faultyWebapps =
            new ConcurrentHashMap<String, JaggeryApplication>();

    public JaggeryApplicationsHolder(File webappsDir) {
        this.webappsDir = webappsDir;
    }

    public Map<String, JaggeryApplication> getStartedWebapps() {
        return startedWebapps;
    }

    public Map<String, JaggeryApplication> getFaultyWebapps() {
        return faultyWebapps;

    }

    public Map<String, JaggeryApplication> getStoppedWebapps() {
        return stoppedWebapps;
    }

    public void stopWebapp(JaggeryApplication webapp) throws CarbonException {
        boolean stopped = webapp.stop();
        if (stopped) {
            String fileName = webapp.getWebappFile().getName();
            startedWebapps.remove(fileName);
            stoppedWebapps.put(fileName, webapp);
        }
    }

    public void undeployWebapp(JaggeryApplication webapp) throws CarbonException {
        webapp.undeploy();
        String fileName = webapp.getWebappFile().getName();
        startedWebapps.remove(fileName);
        stoppedWebapps.remove(fileName);
        faultyWebapps.remove(fileName);
    }

    public File getWebappsDir() {
        return webappsDir;
    }
}

