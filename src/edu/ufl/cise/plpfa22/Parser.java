package edu.ufl.cise.plpfa22;

//import edu.ufl.cise.plpfa22.ast.*;
//import edu.ufl.cise.plpfa22.ast.Dimension;

import edu.ufl.cise.plpfa22.ast.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BinaryOperator;

public class Parser implements IParser {
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


    public Parser(ILexer lexer){
        this.lexer = lexer;
    }



    void consume() throws PLPException {
        currentToken = (Token) lexer.next();
    }

    void match(IToken.Kind kind) throws PLPException {
        if (kind == currentToken.getKind()) {
            consume();
        } else {
            throw new SyntaxException("expected currentToken " + currentToken.getKind() + " to match " + kind);
        }
    }

    public Expression expr() throws PLPException {
        IToken firstToken = currentToken;
        Expression left = null;
        Expression right = null;
        left = aditiveExpresion();

        while (currentToken.getKind() == IToken.Kind.LT || currentToken.getKind() == IToken.Kind.GT || currentToken.getKind() == IToken.Kind.EQ || currentToken.getKind() == IToken.Kind.NEQ || currentToken.getKind() == IToken.Kind.LE || currentToken.getKind() == IToken.Kind.GE) {
            IToken op = currentToken;
            if (currentToken.getKind() == IToken.Kind.LT) {
                match(IToken.Kind.LT);
            } else if (currentToken.getKind() == IToken.Kind.GT) {
                match(IToken.Kind.GT);
            } else if (currentToken.getKind() == IToken.Kind.EQ) {
                match(IToken.Kind.EQ);
            } else if (currentToken.getKind() == IToken.Kind.NEQ) {
                match(IToken.Kind.NEQ);
            } else if (currentToken.getKind() == IToken.Kind.LE) {
                match(IToken.Kind.LE);
            } else if (currentToken.getKind() == IToken.Kind.GE) {
                match(IToken.Kind.GE);
            } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
            right = aditiveExpresion();
            left = new ExpressionBinary(firstToken, left, op, right);
        }
        return left;
    }

    Expression aditiveExpresion() throws PLPException {
            IToken firstToken = currentToken;
            Expression left = null;
            Expression right = null;
            left = multiplicativeExpresion();
            while(currentToken.getKind()== IToken.Kind.PLUS || currentToken.getKind()== IToken.Kind.MINUS){
                IToken op = currentToken;
                if(currentToken.getKind()== IToken.Kind.PLUS){
                    match(IToken.Kind.PLUS);
                }
                else if(currentToken.getKind()== IToken.Kind.MINUS){
                    match(IToken.Kind.MINUS);
                } else {
                    throw new SyntaxException("expected logical and token in LogicalOrExpr");
                }
                right = multiplicativeExpresion();
                left = new ExpressionBinary(firstToken, left, op, right);
            }
            return left;
        }


