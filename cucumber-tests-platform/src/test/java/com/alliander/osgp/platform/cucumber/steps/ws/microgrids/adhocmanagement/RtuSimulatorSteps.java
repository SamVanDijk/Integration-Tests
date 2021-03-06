package com.alliander.osgp.platform.cucumber.steps.ws.microgrids.adhocmanagement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.iec61850.Iec61850MockServer;
import com.alliander.osgp.platform.cucumber.steps.ws.microgrids.MicrogridsStepsBase;

import cucumber.api.java.en.Given;

public class RtuSimulatorSteps extends MicrogridsStepsBase {

    private static final int NUMBER_OF_INPUTS_FOR_MOCK_VALUE = 3;
    private static final int INDEX_LOGICAL_DEVICE_NAME = 0;
    private static final int INDEX_NODE_NAME = 1;
    private static final int INDEX_NODE_VALUE = 2;

    @Autowired
    private Iec61850MockServer mockServer;

    @Given("^an rtu simulator returning$")
    public void anRtuSimulatorReturning(final List<List<String>> mockValues) throws Throwable {

        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);
            this.mockServer.mockValue(logicalDeviceName, node, value);
        }
    }
}
