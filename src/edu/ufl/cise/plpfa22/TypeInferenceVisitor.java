package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;

import java.util.*;


//check if call procedure is checking if the varble is valid procedure used in right nest level
public class TypeInferenceVisitor implements ASTVisitor {


    Boolean typeFail = true;
    Boolean notCompleteType = true;
    int nestLvl;
    Hashtable<String, HashSet> varNest = new Hashtable<>();
    HashSet<String> nestSet = new HashSet<>();
    int tempnest=0;
    int numChanges;
    boolean finalPass=false;

    @Override
    public Object visitProgram(Program program, Object arg) throws PLPException {
        nestSet.clear();
//        System.out.println("========================");
        varNest.put("0", nestSet);
        while(typeFail && notCompleteType){
            numChanges=0;
            program.block.visit(this, arg);
//            System.out.println("numChanges: "+numChanges);
            if(numChanges==0){
                notCompleteType=false;
                break;
            }
        }
//        System.out.println("$$$$$$$$$final pass started$$$$$$$$$");
        finalPass=true;
        program.block.visit(this, arg);

        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLPException {
        Block block1 = block;
        varNest.put(Integer.toString(tempnest), nestSet);
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
            tempnest+=1;
            procIdents.get(i).visit(this, arg);
        }
        block1.statement.visit(this, arg);

        //this is the error
        nestSet = varNest.get(Integer.toString(tempnest));
//        System.out.println(nestSet);
        return null;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
//        nestLvl = constDec.getNest();
//        nestSet = varNest.get(Integer.toString(nestLvl));
//        nestSet.add(String.valueOf(constDec.ident.getText()));
//        varNest.put(Integer.toString(nestLvl),nestSet);

        String typeName = constDec.val.getClass().getName();
//        System.out.println(typeName);
        if(typeName=="java.lang.Integer"){
//            varNest.get(nestLvl);
//            nestSet.remove(String.valueOf(constDec.ident.getText()));
//            varNest.put(Integer.toString(nestLvl),nestSet);
            constDec.setType(Types.Type.NUMBER);

        }
        else if(typeName=="java.lang.String"){
//            varNest.get(nestLvl);
//            nestSet.remove(String.valueOf(constDec.ident.getText()));
//            varNest.put(Integer.toString(nestLvl),nestSet);
            constDec.setType(Types.Type.STRING);
        }
        else if(typeName=="java.lang.Boolean"){
//            varNest.get(nestLvl);
//            nestSet.remove(String.valueOf(constDec.ident.getText()));
//            varNest.put(Integer.toString(nestLvl),nestSet);
            constDec.setType(Types.Type.BOOLEAN);
        }
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
//        nestLvl = varDec.getNest();
//        nestSet = varNest.get(Integer.toString(nestLvl));
//        nestSet.add(String.valueOf(varDec.ident.getText()));
//        varNest.put(Integer.toString(nestLvl),nestSet);
        return null;
    }

    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        if(procDec.getType()!=Types.Type.PROCEDURE){
//            System.out.println("0000000000000000000000");
            numChanges++;
            procDec.setType(Types.Type.PROCEDURE);
        }
//        nestLvl=procDec.getNest();
//        System.out.println("test:"+nestLvl);
//        nestSet.clear();
//        varNest.put(Integer.toString(nestLvl+1), nestSet);
        procDec.block.visit(this, arg);
        return null;
    }

    @Override
    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
//        System.out.println(statementAssign.ident.getText());
//        nestLvl = statementAssign.ident.getNest();
        statementAssign.ident.visit(this, arg);
        statementAssign.expression.visit(this, arg);
        Declaration tempDec = statementAssign.ident.getDec();
        Expression tempTyp = statementAssign.expression;

