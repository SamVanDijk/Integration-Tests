Feature: Organization management
  As a ...
  I want to manage the organizations in the platform
  In order ...

  Scenario: Create a new organization
    When receiving a create organization request
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | Prefix                     | MAA                 |
      | FunctionGroup              | ADMIN               |
      | Enabled                    | True                |
      | Domains                    | COMMON              |
    Then the create organization response is successfull
    And the entity organization exists
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | Prefix                     | MAA                 |
      | FunctionGroup              | ADMIN               |
      | Enabled                    | true                |
      | Domains                    | COMMON;             |

  Scenario: Create an already existing organization
    Given an organization
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | Prefix                     | MAA                 |
      | FunctionGroup              | ADMIN               |
      | Enabled                    | True                |
      | Domains                    | COMMON              |
    When receiving a create organization request
      | OrganizationIdentification | ATestOrganization      |
      | Name                       | Different Organization |
      | Prefix                     | MAA                    |
      | FunctionGroup              | ADMIN                  |
      | Enabled                    | True                   |
      | Domains                    | COMMON                 |
    Then the create organization response contains
      | FaultCode      | SOAP-ENV:Server                                                                                                               |
      | FaultString    | com.alliander.osgp.shared.exceptionhandling.TechnicalException                                                                |
      | FaultType      | TechnicalFault                                                                                                                |
      | Component      | WS_ADMIN                                                                                                                      |
      | InnerException | org.springframework.transaction.TransactionSystemException                                                                    |
      | InnerMessage   | Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Transaction marked as rollbackOnly |
    And the organization with name "Different Organization" should not be created

  # For this a special test is created in the SoapUI project, which uses the Unknown Organization as an organizationIdentification in the header.
  Scenario: Create an organization as an unauthorized organization
    When receiving a create organization request as an unauthorized organization
      | OrganizationIdentification | ATestOrganization |
      | Name                       | An Organization   |
      | Prefix                     | MAA               |
      | FunctionGroup              | ADMIN             |
      | Enabled                    | True              |
      | Domains                    | COMMON            |
    Then the create organization response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_ORGANISATION                                             |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              101 |
      | Message        | UNKNOWN_ORGANISATION                                             |
      | Component      | WS_ADMIN                                                         |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "unknown-organization" could not be found.  |
    And the organization with name "An Organization" should not be created

  Scenario Outline: Creating an organization with an invalid organization identification
    When receiving a create organization request
      | OrganizationIdentification | <Invalid Organization Identification> |
      | Name                       | A Test Organization                   |
      | Prefix                     | MAA                                   |
      | FunctionGroup              | ADMIN                                 |
      | Enabled                    | True                                  |
      | Domains                    | COMMON                                |
    Then the create organization response contains
      | FaultCode        | SOAP-ENV:Client     |
      | FaultString      | Validation error    |
      | FaultType        | ValidationError     |
      | ValidationErrors | <Validation Errors> |
    And the organization with name "A Test Organization" should not be created

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | Invalid Organization Identification | Validation Errors                                                                                                                                                                                                                           |
      | A Test Organization                 | cvc-pattern-valid: Value 'A Test Organization' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value 'A Test Organization' of element 'ns1:OrganisationIdentification' is not valid. |
      |                                     | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns1:OrganisationIdentification' is not valid.                          |

  Scenario: Remove an existing organization
    Given an organization
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | Prefix                     | MAA                 |
      | FunctionGroup              | ADMIN               |
      | Enabled                    | True                |
      | Domains                    | COMMON              |
    When receiving a remove organization request
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | FunctionGroup              | ADMIN               |
    Then the remove organization response is successfull
    And the organization with organization identification "ATestOrganization" should be disabled

  Scenario: Remove a non existing organization
    When receiving a remove organization request
      | OrganizationIdentification | ATestOrganization   |
      | Name                       | A Test Organization |
      | FunctionGroup              | ADMIN               |
    Then the remove organization response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_ORGANISATION                                             |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              101 |
      | Message        | UNKNOWN_ORGANISATION                                             |
      | Component      | WS_ADMIN                                                         |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "ATestOrganization" could not be found.     |
