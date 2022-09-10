package edu.ufl.cise.plpfa22;

import java.util.*;
import java.util.stream.Collectors;

public class Token implements IToken {

    private Kind Type;
    private char[] sourceCode;
    private SourceLocation pos;
    public Token(Kind Type, char[] sourceCode, SourceLocation pos, int length) {
        this.Type = Type;
        this.sourceCode = sourceCode;
        this.pos = pos;
    }

    @Override
    public Kind getKind() {
        return Type;
    }

    @Override
    public char[] getText() {
        return sourceCode;
    }



    @Override
    public SourceLocation getSourceLocation() {
        System.out.println("pos:" +pos);
        return pos;
    }

    @Override
    public int getIntValue() {
        return Integer.valueOf(String.valueOf(sourceCode));
    }

//    @Override
//    public float getFloatValue() {
//        return Float.valueOf(String.valueOf(sourceCode));
//    }

    @Override
    public boolean getBooleanValue() {
        return Boolean.valueOf(String.valueOf(sourceCode));
    }

    @Override
    public String getStringValue() {

        String resString = "";
        String SC = String.valueOf(sourceCode);


        for (int i = 1; i < SC.length() - 1; i++) {

            if (SC.charAt(i) != '\\') {
                resString += SC.charAt(i);
            } else {
                switch (SC.charAt(i + 1)) {

                    case 'r' -> {
                        resString += '\r';
                    }
                    case 'f' -> {
                        resString += '\f';
                    }
                    case 'n' -> {
                        resString += '\n';
                    }
                    case 't' -> {
                        resString += '\t';
                    }
                    case 'b' -> {
                        resString += '\b';
                    }
                    case '"' -> {
                        resString += '\"';
                    }
                    case '\'' -> {
                        resString += '\'';
                    }
                    case '\\' -> {
                        resString += '\\';
                    }

                }
                i++;
            }

        }

        return resString;
    }

}