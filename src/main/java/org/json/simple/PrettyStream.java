package org.json.simple;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 *
 * @author David A. Solin
 *
 */
public class PrettyStream extends FilterOutputStream {
    private static final byte[] LF = System.getProperty("line.separator").getBytes();

    private int indent=0, lastchar=-1;
    private boolean quotation=false, escape=false;

    public PrettyStream(OutputStream out) {
	super(out);
    }

    @Override
    public void write(byte[] buff, int offset, int len) throws IOException {
	len = Math.min(offset + len, buff.length);
	for (int i=offset; i < len; i++) {
	    write((int)buff[i]);
	}
    }

    @Override
    public void write(byte[] buff) throws IOException {
	write(buff, 0, buff.length);
    }

    @Override
    public void write(int b) throws IOException {
	if (quotation || escape) {
	    out.write(b);
	} else if (lastchar == '[') {
	    out.write(lastchar);
	    lastchar = -1;
	    if (b == ']') {
		out.write(b);
	    } else {
		out.write(LF);
		indent++;
		writeIndentation();
		writeByte(b);
	    }
	} else {
	    writeByte(b);
	}
	if (escape) {
	    escape = false;
	} else {
	    switch(b) {
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

    private void writeByte(int b) throws IOException {
	switch(b) {
	  case '[':
	    lastchar = b;
	    break;
	  case '{':
	    out.write(b);
	    out.write(LF);
	    indent++;
	    writeIndentation();
	    break;
	  case ',':
	    out.write(b);
	    out.write(LF);
	    writeIndentation();
	    break;
	  case ']':
	  case '}':
	    out.write(LF);
	    indent--;
	    writeIndentation();
	    out.write(b);
	    break;
	  case ':':
	    out.write(' ');
	    out.write(b);
	    out.write(' ');
	    break;
	  default:
	    out.write(b);
	    break;
	}
    }

    // Private

    private static final byte[] TAB = {' ',' ',' ',' '};

    private void writeIndentation() throws IOException {
	for (int i = 0; i < indent; i++) {
	    out.write(TAB);
	}
    }
}
