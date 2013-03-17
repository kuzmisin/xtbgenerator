package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.SourceFile;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

class XtbGeneratorRunner {

    protected static Options options = new Options();

    static {
        options.addOption("projectId", true, "Project ID");
        options.addOption("lang", true, "Lang");
        options.addOption("js", true, "Input JS file");
        options.addOption("translations_file", true, "XTB translation file");
        options.addOption("xtb_output_file", true, "XTB output file");
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new GnuParser();

        try {
            CommandLine line = parser.parse(options, args);

            Collection<SourceFile> jsFiles = new ArrayList<>();

            // JS files from CLI option --js
            final String[] jsFileOptions = line.getOptionValues("js");
            if (jsFileOptions != null) {
                for (int i = 0; i < jsFileOptions.length; i++) {
                    jsFiles.add(SourceFile.fromFile(jsFileOptions[i]));
                }
            }

            // JS files from CLI arguments
            final String[] jsFileArgs = line.getArgs();
            if (jsFileArgs != null) {
                for (int i = 0; i < jsFileArgs.length; i++) {
                    jsFiles.add(SourceFile.fromFile(jsFileArgs[i]));
                }
            }

            final String projectId = line.getOptionValue("projectId");
            final String lang = line.getOptionValue("lang");
            final String translationFile = line.getOptionValue("translations_file");
            final String xtbOutputFile = line.getOptionValue("xtb_output_file");

            if (lang == null) {
                usage("lang cannot be empty");

            } else if (jsFiles.isEmpty()) {
                usage("no JS file(s) to parse ...");

            } else {
                XtbGenerator.process(lang, projectId, jsFiles, translationFile, xtbOutputFile);
            }

        } catch (ParseException e) {
            usage(e.getMessage());
        }
    }

    protected static void usage(String errorMessage) {
        System.out.println("Usage: XtbGenerator --lang <arg> [--projectId <arg>] --js <FILE1> [--js <FILE2>]");
        System.out.println("or: XtbGenerator --lang <arg> [--projectId <arg>] FILE1 [FILE2]");
        System.out.println();

        System.out.println("Params:");
        System.out.println("\t--lang\t" + options.getOption("lang").getDescription());
        System.out.println("\t--projectId" + options.getOption("projectId").getDescription());
        System.out.println("\t--js\t" + options.getOption("js").getDescription());
        System.out.println("\t--translations_file\t" + options.getOption("translations_file").getDescription());
        System.out.println("\t--xtb_output_file\t" + options.getOption("xtb_output_file").getDescription());

        if (errorMessage != null) {
            System.out.println();
            System.out.println("Error: " + errorMessage);
        }

        System.exit(2);
    }
}