package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class ScopeVisitor implements ASTVisitor {

    int nestingLevel=0; //

    String scopeID; //

    Stack<String> ScopeStack = new Stack<String>(); //

    HashMap<String, LinkedList> SymbolTab = new HashMap(); //

    List<Object> TempSymbolValue = new ArrayList<Object> (); //

    LinkedList<List> symbolValue = new LinkedList<List>(); //

    Object lookUpAtribute;



    public void enterScope(){
        scopeID = UUID.randomUUID().toString();
        ScopeStack.add(scopeID);
        nestingLevel++;
    }

    public void closeScope(){
        ScopeStack.pop();
        nestingLevel--;
    }

    public void insert(String ID, Object attributes) throws PLPException{
//        List<Object> symbolValue = new ArrayList<Object> ();
        TempSymbolValue.clear();
        TempSymbolValue.add(scopeID);
        TempSymbolValue.add(attributes);
        if(SymbolTab.containsKey(ID)){
            // retrive the linked list of symbolTab key to link new symbolValue
            symbolValue = SymbolTab.get(ID);
            //searching if the element is declared twice in the same scope
            TempSymbolValue = symbolValue.getFirst();
            if(TempSymbolValue.get(0)==scopeID){
                throw new ScopeException("Declaring varible second time in the same scope");
            } else {
                //adding value of tempLst to retrived LinkedList
                symbolValue.add(TempSymbolValue);
                //replace the linkedList of s
                SymbolTab.put(ID,symbolValue);
            }
        } else{
            //clearing linked list
            symbolValue.clear();
            //adding value of tempLst to LinkedList
            symbolValue.add(TempSymbolValue);
            //adding linked list to value of Symbol table Key
            SymbolTab.put(ID,symbolValue);
        }
    }

    public void lookUp(String ID) throws PLPException{
        if(SymbolTab.containsKey(ID)){
            symbolValue = SymbolTab.get(ID);
            //traversing through linked list to find each scope value
            for(int i=0;i<symbolValue.size();i++){
                TempSymbolValue = symbolValue.get(i);
                scopeID = (String) TempSymbolValue.get(0);
                for (int j=0;j<ScopeStack.size();j++){
                    if(ScopeStack.get(j)==scopeID){
                        lookUpAtribute=symbolValue.get(i).get(1);
                    }
                }
            }

        } else {
            throw new ScopeException("ID is not declared before used");
        }
    }

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
//        String name = new String();
        scopeID = UUID.randomUUID().toString();
        List<ConstDec> consIdents = block1.constDecs;
        for(int i=0;i<consIdents.size();i++){
            TempIdent = consIdents.get(i).ident;

            String name = "";
            char[] tempname = TempIdent.getText();
            for(char c:tempname){
                name= name+ c;
            }
//            System.out.println(name);

            insert(name, arg);
        }
        List<VarDec> varIdents = block1.varDecs;
        for(int i=0;i<varIdents.size();i++){
            TempIdent = varIdents.get(i).ident;

            String name = "";
            char[] tempname = TempIdent.getText();
            for(char c:tempname){
                name= name + c;
            }
//            System.out.println(name);
            insert(name, arg);
        }
        List<ProcDec> procIdents = block1.procedureDecs;
        for(int i=0;i<procIdents.size();i++){
//            Object tempProc =
            procIdents.get(i).visit(this, arg);
            closeScope();
//            return tempProc;
        }
        Statement statement = block1.statement;

        statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        procDec.setNest(nestingLevel);
        enterScope();
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
//        System.out.println(varStatname);
        lookUp(varStatname);
        Object lookUpAtribute = this.lookUpAtribute;
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
        return expressionNumLit.toString();
    }

    @Override
    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        return expressionStringLit.toString();
    }

    @Override
    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        return expressionBooleanLit.firstToken;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLPException {

        return null;
    }
}
