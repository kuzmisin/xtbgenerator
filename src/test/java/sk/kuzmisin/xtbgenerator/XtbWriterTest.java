package sk.kuzmisin.xtbgenerator;

import com.google.javascript.jscomp.GoogleJsMessageIdGenerator;
import com.google.javascript.jscomp.JsMessage;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class XtbWriterTest {
    /**
     * Mock XtbWriter
     */
    class MockXtbWriter extends XtbWriter {

        public MockXtbWriter(Writer writer, String lang, Map<String, JsMessage> messages) {
            super(writer, lang, messages);
        }

        @Override
        public void write() throws IOException {}
    }

    @Test
    public void testWriteMessages() throws Exception {
        Map<String, JsMessage> messages = new LinkedHashMap<>();
        GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(null);

        messages.put(
            "DUMMY_ID_1",
            new JsMessage.Builder("MSG_TEST_1").
                appendStringPart("Test 1").
                setDesc("Description 1").
                setSourceName("file.js").
                build(idGenerator)
        );

        messages.put(
            "DUMMY_ID_2",
            new JsMessage.Builder("MSG_TEST_2").
                    appendStringPart("Test 2 ").
                    appendPlaceholderReference("ph1").
                    appendStringPart(" continue message ").
                    appendPlaceholderReference("ph2").
                    setDesc("Description 2").
                    setSourceName("file.js").
                    build(idGenerator)
        );

        messages.put(
            "DUMMY_ID_3",
            new JsMessage.Builder("MSG_TEST_3").
                    appendStringPart("HTML <> &").
                    setDesc("Description 3").
                    setSourceName("file.js").
                    build(idGenerator)
        );

        messages.put(
            "DUMMY_ID_4",
            new JsMessage.Builder("MSG_TEST_4").
                    appendStringPart("Test 4").
                    setDesc("Escape & \"me\"").
                    setSourceName("file.js").
                    build(idGenerator)
        );

        StringWriter writer = new StringWriter();

        final MockXtbWriter xtbWriter = new MockXtbWriter(writer, "cs", messages);
        xtbWriter.writeMessages();

        final String expected =
                "\t<translation id=\"2426017083238799036\" key=\"MSG_TEST_1\" source=\"file.js\" desc=\"Description 1\">Test 1</translation>\n" +
                "\t<translation id=\"3160123618072793522\" key=\"MSG_TEST_2\" source=\"file.js\" desc=\"Description 2\">Test 2 <ph name=\"PH1\" /> continue message <ph name=\"PH2\" /></translation>\n" +
                "\t<translation id=\"7594360968243980581\" key=\"MSG_TEST_3\" source=\"file.js\" desc=\"Description 3\">HTML &lt;&gt; &amp;</translation>\n" +
                "\t<translation id=\"9130919297188675801\" key=\"MSG_TEST_4\" source=\"file.js\" desc=\"Escape &amp; &quot;me&quot;\">Test 4</translation>\n";

        assertEquals(expected, writer.toString());
    }
}
