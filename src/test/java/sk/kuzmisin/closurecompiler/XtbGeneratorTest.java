package sk.kuzmisin.closurecompiler;

import com.google.javascript.jscomp.JsMessage;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.*;

public class XtbGeneratorTest {

    @Test
    public void testGetMessages() throws IOException {
        XtbGenerator xtbGenerator = new XtbGenerator();
        xtbGenerator.setJsFile(makeJsCollectionFromFileName("messages.js"));
        Map<String, JsMessage> messages = xtbGenerator.getMessagesFromJs();

        assertEquals(4, messages.size());
    }

    protected Collection<SourceFile> makeJsCollectionFromFileName(String filename) throws IOException {
        InputStream fileStream = XtbGenerator.class.getResourceAsStream("/" + filename);
        SourceFile file = SourceFile.fromInputStream(filename, fileStream);

        Collection<SourceFile> sourceFileCollections = new ArrayList<>();
        sourceFileCollections.add(file);

        return sourceFileCollections;
    }
}
