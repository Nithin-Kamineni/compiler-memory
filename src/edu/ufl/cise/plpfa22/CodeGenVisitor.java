package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.IToken.Kind;
import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.ast.Types.Type;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

public class CodeGenVisitor implements ASTVisitor, Opcodes {
    final String packageName;
    final String className;
    final String sourceFileName;
    final String fullyQualifiedClassName;
    final String classDesc;
    Boolean procVisit;
    ClassWriter classWriter;

    //creating the list to return bytecode list
    List<CodeGenUtils.GenClass> res = new ArrayList<>();

    public CodeGenVisitor(String className, String packageName, String
            sourceFileName) {
        super();
        this.packageName = packageName;
        this.className = className;
        this.sourceFileName = sourceFileName;
        this.fullyQualifiedClassName = packageName + "/" + className;
        this.classDesc="L"+this.fullyQualifiedClassName+';';
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLPException {

        //create a classWriter and visit it
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        //Hint:  if you get failures in the visitMaxs, try creating a ClassWriter with 0
        // instead of ClassWriter.COMPUTE_FRAMES.  The result will not be a valid classfile,
        // but you will be able to print it so you can see the instructions. After fixing,
        // restore ClassWriter.COMPUTE_FRAMES
        classWriter.visit(V18, ACC_PUBLIC | ACC_SUPER, fullyQualifiedClassName, null, "java/lang/Object", new String[] {"java/lang/Runnable" });
        classWriter.visitSource(className+".java",null);

        //simple AST visitor to visit all procedures declarations and annotating them with their JVM names
        procVisit = true;
        program.block.visit(this, classWriter);
        procVisit = false;


        //visit the block, passing it the methodVisitor ??

        //creating methodVisitor object
        MethodVisitor methodVisitor;
        //<init> jvm
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object","<init>","()V", false);
        methodVisitor.visitInsn(RETURN);

        methodVisitor.visitMaxs(1,1);
        methodVisitor.visitEnd();

        //main jvm
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitTypeInsn(NEW, fullyQualifiedClassName);
        methodVisitor.visitInsn(DUP);

        methodVisitor.visitMethodInsn(INVOKESPECIAL, fullyQualifiedClassName, "<init>", "()V", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, fullyQualifiedClassName, "run", "()V", false);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2,1);
        methodVisitor.visitEnd();

        program.block.visit(this, classWriter);

        System.out.println("ttteee");
        classWriter.visitEnd();


        res.add(new CodeGenUtils.GenClass(fullyQualifiedClassName, classWriter.toByteArray()));
        //returning byte class
        return res;

        //return the bytes making up the classfile
//         return classWriter.toByteArray();

    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLPException {
        if(procVisit) {
            ClassWriter classWriter = (ClassWriter) arg;
            for (VarDec varDec : block.varDecs) {
                varDec.visit(this, classWriter);
            }
            for (ProcDec procDec : block.procedureDecs) {
                procDec.visit(this, classWriter);
            }
        } else if(!procVisit){
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            methodVisitor.visitCode();

            //add instructions from statement to method
            block.statement.visit(this, methodVisitor);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2,1);
            methodVisitor.visitEnd();
        }
        return null;
    }

    @Override
    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
//        MethodVisitor mv = (MethodVisitor)arg;
//        String constName = String.valueOf(constDec.ident.getText());
//        Type constType = constDec.getType();
//        String descriptor = null;
//        if(constType==Type.BOOLEAN){
//            descriptor="Z";
//        }
//        else if(constType==Type.STRING){
//            descriptor="Ljava/lang/String;";
//        }
//        else{
//            descriptor="I";
//        }
//        FieldVisitor fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, constName, descriptor, null, null);
//        fieldVisitor.visitEnd();
//
//        switch (descriptor){
//            case "Z", "I" -> mv.visitInsn(ICONST_0);
//            case "Ljava/lang/String;" -> {
//                String constValue = (String)constDec.val;
//                mv.visitLdcInsn(constValue);
//            }
//        }
//        mv.visitFieldInsn(PUTSTATIC, className, constName, descriptor);
//        mv.visitEnd();
        return null;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
            ClassWriter classWriter = (ClassWriter)arg;
            String varName = String.valueOf(varDec.ident.getText());
            String varDescriptor = varDec.getDescriptor();

