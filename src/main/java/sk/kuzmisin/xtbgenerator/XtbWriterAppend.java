package sk.kuzmisin.xtbgenerator;

import com.google.javascript.jscomp.JsMessage;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XtbWriterAppend extends XtbWriter {

    protected String content;

    public XtbWriterAppend(Writer writer, String lang, Map<String, JsMessage> messages, String content) {
        super(writer, lang, messages);
        this.content = content;
    }

    @Override
    public void write() throws IOException {
        writeContent();
        writeMessages();
        writeFooter();
    }

    protected void writeContent() throws IOException {
        Pattern pattern = Pattern.compile("\\s*</translationbundle>\\s*$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        writer.append(matcher.replaceAll("") + EOL);
    }
}
