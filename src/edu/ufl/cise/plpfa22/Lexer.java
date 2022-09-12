package edu.ufl.cise.plpfa22;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer implements ILexer {

    int position = 0;
    int line = 1;
    int col =  1;
    int startPosition = 0;


    private enum STATE {
        START, IN_IDENT, HAVE_ZERO, IN_STRING, IN_NUM, WHITE_SPACE, TIMES, HAVE_LT, HAVE_EX, HAVE_GT,
        HAVE_SLASH, IN_COMMENT, HAVE_FSLASH, IN_ASSIGN;
    }

    private STATE currState;
    ArrayList<Token> tokens = new ArrayList<Token>();
    String sourceCode;
    HashMap<String, IToken.Kind> rsrvd = new HashMap<>();

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        currState = STATE.START;
        HashMap();
    }


    @Override
    public IToken next() throws LexicalException {

        if (position >= sourceCode.length()) {
            Token token = new Token(IToken.Kind.EOF, null, null, 0);
            tokens.add(token);

            return token;
        }

        Token token = getToken();
        tokens.add(token);
        return token;
    }

    @Override
    public IToken peek() throws LexicalException {

        if (position >= sourceCode.length()) {
            Token token = new Token(IToken.Kind.EOF, null, null, 0);
            col -= (position - startPosition) + 1;
            position = startPosition;

            return token;
        }
        Token token = getToken();

        if (token.getKind() == IToken.Kind.EOF) {
            col--;
            position--;
        }
        else {
            col -= (position - startPosition);
            position = startPosition;
        }
        return token;
    }



    private void HashMap() {
        rsrvd.put("TRUE", IToken.Kind.BOOLEAN_LIT);
        rsrvd.put("FALSE", IToken.Kind.BOOLEAN_LIT);

        rsrvd.put("CONST", IToken.Kind.KW_CONST);
        rsrvd.put("VAR", IToken.Kind.KW_VAR);
        rsrvd.put("PROCEDURE", IToken.Kind.KW_PROCEDURE);
        rsrvd.put("CALL", IToken.Kind.KW_CALL);

        rsrvd.put("BEGIN", IToken.Kind.KW_BEGIN);
        rsrvd.put("END", IToken.Kind.KW_END);
        rsrvd.put("IF", IToken.Kind.KW_IF);
        rsrvd.put("THEN", IToken.Kind.KW_THEN);
        rsrvd.put("WHILE", IToken.Kind.KW_WHILE);
        rsrvd.put("DO", IToken.Kind.KW_DO);

    }


    private Token getToken() throws LexicalException {

        IToken.SourceLocation token_position = null;

        while (true) {
            if (position >= sourceCode.length()) {
                // Token newToken = new Token(IToken.Kind.EOF, null, null, 0);
                // return newToken;
                return new Token(IToken.Kind.EOF, null, null, 0);
            }

            char ch = sourceCode.charAt(position);
            switch (currState) {
                // DFA
                case START -> {
                    switch (ch) {
                        case ' ', '\t', '\r' -> {
                            col++;
                            position++;
                        }
                        case '\n' -> {
                            line++;
                            position++;
                            col = 1;
                        }

                        case '=' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            System.out.println(ch);
                            token_position = new IToken.SourceLocation(line, col);
                            Token token = new Token(IToken.Kind.EQ, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return  token;
                        }
                        case '+' -> {


                            startPosition = position;
                            // can i remove this?
//                            Token token = new Token(IToken.Kind.PLUS, String.valueOf(ch), token_position, 1);
                            char[] cht = {ch};
//                            System.out.println(ch);
//                            col++;
//                            position++;
                            token_position = new IToken.SourceLocation(line, col);
                            Token token = new Token(IToken.Kind.PLUS, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
//                            System.out.println(col);
//                            System.out.println(position);
//                            token_position = new IToken.SourceLocation(col,position);
//                            token = new Token(IToken.Kind.PLUS, cht, token_position, 1);

                            return token;

//                            currState = STATE.HAVE_ZERO;
                        }
                        case ':' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            currState = STATE.IN_ASSIGN;
                            col++;
                            position++;
                        }
                        case '#' -> {
//                            currState = STATE.IN_COMMENT;
//                            col++;
//                            position++;
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.NEQ, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '.' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.DOT, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '*' -> {
//                            col++;
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.TIMES, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '%' -> {
//                            col++;
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.MOD, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '?' -> {
//                            col++;
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.QUESTION, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '/' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            char[] cht = {ch};
//                            Token token = new Token(IToken.Kind.DIV, cht, token_position, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;

                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.HAVE_FSLASH;
                            col++;
                            position++;
                        }


                        case ';' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.SEMI, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

//                        case '|' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.OR, String.valueOf(ch), token_position, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case '^' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.RETURN, String.valueOf(ch), token_position, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }

                        case ',' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.COMMA, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '-' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            System.out.println(ch);
//                            col++;
//                            position++;
//                            token_position = new IToken.SourceLocation(line,col);
                            Token token = new Token(IToken.Kind.MINUS, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }

                        case '<' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.HAVE_LT;
                            col++;
                            position++;
                        }
                        case '!' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            currState = STATE.HAVE_EX;
//                            col++;
//                            position++;
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            // can i remove this?
//                            Token token = new Token(IToken.Kind.PLUS, String.valueOf(ch), token_position, 1);
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.BANG, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }
//                        case '[' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.LSQUARE, String.valueOf(ch), token_position, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case ']' -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.RSQUARE, String.valueOf(ch), token_position, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }

                        case '(' -> {

                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.LPAREN, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }
                        case ')' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.RPAREN, cht, token_position, 1);
                            tokens.add(token);
                            col++;
                            position++;
                            return token;
                        }


                        case '0' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.HAVE_ZERO;
                            col++;
                            position++;
                        }
                        case '>' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.HAVE_GT;
                            col++;
                            position++;
                        }
                        case '"' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.IN_STRING;
                            col++;
                            position++;
                        }

                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            token_position = new IToken.SourceLocation(line, col);
                            startPosition = position;
                            currState = STATE.IN_NUM;
                            col++;
                            position++;
                        }

                        default -> {
                            if (Character.isJavaIdentifierStart(ch)) {
                                token_position = new IToken.SourceLocation(line, col);
                                startPosition = position;
                                System.out.println("test - *");
                                currState = STATE.IN_IDENT;
                                col++;
                                position++;

                            } else {
                                token_position = new IToken.SourceLocation(line, col);
                                throw new LexicalException("Invalid character", token_position.line(),
                                        token_position.column());
                            }
                        }

                    }
                }


                case HAVE_LT -> {

                    switch (ch) {

                        case '=' -> {

                            String temp = sourceCode.substring(startPosition, position + 1);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.LE, tempChars,
                                    token_position, (position + 1) - startPosition);
                            currState = STATE.START;
                            col++;
                            position++;
                            return token;
                        }
//                        case '-' -> {
//                            Token token = new Token(IToken.Kind.LARROW,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_position, (position + 1) - startPosition);
//                            currState = STATE.HAVE_MINUS;
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case '<' -> {
//                            Token token = new Token(IToken.Kind.LANGLE,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_position, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
                        default -> {
                            String temp = sourceCode.substring(startPosition, position);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }
                            Token token = new Token(IToken.Kind.LT, tempChars,
                                    token_position,
                                    1);
                            currState = STATE.START;
                            return token;
                        }

                    }

                }

