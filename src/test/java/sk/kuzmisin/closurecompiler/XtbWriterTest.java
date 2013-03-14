package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.GoogleJsMessageIdGenerator;
import com.google.javascript.jscomp.JsMessage;
import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

public class XtbWriterTest {
    @Test
    public void testWrite() throws Exception {
        Map<String, JsMessage> messages = new HashMap<>();
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

        StringWriter writer = new StringWriter();

        XtbWriter xtbWriter = new XtbWriter(writer, messages);
        xtbWriter.write();

        final String expected =
                "\t<translation id=\"2426017083238799036\" key=\"MSG_TEST_1\" source=\"file.js\" desc=\"Description 1\">Test 1</translation>\n" +
                "\t<translation id=\"3160123618072793522\" key=\"MSG_TEST_2\" source=\"file.js\" desc=\"Description 2\">Test 2 <ph name=\"ph1\" /> continue message <ph name=\"ph2\" /></translation>\n" +
                "\t<translation id=\"7594360968243980581\" key=\"MSG_TEST_3\" source=\"file.js\" desc=\"Description 3\">HTML &lt;&gt; &amp;</translation>\n";

        assertEquals(expected, writer.toString());
    }
}
