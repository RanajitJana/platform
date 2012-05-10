package org.wso2.automation.common.test.esb.util;

import org.wso2.carbon.admin.service.AdminServiceSynapseConfigAdmin;
import org.wso2.platform.test.core.ProductConstant;
import org.wso2.platform.test.core.utils.endpointutils.EsbEndpointSetter;
import org.wso2.platform.test.core.utils.environmentutils.ManageEnvironment;

import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Class used to upload synapse config to the ESB
 */
public class ConfigUploader {

    /**
     * changing axis2 server configurations with application server details.
     *
     * @param environmentObj Environment Object
     * @param configFileName File Name need to be upload
     * @throws XMLStreamException Exception
     * @throws ServletException   Exception
     * @throws RemoteException    Exception
     */
    public ConfigUploader(ManageEnvironment environmentObj, String configFileName)
            throws XMLStreamException, ServletException, IOException, MalformedURLException {
        XMLStringTransformer xmlStringTransformer = new XMLStringTransformer();
        String resourcePath = ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME) + File.separator + "mediatorconfig" + File.separator;
        AdminServiceSynapseConfigAdmin synapseConfigAdmin = new AdminServiceSynapseConfigAdmin(environmentObj.getEsb().getSessionCookie(), environmentObj.getEsb().getBackEndUrl());
        String bufferConfig = xmlStringTransformer.convertXMLFileToString(resourcePath + configFileName);
        EsbEndpointSetter endpointSetter = new EsbEndpointSetter();
        URL mediatorURL = new URL("file://"+resourcePath + configFileName);
        DataHandler proxyHandler = new DataHandler(mediatorURL);

        if (bufferConfig.contains("localhost")) {
            bufferConfig = bufferConfig.replaceAll("localhost", environmentObj.getAs().getProductVariables().getHostName());
        }
        if (bufferConfig.contains("9000")) {
            bufferConfig = bufferConfig.replaceAll("9000", environmentObj.getAs().getProductVariables().getHttpPort());
        }
        try {
            Thread.sleep(5000);
            synapseConfigAdmin.updateConfiguration(endpointSetter.setEndpointURL(proxyHandler).toString());
            //Thread.sleep(environmentBuilder.getFrameworkSettings().getEnvironmentVariables().getDeploymentDelay());
            // cant set environment delay.because no meaningful of delay 60000 ms every test case.
            Thread.sleep(30000);
        } catch (InterruptedException ignored) {
        }
    }


}