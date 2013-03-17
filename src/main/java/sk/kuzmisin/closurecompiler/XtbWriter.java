package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.JsMessage;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

abstract class XtbWriter {

    protected final String EOL = "\n";

    protected final String INDENT = "\t";

    protected Writer writer;

    protected Map<String, JsMessage> messages;

    protected String lang;

    public XtbWriter(Writer writer, String lang, Map<String, JsMessage> messages) {
        this.writer = writer;
        this.messages = messages;
        this.lang = lang;
    }

    abstract public void write() throws IOException;

    protected void writeFooter() throws IOException {
        writer.append("</translationbundle>");
    }

    protected void writeMessages() throws IOException {
        Iterator<JsMessage> iterator = messages.values().iterator();
        while (iterator.hasNext()) {
            JsMessage message = iterator.next();
            writer.append(
                INDENT +
                "<translation id=\"" + message.getId() + "\" " +
                        "key=\"" + message.getKey() + "\" " +
                        "source=\"" + message.getSourceName() + "\" " +
                        "desc=\"" + escapeDesc(message.getDesc()) + "\"" +
                        ">"
            );

            for (CharSequence part : message.parts()) {
                if (part instanceof JsMessage.PlaceholderReference) {
                    writer.append("<ph name=\"" + ((JsMessage.PlaceholderReference) part).getName() + "\" />");
                } else {
                    writer.append(escape(part));
                }
            }

            writer.append(
                "</translation>" +
                EOL
            );
        }
    }

    protected String escape(CharSequence value) {
        return value.toString().
            replace("&", "&amp;").
            replace("<", "&lt;").
            replace(">", "&gt;");
    }

    protected String escapeDesc(CharSequence value) {
        return value.toString().replace("\"", "&quot;");
    }
}
