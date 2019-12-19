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
    private int indent=0, lastchar=-1;
    private boolean quotation=false, escape=false;

    public PrettyStream(OutputStream out) {
	super(out);
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
		out.write('\n');
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
	    out.write('\n');
	    indent++;
	    writeIndentation();
	    break;
	  case ',':
	    out.write(b);
	    out.write('\n');
	    writeIndentation();
	    break;
	  case ']':
	  case '}':
	    out.write('\n');
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