package sk.kuzmisin.xtbgenerator;

import com.google.javascript.jscomp.*;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class XtbGenerator {

    protected String lang;

    protected String projectId;

    protected Collection<SourceFile> jsFiles;

    protected String translationFile;

    protected String xtbOutputFile;

    public static void process(String lang, String projectId, Collection<SourceFile> jsFiles, String translationFile, String xtbOutputFile)
            throws IOException {

        final XtbGenerator xtbGenerator = new XtbGenerator();
        xtbGenerator.setLang(lang);
        xtbGenerator.setProjectId(projectId);
        xtbGenerator.setJsFile(jsFiles);
        xtbGenerator.setTranslationFile(translationFile);
        xtbGenerator.setXtbOutputFile(xtbOutputFile);

        xtbGenerator.run();
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setJsFile(Collection<SourceFile> jsFiles) {
        this.jsFiles = jsFiles;
    }

    public void setTranslationFile(String translationFile) {
        this.translationFile = translationFile;
    }

    public void setXtbOutputFile(String xtbOutputFile) {
        this.xtbOutputFile = xtbOutputFile;
    }

    public void run() throws IOException {
        Writer outputWriter = null;

        try {
            // process messages
            Map<String, JsMessage> messages = getMessages();
            String translationContent = getTranslationFileContent();

            // XtbWriter (append/empty)
            XtbWriter xtbWriter;

            // get(create) output writer
            outputWriter = getOutputWriter();

            if (translationContent == null) {
                xtbWriter = new XtbWriterEmpty(outputWriter, lang, messages);
            } else {
                xtbWriter = new XtbWriterAppend(outputWriter, lang, messages, translationContent);
            }

            xtbWriter.write();

        } finally {
            if (outputWriter != null) {
                outputWriter.close();
            }
        }
    }

    public Map<String, JsMessage> getMessages() throws IOException {
        final Map<String, JsMessage> jsMessages = getMessagesFromJs();
        XtbMessageBundle xtbMessageBundle = getMessageBundleFromTranslationFile();
        if (xtbMessageBundle == null) {
            return jsMessages;
        }

        final Map<String, JsMessage> messages = new LinkedHashMap<>();
        final Iterator<String> jsMessagesIterator = jsMessages.keySet().iterator();

        while (jsMessagesIterator.hasNext()) {
            final String messageId = jsMessagesIterator.next();
            if (xtbMessageBundle.getMessage(messageId) == null) {
                messages.put(messageId, jsMessages.get(messageId));
            }
        }

        return messages;
    }

    public Map<String, JsMessage> getMessagesFromJs() throws IOException {
        final JsMessageExtractor extractor = new JsMessageExtractor(
                new GoogleJsMessageIdGenerator(projectId), JsMessage.Style.CLOSURE
        );

        final Collection<JsMessage> messages = extractor.extractMessages(jsFiles);
        final Iterator<JsMessage> iterator = messages.iterator();

        final Map<String, JsMessage> messageMap = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            JsMessage message = iterator.next();
            messageMap.put(message.getId(), message);
        }

        return messageMap;
    }

    public XtbMessageBundle getMessageBundleFromTranslationFile() throws IOException {
        InputStream translationInputStream = getTranslationFileInputStream();
        if (translationInputStream == null) {
            return null;
        }

        final XtbMessageBundle xtbMessageBundle = new XtbMessageBundle(translationInputStream, projectId);
        translationInputStream.close();

        return xtbMessageBundle;
    }

    protected String getTranslationFileContent() throws IOException {
        InputStream translationInputStream = getTranslationFileInputStream();
        if (translationInputStream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(translationInputStream));
        StringBuilder content = new StringBuilder();

        char[] buffer = new char[512];
        int l;

        while ((l = reader.read(buffer)) > 0) {
            content.append(buffer, 0, l);
        }

        reader.close();
        return content.toString();
    }

    protected InputStream getTranslationFileInputStream() throws FileNotFoundException {
        if (translationFile == null) {
            return null;
        }
        File inFile = new File(translationFile);
        if (!inFile.exists()) {
            return null;
        }
        return new FileInputStream(inFile);
    }

    protected Writer getOutputWriter() throws IOException {
        if (xtbOutputFile == null) {
            return new OutputStreamWriter(System.out);
        }
        File outFile = new File(xtbOutputFile);
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        return new OutputStreamWriter(new FileOutputStream(outFile));
    }
}
