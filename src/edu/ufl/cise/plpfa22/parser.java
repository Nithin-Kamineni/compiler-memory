package edu.ufl.cise.plpfa22;

//import edu.ufl.cise.plpfa22.ast.*;
//import edu.ufl.cise.plpfa22.ast.Dimension;

import edu.ufl.cise.plpfa22.ast.ASTNode;
import edu.ufl.cise.plpfa22.ast.Expression;
import edu.ufl.cise.plpfa22.ast.VarDec;

import java.util.HashSet;

public class parser implements IParser {
    ILexer lexer;
    IToken currentToken;
    HashSet<IToken.Kind> ProgramSet = new HashSet();
    HashSet<IToken.Kind> BlockSet = new HashSet();
    HashSet<IToken.Kind> StatmentSet = new HashSet();
    HashSet<IToken.Kind> ExpressionSet = new HashSet();
    HashSet<IToken.Kind> AdditiveExpSet = new HashSet();
    HashSet<IToken.Kind> MultiExpSet = new HashSet();
    HashSet<IToken.Kind> PrimaryExprSet = new HashSet();
    HashSet<IToken.Kind> PixelSelectorSet = new HashSet();
    HashSet<IToken.Kind> ConstSet = new HashSet();


    public void Parser(ILexer lexer) throws LexicalException {
        this.lexer = lexer;
        currentToken = (Token) lexer.next();

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

            IToken firstToken = currentToken;
            ExpresionNode E = null;
            if(currentToken.getKind()==IToken.Kind.IDENT){
                E = new IntLitExpr(firstToken);
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


    public void block1() throws PLPException {
        match(IToken.Kind.KW_CONST);
        match(IToken.Kind.IDENT);
        match(IToken.Kind.EQ);
        if (currentToken.getKind() == IToken.Kind.NUM_LIT || currentToken.getKind() == IToken.Kind.STRING_LIT || currentToken.getKind() == IToken.Kind.BOOLEAN_LIT) {
            if (currentToken.getKind() == IToken.Kind.NUM_LIT) {
                match(IToken.Kind.NUM_LIT);
            } else if (currentToken.getKind() == IToken.Kind.STRING_LIT) {
                match(IToken.Kind.STRING_LIT);
            } else if (currentToken.getKind() == IToken.Kind.BOOLEAN_LIT) {
                match(IToken.Kind.BOOLEAN_LIT);
            } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        } else {
            throw new SyntaxException("expected logical and token in LogicalOrExpr");
        }
        while(currentToken.getKind()==IToken.Kind.COMMA){
            match(IToken.Kind.COMMA);
            match(IToken.Kind.IDENT);
            match(IToken.Kind.EQ);
            if (currentToken.getKind() == IToken.Kind.NUM_LIT || currentToken.getKind() == IToken.Kind.STRING_LIT || currentToken.getKind() == IToken.Kind.BOOLEAN_LIT) {
                if (currentToken.getKind() == IToken.Kind.NUM_LIT) {
                    match(IToken.Kind.NUM_LIT);
                } else if (currentToken.getKind() == IToken.Kind.STRING_LIT) {
                    match(IToken.Kind.STRING_LIT);
                } else if (currentToken.getKind() == IToken.Kind.BOOLEAN_LIT) {
                    match(IToken.Kind.BOOLEAN_LIT);
                } else {
                    throw new SyntaxException("expected logical and token in LogicalOrExpr");
                }
            } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        }
    }

    public void block2() throws PLPException {
        match(IToken.Kind.KW_VAR);
        match(IToken.Kind.IDENT);
        while (currentToken.getKind() == IToken.Kind.COMMA){
            match(IToken.Kind.COMMA);
            match(IToken.Kind.IDENT);
        }
        match(IToken.Kind.SEMI);
    }

    public void block3() throws PLPException{
        match(IToken.Kind.KW_PROCEDURE);
        match(IToken.Kind.IDENT);
        match(IToken.Kind.SEMI);
        block1();
        block2();
        block3();
        match(IToken.Kind.SEMI);
    }

    public void statement() throws PLPException {
        if (currentToken.getKind() == IToken.Kind.IDENT) {
            match(IToken.Kind.IDENT);
            match(IToken.Kind.ASSIGN);
            expr();
        } else if (currentToken.getKind() == IToken.Kind.KW_CALL) {
            match(IToken.Kind.KW_CALL);
            match(IToken.Kind.IDENT);
        } else if (currentToken.getKind() == IToken.Kind.QUESTION) {
            match(IToken.Kind.QUESTION);
            match(IToken.Kind.IDENT);
        } else if (currentToken.getKind() == IToken.Kind.BANG) {
            match(IToken.Kind.BANG);
            expr();
        } else if (currentToken.getKind() == IToken.Kind.KW_BEGIN) {
            match(IToken.Kind.KW_BEGIN);
            statement();
            while (currentToken.getKind() == IToken.Kind.SEMI) {
                match(IToken.Kind.SEMI);
                statement();
            }
            match(IToken.Kind.KW_END);
        } else if (currentToken.getKind() == IToken.Kind.KW_IF) {
            match(IToken.Kind.KW_IF);
            expr();
            match(IToken.Kind.KW_THEN);
            statement();
        } else if (currentToken.getKind() == IToken.Kind.KW_IF) {
            match(IToken.Kind.KW_IF);
            expr();
            match(IToken.Kind.KW_THEN);
            statement();
        }
    }

    public void block() throws PLPException{
        if(currentToken.getKind() == IToken.Kind.KW_CONST){
            while (currentToken.getKind() == IToken.Kind.KW_CONST){
                block1();
            }
            while (currentToken.getKind() == IToken.Kind.KW_VAR){
                block2();
            }
            while (currentToken.getKind() == IToken.Kind.KW_PROCEDURE){
                block3();
            }
        }
        else{
            throw new SyntaxException("expected logical and token in LogicalOrExpr");
        }
        statement();
    }

    ////////////////////////////////////////////////////////////////

    abstract class Expression extends ASTNode {
        public final IToken firstToken;
        public Expression(IToken firstToken) {
            super(firstToken);
            this.firstToken = firstToken;
        }
    }

    class ExpressionNumLit extends Expression {
        public  ExpressionNumLit(IToken firstToken){
            super(firstToken);
        }
        public int getValue(){
            return firstToken.getIntValue();
        }
    }

    class ExpressionBinary extends Expression{
        public final Expression left;
        public final IToken op;
        public final Expression right;

        public ExpressionBinary(IToken firstToken, Expression left, IToken op, Expression right){
            super(firstToken);
            this.left=left;
            this.op=op;
            this.right=right;
        }
    }

    ////////////////////////////////////////////////////////////////

    abstract class ExpresionNode extends ASTNode {
        public final IToken firstToken;

        public ExpresionNode(IToken firstToken) {
            super(firstToken);
            this.firstToken = firstToken;
        }
    }

    class additiveExp extends ASTNode {

    }


    abstract class primaryExpr extends ASTNode {
        public final IToken firstToken;
        public primaryExpr(IToken firstToken){
            super(firstToken);
            this.firstToken=firstToken;
        }
    }

    class IdentPrimExpr extends  primaryExpr{
        public  IdentPrimExpr(IToken firstToken){
            super(firstToken);
        }
    }

    class constValPrimExpr extends primaryExpr{
        public final IToken numLitConst;
        public final IToken strLitConst;
        public final IToken boolLitConst;
        public constValPrimExpr(IToken firstToken, IToken numLitConst, IToken strLitConst, IToken boolLitConst){
            super(firstToken);
            this.numLitConst=numLitConst;
            this.strLitConst=strLitConst;
            this.boolLitConst=boolLitConst;
        }
    }






    ASTNode ProgramFunc() throws PLPException {
        IToken firstToken = currentToken;
        block();
        ProgramFunc();

    }

}

