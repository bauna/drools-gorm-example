package drools.gorm.example

import org.drools.SessionConfiguration
import org.drools.KnowledgeBase
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.KnowledgeBuilder
import org.drools.runtime.Environment
import org.drools.runtime.StatefulKnowledgeSession
import org.jbpm.process.instance.ProcessInstance
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory
import org.drools.gorm.processinstance.GormSignalManagerFactory
import org.drools.gorm.session.SingleSessionCommandService
import org.drools.gorm.processinstance.GormWorkItemManagerFactory
import org.drools.gorm.processinstance.GormProcessInstanceManagerFactory;

class JbpmService {

    static transactional = true

    def kstore

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
