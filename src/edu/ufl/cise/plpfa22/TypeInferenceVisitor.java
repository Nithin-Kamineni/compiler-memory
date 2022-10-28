package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;

import java.util.List;

public class TypeInferenceVisitor implements ASTVisitor {


    Boolean typeFail = true;
    Boolean notCompleteType = true;
    String tempType;
    int nestLvl;

    @Override
    public Object visitProgram(Program program, Object arg) throws PLPException {
        while(typeFail && notCompleteType){
            program.block.visit(this, arg);
            notCompleteType=false;
        }
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
        block1.statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
//        System.out.println("==============");
        String typeName = constDec.val.getClass().getName();
//        System.out.println(typeName);
        if(typeName=="java.lang.Integer"){
            constDec.setType(Types.Type.NUMBER);
        }
        else if(typeName=="java.lang.String"){
            constDec.setType(Types.Type.STRING);
        }
        else if(typeName=="java.lang.Boolean"){
            constDec.setType(Types.Type.BOOLEAN);
        }
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        System.out.println(statementAssign.ident.getText());
//        nestLvl = statementAssign.ident.getNest();
        statementAssign.ident.visit(this, arg);
        statementAssign.expression.visit(this, arg);

        Declaration tempDec = statementAssign.ident.getDec();
        Expression tempTyp = statementAssign.expression;

//        System.out.println("abc:"+tempDec.getClass().getName());
        if(tempDec.getClass().getName()=="edu.ufl.cise.plpfa22.ast.ConstDec"){
//            System.out.println(1);
            throw new TypeCheckException("Assigning wrong type to the variable");
        }
        else if(tempDec.getType()==null){
            tempDec.setType(tempTyp.getType());
        }
        else if(tempDec.getType()!=null && tempTyp.getType()==null){
            tempTyp.setType(tempDec.getType());
        } //incomplete
        else if(tempDec.getType()==tempTyp.getType()){
            tempDec.setType(tempTyp.getType());
        }
        else{
            throw new TypeCheckException("Assigning wrong type to the variable");
        }
        System.out.println("result id:"+statementAssign.ident.getDec().getType());
        System.out.println("result exp:"+statementAssign.expression.getType());
        System.out.println("----------------");
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        System.out.println("ttttttttttttt");
        System.out.println(statementInput.ident.getDec().getType());
        if (statementInput.ident.getDec().getType()==null) {
            throw new TypeCheckException("ID not declared");
        }
        return null;
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        statementOutput.expression.visit(this, arg);
        if(statementOutput.expression.getType()==null){
            throw new TypeCheckException("Don't have a type before outputting");
        }
        //System.out.println(statementOutput.expression.getType());
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
        statementIf.statement.visit(this,arg);
        return null;
    }

    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        expressionBinary.e0.visit(this, arg);
        expressionBinary.e1.visit(this, arg);

        Expression left = expressionBinary.e0;
        Expression right = expressionBinary.e1;
        String opStr = String.valueOf(expressionBinary.op.getText());

        System.out.println("Binary:"+String.valueOf(left.firstToken.getText())+":"+left.getType());
        System.out.println("op:"+opStr);
        System.out.println("Binary:"+String.valueOf(right.firstToken.getText())+":"+right.getType());

        Types.Type tempType = null;
        switch (opStr) {
            case "*", "/" -> {
//                check(lType == Type.BOOLEAN && rType == Type.BOOLEAN, binaryExpr, "Booleans required");
                if(left.getType()==right.getType() && left.getType()!=Types.Type.PROCEDURE){
                    tempType = left.getType();
                }
                else {
                    throw new TypeCheckException("Types are not compatible1");
                }
            }
            case "+" -> {
                if(left.getType()==right.getType() && left.getType()!= Types.Type.BOOLEAN && left.getType()!=Types.Type.PROCEDURE){
                    tempType = left.getType();
                }
                else {
                    throw new TypeCheckException("Types are not compatible2");
                }
            }
            case "-" -> {
                if(left.getType()==right.getType() && left.getType()!= Types.Type.BOOLEAN && left.getType()!= Types.Type.STRING && left.getType()!=Types.Type.PROCEDURE){
                    tempType = left.getType();
                }
                else {
                    throw new TypeCheckException("Types are not compatible3");
                }
            }
            case "=" -> {
                if(left.getType()==right.getType() && left.getType()!=Types.Type.PROCEDURE){
                    tempType = Types.Type.BOOLEAN;
                }
                else {
                    throw new TypeCheckException("Types are not compatible4");
                }
            }

        }


        expressionBinary.setType(tempType);
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        //use get declaration and infer values from too
        if(expressionIdent.getNest()<nestLvl){
            System.out.println("Not Same nest level");
        }
        Declaration dec = expressionIdent.getDec();
        expressionIdent.setType(dec.getType());
        return null;
    }

    @Override
    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
        expressionNumLit.setType(Types.Type.NUMBER);
        return null;
    }

    @Override
    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        expressionStringLit.setType(Types.Type.STRING);
        return null;
    }

    @Override
    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        expressionBooleanLit.setType(Types.Type.BOOLEAN);
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLPException {
//        System.out.println("iiiiiiiiiiii");
//        System.out.println(ident.getDec().getClass());
        return null;
    }
}
