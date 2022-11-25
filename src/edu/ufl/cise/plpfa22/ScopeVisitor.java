package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import java.util.List;
import java.util.UUID;

public class ScopeVisitor implements ASTVisitor {

    symbolTable ST = new symbolTable();
    int PassNumber =0;
    String scopeId = UUID.randomUUID().toString();
    String prevpath = "";

    @Override
    public Object visitProgram(Program program, Object arg) throws PLPException {
        Block block = program.block;
        block.visit(this, arg);
        PassNumber++;
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
        if(PassNumber==1) {
            String name = String.valueOf(constDec.ident.getText());
            constDec.setNest(ST.currentScope);  //setting nest level
            ST.addDec(name, constDec);
        }
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        if(PassNumber==1) {
            String name = String.valueOf(varDec.ident.getText());

            varDec.setNest(ST.currentScope);  //setting nest level
            ST.addDec(name, varDec);
        }
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        if(PassNumber==0) {
            String name = String.valueOf(procDec.ident.getText());

            procDec.setProcpath(prevpath+"$"+name);

            prevpath = procDec.getProcpath();
            ST.addDec(name, procDec);
         // 5, 6, param 16
            procDec.setNest(ST.currentScope);              //setting nest level
            ST.enterScopeFirst();
            procDec.block.visit(this, arg);
            ST.closeScope();
        }
        else if(PassNumber==1){
            procDec.setNest(ST.currentScope);              //setting nest level
            ST.enterScope();
            procDec.block.visit(this, arg);
            ST.closeScope();
        }
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
        if(PassNumber==1){
            Ident callTempIdent = statementCall.ident;
            callTempIdent.visit(this, arg);
            String varStatname = String.valueOf(statementCall.ident.getText());

            Declaration tempDec = ST.lookup(varStatname);

            if (tempDec == null) {
                throw new ScopeException("ID is not declared in this scope or previous nested scopes");
            }
        }

        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        if(PassNumber==1) {
            String varStatname = String.valueOf(statementInput.ident.getText());
            Declaration tempDec = ST.lookup(varStatname);

            //type check
            if (tempDec == null) {
                throw new ScopeException("ID is not declared in this scope or previous nested scopes");
            }
            statementInput.ident.visit(this, arg);
        }
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
        if(PassNumber==1) {
            String varStatname = String.valueOf(expressionIdent.firstToken.getText());

            Declaration tempDec = ST.lookup(varStatname);
            expressionIdent.setNest(ST.currentScope);
//        System.out.println(varStatname+":here:"+tempDec);
            if (tempDec == null) {
                throw new ScopeException("ID is not declared in this scope or previous nested scopes");
            }
            expressionIdent.setDec(tempDec);
        }
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
        if(PassNumber==1) {
            String varStatname = String.valueOf(ident.firstToken.getText());

            ident.setNest(ST.currentScope);
//        System.out.println("ident:"+varStatname);
            Declaration tempDec = ST.lookup(varStatname);
//        System.out.println("dec:"+tempDec);
//        System.out.println("nest:"+ident.getNest());
//        System.out.println(varStatname);
            if (tempDec == null) {
                throw new ScopeException("ID is not declared in this scope or previous nested scopes");
            }
            ident.setDec(tempDec);
        }
        return null;
    }
}
