package skaianet.die.front;

import skaianet.die.utils.Utilities;

import java.io.PrintStream;

import static skaianet.die.front.Token.*;

class Tokenizer {
    private final String filename, code;
    private int index = 0;
    private Object associated;
    private Color currentColor = Color.NO_THREAD;

    public Tokenizer(String filename, String code) {
        this.filename = filename;
        this.code = code;
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
        while (accept(' ') || accept('\t') || accept('\n') || accept('\r')) ; // Strip whitespace
        if (isEOF()) {
            return Token.NONE;
        }
        associated = null;
        char c = nextChar();
        if (c == '@') {
            this.currentColor = Color.parse(nextChar(), nextChar(), nextChar());
            if (nextChar() != '@') {
                throw new ParsingException("Bad color descriptor!");
            }
            return next();
        }
        if (c == '/' && accept('/')) { // Comment!
            while (nextChar() != '\n') ;
            return next();
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
                return accept('=') ? CMPNE : NOT;
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
            boolean isInt = true, hadDot = false, isDouble = true;
            while ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                    || c == '~' || c == '_' || c == '#' || c == '$' || (isDouble && !hadDot && c == '.')) {
                isInt &= (c >= '0' && c <= '9');
                isDouble &= (c >= '0' && c <= '9') || (c == '.' && !hadDot);
                hadDot |= c == '.';
                ibuf.append(c);
                if (isEOF()) {
                    index++; // To counteract the index-- that will happen momentarily.
                    break;
                }
                c = nextChar();
            }
            index--; // Push back the last character.
            if (ibuf.length() == 0) {
                throw new ParsingException("Unhandled character: " + nextChar());
            }
            String stringValue = ibuf.toString();
            if (hadDot && isDouble) {
                associated = Double.parseDouble(stringValue);
                return DOUBLE;
            } else if (isInt) {
                associated = Integer.parseInt(stringValue);
                return INTEGER;
            } else {
                Token t = Token.lookupIdentifierToken(stringValue);
                if (t != null) {
                    return t;
                } else {
                    associated = stringValue;
                    return IDENTIFIER;
                }
            }
        }
    }

    public Object getAssociated() {
        return associated;
    }

    public Color getAssocColor() {
        return currentColor;
    }

    public String traceInfo() {
        return filename + ":" + (1 + Utilities.countCharacters(code, '\n', 0, index));
    }

    public void dumpCode(PrintStream out) {
        out.println(code);
    }
}