//                case HAVE_EX -> {
//
//                    switch (ch) {
//
////                        case '=' -> {
////                            Token token = new Token(IToken.Kind.NOT_EQUALS,
////                                    sourceCode.substring(startPosition, position + 1),
////                                    token_position, (position + 1) - startPosition);
////                            currState = STATE.START;
////                            col++;
////                            position++;
////                            return token;
////                        }
//                        default -> {
//                            String temp = sourceCode.substring(startPosition, position);
//                            char[] identChars = new char[temp.length()];
//                            for(int i = 0; i<temp.length();i++){
//                                identChars[i]=temp.charAt(i);
//                            }
//                            Token token = new Token(IToken.Kind.BANG, identChars,
//                                    token_position,
//                                    1);
//                            currState = STATE.START;
//                            return token;
//                        }
//
//                    }
//
//                }

//                case HAVE_ZERO -> {
//                    switch (ch) {
//                        case '.' -> {
//                            currState = STATE.HAVE_DOT;
//                            position++;
//                            col++;
//                        }
//                        default -> {
//
//                            String temp = sourceCode.substring(startPosition, position);
//                            char[] tempChars = new char[temp.length()];
//                            for(int i = 0; i<temp.length();i++){
//                                tempChars[i]=temp.charAt(i);
//                            }
//
//                            Token token = new Token(IToken.Kind.NUM_LIT, tempChars,
//                                    token_position, (position - startPosition));
//                            currState = STATE.START;
//                            return token;
//                        }
//                    }
//                }

                case IN_IDENT -> {
                    if (Character.isJavaIdentifierPart(ch)) {
                        col++;
                        position++;

                    } else {
                        String ident = sourceCode.substring(startPosition, position);

                        char[] identChars = new char[ident.length()];
                        for(int i = 0; i<ident.length();i++){
                            identChars[i]=ident.charAt(i);
                        }
                        System.out.println("col:"+col);
                        for (Map.Entry<String, IToken.Kind> entry : rsrvd.entrySet()) {
                            if (ident.equals(entry.getKey())) {
                                Token token = new Token(entry.getValue(), identChars, token_position,
                                        (position - startPosition));
                                currState = STATE.START;
                                return token;
                            }
                        }
//                        System.out.println(identChars[0]);
                        token_position = new IToken.SourceLocation(line, col-(identChars.length));
                        Token token = new Token(IToken.Kind.IDENT, identChars, token_position, position - startPosition);
                        currState = STATE.START;
                        return token;
                    }
                }
