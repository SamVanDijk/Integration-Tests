/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.common;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.alliander.osgp.platform.cucumber.support.RunXpathResult;

/**
 * Class with generic response steps
 */
@Component
public class ResponseSteps {

    /**
     * Verify the response in case of an error.
     *
     * @param expectedResult
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public static void VerifyFaultResponse(final RunXpathResult runXpathResult, final String response,
            final Map<String, String> expectedResult) throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException {

        // First check the common stuff.
        if (expectedResult.containsKey("FaultCode")) {
            Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/faultcode",
                    expectedResult.get("FaultCode")));
        }
        if (expectedResult.containsKey("FaultString")) {
            Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/faultstring",
                    expectedResult.get("FaultString")));
        }

        // Now check the test case depended response types.
        if (expectedResult.containsKey("FaultType")) {
            if (expectedResult.get("FaultType").equals("ValidationError")) {
                final String[] validationErrors = expectedResult.get("ValidationErrors").split(";");
                // TODO: Validation exact number of validation errors to be the
                for (int i = 1; i <= validationErrors.length; i++) {
                    // TODO: Check validations ....
                }
            } else {
                // FunctionalError or TechnicalError
                if (expectedResult.containsKey("Code")) {
                    Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/detail/"
                            + expectedResult.get("FaultType") + "/Code", expectedResult.get("Code")));
                }
                if (expectedResult.containsKey("Message")) {
                    Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/detail/"
                            + expectedResult.get("FaultType") + "/Message", expectedResult.get("Message")));
                }
                if (expectedResult.containsKey("Component")) {
                    Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/detail/"
                            + expectedResult.get("FaultType") + "/Component", expectedResult.get("Component")));
                }
                if (expectedResult.containsKey("InnerException")) {
                    Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/detail/"
                            + expectedResult.get("FaultType") + "/InnerException", expectedResult.get("InnerException")));
                }
                if (expectedResult.containsKey("InnerMessage")) {
                    Assert.assertTrue(runXpathResult.assertXpath(response, "/Envelope/Body/Fault/detail/"
                            + expectedResult.get("FaultType") + "/InnerMessage", expectedResult.get("InnerMessage")));
                }
            }
        }
    }
}