package sk.kuzmisin.xtbgenerator;

import com.google.common.io.Files;
import com.google.javascript.jscomp.JsMessage;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.XtbMessageBundle;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class XtbGeneratorTest {

    protected StringWriter output;

    /**
     * Mock for XtbGenerator
     */
    class MockXtbGenerator extends XtbGenerator {
        protected InputStream getTranslationFileInputStream() {
            if (this.translationFile == null) {
                return null;
            }
            return XtbGeneratorTest.class.getResourceAsStream("/" + "messages.xtb");
        }

        protected Writer getOutputWriter() {
            output = new StringWriter();
            return output;
        }
    }

    /**
     * Test loading JS messages from JS file into Map<Id, JsMessage>
     */
    @Test
    public void testGetMessagesFromJs() throws IOException {
        XtbGenerator xtbGenerator = new XtbGenerator();
        xtbGenerator.setJsFile(getTestCollectionFrom("messages.js"));
        Map<String, JsMessage> messages = xtbGenerator.getMessagesFromJs();

        assertEquals(4, messages.size());
    }

    /**
     * Test loading messages from XTB file
     */
    @Test
    public void testGetMessagesFromTranslationFile() throws IOException {
        final MockXtbGenerator xtbGenerator = new MockXtbGenerator();
        xtbGenerator.setTranslationFile("messages.xtb");
        XtbMessageBundle xtbMessageBundle = xtbGenerator.getMessageBundleFromTranslationFile();

        Iterator<JsMessage> bundleIterator = xtbMessageBundle.getAllMessages().iterator();
        int i;
        for (i = 0; bundleIterator.hasNext(); i++) {
            bundleIterator.next();
        }

        assertEquals(4, i);
    }

    /**
     * Test comparing JS vs. XTB -> method must return only "NEW"
     */
    @Test
    public void getMessages() throws IOException {
        final MockXtbGenerator xtbGenerator = new MockXtbGenerator();
        xtbGenerator.setTranslationFile("messages.xtb");

        // test - nothing to add
        xtbGenerator.setJsFile(getTestCollectionFrom("messages.js"));
        Map<String, JsMessage> messages = xtbGenerator.getMessages();
        assertEquals(0, messages.size());

        // test - 2 new to add
        xtbGenerator.setJsFile(getTestCollectionFrom("messages.js", "messages2add.js"));
        messages = xtbGenerator.getMessages();
        assertEquals(2, messages.size());

        assertNotNull(messages.get("8934769802850633699")); // MSG_TEST_5
        assertNotNull(messages.get("601502449317640790")); // MSG_TEST_6
    }

    /**
     * Complex test without already existed translation file
     */
    @Test
    public void testEmpty() throws IOException, URISyntaxException {
        final MockXtbGenerator xtbGenerator = new MockXtbGenerator();
        xtbGenerator.setJsFile(getTestCollectionFrom("messages.js"));
        xtbGenerator.setLang("cs");
        xtbGenerator.run();

        String expected = Files.toString(
            new File(XtbGeneratorTest.class.getResource("/" + "messages.xtb").toURI()),
            Charset.defaultCharset()
        );

        assertEquals(expected, output.toString());
    }

    /**
     * Complex test against translation file
     */
    @Test
    public void testAppend() throws IOException, URISyntaxException {
        final MockXtbGenerator xtbGenerator = new MockXtbGenerator();
        xtbGenerator.setJsFile(getTestCollectionFrom("messages.js", "messages2add.js"));
        xtbGenerator.setTranslationFile("messages.xtb");
        xtbGenerator.setLang("cs");
        xtbGenerator.run();

        String expected = Files.toString(
                new File(XtbGeneratorTest.class.getResource("/" + "messages_append.xtb").toURI()),
                Charset.defaultCharset()
        );

        assertEquals(expected, output.toString());
    }

    protected Collection<SourceFile> getTestCollectionFrom(String... fileName) throws IOException {
        final Collection<SourceFile> sourceFileCollections = new ArrayList<>();

        for (int i = 0; i < fileName.length; i++) {
            final InputStream fileStream = XtbGeneratorTest.class.getResourceAsStream("/" + fileName[i]);
            sourceFileCollections.add(SourceFile.fromInputStream(fileName[i], fileStream));
            fileStream.close();
        }

        return sourceFileCollections;
    }
}