//                case HAVE_DOT -> {
//                    switch (ch) {
//                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                            currState = STATE.IN_FLOAT;
//                            col++;
//                            position++;
//                        }
//                        default -> {
//                            throw new LexicalException("found error with input number(check number again)",
//                                    token_position.line(), token_position.column());
//                        }
//                    }
//                }

                case IN_NUM -> {
                    switch (ch) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            position++;
                            col++;
                        }
//                        case '.' -> {
//                            currState = STATE.HAVE_DOT;
//                            col++;
//                            position++;
//                        }
                        default -> {
                            String str = sourceCode.substring(startPosition, position);
                            char[] tempChars = new char[str.length()];
                            for(int i = 0; i<str.length();i++){
                                tempChars[i]=str.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.NUM_LIT, tempChars, token_position,
                                    (position - startPosition));

                            try {
                                int temp = Integer.valueOf(str);
                            } catch (Exception e) {
                                throw new LexicalException("Invalid integer", token_position.line(),
                                        token_position.column());
                            }
                            currState = STATE.START;
                            return token;
                        }
                    }
                }
                case HAVE_GT -> {

                    switch (ch) {

                        case '=' -> {

                            String temp = sourceCode.substring(startPosition, position + 1);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.GE, tempChars,
                                    token_position, (position + 1) - startPosition);
                            currState = STATE.START;
                            col++;
                            position++;
                            return token;
                        }
//                        case '>' -> {
//                            Token token = new Token(IToken.Kind.RANGLE,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_position, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
                        default -> {

                            String temp = sourceCode.substring(startPosition, position);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.GT, tempChars,
                                    token_position,
                                    1);
                            currState = STATE.START;
                            return token;
                        }

                    }

                }

