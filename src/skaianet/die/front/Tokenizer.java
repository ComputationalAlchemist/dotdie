package skaianet.die.front;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static skaianet.die.front.Token.*;

/**
 * Created on 2014-07-24.
 */
public class Tokenizer {
    private final String filename;
    private final String code;
    private int index = 0;
    private Object associated;

    public Tokenizer(String filename, String code) {
        this.filename = filename;
        this.code = code;
    }

    public Tokenizer(String filename, Reader reader) throws IOException {
        this.filename = filename;
        BufferedReader byline = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
        StringBuilder code = new StringBuilder(1024);
        while (true) {
            String line = byline.readLine();
            if (line == null) {
                break;
            }
            code.append(line).append('\n');
        }
        this.code = code.toString();
    }

    private boolean isEOF() {
        return index >= code.length();
    }

    private char nextChar() throws ParsingException {
        if (isEOF()) {
            throw new ParsingException("Unexpected end of file!");
        }
        return code.charAt(index++);
    }

    private boolean accept(char c) {
        if (!isEOF() && code.charAt(index) == c) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    public Token next() throws ParsingException {
        //noinspection StatementWithEmptyBody
        while (accept(' ') || accept('\t') || accept('\n') || accept('\r')) ; // Strip whitespace
        if (isEOF()) {
            return Token.NONE;
        }
        associated = null;
        char c = nextChar();
        while (c == '/' && accept('/')) { // Comment!
            while (nextChar() != '\n') ;
            c = nextChar();
        }
        switch (c) {
            case '{':
                return OPEN_CURLY;
            case '}':
                return CLOSE_CURLY;
            case ';':
                return SEMICOLON;
            case '(':
                return OPEN_PAREN;
            case ')':
                return CLOSE_PAREN;
            case '.':
                return DOT;
            case '[':
                return OPEN_SQUARE;
            case ']':
                return CLOSE_SQUARE;
            case ',':
                return COMMA;
            case '+':
                return accept('=') ? SETADD : ADD;
            case '-':
                return accept('=') ? SETSUBTRACT : SUBTRACT;
            case '*':
                return accept('=') ? SETMULTIPLY : MULTIPLY;
            case '/':
                return accept('=') ? SETDIVIDE : DIVIDE;
            case '%':
                return accept('=') ? SETREMAINDER : REMAINDER;
            case '&':
                return accept('&') ? LAND : accept('=') ? SETAND : BIAND;
            case '|':
                return accept('|') ? LOR : accept('=') ? SETOR : BIOR;
            case '^':
                return accept('=') ? SETXOR : BIXOR;
            case '<':
                return accept('<') ? (accept('=') ? SETLSHIFT : LSHIFT) : accept('=') ? CMPLE : CMPLT;
            case '>':
                return accept('>') ? (accept('>') ? (accept('=') ? SETRLSHIFT : RLSHIFT) : (accept('=') ? SETRASHIFT : RASHIFT)) : accept('=') ? CMPGE : CMPGT;
            case '!':
                return accept('=') ? CMPNE : LINV; // TODO: Use LINV
            case '=':
                return accept('=') ? CMPEQ : SET;
            case '\\':
                associated = (int) nextChar();
                return INTEGER;
        }
        if (c == '`' || c == '\'') { // Examples: `Hello, World'  ``I can embed ' closing things!''
            int count = 1;
            while (accept(c)) {
                count++;
            }
            char expected = (c == '`') ? '\'' : '`';
            int sequential = 0;
            StringBuilder sbuf = new StringBuilder();
            while (sequential < count) {
                char cur = nextChar();
                if (cur == expected) {
                    sequential++;
                } else {
                    sequential = 0;
                }
                sbuf.append(cur);
            }
            sbuf.setLength(sbuf.length() - count);
            associated = sbuf.toString();
            return STRING;
        } else {
            StringBuilder ibuf = new StringBuilder();
            boolean isInt = true;
            while ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                    || c == '~' || c == '@' || c == '_' || c == '#' || c == '$') {
                isInt &= (c >= '0' && c <= '9');
                ibuf.append(c);
                if (isEOF()) {
                    index++; // To counteract the index-- that will happen momentarily.
                    break;
                }
                c = nextChar();
            }
            index--; // Push back the last character.
            String strval = ibuf.toString();
            if (isInt) {
                associated = Integer.parseInt(strval);
                return INTEGER;
            } else if ("~ATH".equalsIgnoreCase(strval)) {
                return ATH;
            } else if ("EXECUTE".equalsIgnoreCase(strval)) {
                return EXECUTE;
            } else if ("import".equalsIgnoreCase(strval)) {
                return IMPORT;
            } else if ("NULL".equalsIgnoreCase(strval)) {
                return NULL;
            } else if ("TRUE".equalsIgnoreCase(strval)) {
                return TRUE;
            } else if ("FALSE".equalsIgnoreCase(strval)) {
                return FALSE;
            } else if ("THIS".equalsIgnoreCase(strval)) {
                return THIS;
            } else if ("U~F".equalsIgnoreCase(strval)) {
                return UTILDEF;
            } else if ("RETURN".equalsIgnoreCase(strval)) {
                return RETURN;
            } else {
                associated = strval;
                return IDENTIFIER;
            }
        }
        // INTEGER, STRING, IDENTIFIER, EXECUTE, ~ATH, IMPORT
    }

    public Object getAssociated() {
        return associated;
    }

    public String traceInfo() {
        int lineNo = 1;
        for (int i = 0; i < index; i++) {
            if (code.charAt(i) == '\n') {
                lineNo++;
            }
        }
        return "near " + filename + ":" + lineNo; // + ":" + colNo; // TODO: More accuracy!
    }

    public void dumpCode() {
        System.out.println(code);
    }
}
