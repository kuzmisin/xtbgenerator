package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.JsMessage;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class XtbWriter {

    protected final String EOL = "\n";

    protected final String INDENT = "\t";

    protected Writer writer;

    protected Map<String, JsMessage> messages;

    public XtbWriter(Writer writer, Map<String, JsMessage> messages) {
        this.writer = writer;
        this.messages = messages;
    }

    public void write() throws IOException {
        Iterator<JsMessage> iterator = messages.values().iterator();
        while (iterator.hasNext()) {
            JsMessage message = iterator.next();
            writer.append(
                INDENT +
                "<translation id=\"" + message.getId() + "\" " +
                        "key=\"" + message.getKey() + "\" " +
                        "source=\"" + message.getSourceName() + "\" " +
                        "desc=\"" + message.getDesc() + "\"" +
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
        writer.flush();
    }

    protected String escape(CharSequence value) {
        return value.toString().
            replace("&", "&amp;").
            replace("<", "&lt;").
            replace(">", "&gt;");
    }
}