//                case IN_FLOAT -> {
//                    switch (ch) {
//                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                            col++;
//                            position++;
//                        }
//                        default -> {
//                            String fl = sourceCode.substring(startPosition, position);
//
//                            Token token = new Token(IToken.Kind.FLOAT_LIT,
//                                    sourceCode.substring(startPosition, position),
//                                    token_position, (position - startPosition));
//
//                            try {
//                                float floatValue = Float.valueOf(fl);
//                            } catch (Exception e) {
//                                throw new LexicalException("Invalid float input", token_position.line(),
//                                        token_position.column());
//                            }
//                            currState = STATE.START;
//                            return token;
//                        }
//                    }
//                }

//                case HAVE_MINUS -> {
//
//                    switch (ch) {
//
//                        case '>' -> {
//                            Token token = new Token(IToken.Kind.RARROW,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_position, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        default -> {
//                            Token token = new Token(IToken.Kind.MINUS, sourceCode.substring(startPosition, position),
//                                    token_position, 1);
//                            currState = STATE.START;
//                            return token;
//                        }
//
//                    }
//
//                }
                case IN_STRING -> {

                    switch (ch) {

                        case '\\' -> {
                            currState = STATE.HAVE_SLASH;
                            col++;
                            position++;
                        }
                        case '"' -> {
                            String temp = sourceCode.substring(startPosition, position + 1);
                            char[] tempList = new char[temp.length()];
                            for (int i = 0;i<temp.length();i++){
                                tempList[i] = temp.charAt(i);
                            }

//                            token_position = new IToken.SourceLocation(line, col);
                            Token token = new Token(IToken.Kind.STRING_LIT,
//                                    sourceCode.substring(startPosition, position + 1),
                                    tempList,
                                    token_position, (position + 1) - startPosition);
                            currState = STATE.START;
                            col++;
                            position++;
                            return token;
                        }
                        case '\n' -> {
                            col = 1;
                            line++;
                            position++;
                        }
                        default -> {
                            col++;
                            position++;
                        }

                    }

                }
                case IN_COMMENT -> {

                    switch (ch) {

                        case '\n' -> {
                            currState = STATE.START;
                            line++;
                            col = 1;
                            position++;
                        }

                        default -> {
                            col++;
                            position++;
                        }

                    }

                }
                case HAVE_SLASH -> {

                    switch (ch) {

                        case 'b', 't', 'n', 'f', 'r', '\\', '\'', '\"' -> {
                            currState = STATE.IN_STRING;
                            col++;
                            position++;
                        }
                        default -> {
                            throw new LexicalException("Invalid escape character", token_position.line(),
                                    token_position.column());
                        }

                    }

                }
                case HAVE_FSLASH -> {

                    switch (ch){

                        case '/' -> {
                            currState = STATE.IN_COMMENT;
                            col++;
                            position++;
                        }
                        default -> {
//                            token_position = new IToken.SourceLocation(line, col);
//                            System.out.println(col);
//                            startPosition = position;
                            char[] cht = {ch};
                            Token token = new Token(IToken.Kind.DIV, cht, token_position, 1);
                            tokens.add(token);
                            currState = STATE.START;
//                            col++;
//                            position++;
                            return token;


//                            String temp = sourceCode.substring(startPosition, position);
//                            char[] tempChars = new char[temp.length()];
//                            for(int i = 0; i<temp.length();i++){
//                                tempChars[i]=temp.charAt(i);
//                            }
//                            Token token = new Token(IToken.Kind.LT, tempChars,
//                                    token_position,
//                                    1);
//                            currState = STATE.START;
//                            return token;
                        }
                    }
                }

                case IN_ASSIGN -> {

                    switch (ch){

                        case '=' -> {

                            String temp = sourceCode.substring(startPosition, position + 1);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.ASSIGN, tempChars,
                                    token_position, (position + 1) - startPosition);
                            currState = STATE.START;
                            col++;
                            position++;
                            return token;
                        }
                        default -> {
                            throw new LexicalException("Invalid escape character", token_position.line(),
                                    token_position.column());
                        }
                    }
                }
            }

        }

    }

}