//        System.out.println("abc:"+tempDec.getClass().getName());
        if(tempDec.getClass().getName()=="edu.ufl.cise.plpfa22.ast.ConstDec"){
//            System.out.println(1);
            throw new TypeCheckException("Assigning s type to the constant variable");
        }
        else if(tempDec.getClass().getName()=="edu.ufl.cise.plpfa22.ast.ProcDec"){
//            System.out.println(1);
            throw new TypeCheckException("Assigning wrong type to the procedure variable");
        }
        else if(tempDec.getType()==null){
            if(tempTyp.getType()!=null){
//                System.out.println("#################################");
                numChanges++;
            }
            tempDec.setType(tempTyp.getType());

//            String identName = String.valueOf(statementAssign.ident.getText());
//            nestLvl = statementAssign.ident.getNest();
//            nestSet = varNest.get(Integer.toString(nestLvl));
//            nestSet.remove(identName);
//            varNest.put(Integer.toString(nestLvl),nestSet);
        }
        //below else if could be a wrong statment aaaa
        else if(tempDec.getType()!=null && tempTyp.getType()==null){
//            System.out.println("1111111111111111111111111");
            numChanges++;
            tempTyp.setType(tempDec.getType());

//            String identName = String.valueOf(statementAssign.expression.firstToken.getText());
//            statementAssign.expression.visit(this, arg);
//            nestLvl = (int) statementAssign.expression.visit(this, arg);
//            nestSet = varNest.get(Integer.toString(nestLvl));
//            nestSet.remove(identName);
//            varNest.put(Integer.toString(nestLvl),nestSet);
        } //incomplete
        else if(tempDec.getType()==tempTyp.getType()){
            tempDec.setType(tempTyp.getType());
        }
        else{
            throw new TypeCheckException("Assigning wrong type to the variable");
        }
//        System.out.println("result id:"+statementAssign.ident.getDec().getType());
//        System.out.println("result exp:"+statementAssign.expression.getType());
//        System.out.println("----------------");
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        String typeName = statementCall.ident.getDec().getClass().getName();
//        System.out.println(typeName);
//        System.out.println(typeName.getClass().getName());
        if(typeName!="edu.ufl.cise.plpfa22.ast.ProcDec"){
            throw new TypeCheckException("Call ident is not a procedure");
        }
            return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        if (statementInput.ident.getDec().getType()==null && finalPass) {
            throw new TypeCheckException("ID not declared");
        }
        else if(statementInput.ident.getDec().getType()== Types.Type.PROCEDURE || statementInput.ident.getDec().getClass().getName()=="edu.ufl.cise.plpfa22.ast.ConstDec"){
            throw new TypeCheckException("ID input not legal, type is a procedure or const");
        }
        return null;
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        statementOutput.expression.visit(this, arg);
        if(statementOutput.expression.getType()==null && finalPass){
            throw new TypeCheckException("Don't have a type before outputting");
        }
        else if(statementOutput.expression.getType()==Types.Type.PROCEDURE){
            throw new TypeCheckException("ID declared is a procedure");
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
        if(statementIf.expression.getType()!=Types.Type.BOOLEAN && finalPass){
//            System.out.println(statementIf.expression.getType());
            throw new TypeCheckException("expression of IF has no bool type");
        }
        statementIf.statement.visit(this,arg);
        return null;
    }

    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        statementWhile.expression.visit(this, arg);
        if(statementWhile.expression.getType()!=Types.Type.BOOLEAN && finalPass){
//            System.out.println(statementWhile.expression.getType());
            throw new TypeCheckException("expression of WHILE has no bool type");
        }
        statementWhile.statement.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        expressionBinary.e0.visit(this, arg);
        expressionBinary.e1.visit(this, arg);

        String opStr = String.valueOf(expressionBinary.op.getText());
//        int LnestLvl = (int) expressionBinary.e0.visit(this, arg);
//        int RnestLvl;
//        if(opStr.equals("")){
//            System.out.println("888888888888888");
//            RnestLvl = (int) expressionBinary.e1.visit(this, arg);
//        }
//        System.out.println("______________");
        Expression left = expressionBinary.e0;
        Expression right = expressionBinary.e1;


        Types.Type tempType = null;
        String tempId;
