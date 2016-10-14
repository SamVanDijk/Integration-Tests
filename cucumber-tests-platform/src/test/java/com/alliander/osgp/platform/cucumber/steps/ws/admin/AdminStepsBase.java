package com.alliander.osgp.platform.cucumber.steps.ws.admin;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;
import com.alliander.osgp.platform.cucumber.agents.ws.osgp.admin.DeviceManagementClient;

public abstract class AdminStepsBase extends SoapUiRunner {

	@Autowired
	protected DeviceManagementClient client;
	
    /**
     * Constructor.
     * The steps in this folder use the Admin soapui project.
     */
    protected AdminStepsBase() {
    	super("soap-ui-project/Admin-SoapUI-project.xml");
    }
}
