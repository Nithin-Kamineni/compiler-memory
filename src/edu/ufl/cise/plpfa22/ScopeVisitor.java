package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class ScopeVisitor implements ASTVisitor {

    symbolTable ST = new symbolTable();

    @Override
    public Object visitProgram(Program program, Object arg) throws PLPException {
        Block block = program.block;
        block.visit(this, arg);
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLPException {
        Block block1 = block;
        List<ConstDec> consIdents = block1.constDecs;
        for(int i=0;i<consIdents.size();i++){
            consIdents.get(i).visit(this, arg);
        }
        List<VarDec> varIdents = block1.varDecs;
        for(int i=0;i<varIdents.size();i++){
            varIdents.get(i).visit(this, arg);
        }
        List<ProcDec> procIdents = block1.procedureDecs;
        for(int i=0;i<procIdents.size();i++){
            procIdents.get(i).visit(this, arg);
        }
        block1.statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        String name = String.valueOf(constDec.ident.getText());constDec.setNest(ST.currentScope);  //setting nest level
        ST.addDec(name, constDec);
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        String name = String.valueOf(varDec.ident.getText());
        varDec.setNest(ST.currentScope);  //setting nest level
        ST.addDec(name, varDec);
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        String name = String.valueOf(procDec.ident.getText());
        ST.addDec(name, procDec);
        procDec.setNest(ST.currentScope);              //setting nest level
        ST.enterScope();
        procDec.block.visit(this, arg);
        ST.closeScope();
        return null;
    }

    @Override
    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        Ident varStat = statementAssign.ident;
        Expression expStat = statementAssign.expression;

        varStat.visit(this, arg);
        expStat.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        Ident callTempIdent = statementCall.ident;

        String varStatname = String.valueOf(statementCall.ident.getText());
        Declaration tempDec = ST.lookup(varStatname);
//        System.out.println("procedure varible:"+varStatname);
//        System.out.println("procedure declaration:"+tempDec);
//        check(tempDec!=null, statementCall, "Called procegure Not declared");
//        callTempIdent.setNest(ST.currentScope);
//        callTempIdent.setDec(tempDec);
        callTempIdent.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        String varStatname = String.valueOf(statementInput.ident.getText());
        Declaration tempDec = ST.lookup(varStatname);
        //type check
        statementInput.ident.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        statementOutput.expression.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
        List<Statement> tempStatBlock = statementBlock.statements;
        for (int i=0;i<tempStatBlock.size();i++){
            tempStatBlock.get(i).visit(this, arg);
        }
        return null;
    }

    @Override
    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
        statementIf.expression.visit(this,arg);
        statementIf.statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        statementWhile.expression.visit(this, arg);
        statementWhile.statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        expressionBinary.e0.visit(this, arg);
        expressionBinary.e1.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        String varStatname = String.valueOf(expressionIdent.firstToken.getText());

        Declaration tempDec = ST.lookup(varStatname);
        expressionIdent.setNest(ST.currentScope);
        expressionIdent.setDec(tempDec);
        return null;
    }

    @Override
    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
//        expressionNumLit.getFirstToken();
//        expressionNumLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
//        expressionStringLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
//        expressionBooleanLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        String varStatname = String.valueOf(ident.firstToken.getText());

        ident.setNest(ST.currentScope);
        System.out.println("ident:"+varStatname);
        Declaration tempDec = ST.lookup(varStatname);
        System.out.println("dec:"+tempDec);
        System.out.println("nest:"+ident.getNest());
//        System.out.println(varStatname);
        if(tempDec==null){
            throw new ScopeException("ID is not declared in this scope or previous nested scopes");
        }
        ident.setDec(tempDec);
//        ident.visit(this, arg);
        return null;
    }
}
