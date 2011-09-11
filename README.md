RuleFlow to BPMN2 migrator
==========================
Simple tool for migrating your JBoss Drools RuleFlow process definition files to the now standard BPMN2 definition.

Running
-------
You can package the migrator and run the org.locademiaz.jbpm.migrator.RuleFlowMigratorCli MainClass or directly via maven using the exec:java plugin:goal. For arguments description try --help option, for example: 
     mvn exec:java -Dexec.args="--help"

