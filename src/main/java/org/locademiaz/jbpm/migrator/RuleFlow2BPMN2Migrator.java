package org.locademiaz.jbpm.migrator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.xml.SemanticModules;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.ProcessSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;

public class RuleFlow2BPMN2Migrator {
    
    public void convertToBpmn2(InputStream ruleFlowInputStream, OutputStream bpmn2OutputStream) throws SAXException, IOException {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf).addSemanticModule( new ProcessSemanticModule() );
        SemanticModules semanticModules = ((PackageBuilderConfiguration) conf).getSemanticModules();
        XmlProcessReader processReader = new XmlProcessReader(semanticModules );
        RuleFlowProcess p = (RuleFlowProcess) processReader.read( ruleFlowInputStream );
        bpmn2OutputStream.write( XmlBPMNProcessDumper.INSTANCE.dump(p).getBytes() );
    }
}