            FieldVisitor fieldVisitor = classWriter.visitField(0, varName, varDescriptor, null, null);
            fieldVisitor.visitEnd();
        return null;
    }

    @Override
    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        statementAssign.expression.visit(this, arg);
        statementAssign.ident.visit(this, arg);
        return null;
    }

    @Override
    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        System.out.println("came here to expression Ident");
        MethodVisitor mv = (MethodVisitor)arg;
        if(expressionIdent.getDec() instanceof ConstDec){
            System.out.println("This is const variable");
            System.out.println(String.valueOf(expressionIdent.firstToken.getText())+":"+((ConstDec) expressionIdent.getDec()).val);
            mv.visitLdcInsn(((ConstDec) expressionIdent.getDec()).val);
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD,fullyQualifiedClassName, String.valueOf(expressionIdent.firstToken.getText()), expressionIdent.getDec().getDescriptor());
        }
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        MethodVisitor mv = (MethodVisitor)arg;
        //go up the nest level ?
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(SWAP);
        mv.visitFieldInsn(PUTFIELD,fullyQualifiedClassName, String.valueOf(ident.firstToken.getText()), ident.getDec().getDescriptor());
        return null;
    }

    @Override
    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitStatementOutput(StatementOutput statementOutput, Object
            arg) throws PLPException {
            MethodVisitor mv = (MethodVisitor)arg;
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            statementOutput.expression.visit(this, arg);
            Type etype = statementOutput.expression.getType();
            String JVMType = (etype.equals(Type.NUMBER) ? "I" :
                    (etype.equals(Type.BOOLEAN) ? "Z" : "Ljava/lang/String;"));
            String printlnSig = "(" + JVMType +")V";
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", printlnSig, false);
        return null;
    }

    @Override
    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        MethodVisitor mv = (MethodVisitor) arg;
        Type argType = expressionBinary.e0.getType();
        Kind op = expressionBinary.op.getKind();
        switch (argType) {
            case BOOLEAN -> {
                expressionBinary.e0.visit(this, arg);
                expressionBinary.e1.visit(this, arg);
//                System.out.println("");
                switch (op) {
                    case PLUS -> mv.visitInsn(IADD);
                    case TIMES -> mv.visitInsn(IMUL);
                    case EQ -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPNE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);

                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case NEQ -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPEQ, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case LT -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPGE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case LE -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPGT, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case GT -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPLE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case GE -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPLT, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    default -> {
                        throw new IllegalStateException("code gen bug in visitExpressionBinary Boolean");
                    }
                }
            }
            case NUMBER -> {
                expressionBinary.e0.visit(this, arg);
                expressionBinary.e1.visit(this, arg);
                switch (op) {
                    case PLUS -> mv.visitInsn(IADD);
                    case MINUS -> mv.visitInsn(ISUB);
                    case TIMES -> mv.visitInsn(IMUL);
                    case DIV -> mv.visitInsn(IDIV);
                    case MOD -> mv.visitInsn(IREM);
                    case EQ -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPNE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);

                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case NEQ -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPEQ, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case LT -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPGE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case LE -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPGT, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case GT -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPLE, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    case GE -> {
                        Label labelNumEqFalseBr = new Label();
                        mv.visitJumpInsn(IF_ICMPLT, labelNumEqFalseBr);
                        mv.visitInsn(ICONST_1);

                        Label labelPostNumEq = new Label();
                        mv.visitJumpInsn(GOTO, labelPostNumEq);
                        mv.visitLabel(labelNumEqFalseBr);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(labelPostNumEq);
                    }
                    default -> {
                        throw new IllegalStateException("code gen bug in visitExpressionBinary NUMBER");
                    }
                }
                ;
            }
            case STRING -> {
                expressionBinary.e0.visit(this, arg);
                expressionBinary.e1.visit(this, arg);
                switch (op) {
                    case PLUS -> {
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String","concat","("+"Ljava/lang/String;"+")"+"Ljava/lang/String;", false);
                    }
                    case EQ -> {
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String","equals","("+"Ljava/lang/Object;"+")"+"Z", false);
                    }
                    case NEQ -> {
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String","equals","("+"Ljava/lang/Object;"+")"+"Z", false);
                        mv.visitMethodInsn(INVOKESTATIC, "edu/ufl/cise/plpfa22/stringRuntime","not","(Z)Z", false);
                    }
                    case LT -> {
                        mv.visitMethodInsn(INVOKESTATIC, "edu/ufl/cise/plpfa22/stringRuntime","lt","("+"Ljava/lang/String;"+"Ljava/lang/String;"+")"+"Z", false);
                    }
                    case LE -> {
                        mv.visitMethodInsn(INVOKESTATIC, "edu/ufl/cise/plpfa22/stringRuntime","le","("+"Ljava/lang/String;"+"Ljava/lang/String;"+")"+"Z", false);
                    }
                    case GT -> {
                        mv.visitMethodInsn(INVOKESTATIC, "edu/ufl/cise/plpfa22/stringRuntime","gt","("+"Ljava/lang/String;"+"Ljava/lang/String;"+")"+"Z", false);
                    }
                    case GE -> {
                        mv.visitMethodInsn(INVOKESTATIC, "edu/ufl/cise/plpfa22/stringRuntime","ge","("+"Ljava/lang/String;"+"Ljava/lang/String;"+")"+"Z", false);
                    }
                    default -> {
                        throw new IllegalStateException("code gen bug in visitExpressionBinary String");
                    }
                }
            }
            default -> {
                throw new IllegalStateException("code gen bug in visitExpressionBinary");
            }
        }
        return null;
    }



    @Override
    public Object visitStatementBlock(StatementBlock statementBlock, Object arg)
            throws PLPException {

        for(int i=0;i<statementBlock.statements.size();i++){
            statementBlock.statements.get(i).visit(this,arg);
        }
        return null;
    }
    @Override
    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
            MethodVisitor mv = (MethodVisitor)arg;
            statementIf.expression.visit(this, arg);

            Label labelNumEqFalseBr = new Label();
            mv.visitInsn(ICONST_1);
            mv.visitJumpInsn(IF_ICMPLT, labelNumEqFalseBr);

            statementIf.statement.visit(this,arg);
            mv.visitLabel(labelNumEqFalseBr);
        return null;
    }
    @Override
    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        MethodVisitor mv = (MethodVisitor)arg;
        Label begin = new Label();
        Label  end = new Label();
        Expression guardExp = statementWhile.expression;

        mv.visitLabel(begin);
        guardExp.visit(this, arg);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPLT, end);
        statementWhile.statement.visit(this, arg);
        mv.visitJumpInsn(GOTO, begin);
        mv.visitLabel(end);
        return null;
    }

    @Override
    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
            MethodVisitor mv = (MethodVisitor)arg;
            mv.visitLdcInsn(expressionNumLit.getFirstToken().getIntValue());
        return null;
    }
    @Override
    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
            MethodVisitor mv = (MethodVisitor)arg;
            mv.visitLdcInsn(expressionStringLit.getFirstToken().getStringValue());
        return null;
    }
    @Override
    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
            MethodVisitor mv = (MethodVisitor)arg;
            mv.visitLdcInsn(expressionBooleanLit.getFirstToken().getBooleanValue());
        return null;
    }
    @Override
    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException
    {
        ClassWriter classWriter = (ClassWriter)arg;
        if(procVisit){
            //System.out.println(fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()));
//            classWriter.visit(V18, ACC_SUPER, fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()), null, "java/lang/Object", new String[] {"java/lang/Runnable" });
            classWriter.visitNestMember(fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()));
        }
        else{
            classWriter.visit(V18, ACC_SUPER, fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()), null, "java/lang/Object", new String[] {"java/lang/Runnable" });
