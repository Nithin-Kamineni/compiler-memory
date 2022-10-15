package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class ScopeVisitor implements ASTVisitor {

//    int nestingLevel=0; //

//    String scopeID; //

//    Stack<String> ScopeStack = new Stack<String>(); //

//    HashMap<String, LinkedList> SymbolTab = new HashMap(); //

//    List<Object> TempSymbolValue = new ArrayList<Object> (); //

//    LinkedList<List> symbolValue = new LinkedList<List>(); //

//    Object lookUpAtribute;



//    public void enterScope(){
//        scopeID = UUID.randomUUID().toString();
//        ScopeStack.add(scopeID);
//        nestingLevel++;
//    }
//
//    public void closeScope(){
//        ScopeStack.pop();
//        nestingLevel--;
//    }
//
//    public void insert(String ID, Object attributes) throws PLPException{
////        List<Object> symbolValue = new ArrayList<Object> ();
//        TempSymbolValue.clear();
//        TempSymbolValue.add(scopeID);
//        TempSymbolValue.add(attributes);
//        if(SymbolTab.containsKey(ID)){
//            // retrive the linked list of symbolTab key to link new symbolValue
//            symbolValue = SymbolTab.get(ID);
//            //searching if the element is declared twice in the same scope
//            TempSymbolValue = symbolValue.getFirst();
//            if(TempSymbolValue.get(0)==scopeID){
//                throw new ScopeException("Declaring varible second time in the same scope");
//            } else {
//                //adding value of tempLst to retrived LinkedList
//                symbolValue.add(TempSymbolValue);
//                //replace the linkedList of s
//                SymbolTab.put(ID,symbolValue);
//            }
//        } else{
//            //clearing linked list
//            symbolValue.clear();
//            //adding value of tempLst to LinkedList
//            symbolValue.add(TempSymbolValue);
//            //adding linked list to value of Symbol table Key
//            SymbolTab.put(ID,symbolValue);
//        }
//    }
//
//    public void lookUp(String ID) throws PLPException{
//        if(SymbolTab.containsKey(ID)){
//            symbolValue = SymbolTab.get(ID);
//            //traversing through linked list to find each scope value
//            for(int i=0;i<symbolValue.size();i++){
//                TempSymbolValue = symbolValue.get(i);
//                scopeID = (String) TempSymbolValue.get(0);
//                for (int j=0;j<ScopeStack.size();j++){
//                    if(ScopeStack.get(j)==scopeID){
//                        lookUpAtribute=symbolValue.get(i).get(1);
//                    }
//                }
//            }
//
//        } else {
//            throw new ScopeException("ID is not declared before used");
//        }
//    }

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
        IToken TempIdent;
        List<ConstDec> consIdents = block1.constDecs;  //setting nest level
        for(int i=0;i<consIdents.size();i++){
            consIdents.get(i).setNest(ST.currentScope);
            TempIdent = consIdents.get(i).ident;

            String name = "";
            char[] tempname = TempIdent.getText();
            for(char c:tempname){
                name= name+ c;
            }
//            System.out.println(name);
            Declaration value = consIdents.get(i);
            ST.addDec(name, consIdents.get(i));
        }
        List<VarDec> varIdents = block1.varDecs;
        for(int i=0;i<varIdents.size();i++){
            varIdents.get(i).setNest(ST.currentScope);  //setting nest level
            TempIdent = varIdents.get(i).ident;

            String name = "";
            char[] tempname = TempIdent.getText();
            for(char c:tempname){
                name= name + c;
            }
//            System.out.println(name);
            ST.addDec(name, null);
        }
        List<ProcDec> procIdents = block1.procedureDecs;
        for(int i=0;i<procIdents.size();i++){
//            Object tempProc =
            procIdents.get(i).setNest(ST.currentScope);              //setting nest level
            procIdents.get(i).visit(this, arg);
            ST.closeScope();
//            return tempProc;
        }
        Statement statement = block1.statement;

        statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        char[] tempname = procDec.ident.getText();
        String name = "";
        for(char c:tempname){
            name= name+ c;
        }
        ST.addDec(name, procDec);
        ST.enterScope();
        procDec.block.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        Ident varStat = statementAssign.ident;
        Expression expStat = statementAssign.expression;
        String varStatname = "";
        char[] tempname = varStat.getText();
        for(char c:tempname){
            varStatname= varStatname+ c;
        }

        varStat.setNest(ST.currentScope);
        ST.addDec(varStatname, varStat.getDec());
        expStat.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        Ident callTempIdent = statementCall.ident;
        callTempIdent.setNest(ST.currentScope);

        String varStatname = "";
        char[] tempname = callTempIdent.getText();
        for(char c:tempname){
            varStatname= varStatname+ c;
        }
        Declaration tempDec = ST.lookup(varStatname);
        callTempIdent.setDec(tempDec);
        callTempIdent.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        Ident tempStatInputIdent = statementInput.ident;

        String varStatname = "";
        char[] tempname = tempStatInputIdent.getText();
        for(char c:tempname){
            varStatname= varStatname+ c;
        }

        tempStatInputIdent.setNest(ST.currentScope);
        Declaration tempDec = ST.lookup(varStatname);
        tempStatInputIdent.setDec(tempDec);
        tempStatInputIdent.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        Expression tempExp = statementOutput.expression;
        tempExp.visit(this, arg);
        System.out.println(tempExp);
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
        Expression tempStatIfExp = statementIf.expression;
        Statement tempStatmentIf = statementIf.statement;
        tempStatIfExp.visit(this, arg);
        tempStatmentIf.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        Expression tempStatWhileExp = statementWhile.expression;
        Statement tempStatmentWhile = statementWhile.statement;
        tempStatWhileExp.visit(this, arg);
        tempStatmentWhile.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        char[] tempExpIdent = expressionIdent.firstToken.getText();
        String varStatname = "";
        for(char c:tempExpIdent){
            varStatname = varStatname+ c;
        }
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
        expressionNumLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        expressionStringLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        expressionBooleanLit.visit(this, arg);
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        ident.setNest(ST.currentScope);

        char[] tempIdentName = ident.firstToken.getText();
        String varStatname = "";
        for(char c:tempIdentName){
            varStatname= varStatname+ c;
        }

        Declaration tempDec = ST.lookup(varStatname);
        ident.setDec(tempDec);
        ident.visit(this, arg);
        return null;
    }
}
