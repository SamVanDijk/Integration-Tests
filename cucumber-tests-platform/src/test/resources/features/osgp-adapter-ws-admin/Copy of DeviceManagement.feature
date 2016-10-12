Feature: Device management 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...
    
@OslpMockServer
Scenario: Find device authorizations
    Given a device 
        | DeviceIdentification | TEST1024000000002 		 |
        | Active               | True              		 | 
    And a device authorization
    		| DeviceIdentification | TEST1024000000002 		 |
    		| DeviceFunctionGroup  | OWNER                 |
    And a device authorization
    		| DeviceIdentification | TEST1024000000002 		 |
    		| DeviceFunctionGroup  | CONFIGURATION         |
    And a device authorization
    		| DeviceIdentification | TEST1024000000002 		 |
    		| DeviceFunctionGroup  | SCHEDULING            |
   When receiving a find device authorizations request
        | DeviceIdentification | TEST1024000000002 		 |
   Then the find device authorizations response contains
        | DeviceIdentification | TEST1024000000002 		 |
        | DeviceFunctionGroup | OWNER                  |
    And the find device authorizations response contains
        | DeviceIdentification | TEST1024000000002 		 |
        | DeviceFunctionGroup | CONFIGURATION          |
