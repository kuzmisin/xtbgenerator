package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.GoogleJsMessageIdGenerator;
import com.google.javascript.jscomp.JsMessage;
import com.google.javascript.jscomp.JsMessageExtractor;
import com.google.javascript.jscomp.SourceFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class XtbGenerator {

    protected String lang;

    protected String projectId;

    protected Collection<SourceFile> jsFiles;

    protected String translationFile;

    public static void process(String lang, String projectId, Collection<SourceFile> jsFiles, String translationFile) throws IOException {
        final XtbGenerator xtbGenerator = new XtbGenerator();
        xtbGenerator.setLang(lang);
        xtbGenerator.setProjectId(projectId);
        xtbGenerator.setJsFile(jsFiles);
        xtbGenerator.setTranslationFile(translationFile);

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

    public void run() throws IOException {
        final XtbWriter xtbWriter = new XtbWriter(new OutputStreamWriter(System.out), getMessagesFromJs());
        xtbWriter.write();
    }

    /**
     * Return messages in MAP <id, JsMessage>
     */
    public Map<String, JsMessage> getMessagesFromJs() throws IOException {
        final JsMessageExtractor extractor = new JsMessageExtractor(
                new GoogleJsMessageIdGenerator(projectId), JsMessage.Style.CLOSURE
        );

        final Collection<JsMessage> messages = extractor.extractMessages(jsFiles);
        final Iterator<JsMessage> iterator = messages.iterator();

        Map<String, JsMessage> messageMap = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            JsMessage message = iterator.next();
            messageMap.put(message.getId(), message);
        }

        return messageMap;
    }
}
