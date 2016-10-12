Feature: Device management 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...
    
Scenario: Activate a device
  	Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | Active               | False             | 
     When receiving a activate device request
        | DeviceIdentification | TEST1024000000001 |
	   Then the activate device response contains
	      | Result | OK |
	    And the device with device identification "TEST1024000000001" should be active

Scenario: Deactivate a device
    Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | Active               | True              | 
     When receiving a deactivate device request
        | DeviceIdentification | TEST1024000000001 |
     Then the deactivate device response contains
        | Result | OK |
      And the device with device identification "TEST1024000000001" should be inactive

# This scenario tests a find device based on all authorisations and all function groups.
# Organisation is never filled in so the same organisation is being used.
Scenario Outline: Find device authorizations
    Given a device 
        | DeviceIdentification | TEST1024000000002 		 |
        | Active               | True              		 | 
    And a device authorization
    		| DeviceIdentification | TEST1024000000002 		 |
    		| DeviceFunctionGroup  | <DeviceFunctionGroup> |
     When receiving a find device authorizations request
        | DeviceIdentification | TEST1024000000002 		 |
     Then the find device authorizations response contains
        | DeviceIdentification | TEST1024000000002 		 |
        | deviceAuthorisations | <DeviceFunctionGroup> |

			# These examples work for above scenario ONLY. 
      Examples:
        | DeviceFunctionGroup |
        | OWNER               |
        | INSTALLATION        |
        | AD_HOC              |
        | MANAGEMENT          |
        | FIRMWARE            |
        | SCHEDULING          |
        | TARIFF_SCHEDULING   |
        | CONFIGURATION       |
        | MONITORING          |


# This scenario tests a find device based on all authorisations and all function groups.
# Organisation is never filled in so the same organisation is being used.
Scenario Outline: Find device authorizations
    Given a device 
        | DeviceIdentification | TEST1024000000003 		 												|
        | Active               | True              		 												| 
    And a device authorization
    		| DeviceIdentification | TEST1024000000003 		 												|
    		| DeviceFunctionGroup  | <DeviceFunctionGroup> 												|
    And a device authorization
    		| DeviceIdentification | TEST1024000000003 		 												|
    		| DeviceFunctionGroup  | <DeviceFunctionGroup2> 											|
     When receiving a find device authorizations request
        | DeviceIdentification | TEST1024000000003 		 												|
     Then the find device authorizations response contains
        | DeviceIdentification | TEST1024000000003 		 												|
        | deviceAuthorisations | <DeviceFunctionGroup>,<DeviceFunctionGroup2> |

			# These examples work for above scenario ONLY. 
      Examples:
        | DeviceFunctionGroup |DeviceFunctionGroup2 |
        | OWNER               |OWNER                |
        | INSTALLATION        |INSTALLATION         |
        | AD_HOC              |AD_HOC               |
        | MANAGEMENT          |MANAGEMENT           |
        | FIRMWARE            |FIRMWARE             |
        | SCHEDULING          |SCHEDULING           |
        | TARIFF_SCHEDULING   |TARIFF_SCHEDULING    |
        | CONFIGURATION       |CONFIGURATION        |
        | MONITORING          |MONITORING           |