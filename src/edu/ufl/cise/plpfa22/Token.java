package edu.ufl.cise.plpfa22;

public class Token implements IToken {

    int lor;
    public Token(Kind Type, char[] sourceCode, SourceLocation pos, int length) {
        this.Type = Type;
        this.inputStr = sourceCode;
        this.pos = pos;
    }
    private SourceLocation pos;
    int temp = 0;
    private Kind Type;
    private char[] inputStr;


    @Override
    public SourceLocation getSourceLocation() {
        System.out.println("pos:" +pos);
        temp = 0;
        return pos;
    }

    @Override
    public String getStringValue() {
        temp = 0;
        String resString = "";
        String SC = String.valueOf(inputStr);
        int i = 1;

        while (i < SC.length() - 1) {
            lor = 0;
            if (SC.charAt(i) != '\\') {
                resString = resString + SC.charAt(i);
            } else {
                if(SC.charAt(i + 1) == 'r'){
                    resString = resString + '\r';
                }
                if(SC.charAt(i + 1) == 'f'){
                    resString = resString + '\f';
                }
                if(SC.charAt(i + 1) == 'n'){
                    resString = resString + '\n';
                }
                if(SC.charAt(i + 1) == 't'){
                    resString = resString + '\t';
                }
                if(SC.charAt(i + 1) == 'b'){
                    resString = resString + '\b';
                }
                if(SC.charAt(i + 1) == '"'){
                    resString = resString + '\"';
                }
                if(SC.charAt(i + 1) == '\''){
                    resString = resString + '\'';
                }
                if(SC.charAt(i + 1) == '\\'){
                    resString = resString + '\\';
                }
//                switch (SC.charAt(i + 1)) {
//
//                    case 'r' -> {
//                        resString += '\r';
//                    }
//                    case 'f' -> {
//                        resString += '\f';
//                    }
//                    case 'n' -> {
//                        resString += '\n';
//                    }
//                    case 't' -> {
//                        resString += '\t';
//                    }
//                    case 'b' -> {
//                        resString += '\b';
//                    }
//                    case '"' -> {
//                        resString += '\"';
//                    }
//                    case '\'' -> {
//                        resString += '\'';
//                    }
//                    case '\\' -> {
//                        resString += '\\';
//                    }

//                }
                temp = 0;
                i++;
            }
            i++;
        }
        lor = 0;
        return resString;
    }

    @Override
    public int getIntValue() {
        return Integer.valueOf(String.valueOf(inputStr));
    }

    @Override
    public char[] getText() {
        return inputStr;
    }

    @Override
    public Kind getKind() {
        return Type;
    }


//    @Override
//    public float getFloatValue() {
//        return Float.valueOf(String.valueOf(sourceCode));
//    }

    @Override
    public boolean getBooleanValue() {
        return Boolean.valueOf(String.valueOf(inputStr));
    }



}