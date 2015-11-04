package sk.kuzmisin.xtbgenerator;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.SourceFile;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class XtbGeneratorRunner {

    protected static Options options = new Options();

    static {
        options.addOption("projectId", true, "Project ID");
        options.addOption("lang", true, "Lang");
        options.addOption("js", true, "Input JS file, possibly with * or ** wildcards for glob search");
        options.addOption("translations_file", true, "XTB translation file");
        options.addOption("xtb_output_file", true, "XTB output file");
        options.addOption("flagfile", true, "A file containing additional command-line options");
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new GnuParser();

        try {
            CommandLine line = parser.parse(options, args);

            Collection<SourceFile> jsFiles = new ArrayList<>();

            // JS files from CLI option --js
            final String[] jsFileOptions = line.getOptionValues("js");

            // JS files from CLI arguments
            final String[] jsFileArgs = line.getArgs();

            // expand globs in input file paths
            List<String> jsFileNames = getJsFiles(
                    Arrays.asList(jsFileOptions),
                    Arrays.asList(jsFileArgs)
            );

            for (String jsFile : jsFileNames)
            {
                jsFiles.add(SourceFile.fromFile(jsFile));
            }

            final String projectId = line.getOptionValue("projectId");
            final String lang = line.getOptionValue("lang");
            final String translationFile = line.getOptionValue("translations_file");
            final String xtbOutputFile = line.getOptionValue("xtb_output_file");
            final String flagFile = line.getOptionValue("flagfile");

            if (flagFile != null)
            {
                processFlagFile(flagFile, jsFiles);
            }

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

    private static List<String> getJsFiles(List<String> js, List<String> arguments) throws IOException {
        List<String> patterns = new ArrayList<>();
        patterns.addAll(js);
        patterns.addAll(arguments);
        return CommandLineRunner.findJsFiles(patterns);
    }

    private static void processFlagFile(String flagFile, Collection<SourceFile> jsFiles) throws IOException {

        Path flagPath = Paths.get(flagFile);
        BufferedReader buffer = java.nio.file.Files.newBufferedReader(flagPath, UTF_8);

        String line;
        Pattern pattern = Pattern.compile("^--([a-zA-Z_]+)\\s+(.*)$");

        while ((line = buffer.readLine()) != null) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches() && matcher.group(1).equals("js"))
            {
                jsFiles.add(SourceFile.fromFile(matcher.group(2)));
            }
        }

        buffer.close();
    }

    protected static void usage(String errorMessage) {
        System.out.println("Usage: XtbGenerator --lang <arg> [--projectId <arg>] --js <FILE1> [--js <FILE2>]");
        System.out.println("or: XtbGenerator --lang <arg> [--projectId <arg>] FILE1 [FILE2]");
        System.out.println();

        System.out.println("Params:");
        System.out.println("\t--lang\t\t\t: " + options.getOption("lang").getDescription());
        System.out.println("\t--projectId\t\t: " + options.getOption("projectId").getDescription());
        System.out.println("\t--js\t\t\t: " + options.getOption("js").getDescription());
        System.out.println("\t--translations_file\t: " + options.getOption("translations_file").getDescription());
        System.out.println("\t--xtb_output_file\t: " + options.getOption("xtb_output_file").getDescription());
        System.out.println("\t--flagfile\t\t: " + options.getOption("flagfile").getDescription());

        if (errorMessage != null) {
            System.err.println();
            System.err.println("Error: " + errorMessage);
        }

        System.exit(2);
    }
}