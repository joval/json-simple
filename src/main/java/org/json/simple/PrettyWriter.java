package org.json.simple;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;

/**
 *
 * @author David A. Solin
 *
 */
class PrettyWriter extends FilterWriter {
    private static final char[] LF = System.getProperty("line.separator").toCharArray();
    private static final char SPACE = ' ';

    private int indent=0, lastchar=-1;
    private boolean quotation=false, escape=false;

    public PrettyWriter(Writer out) {
	super(out instanceof BufferedWriter ? out : new BufferedWriter(out, 1024));
    }

    @Override
    public void write(char[] buff, int offset, int len) throws IOException {
	len = Math.min(offset + len, buff.length);
	for (int i=offset; i < len; i++) {
	    write((int)buff[i]);
	}
    }

    @Override
    public void write(int c) throws IOException {
	if (quotation || escape) {
	    out.write(c);
	} else if (lastchar == '[') {
	    out.write(lastchar);
	    lastchar = -1;
	    if (c == ']') {
		out.write(c);
	    } else {
		out.write(LF);
		indent++;
		writeIndentation();
		writeChar(c);
	    }
	} else {
	    writeChar(c);
	}
	if (escape) {
	    escape = false;
	} else {
	    switch(c) {
	      case '"':  // toggle quotation
		quotation = !quotation;
		break;
	      case '\\': // escape the next character
		escape = true;
		break;
	    }
	}
    }

    // Private

    private void writeChar(int c) throws IOException {
	switch(c) {
	  case '[':
	    lastchar = c;
	    break;
	  case '{':
	    out.write(c);
	    out.write(LF);
	    indent++;
	    writeIndentation();
	    break;
	  case ',':
	    out.write(c);
	    out.write(LF);
	    writeIndentation();
	    break;
	  case ']':
	  case '}':
	    out.write(LF);
	    indent--;
	    writeIndentation();
	    out.write(c);
	    break;
	  case ':':
	    out.write(SPACE);
	    out.write(c);
	    out.write(SPACE);
	    break;
	  default:
	    out.write(c);
	    break;
	}
    }

    // Private

    private static final char[] TAB = {SPACE, SPACE, SPACE, SPACE};

    private void writeIndentation() throws IOException {
	for (int i = 0; i < indent; i++) {
	    out.write(TAB);
	}
    }
}