        Expression multiplicativeExpresion() throws PLPException{
            IToken firstToken = currentToken;
            Expression left = null;
            Expression right = null;
            left = primaryExpresion();
            while(currentToken.getKind()== IToken.Kind.TIMES || currentToken.getKind()== IToken.Kind.DIV || currentToken.getKind()== IToken.Kind.MOD){
                IToken op = currentToken;
                if(currentToken.getKind()== IToken.Kind.TIMES){
                match(IToken.Kind.TIMES);
                }
                else if(currentToken.getKind()== IToken.Kind.DIV){
                match(IToken.Kind.DIV);
                }
                else if(currentToken.getKind()== IToken.Kind.MOD){
                match(IToken.Kind.MOD);
                } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
                }
                right = primaryExpresion();
                left = new ExpressionBinary(firstToken, left, op, right);
            }
            return left;
        }

        Expression primaryExpresion() throws PLPException{
//            if(!(currentToken.getKind()==IToken.Kind.IDENT || currentToken.getKind()==IToken.Kind.LPAREN)){
//                System.out.println("ERROR");
//                do {
//                    currentToken=lexer.next();
//                } while { currentToken.getKind() == IToken.Kind.IDENT || IToken.Kind.IDENT}
//            }

            IToken firstToken = currentToken;

            if(currentToken.getKind()==IToken.Kind.IDENT){
//                E = new IntLitExpr(firstToken);
                ExpressionIdent NodePE = new ExpressionIdent(firstToken);
                match(IToken.Kind.IDENT);
                return NodePE;
            }
            else if(currentToken.getKind()==IToken.Kind.NUM_LIT || currentToken.getKind()==IToken.Kind.STRING_LIT || currentToken.getKind()==IToken.Kind.BOOLEAN_LIT){
                Expression NodePE =  constVal();
                return NodePE;
            }
            else if(currentToken.getKind()==IToken.Kind.LPAREN){
                match(IToken.Kind.LPAREN);
                Expression NodePE =  expr();
                match(IToken.Kind.RPAREN);
                return NodePE;
            }
            else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        }

        Expression constVal() throws PLPException{
        IToken firstToken = currentToken;
        if(currentToken.getKind()== IToken.Kind.NUM_LIT){
                ExpressionNumLit NodeCV = new ExpressionNumLit(currentToken);
                match(IToken.Kind.NUM_LIT);
                return NodeCV;
            }
            else if(currentToken.getKind()== IToken.Kind.STRING_LIT){
                ExpressionStringLit NodeCV = new ExpressionStringLit(currentToken);
                match(IToken.Kind.STRING_LIT);
                return NodeCV;
            }
            else if(currentToken.getKind()== IToken.Kind.BOOLEAN_LIT){
                ExpressionBooleanLit NodeCV = new ExpressionBooleanLit(currentToken);
                match(IToken.Kind.BOOLEAN_LIT);
                return NodeCV;
            }
            else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        }


//    public void block1() throws PLPException {
//        IToken firstToken = currentToken;
//        List<ConstDec> blockConstVals = new ArrayList<ConstDec>();
//        match(IToken.Kind.KW_CONST);
//        IToken Id = null;
//        if (currentToken.getKind() == IToken.Kind.IDENT) {
//            Id = currentToken;
//            match(IToken.Kind.IDENT);
//        }
//        match(IToken.Kind.EQ);
//        Expression val = constVal();
//        ConstDec blk1 = new ConstDec(firstToken, Id, val);
//        blockConstVals.add(blk1);
//
//        while (currentToken.getKind() == IToken.Kind.COMMA) {
//            match(IToken.Kind.COMMA);
//            if (currentToken.getKind() == IToken.Kind.IDENT) {
//                Id = currentToken;
//                match(IToken.Kind.IDENT);
//            }
//            match(IToken.Kind.EQ);
//            val = constVal();
//            blk1 = new ConstDec(firstToken, Id, val);
//            blockConstVals.add(blk1);
//        }
//    }

//    public void block2() throws PLPException {
//        match(IToken.Kind.KW_VAR);
//        match(IToken.Kind.IDENT);
//        while (currentToken.getKind() == IToken.Kind.COMMA){
//            match(IToken.Kind.COMMA);
//            match(IToken.Kind.IDENT);
//        }
//        match(IToken.Kind.SEMI);
//    }

