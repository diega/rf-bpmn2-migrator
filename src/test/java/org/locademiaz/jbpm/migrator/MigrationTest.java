package org.locademiaz.jbpm.migrator;

import java.io.*;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MigrationTest {

    @Test
    public void simpleOutputTest() throws SAXException, IOException{
        RuleFlow2BPMN2Migrator migrator = new RuleFlow2BPMN2Migrator();
        InputStream ruleflowInputStream = MigrationTest.class.getResourceAsStream( "/ruleflow.rf" );
        migrator.convertToBpmn2( ruleflowInputStream, System.out );
    }

    @Test
    public void testWorkItemMigration() throws IOException, SAXException {
        final String processId = "com.sample.ruleflow";
        final String workItemName = "Some Work";
        final String processFileName = "/ruleflow.rf";
        final StringBuilder workItemValue = new StringBuilder();

        startProcessWithWorkItem(
            MigrationTest.class.getResourceAsStream(processFileName),
            ResourceType.DRF,
            processId,
            workItemName,
            new WorkItemHandler() {
                @Override
                public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                    workItemValue.append(workItem.getParameter("workItemParam1"));
                    manager.completeWorkItem(workItem.getId(), null);
                }

                @Override
                public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                }
            }
        );

        final ByteArrayOutputStream bpmn2OutputStream = new ByteArrayOutputStream();
        new RuleFlow2BPMN2Migrator().convertToBpmn2(MigrationTest.class.getResourceAsStream(processFileName), bpmn2OutputStream);

        startProcessWithWorkItem(
            new ByteArrayInputStream(bpmn2OutputStream.toByteArray()),
            ResourceType.BPMN2,
            processId,
            workItemName,
            new WorkItemHandler() {
                @Override
                public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                    Assert.assertEquals(workItemValue.toString(), workItem.getParameter("workItemParam1"));
                    manager.completeWorkItem(workItem.getId(), null);
                }

                @Override
                public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                }
            }
        );
    }

    private void startProcessWithWorkItem(InputStream process,
                                          ResourceType processType,
                                          String processId,
                                          String workItemName,
                                          WorkItemHandler handler) {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(process), processType);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);
        ksession.startProcess(processId);
        ksession.dispose();
    }
}
