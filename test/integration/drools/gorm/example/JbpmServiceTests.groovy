package drools.gorm.example

import grails.test.*

class JbpmServiceTests extends GrailsUnitTestCase {

    def jbpmService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testHelloWorld() {
        assertNotNull(jbpmService)
        jbpmService.startProcess("com.bauna.droolsjbpm.gorm.hello")
    }
}