//    public void block3() throws PLPException{
//        match(IToken.Kind.KW_PROCEDURE);
//        match(IToken.Kind.IDENT);
//        match(IToken.Kind.SEMI);
//        block1();
//        block2();
//        block3();
//        match(IToken.Kind.SEMI);
//    }

    public Statement statement() throws PLPException {
        IToken firstToken = currentToken;
        if (currentToken.getKind() == IToken.Kind.IDENT) {
            IToken ident1 = currentToken;
            match(IToken.Kind.IDENT);
            match(IToken.Kind.ASSIGN);
            Expression ex1 = expr();
            Ident ident2 = new Ident(ident1);
            return new StatementAssign(firstToken, ident2, ex1);
        } else if (currentToken.getKind() == IToken.Kind.KW_CALL) {
            match(IToken.Kind.KW_CALL);
            if (currentToken.getKind() == IToken.Kind.IDENT) {
                Ident ident3 = new Ident(currentToken);
                match(IToken.Kind.IDENT);
                return new StatementCall(firstToken, ident3);
            } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        } else if (currentToken.getKind() == IToken.Kind.QUESTION) {
            match(IToken.Kind.QUESTION);
            if (currentToken.getKind() == IToken.Kind.IDENT){
                Ident ident4 = new Ident(currentToken);
                match(IToken.Kind.IDENT);
                return new StatementInput(firstToken, ident4);
            } else {
                throw new SyntaxException("expected logical and token in LogicalOrExpr");
            }
        } else if (currentToken.getKind() == IToken.Kind.BANG) {
            match(IToken.Kind.BANG);
            Expression ex2 = expr();
            return new StatementOutput(firstToken, ex2);
        } else if (currentToken.getKind() == IToken.Kind.KW_BEGIN) {
            match(IToken.Kind.KW_BEGIN);
            List<Statement> statementsLst = new ArrayList<Statement>();
            Statement st1 = statement();
            statementsLst.add(st1);
            while (currentToken.getKind() == IToken.Kind.SEMI) {
                match(IToken.Kind.SEMI);
                st1 = statement();
                statementsLst.add(st1);
            }
            match(IToken.Kind.KW_END);
            return new StatementBlock(firstToken ,statementsLst);
        } else if (currentToken.getKind() == IToken.Kind.KW_IF) {
            match(IToken.Kind.KW_IF);
            Expression ex3 = expr();
            match(IToken.Kind.KW_THEN);

            Statement st2 = statement();
            return new StatementIf(firstToken, ex3, st2);
        } else if (currentToken.getKind() == IToken.Kind.KW_WHILE) {

            match(IToken.Kind.KW_WHILE);

            Expression ex4 = expr();
            match(IToken.Kind.KW_DO);
            Statement s3 = statement();
            return new StatementWhile(firstToken, ex4, s3);
        } else {
            return new StatementEmpty(firstToken);
        }
    }

    public Block block() throws PLPException{
        IToken firstToken = currentToken;
        List<ConstDec> blockConstVals = new ArrayList<ConstDec>();
        List<VarDec> blockVar = new ArrayList<VarDec>();
        List<ProcDec> procVar = new ArrayList<ProcDec>();
        //block 1
            Object ConVal=null;
            while (currentToken.getKind() == IToken.Kind.KW_CONST){
                match(IToken.Kind.KW_CONST);
                IToken Id = null;
                if (currentToken.getKind() == IToken.Kind.IDENT) {
                    Id = currentToken;
                    match(IToken.Kind.IDENT);
                }
                match(IToken.Kind.EQ);
//                Expression val = constVal();
                    if(currentToken.getKind()== IToken.Kind.NUM_LIT){
                        ConVal = currentToken.getIntValue();
                        match(IToken.Kind.NUM_LIT);
                    }
                    else if(currentToken.getKind()== IToken.Kind.STRING_LIT){
                        ConVal = currentToken.getStringValue();
                        match(IToken.Kind.STRING_LIT);
                    }
                    else if(currentToken.getKind()== IToken.Kind.BOOLEAN_LIT){
                        ConVal = currentToken.getBooleanValue();
                        match(IToken.Kind.BOOLEAN_LIT);
                    }
                    else {
                        throw new SyntaxException("expected logical and token in LogicalOrExpr");
                    }

                ConstDec blk1 = new ConstDec(firstToken, Id, ConVal);
                blockConstVals.add(blk1);

                while (currentToken.getKind() == IToken.Kind.COMMA) {
                    match(IToken.Kind.COMMA);
                    if (currentToken.getKind() == IToken.Kind.IDENT) {
                        Id = currentToken;
                        match(IToken.Kind.IDENT);
                    }
                    match(IToken.Kind.EQ);
//                    ConVal = constVal();
                    if(currentToken.getKind()== IToken.Kind.NUM_LIT){
                        ConVal = currentToken.getIntValue();
                        match(IToken.Kind.NUM_LIT);
                    }
                    else if(currentToken.getKind()== IToken.Kind.STRING_LIT){
                        ConVal = currentToken.getStringValue();
                        match(IToken.Kind.STRING_LIT);
                    }
                    else if(currentToken.getKind()== IToken.Kind.BOOLEAN_LIT){
                        ConVal = currentToken.getBooleanValue();
                        match(IToken.Kind.BOOLEAN_LIT);
                    }
                    else {
                        throw new SyntaxException("expected logical and token in LogicalOrExpr");
                    }
                    blk1 = new ConstDec(firstToken, Id, ConVal);
                    blockConstVals.add(blk1);
                }
                match(IToken.Kind.SEMI);
            }
            //block 2

            while (currentToken.getKind() == IToken.Kind.KW_VAR){
                match(IToken.Kind.KW_VAR);
                VarDec blk2 = null;
                if(currentToken.getKind() == IToken.Kind.IDENT){
                    blk2 = new VarDec(firstToken, currentToken);
                    match(IToken.Kind.IDENT);
                    blockVar.add(blk2);
                }
                while (currentToken.getKind() == IToken.Kind.COMMA){
                    match(IToken.Kind.COMMA);
                    if(currentToken.getKind() == IToken.Kind.IDENT){
                        blk2 = new VarDec(firstToken, currentToken);
                        match(IToken.Kind.IDENT);
                        blockVar.add(blk2);
                    }
                }
                match(IToken.Kind.SEMI);
            }
            //block 3
            while (currentToken.getKind() == IToken.Kind.KW_PROCEDURE){
                ProcDec blk3 = null;
                IToken idName = null;
                match(IToken.Kind.KW_PROCEDURE);
                if(currentToken.getKind() == IToken.Kind.IDENT){
                    idName = currentToken;
                    match(IToken.Kind.IDENT);
                }
                match(IToken.Kind.SEMI);
                Block body = block();
                blk3 = new ProcDec(firstToken,idName,body);
                match(IToken.Kind.SEMI);
                procVar.add(blk3);
            }
            Statement stat = statement();

            return new Block(firstToken, blockConstVals, blockVar, procVar, stat);
    }

    ////////////////////////////////////////////////////////////////

