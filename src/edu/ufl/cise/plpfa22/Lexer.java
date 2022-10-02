package edu.ufl.cise.plpfa22;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer implements ILexer {

    private STATE currentState;

    HashMap<String, IToken.Kind> reservedWords = new HashMap<>();
    ArrayList<Token> tokenList = new ArrayList<Token>();

    String inputStr;
    int row = 1;
    int currentPoint = 0;
    int column =  1;
    int initPoint = 0;
    int sti;

    private enum STATE {
        START, IN_ID, IN_STR, IN_SP, IN_ASSIGN, IN_LA, IN_NUM, IN_RA, HAVE_LT, HAVE_FSLASH, HAVE_GT,
        Have_EQ, HAVE_SLASH, IN_ARRW, IN_COMMENT;
    }
    int StateNum = 0;


    public Lexer(String inputStr) {
        sti = 0;
        this.inputStr = inputStr+" ";
        StateNum=0;
        currentState = STATE.START;

        reservedWords.put("PROCEDURE", IToken.Kind.KW_PROCEDURE);
        reservedWords.put("CALL", IToken.Kind.KW_CALL);

        reservedWords.put("FALSE", IToken.Kind.BOOLEAN_LIT);
        reservedWords.put("TRUE", IToken.Kind.BOOLEAN_LIT);
        reservedWords.put("CONST", IToken.Kind.KW_CONST);
        reservedWords.put("VAR", IToken.Kind.KW_VAR);
        reservedWords.put("THEN", IToken.Kind.KW_THEN);
        reservedWords.put("WHILE", IToken.Kind.KW_WHILE);
        reservedWords.put("DO", IToken.Kind.KW_DO);
        reservedWords.put("BEGIN", IToken.Kind.KW_BEGIN);
        reservedWords.put("END", IToken.Kind.KW_END);
        reservedWords.put("IF", IToken.Kind.KW_IF);



    }

    @Override
    public IToken peek() throws LexicalException {

        if (inputStr.length() <= currentPoint) {
            Token token = new Token(IToken.Kind.EOF, null, null, 0);
            StateNum = 0;
            column = column - ((currentPoint - initPoint) + 1);
            currentPoint = initPoint;
            sti = 0;
            return token;
        }
        sti = 0;
        Token token = getToken();

        if (token.getKind() == IToken.Kind.EOF) {
            StateNum = 0;
            column--;
            sti = 0;
            currentPoint--;
            sti = 0;
        }
        else {
            column = column - (currentPoint - initPoint);
            StateNum = 0;
            currentPoint = initPoint;
            sti = 0;
        }
        return token;
    }

    @Override
    public IToken next() throws LexicalException {

        if (inputStr.length() <= currentPoint) {

            StateNum = 0;
            Token token = new Token(IToken.Kind.EOF, null, null, 0);
            sti = 0;
            tokenList.add(token);
            return token;
        }


        Token token = getToken();
        StateNum = 0;
        tokenList.add(token);
        sti = 0;
        return token;
    }


    private Token getToken() throws LexicalException {

        int token_ref = 0;
        IToken.SourceLocation location = null;
        boolean CompleteString = true;

        while (true) {
            StateNum = 0;
            if (inputStr.length() <= currentPoint) {
                // Token newToken = new Token(IToken.Kind.EOF, null, null, 0);
                // return newToken;
                StateNum = 0;
                return new Token(IToken.Kind.EOF, null, null, 0);
            }

            char character = inputStr.charAt(currentPoint);
            switch (currentState) {
                // DFA
                case START -> {
                    switch (character) {
                        case '*' -> {
//                            col++;
                            token_ref = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.TIMES, cht, location, 1);
                            sti = 0;
                            tokenList.add(token);
                            StateNum = 0;
                            column++;
                            sti = 0;
                            currentPoint++;
                            return token;
                        }

                        case ' ', '\t', '\r' -> {
                            token_ref = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }

                        case '?' -> {
//                            col++;
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            token_ref = 0;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.QUESTION, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            sti = 0;
                            return token;
                        }
                        case '=' -> {
                            token_ref = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
                            System.out.println(character);
                            location = new IToken.SourceLocation(row, column);
                            Token token = new Token(IToken.Kind.EQ, cht, location, 1);
                            sti = 0;
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return  token;
                        }

                        case '\n' -> {
                            token_ref = 0;
                            row++;
                            currentPoint++;
                            StateNum = 0;
                            column = 1;
                        }

//                        case '\\' -> {
//                            col++;
//                            col++;
//                            position++;
//                        }
                        case '%' -> {
//                            col++;
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            sti = 0;
                            initPoint = currentPoint;
                            char[] cht = {character};
                            token_ref = 0;
                            Token token = new Token(IToken.Kind.MOD, cht, location, 1);
                            tokenList.add(token);
                            sti = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
                        case '#' -> {
//                            currState = STATE.IN_COMMENT;
//                            col++;
//                            position++;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.NEQ, cht, location, 1);
                            sti = 0;
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }

                        case '+' -> {

                            token_ref = 0;
                            initPoint = currentPoint;
                            // can i remove this?
//                            Token token = new Token(IToken.Kind.PLUS, String.valueOf(character), token_l, 1);

                            char[] cht = {character};
//                            System.out.println(character);
//                            col++;
//                            position++;
                            location = new IToken.SourceLocation(row, column);
                            Token token = new Token(IToken.Kind.PLUS, cht, location, 1);
                            sti = 0;
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
//                            System.out.println(col);
//                            System.out.println(position);
//                            token_l = new IToken.SourceLocation(col,position);
//                            token = new Token(IToken.Kind.PLUS, cht, token_l, 1);

                            return token;

//                            currState = STATE.HAVE_ZERO;
                        }
                        case ':' -> {
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            currentState = STATE.IN_ASSIGN;
                            sti = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }

                        case '.' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
                            token_ref = 0;
                            Token token = new Token(IToken.Kind.DOT, cht, location, 1);
                            sti = 0;
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }


                        case '/' -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            char[] cht = {character};
//                            Token token = new Token(IToken.Kind.DIV, cht, token_l, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            sti = 0;
                            initPoint = currentPoint;
                            currentState = STATE.HAVE_FSLASH;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }


                        case ';' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
                            token_ref = 0;
                            Token token = new Token(IToken.Kind.SEMI, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }

//                        case '|' -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.OR, String.valueOf(character), token_l, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case '^' -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.RETURN, String.valueOf(character), token_l, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }

                        case ',' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.COMMA, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }

                        case '-' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
                            char[] cht = {character};
//                            System.out.println(character);
//                            col++;
//                            position++;
//                            token_l = new IToken.SourceLocation(line,col);
                            Token token = new Token(IToken.Kind.MINUS, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
                        case '!' -> {
                            sti = 0;
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            currState = STATE.HAVE_EX;
//                            col++;
//                            position++;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            // can i remove this?
//                            Token token = new Token(IToken.Kind.PLUS, String.valueOf(character), token_l, 1);
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.BANG, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
                        case '<' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            token_ref = 0;
                            currentState = STATE.HAVE_LT;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }

//                        case '[' -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.LSQUARE, String.valueOf(character), token_l, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case ']' -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            Token token = new Token(IToken.Kind.RSQUARE, String.valueOf(character), token_l, 1);
//                            tokens.add(token);
//                            col++;
//                            position++;
//                            return token;
//                        }

                        case '(' -> {
                            sti = 0;

                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.LPAREN, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
                        case ')' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.RPAREN, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }


                        case '0' -> {
                            sti = 0;
//                            token_l = new IToken.SourceLocation(line, col);
//                            startPosition = position;
//                            currState = STATE.HAVE_ZERO;
//                            col++;
//                            position++;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            char[] cht = {character};
                            Token token = new Token(IToken.Kind.NUM_LIT, cht, location, 1);
                            tokenList.add(token);
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            currentState = STATE.START;
                            return token;
                        }
                        case '"' -> {
                            sti = 0;
                            System.out.println("opening quote");
                            token_ref = 0;
                            location = new IToken.SourceLocation(row, column);
                            initPoint = currentPoint;
//                            CompleteString = false;
                            currentState = STATE.IN_STR;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }

                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            currentState = STATE.IN_NUM;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }
                        case '>' -> {
                            sti = 0;
                            location = new IToken.SourceLocation(row, column);
                            token_ref = 0;
                            initPoint = currentPoint;
                            currentState = STATE.HAVE_GT;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }

                        default -> {
                            sti = 0;
                            if (Character.isJavaIdentifierStart(character)) {
                                location = new IToken.SourceLocation(row, column);
                                token_ref = 0;
                                initPoint = currentPoint;
//                                System.out.println("test - *");
                                currentState = STATE.IN_ID;
                                column++;
                                StateNum = 0;
                                currentPoint++;

                            } else {
                                StateNum = 0;
                                location = new IToken.SourceLocation(row, column);
                                token_ref = 0;
                                throw new LexicalException("Invalid character", location.line(),
                                        location.column());
                            }
                        }

                    }
                }


                case HAVE_LT -> {

                    switch (character) {

                        case '=' -> {
                            StateNum = 0;
                            String temp = inputStr.substring(initPoint, currentPoint + 1);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.LE, tempChars,
                                    location, (currentPoint + 1) - initPoint);
                            StateNum = 0;
                            currentState = STATE.START;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
//                        case '-' -> {
//                            Token token = new Token(IToken.Kind.LARROW,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_l, (position + 1) - startPosition);
//                            currState = STATE.HAVE_MINUS;
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        case '<' -> {
//                            Token token = new Token(IToken.Kind.LANGLE,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_l, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
                        default -> {
                            sti = 0;
                            String temp = inputStr.substring(initPoint, currentPoint);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }
                            sti = 0;
                            Token token = new Token(IToken.Kind.LT, tempChars,
                                    location,
                                    1);
                            StateNum = 0;
                            currentState = STATE.START;
                            return token;
                        }

                    }

                }

//                case HAVE_EX -> {
//
//                    switch (character) {
//
////                        case '=' -> {
////                            Token token = new Token(IToken.Kind.NOT_EQUALS,
////                                    sourceCode.substring(startPosition, position + 1),
////                                    token_l, (position + 1) - startPosition);
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
//                                    token_l,
//                                    1);
//                            currState = STATE.START;
//                            return token;
//                        }
//
//                    }
//
//                }

//                case HAVE_ZERO -> {
//                    switch (character) {
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
//                                    token_l, (position - startPosition));
//                            currState = STATE.START;
//                            return token;
//                        }
//                    }
//                }

                case IN_ID -> {
                    if (Character.isJavaIdentifierPart(character)) {
                        token_ref = 0;
                        System.out.println(character);
                        column++;
                        sti = 0;
                        currentPoint++;

                    } else {
                        sti = 0;
                        String ident = inputStr.substring(initPoint, currentPoint);
                        token_ref = 0;
                        char[] identChars = new char[ident.length()];
                        StateNum = 0;
                        for(int i = 0; i<ident.length();i++){
                            identChars[i]=ident.charAt(i);
                        }
                        sti = 0;
//                        System.out.println("col:"+ column);
                        for (Map.Entry<String, IToken.Kind> entry : reservedWords.entrySet()) {
                            StateNum = 0;
                            if (ident.equals(entry.getKey())) {
                                token_ref = 0;
                                Token token = new Token(entry.getValue(), identChars, location,
                                        (currentPoint - initPoint));
                                sti = 0;
                                currentState = STATE.START;
                                return token;
                            }
                        }
//                        System.out.println(identChars[0]);
                        sti = 0;
                        location = new IToken.SourceLocation(row, column -(identChars.length));
                        StateNum = 0;
                        System.out.println("col:"+column);
                        System.out.println(ident);
                        Token token = new Token(IToken.Kind.IDENT, identChars, location, currentPoint - initPoint);
                        token_ref = 0;
                        currentState = STATE.START;
                        return token;
                    }
                }
//                case HAVE_DOT -> {
//                    switch (character) {
//                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                            currState = STATE.IN_FLOAT;
//                            col++;
//                            position++;
//                        }
//                        default -> {
//                            throw new LexicalException("found error with input number(check number again)",
//                                    token_l.line(), token_l.column());
//                        }
//                    }
//                }

                case IN_NUM -> {
                    switch (character) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            StateNum = 0;
                            currentPoint++;
                            column++;
                        }
//                        case '.' -> {
//                            currState = STATE.HAVE_DOT;
//                            col++;
//                            position++;
//                        }
                        default -> {
                            StateNum = 0;
                            String str = inputStr.substring(initPoint, currentPoint);
                            token_ref = 0;
                            char[] tempChars = new char[str.length()];
                            for(int i = 0; i<str.length();i++){
                                tempChars[i]=str.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.NUM_LIT, tempChars, location,
                                    (currentPoint - initPoint));

                            try {
                                StateNum = 0;
                                int temp = Integer.valueOf(str);
                                token_ref = 0;
                            } catch (Exception e) {
                                token_ref = 0;
                                throw new LexicalException("Invalid integer", location.line(),
                                        location.column());
                            }
                            token_ref = 0;
                            currentState = STATE.START;
                            sti = 0;
                            return token;
                        }
                    }
                }
                case HAVE_GT -> {

                    switch (character) {

                        case '=' -> {
                            token_ref = 0;
                            String temp = inputStr.substring(initPoint, currentPoint + 1);
                            char[] tempChars = new char[temp.length()];
                            StateNum = 0;
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.GE, tempChars,
                                    location, (currentPoint + 1) - initPoint);
                            StateNum = 0;
                            currentState = STATE.START;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
//                        case '>' -> {
//                            Token token = new Token(IToken.Kind.RANGLE,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_l, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
                        default -> {
                            token_ref = 0;
                            String temp = inputStr.substring(initPoint, currentPoint);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }

                            Token token = new Token(IToken.Kind.GT, tempChars,
                                    location,
                                    1);
                            token_ref = 0;
                            currentState = STATE.START;
                            StateNum = 0;
                            return token;
                        }

                    }

                }

