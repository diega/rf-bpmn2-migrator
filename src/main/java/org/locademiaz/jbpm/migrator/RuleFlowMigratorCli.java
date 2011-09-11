package org.locademiaz.jbpm.migrator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RuleFlowMigratorCli {

    private static Logger logger = LoggerFactory.getLogger(RuleFlowMigratorCli.class);

    public static void main(String[] args) throws IOException {
        final OptionParser parser = new OptionParser();
        final OptionSpec<Void> helpOption = parser.acceptsAll(Arrays.asList("?", "help", "h"), "This help");
        final OptionSpec<File> ruleFlowsOption = parser.acceptsAll(Arrays.asList("i", "ruleFlows"), "List of ruleFlow files (using : as separator)")
                                                    .withRequiredArg().ofType(File.class).withValuesSeparatedBy(':');
        final OptionSpec<File> outputFolderOption = parser.acceptsAll(Arrays.asList("o", "outputFolder"), "Output folder for the generated .bpmn2 files")
                                                    .withRequiredArg().ofType(File.class);
        final OptionSet optionSet = parser.parse(args);

        if(optionSet.has(helpOption)){
            parser.printHelpOn(System.out);
            return;
        }

        final List<File> ruleFiles = optionSet.valuesOf(ruleFlowsOption);
        final File outputFolder = optionSet.valueOf(outputFolderOption);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        final RuleFlow2BPMN2Migrator migrator = new RuleFlow2BPMN2Migrator();
        for (File ruleFile : ruleFiles) {
            final String ruleFileName = ruleFile.getName();
            try {
                final String bpmn2OutputFileName = ruleFileName.replace(".rf", ".bpmn2");
                migrator.convertToBpmn2(new FileInputStream(ruleFile), new FileOutputStream(new File(outputFolder, bpmn2OutputFileName)));
                logger.info("CONVERTED: {} -> {}", ruleFileName, bpmn2OutputFileName);
            } catch (Exception e) {
                logger.info("ERROR: {}, message: {}", ruleFileName, e.getMessage());
                logger.error("ERROR: " + ruleFileName, e);
            }
        }
    }
}
