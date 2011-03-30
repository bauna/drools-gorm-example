package drools.gorm.example

import org.drools.KnowledgeBase
import org.drools.KnowledgeBaseFactory
import org.drools.SessionConfiguration
import org.drools.builder.KnowledgeBuilder
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.gorm.processinstance.GormProcessInstanceManagerFactory
import org.drools.gorm.processinstance.GormSignalManagerFactory
import org.drools.gorm.processinstance.GormWorkItemManagerFactory
import org.drools.gorm.session.SingleSessionCommandService
import org.drools.io.ResourceFactory
import org.drools.runtime.Environment
import org.drools.runtime.StatefulKnowledgeSession
import org.jbpm.process.instance.ProcessInstance

/**
 * JBPM Service example class
 */
class JbpmService {

    static transactional = true

    /** The kstore is automatically declared by the drools-gorm plugin */
    def kstore

    /**
     * Returns a SessionConfiguration containing Drools services
     * implementations for Grails.
     *
     * @return a SessionConfiguration containing Drools services
     * implementations for Grails.
     */
    def getGORMSessionConfig() {
        Properties properties = new Properties();

        properties.setProperty("drools.commandService",
                SingleSessionCommandService.class.getName());
        properties.setProperty("drools.processInstanceManagerFactory",
                GormProcessInstanceManagerFactory.class.getName());
        properties.setProperty("drools.workItemManagerFactory",
                GormWorkItemManagerFactory.class.getName());
        properties.setProperty("drools.processSignalManagerFactory",
                GormSignalManagerFactory.class.getName());

        return new SessionConfiguration(properties);
    }

    /**
     * Construct a kbase with an example flow.
     *
     * @return a KnowledgeBase containing an example flow.
     */
    def KnowledgeBase createKbase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newClassPathResource("helloWord.rf"),
                ResourceType.BPMN2);

        if (kbuilder.hasErrors()) {
            throw new IllegalStateException("error compiling 'helloWord.rf':\n"
                    + kbuilder.errors);
        }

        return kbuilder.newKnowledgeBase();
    }

    /**
     * Start the a process.
     *
     * @param processId  the process id to start.
     * @return the id of the process instance.
     */
    def startProcess(String processId) {

        Environment env = KnowledgeBaseFactory.newEnvironment()

        StatefulKnowledgeSession ksession = kstore.newStatefulKnowledgeSession(createKbase(),
                getGORMSessionConfig(),
                env)

        ProcessInstance pi = ksession.startProcess(processId)

        def pid = pi.id
        ksession.dispose()
        return pid
    }
}