//        System.out.println(left.getClass());
        //In AST tree for expressions find the nodes with the deepest traversal and process it
        switch (opStr) {
            case "*" -> {
//                check(lType == Type.BOOLEAN && rType == Type.BOOLEAN, binaryExpr, "Booleans required");
                if (((left.getType() == null && right.getType() == null) || (left.getType()!=right.getType())) && finalPass) {
                    throw new TypeCheckException("both types are null to: " + opStr);
                } else if(left.getType() == null && right.getType() == null && expressionBinary.getType()!=null){
                    numChanges++; // ! (a+b)=0 exp.type = number
                    left.setType(expressionBinary.getType());
                    right.setType(expressionBinary.getType());
                } else if ((left.getType() != null && right.getType() == null && left.getType() != Types.Type.PROCEDURE && right.getType() != Types.Type.STRING) || (left.getType() == null && right.getType() != null && right.getType() != Types.Type.PROCEDURE && right.getType() != Types.Type.STRING)) {
                    if (left.getType() != null && right.getType() == null) {
//                        System.out.println("22222222222222222222222222");
                        numChanges++;
                        right.setType(left.getType());
                        tempType = left.getType();
                    } else if (left.getType() == null && right.getType() != null) {
//                        System.out.println("333333333333333333333333333");
                        numChanges++;
                        left.setType(right.getType());
                        tempType = right.getType();
                    }
                } else if (left.getType() == right.getType() && left.getType() != Types.Type.PROCEDURE && left.getType() != Types.Type.STRING) {
                    tempType = left.getType();
                } else {
                    throw new TypeCheckException("Types are not compatible1");
                }
            }
            case "+" -> {
                if (((left.getType() == null && right.getType() == null) || (left.getType()!=right.getType())) && finalPass) {
//                    System.out.println(expressionBinary.getType());
                    //4444 infer the types of idents based on the type of the expression
                    throw new TypeCheckException("both types are null to: " + opStr);
                } else if(left.getType() == null && right.getType() == null && expressionBinary.getType()!=null){
                    numChanges++;
                    left.setType(expressionBinary.getType());
                    right.setType(expressionBinary.getType());
                } else if ((left.getType() != null && right.getType() == null && left.getType() != Types.Type.PROCEDURE) || (left.getType() == null && right.getType() != null && right.getType() != Types.Type.PROCEDURE)) {
                    if (left.getType() != null && right.getType() == null) {
//                        System.out.println("444444444444444444444444");
                        numChanges++;
                        right.setType(left.getType());
                        tempType = left.getType();

                        // remo String.valueOf(right.firstToken.getText());
//                        RnestLvl = (int) expressionBinary.e1.visit(this, arg);
//                        System.out.println("nest" + RnestLvl);
//                        nestSet = varNest.get(Integer.toString(RnestLvl));
//                        nestSet.remove(String.valueOf(right.firstToken.getText()));
//                        varNest.put(Integer.toString(RnestLvl), nestSet);
                    } else if (left.getType() == null && right.getType() != null) {
//                        System.out.println("5555555555555555555555");
                        numChanges++;
                        left.setType(right.getType());
                        tempType = right.getType();

//                        LnestLvl = (int) expressionBinary.e0.visit(this, arg);
//                        System.out.println("nest" + LnestLvl);
//                        nestSet = varNest.get(Integer.toString(LnestLvl));
//                        nestSet.remove(String.valueOf(left.firstToken.getText()));
//                        varNest.put(Integer.toString(LnestLvl), nestSet);
                    }
                } else if (left.getType() == right.getType() && left.getType() != Types.Type.PROCEDURE) {
                    tempType = left.getType();
                } else {
                    throw new TypeCheckException("Types are not compatible2");
                }
            }
            case "-", "/", "%" -> {
                if (((left.getType() == null && right.getType() == null) || (left.getType()!=right.getType())) && finalPass) {
                    throw new TypeCheckException("both types are null to: " + opStr);
                } else if(left.getType() == null && right.getType() == null && expressionBinary.getType()!=null){
                    numChanges++;
                    left.setType(expressionBinary.getType());
                    right.setType(expressionBinary.getType());
                } else if ((left.getType() != null && right.getType() == null && left.getType() != Types.Type.PROCEDURE && left.getType() != Types.Type.BOOLEAN && left.getType() != Types.Type.STRING) || (left.getType() == null && right.getType() != null && right.getType() != Types.Type.PROCEDURE && right.getType() != Types.Type.BOOLEAN && right.getType() != Types.Type.STRING)) {
                    if (left.getType() != null && right.getType() == null) {
//                        System.out.println("66666666666666666");
                        numChanges++;
                        right.setType(left.getType());
                        tempType = left.getType();

//                        RnestLvl = (int) expressionBinary.e1.visit(this, arg);
//                        System.out.println("nest" + RnestLvl);
//                        nestSet = varNest.get(Integer.toString(RnestLvl));
//                        nestSet.remove(String.valueOf(right.firstToken.getText()));
//                        varNest.put(Integer.toString(RnestLvl), nestSet);
                    } else if (left.getType() == null && right.getType() != null) {
//                        System.out.println("77777777777777777777777");
                        numChanges++;
                        left.setType(right.getType());
                        tempType = right.getType();

//                        LnestLvl = (int) expressionBinary.e0.visit(this, arg);
//                        System.out.println("nest" + LnestLvl);
//                        nestSet = varNest.get(Integer.toString(LnestLvl));
//                        nestSet.remove(String.valueOf(left.firstToken.getText()));
//                        varNest.put(Integer.toString(LnestLvl), nestSet);
                    }

                } else if (left.getType() == right.getType() && left.getType() != Types.Type.BOOLEAN && left.getType() != Types.Type.STRING && left.getType() != Types.Type.PROCEDURE) {
                    tempType = left.getType();
                } else {
                    throw new TypeCheckException("Types are not compatible3");
                }
            }
            case "=","#",">",">=","<","<=" -> {
//                System.out.println("++++++++++++++++++++++++++++++++++++");
//                System.out.println(left.getType());
//                System.out.println(right.getType());
//                System.out.println("++++++++++++++++++++++++++++++++++++");
                if (((left.getType() == null && right.getType() == null) || (left.getType()!=right.getType())) && finalPass) {
                    throw new TypeCheckException("both types are null to compare");
                } else if ((left.getType() != null && right.getType() == null && left.getType() != Types.Type.PROCEDURE) || (left.getType() == null && right.getType() != null && right.getType() != Types.Type.PROCEDURE)) {
                    if (left.getType() != null && right.getType() == null) {
//                        System.out.println("8888888888888888888888888888");
                        numChanges++;
                        right.setType(left.getType());
                        tempType = Types.Type.BOOLEAN;

                        // remo String.valueOf(right.firstToken.getText());
//                        RnestLvl = (int) expressionBinary.e1.visit(this, arg);
//                        System.out.println("nest" + RnestLvl);
//                        nestSet = varNest.get(Integer.toString(RnestLvl));
//                        nestSet.remove(String.valueOf(right.firstToken.getText()));
//                        varNest.put(Integer.toString(RnestLvl), nestSet);
                    } else if (left.getType() == null && right.getType() != null) {
//                        System.out.println("999999999999999999999999");
                        numChanges++;
                        left.setType(right.getType());
                        tempType = Types.Type.BOOLEAN;

//                        LnestLvl = (int) expressionBinary.e0.visit(this, arg);
//                        System.out.println("nest" + LnestLvl);
//                        nestSet = varNest.get(Integer.toString(LnestLvl));
//                        nestSet.remove(String.valueOf(left.firstToken.getText()));
//                        varNest.put(Integer.toString(LnestLvl), nestSet);
                    }
                } else if (left.getType() == right.getType() && left.getType() != null && left.getType() != Types.Type.PROCEDURE) {
                        tempType = Types.Type.BOOLEAN;
                } else if(left.getType()==right.getType() && left.getType()==null){
                    tempType=null;
                } else {
                        throw new TypeCheckException("Types are not compatible4");
                    }

            }
        }


        if(expressionBinary.getType()==null && tempType==null){
            expressionBinary.setType(tempType);
        }
        else if(expressionBinary.getType()==null && tempType!=null){
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            numChanges++;
            expressionBinary.setType(tempType);
        }
        else if(expressionBinary.getType()==tempType){
            expressionBinary.setType(tempType);
        }
        else if(expressionBinary.getType()!=null && tempType==null){
            tempType=expressionBinary.getType();
        }
        else if(expressionBinary.getType()!=tempType){
//            System.out.println(expressionBinary.getType());
//            System.out.println(tempType);
            throw new TypeCheckException("Type of sub expression cannot be compatible to inferred type of expression");
        }
//        System.out.println("Binary:"+String.valueOf(left.firstToken.getText())+":"+left.getType());
//        System.out.println("op:"+opStr);
//        System.out.println("Binary:"+String.valueOf(right.firstToken.getText())+":"+right.getType());
//        return LnestLvl;
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        //use get declaration and infer values from too
//        if(expressionIdent.getNest()<nestLvl){
//            System.out.println("Not Same nest level");
//        }


        Declaration dec = expressionIdent.getDec();
        if(expressionIdent.getType()==null){
            expressionIdent.setType(dec.getType());
        }
        else if(expressionIdent.getType()!=null && dec.getType()==null){
            numChanges++;
            dec.setType(expressionIdent.getType());
        }
        else if (expressionIdent.getType()==dec.getType()){
            expressionIdent.setType(dec.getType());
        }
        else{
//            System.out.println(expressionIdent.getType());
//            System.out.println(dec.getType());
            throw new TypeCheckException("Type of Ident cannot be compatible to inferred type of Ident");
        }

//        System.out.println("only for final: "+String.valueOf(expressionIdent.firstToken.getText())+":"+expressionIdent.getType());


        return expressionIdent.getNest();
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
