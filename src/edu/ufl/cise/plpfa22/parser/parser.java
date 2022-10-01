package edu.ufl.cise.plpfa22;

//import edu.ufl.cise.plpfa22.ast.*;
//import edu.ufl.cise.plpfa22.ast.Dimension;

import edu.ufl.cise.plpfa22.ast.ASTNode;

import java.util.HashSet;

public class parser implements IParser {
    ILexer lexer;
    IToken currentToken;
    HashSet<IToken.Kind> ExprSet = new HashSet();
    HashSet<IToken.Kind> ConditionalExprSet = new HashSet();
    HashSet<IToken.Kind> LogicalOrExprSet = new HashSet();
    HashSet<IToken.Kind> LogicalAndExprSet = new HashSet();
    HashSet<IToken.Kind> ComparisonExprSet = new HashSet();
    HashSet<IToken.Kind> AdditiveExprSet = new HashSet();
    HashSet<IToken.Kind> MultiplicativeExprSet = new HashSet();
    HashSet<IToken.Kind> UnaryExprSet = new HashSet();
    HashSet<IToken.Kind> UnaryExprPostfixSet = new HashSet();
    HashSet<IToken.Kind> PrimaryExprSet = new HashSet();
    HashSet<IToken.Kind> PixelSelectorSet = new HashSet();
    HashSet<IToken.Kind> PrgmSet = new HashSet();
    HashSet<IToken.Kind> NameDefinitionSet = new HashSet();
    HashSet<IToken.Kind> DeclarationSet = new HashSet();
    HashSet<IToken.Kind> DimensionSet = new HashSet();
    HashSet<IToken.Kind> StatementSet = new HashSet();


    public void Parser(ILexer lexer) throws LexicalException {
        this.lexer = lexer;
        currentToken = (Token) lexer.next();
//        ConditionalExprSet.add(IToken.Kind.KW_IF);
//        PrimaryExprSet.add(IToken.Kind.BOOLEAN_LIT);
//        PrimaryExprSet.add(IToken.Kind.STRING_LIT);
//        PrimaryExprSet.add(IToken.Kind.NUM_LIT);
//        PrimaryExprSet.add(IToken.Kind.IDENT);
//        PrimaryExprSet.add(IToken.Kind.LPAREN);
//        UnaryExprPostfixSet.addAll(PrimaryExprSet);
//        UnaryExprSet.addAll(UnaryExprPostfixSet);
//        UnaryExprSet.add(IToken.Kind.BANG);
//        UnaryExprSet.add(IToken.Kind.MINUS);
//
//        MultiplicativeExprSet.addAll(UnaryExprSet);
//        AdditiveExprSet.addAll(UnaryExprSet);
//        ComparisonExprSet.addAll(UnaryExprSet);
//        LogicalAndExprSet.addAll(UnaryExprSet);
//        LogicalOrExprSet.addAll(UnaryExprSet);
//
//        ExprSet.addAll(ConditionalExprSet);
//        ExprSet.addAll(LogicalOrExprSet);

    }



    void consume() throws PLPException {
        currentToken = (Token) lexer.next();
    }

    void match(IToken.Kind kind) throws PLPException {
        if (kind == currentToken.getKind()) {
//            currentToken = (Token) lexer.next();
            consume();
        } else {
            throw new SyntaxException("expected currentToken " + currentToken.getKind() + " to match " + kind);
        }
    }

    void expr() throws PLPException {
        aditiveExpresion();
        while (currentToken.getKind() == IToken.Kind.LT || currentToken.getKind() == IToken.Kind.GT || currentToken.getKind() == IToken.Kind.EQ || currentToken.getKind() == IToken.Kind.NEQ || currentToken.getKind() == IToken.Kind.LE || currentToken.getKind() == IToken.Kind.GE) {
            if (currentToken.getKind() == IToken.Kind.LT) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.GT) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.EQ) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.NEQ) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.LE) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.GE) {
                match(IToken.Kind.LT);
            }
            aditiveExpresion();
        }
    }

    void aditiveExpresion() throws PLPException {
            multiplicativeExpresion();
            while(currentToken.getKind()== IToken.Kind.PLUS || currentToken.getKind()== IToken.Kind.MINUS){
                if(currentToken.getKind()== IToken.Kind.PLUS){
                    match(IToken.Kind.PLUS);
                }
                else if(currentToken.getKind()== IToken.Kind.MINUS){
                    match(IToken.Kind.MINUS);
                }
                multiplicativeExpresion();
            }
        }


        void multiplicativeExpresion() throws PLPException{
        primaryExpresion();
        while(currentToken.getKind()== IToken.Kind.TIMES || currentToken.getKind()== IToken.Kind.DIV || currentToken.getKind()== IToken.Kind.MOD){
            if(currentToken.getKind()== IToken.Kind.TIMES){
                match(IToken.Kind.TIMES);
                }
            else if(currentToken.getKind()== IToken.Kind.DIV){
                match(IToken.Kind.DIV);
                }
            else if(currentToken.getKind()== IToken.Kind.MOD){
                match(IToken.Kind.MOD);
                }
            else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
            primaryExpresion();
            }
        }

        void primaryExpresion() throws PLPException{
//            if(!(currentToken.getKind()==IToken.Kind.IDENT || currentToken.getKind()==IToken.Kind.LPAREN)){
//                System.out.println("ERROR");
//                do {
//                    currentToken=lexer.next();
//                } while { currentToken.getKind() == IToken.Kind.IDENT || IToken.Kind.IDENT}
//            }
            if(currentToken.getKind()==IToken.Kind.IDENT){
                match(IToken.Kind.IDENT);
            }
            else if(currentToken.getKind()==IToken.Kind.NUM_LIT || currentToken.getKind()==IToken.Kind.STRING_LIT || currentToken.getKind()==IToken.Kind.BOOLEAN_LIT){
                if(currentToken.getKind()== IToken.Kind.NUM_LIT){
                    match(IToken.Kind.NUM_LIT);
                }
                else if(currentToken.getKind()== IToken.Kind.STRING_LIT){
                    match(IToken.Kind.STRING_LIT);
                }
                else if(currentToken.getKind()== IToken.Kind.BOOLEAN_LIT){
                    match(IToken.Kind.BOOLEAN_LIT);
                }
                else {
                    throw new SyntaxException("expected logical and token in LogicalOrExpr");
                }
            }
            else if(currentToken.getKind()==IToken.Kind.LPAREN){
                match(IToken.Kind.LPAREN);
                expr();
                match(IToken.Kind.LPAREN);
            }
            else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        }

    public ASTNode parse() throws PLPException {
        while (true) {
            consume();
            return ProgramFunc();
        }
    }



    void ASTNode(){
        IToken firstToken = currentToken;
    }

    block

    ASTNode ProgramFunc() throws PLPException {
        IToken firstToken = currentToken;
        if(currentToken.getKind() == IToken.Kind.KW_CONST){
            block();
        }


    }

}

