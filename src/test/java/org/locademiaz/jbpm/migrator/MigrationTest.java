package org.locademiaz.jbpm.migrator;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.SAXException;

public class MigrationTest {

    @Test
    public void simpleOutputTest() throws SAXException, IOException{
        RuleFlow2BPMN2Migrator migrator = new RuleFlow2BPMN2Migrator();
        InputStream ruleflowInputStream = MigrationTest.class.getResourceAsStream( "/ruleflow.rf" );
        migrator.convertToBpmn2( ruleflowInputStream, System.out );
    }
}