//    abstract class Expression extends ASTNode {
//        public final IToken firstToken;
//        public Expression(IToken firstToken) {
//            super(firstToken);
//            this.firstToken = firstToken;
//        }
//
//        public abstract Object visit(ASTVisitor v, Object args);
//    }
//
//    class ExpressionNumLit extends Expression {
//        public  ExpressionNumLit(IToken firstToken){
//            super(firstToken);
//        }
//        public int getValue(){
//            return firstToken.getIntValue();
//        }
//
//        @Override
//        public Object visit(ASTVisitor v, Object arg) {
//            return v.visitExpressionNumLit(this, arg);
//        }
//    }
//
//    class ExpressionBinary extends Expression{
//        public final Expression left;
//        public final IToken op;
//        public final Expression right;
//
//        public ExpressionBinary(IToken firstToken, Expression left, IToken op, Expression right){
//            super(firstToken);
//            this.left=left;
//            this.op=op;
//            this.right=right;
//        }
//        @Override
//        public Object visit(ASTVisitor v, Object arg) {
//            return v.visitExpressionBinary(this, arg);
//        }
//
//    }

    ////////////////////////////////////////////////////////////////

//    abstract class ExpresionNode extends ASTNode {
//        public final IToken firstToken;
//
//        public ExpresionNode(IToken firstToken) {
//            super(firstToken);
//            this.firstToken = firstToken;
//        }
//    }
//
//    class additiveExp extends ASTNode {
//
//    }
//
//
//    abstract class primaryExpr extends ASTNode {
//        public final IToken firstToken;
//        public primaryExpr(IToken firstToken){
//            super(firstToken);
//            this.firstToken=firstToken;
//        }
//    }
//
//    class IdentPrimExpr extends  primaryExpr{
//        public  IdentPrimExpr(IToken firstToken){
//            super(firstToken);
//        }
//    }
//
//    class constValPrimExpr extends primaryExpr{
//        public final IToken numLitConst;
//        public final IToken strLitConst;
//        public final IToken boolLitConst;
//        public constValPrimExpr(IToken firstToken, IToken numLitConst, IToken strLitConst, IToken boolLitConst){
//            super(firstToken);
//            this.numLitConst=numLitConst;
//            this.strLitConst=strLitConst;
//            this.boolLitConst=boolLitConst;
//        }
//    }

    ASTNode ProgramFunc() throws PLPException {
        IToken firstToken = currentToken;
        Block blk = block();
        Program blockNode= new Program(firstToken, blk);
        match(IToken.Kind.DOT);
        if (currentToken.getKind() != IToken.Kind.EOF){
            throw new SyntaxException("invalid lines after program scope");
        }
        return blockNode;
    }

    public ASTNode parse() throws PLPException {
        while (true) {
            consume();
            return ProgramFunc();
        }
    }

}