//            classWriter.visitInnerClass(fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()), fullyQualifiedClassName, String.valueOf(procDec.ident.getText()), 0);
//            procDec.block.visit(this, arg);
//            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
//            methodVisitor.visitCode();

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            methodVisitor.visitCode();
            procDec.block.statement.visit(this, classWriter);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();


//            methodVisitor = classWriter.visitMethod(0, "<init>", "(Ledu/ufl/cise/plpfa22/codeGenSamples/Var2;)V", null, null);
            methodVisitor = classWriter.visitMethod(0, "<init>", "(L"+fullyQualifiedClassName+";)V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
//            methodVisitor.visitFieldInsn(PUTFIELD, "edu/ufl/cise/plpfa22/codeGenSamples/Var2$p", "this$0", "Ledu/ufl/cise/plpfa22/codeGenSamples/Var2;");
            methodVisitor.visitFieldInsn(PUTFIELD, fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()), "this$0", "L"+fullyQualifiedClassName+";");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();

            classWriter.visitEnd();

            res.add(new CodeGenUtils.GenClass(fullyQualifiedClassName+"$"+String.valueOf(procDec.ident.getText()), classWriter.toByteArray()));
        }
        return null;
    }

    @Override
    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        MethodVisitor methodVisitor = (MethodVisitor)arg;
//        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        methodVisitor.visitCode();

        System.out.println("came to the procedure1");
        methodVisitor.visitTypeInsn(NEW, fullyQualifiedClassName+"$"+String.valueOf(statementCall.ident.getText()));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, fullyQualifiedClassName+"$"+String.valueOf(statementCall.ident.getText()), "<init>", "(L"+fullyQualifiedClassName+";)V", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, fullyQualifiedClassName+"$"+String.valueOf(statementCall.ident.getText()), "run", "()V", false);

        methodVisitor.visitInsn(RETURN);

        methodVisitor.visitMaxs(3, 1);
        methodVisitor.visitEnd();
        return null;
    }

    @Override
    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        return null;
    }
}