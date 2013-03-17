package sk.kuzmisin.closurecompiler;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class XtbWriterAppendTest {

    /**
     * Test removing "</translationbundle>" from end of the content - multiline
     */
    @Test
    public void testWriteContent1() throws IOException {
        StringWriter writer = new StringWriter();
        XtbWriterAppend xtbWriterAppend = new XtbWriterAppend(writer, "cs", null, null);

        xtbWriterAppend.content = "TEST " + "\r\n" + "\n" + "  </translationbundle>   " + "\n\n\n";
        xtbWriterAppend.writeContent();

        String expected = "TEST" + "\n";
        assertEquals(expected, writer.toString());
    }

    /**
     * Test removing "</translationbundle>" from end of the content - single line
     */
    @Test
    public void testWriteContent2() throws IOException {
        StringWriter writer = new StringWriter();
        XtbWriterAppend xtbWriterAppend = new XtbWriterAppend(writer, "cs", null, null);

        xtbWriterAppend.content = "/>  </translationbundle>   ";
        xtbWriterAppend.writeContent();

        String expected = "/>" + "\n";
        assertEquals(expected, writer.toString());
    }

    /**
     * Test removing "</translationbundle>" from end of the content - really only last ?
     */
    @Test
    public void testWriteContent3() throws IOException {
        StringWriter writer = new StringWriter();
        XtbWriterAppend xtbWriterAppend = new XtbWriterAppend(writer, "cs", null, null);

        xtbWriterAppend.content = "/>  </translationbundle> </translationbundle> ";
        xtbWriterAppend.writeContent();

        String expected = "/>  </translationbundle>" + "\n";
        assertEquals(expected, writer.toString());
    }
}
