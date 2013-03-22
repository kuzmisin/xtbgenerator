package sk.kuzmisin.xtbgenerator;

import com.google.javascript.jscomp.JsMessage;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class XtbWriterEmpty extends XtbWriter {

    public XtbWriterEmpty(Writer writer, String lang, Map<String, JsMessage> messages) {
        super(writer, lang, messages);
    }

    @Override
    public void write() throws IOException {
        writeHeader();
        writeMessages();
        writeFooter();
    }

    protected void writeHeader() throws IOException {
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + EOL +
                "<!DOCTYPE translationbundle>" + EOL +
                "<translationbundle lang=\"" + lang + "\">" + EOL
        );
    }
}