//                case IN_FLOAT -> {
//                    switch (character) {
//                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                            col++;
//                            position++;
//                        }
//                        default -> {
//                            String fl = sourceCode.substring(startPosition, position);
//
//                            Token token = new Token(IToken.Kind.FLOAT_LIT,
//                                    sourceCode.substring(startPosition, position),
//                                    token_l, (position - startPosition));
//
//                            try {
//                                float floatValue = Float.valueOf(fl);
//                            } catch (Exception e) {
//                                throw new LexicalException("Invalid float input", token_l.line(),
//                                        token_l.column());
//                            }
//                            currState = STATE.START;
//                            return token;
//                        }
//                    }
//                }

//                case HAVE_MINUS -> {
//
//                    switch (character) {
//
//                        case '>' -> {
//                            Token token = new Token(IToken.Kind.RARROW,
//                                    sourceCode.substring(startPosition, position + 1),
//                                    token_l, (position + 1) - startPosition);
//                            currState = STATE.START;
//                            col++;
//                            position++;
//                            return token;
//                        }
//                        default -> {
//                            Token token = new Token(IToken.Kind.MINUS, sourceCode.substring(startPosition, position),
//                                    token_l, 1);
//                            currState = STATE.START;
//                            return token;
//                        }
//
//                    }
//
//                }
                case IN_STR -> {

                    switch (character) {

                        case '\\' -> {
                            token_ref = 0;
                            currentState = STATE.HAVE_SLASH;
                            sti = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                        }
                        case '"' -> {
                            CompleteString = true;
                            System.out.println("closing quotes");
                            String temp = inputStr.substring(initPoint, currentPoint + 1);
                            char[] tempList = new char[temp.length()];
                            for (int i = 0;i<temp.length();i++){
                                tempList[i] = temp.charAt(i);
                            }

//                            token_l = new IToken.SourceLocation(line, col);
                            StateNum = 0;
                            Token token = new Token(IToken.Kind.STRING_LIT,
//                                    sourceCode.substring(startPosition, position + 1),
                                    tempList,
                                    location, (currentPoint + 1) - initPoint);
                            token_ref = 0;
                            currentState = STATE.START;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            return token;
                        }
                        case '\n' -> {
                            token_ref = 0;
                            column = 1;
                            CompleteString=true;
                            row++;
                            StateNum = 0;
                            currentPoint++;
                        }
                        default -> {
//                            System.out.println("default case");
                            StateNum = 0;
                            column++;
                            sti = 0;
                            currentPoint++;
//                            System.out.println(sourceCode.charAt(position));
//                            System.out.println(position+" "+sourceCode.length());
                            if(currentPoint==inputStr.length()-2 && inputStr.charAt(currentPoint)!='"'){
                                throw new LexicalException("Invalid escape character", location.line(),
                                        location.column());
                            }
                        }

                    }
//                    if(!CompleteString){
//                        throw new LexicalException("String did not finish", token_l.line(),
//                                token_l.column());
//                    }

                }

                case IN_COMMENT -> {

                    switch (character) {

                        case '\n' -> {
                            token_ref = 0;
                            currentState = STATE.START;
                            StateNum = 0;
                            row++;
                            sti = 0;
                            column = 1;
                            StateNum = 0;
                            currentPoint++;
                        }

                        default -> {
                            token_ref = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            sti = 0;
                        }

                    }

                }
                case HAVE_SLASH -> {

                    switch (character) {

                        case 'b', 't', 'n', 'f', 'r', '\\', '\'', '\"' -> {
                            token_ref = 0;
//                            System.out.println("in here"+inputStr.length());
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            token_ref = 0;
                            if(currentPoint==inputStr.length()-2 && inputStr.charAt(currentPoint)!='"'){
                                token_ref = 0;
                                throw new LexicalException("Invalid escape character", location.line(),
                                        location.column());
                            }
                            else {
                                token_ref = 0;
                                currentState = STATE.IN_STR;
                                sti = 0;
                            }
                        }
                        default -> {
                            StateNum = 0;
                            throw new LexicalException("Invalid escape character", location.line(),
                                    location.column());
                        }

                    }

                }
                case HAVE_FSLASH -> {

                    switch (character){

                        case '/' -> {
                            currentState = STATE.IN_COMMENT;
                            token_ref = 0;
                            column++;
                            StateNum = 0;
                            currentPoint++;
                            sti = 0;
                        }
                        default -> {
//                            token_l = new IToken.SourceLocation(line, col);
//                            System.out.println(col);
//                            startPosition = position;
                            char[] cht = {character};
                            token_ref = 0;
                            Token token = new Token(IToken.Kind.DIV, cht, location, 1);
                            StateNum = 0;
                            tokenList.add(token);
                            token_ref = 0;
                            currentState = STATE.START;
                            sti = 0;
//                            col++;
//                            position++;
                            return token;


//                            String temp = sourceCode.substring(startPosition, position);
//                            char[] tempChars = new char[temp.length()];
//                            for(int i = 0; i<temp.length();i++){
//                                tempChars[i]=temp.charAt(i);
//                            }
//                            Token token = new Token(IToken.Kind.LT, tempChars,
//                                    token_l,
//                                    1);
//                            currState = STATE.START;
//                            return token;
                        }
                    }
                }

                case IN_ASSIGN -> {

                    switch (character){

                        case '=' -> {
                            StateNum = 0;
                            String temp = inputStr.substring(initPoint, currentPoint + 1);
                            char[] tempChars = new char[temp.length()];
                            for(int i = 0; i<temp.length();i++){
                                tempChars[i]=temp.charAt(i);
                            }
                            token_ref = 0;
                            Token token = new Token(IToken.Kind.ASSIGN, tempChars,
                                    location, (currentPoint + 1) - initPoint);
                            StateNum = 0;
                            currentState = STATE.START;
                            sti = 0;
                            column++;
                            token_ref = 0;
                            currentPoint++;
                            return token;
                        }
                        default -> {
                            StateNum = 0;
                            throw new LexicalException("Invalid escape character", location.line(),
                                    location.column());
                        }
                    }
                }
            }

//            if(!CompleteString) throw new LexicalException("String did not finish", token_l.line(),
//                    token_l.column());
        }

    }

}
