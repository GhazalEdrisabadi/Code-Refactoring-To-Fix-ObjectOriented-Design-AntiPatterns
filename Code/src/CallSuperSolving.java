import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.*;
import java.util.*;

public class CallSuperSolving extends JavaParserBaseVisitor<Object> implements JavaParserVisitor<Object>{

    public List<ClassInfo> detectorResult = new ArrayList<>();
    List<JavaParser.ClassBodyDeclarationContext> apMethods;
    List<String> codeTxt = new ArrayList<>();
    List<String> methodBodyTxt = new ArrayList<>();
    String currentClassName;
    ClassInfo classInfoObj = new ClassInfo();
    File file;
    Writer w;
    BufferedWriter bw;

    public void solve(JavaParser javaParser) throws IOException {

        JavaParser.CompilationUnitContext parserContext = javaParser.compilationUnit();
        visitCompilationUnit(parserContext);

        file = new File("Call Super Output.java"); // Specify the filename
        if (file.exists()) {
            file.delete();
            file.createNewFile();
            w = new FileWriter(file);
            bw = new BufferedWriter(w);
        }
        else {
            w = new FileWriter(file);
            bw = new BufferedWriter(w);
        }


        for (int i = 0; i < codeTxt.size(); i++) {
            bw.write(codeTxt.get(i));
            bw.write(" ");
            if(codeTxt.get(i).equals("{")
                    || codeTxt.get(i).equals("}")
                    || codeTxt.get(i).equals(";")){
                bw.newLine();
            }
        }
        bw.close();
    }

    public void traverse ( ParseTree tree ) {
        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if(tree.getChild(i) instanceof TerminalNode){
                codeTxt.add(tree.getChild(i).getText());
            }
            else {
                traverse(tree.getChild(i));
            }
        }
    }

    // Save the entire tree to a temporary list
    public void tempTraverse ( ParseTree tree ) {
        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (tree.getChild(i) instanceof TerminalNode) {
                methodBodyTxt.add(tree.getChild(i).getText());
            } else {
                tempTraverse(tree.getChild(i));
            }
        }
    }

    @Override
    public Void visitCompilationUnit(JavaParser.CompilationUnitContext ctx){

        List<JavaParser.TypeDeclarationContext> typeDecList = ctx.typeDeclaration(); // get typeDeclaration list
        JavaParser.ClassDeclarationContext classDecItem; // define classDeclaration item
        List<JavaParser.ClassOrInterfaceModifierContext> classOrIntModList; // define classOrInterfaceModifier List

        for (int i = 0; i < typeDecList.size(); i++) { // loop on each item in typeDeclaration list

            // get all classOrInterfaceModifiers of this typeDeclaration item
            classOrIntModList = typeDecList.get(i).classOrInterfaceModifier();

            if(classOrIntModList.size() > 0){
                // loop on each item in classOrInterfaceModifier list
                for (int j = 0; j < classOrIntModList.size(); j++) {
                    // add all classOrInterfaceModifiers to write in a file
                    codeTxt.add(classOrIntModList.get(j).getText());
                }
            }

            classDecItem = typeDecList.get(i).classDeclaration(); // get classDeclaration item
            visitClassDeclaration(classDecItem);
        }
        return null;
    }

    @Override
    public Void visitClassDeclaration(JavaParser.ClassDeclarationContext ctx){

        List<String> parents = classInfoObj.getAllParents(detectorResult); // get all parents of source code
        currentClassName = ctx.identifier().getText(); // get current class name

        // loop on all childs of classDeclaration item except classBody
        for (int i = 0; i < ctx.getChildCount()-1; i++) {
            codeTxt.add(ctx.getChild(i).getText());
        }

        if(parents.contains(currentClassName)){ // current class is a superclass
            codeTxt.add("{");
            apMethods = classInfoObj.getApMethods(currentClassName, detectorResult);
            // get methods of this parent class
            List<JavaParser.ClassBodyDeclarationContext> parentMethods = ctx.classBody().classBodyDeclaration();
            for (int i = 0; i < parentMethods.size(); i++) { // loop on each method of this parent class
                visitClassBodyDeclaration(parentMethods.get(i));
            }
            apMethods = classInfoObj.getApMethods(currentClassName, detectorResult);
            for (int i = 0; i < apMethods.size(); i++) { // loop on overriden methods with anti-patern

                codeTxt.add("protected");
                String methodType = apMethods.get(i).memberDeclaration()
                        .methodDeclaration().typeTypeOrVoid().getText();

                String methodName = apMethods.get(i).memberDeclaration()
                        .methodDeclaration().identifier().getText();
                String str = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                ParseTree subtree = apMethods.get(i).memberDeclaration()
                        .methodDeclaration().formalParameters();

                codeTxt.add(methodType);
                codeTxt.add("do"+str);
                traverse(subtree);
                codeTxt.add("{}");
            }
        }
        // current class is a subclass
        else {
            codeTxt.add("{");
            visitClassBody(ctx.classBody());
        }
        codeTxt.add("}");
        return null;
    }

    // for parent refactoring
    @Override
    public Void visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        // get parent method name
        String methodName = ctx.memberDeclaration().methodDeclaration().identifier().getText();
        // get parent method parameters list
        JavaParser.FormalParameterListContext formalParameterItem = ctx.memberDeclaration().methodDeclaration()
                .formalParameters().formalParameterList();
        // get methods with call super antipatern using parent
        List<JavaParser.ClassBodyDeclarationContext> parentApMethods = classInfoObj.getApMethods(currentClassName, detectorResult);
        String parentApMethodName = ""; // define name of method with call super antipatern

        // class has any method with antipattern or not
        if(parentApMethods.size() > 0){
            for (int i = 0; i < parentApMethods.size(); i++) { // loop on each method with call super antipatern
                // get name of method with call super antipatern
                parentApMethodName = parentApMethods.get(i).memberDeclaration().methodDeclaration().identifier().getText();
                // get type of method with call super antipatern
                JavaParser.TypeTypeOrVoidContext methodType = parentApMethods.get(i).memberDeclaration()
                        .methodDeclaration().typeTypeOrVoid();

                if (methodName.equals(parentApMethodName)) { // this method needs refactoring
                    tempTraverse(ctx); // save the entire method to a temporary list
                    for (int j = 0; j < methodBodyTxt.size(); j++) { // loop on each item in temporary list
                        if (methodBodyTxt.get(j).equals("}")) { // runs once
                            // capitalize the first letter of method name with call super antipatern
                            String str = parentApMethodName.substring(0, 1).toUpperCase() + parentApMethodName.substring(1);
                            // refactor for void type methods
                            if(!methodType.getText().equals("void")) {
                                methodBodyTxt.set(methodBodyTxt.size()-1,methodType.getText());
                                methodBodyTxt.add("call"+str+" = ");
                                methodBodyTxt.add("do"+str+"(");
                            }
                            else{
                                methodBodyTxt.set(methodBodyTxt.size()-1,"do"+str+"(");
                            }
                            if(formalParameterItem != null) {
                                visitFormalParameterList(formalParameterItem);
                            }
                            methodBodyTxt.add(");");
                        }
                    }
                    methodBodyTxt.add("}");
                    codeTxt.addAll(methodBodyTxt);
                    methodBodyTxt.clear();
                }
                else { // this method don't need refactoring
                    traverse(ctx); // save the entire method to a final list
                }
            }
        }
        else { // this method don't need refactoring
            traverse(ctx); // save the entire method to a final list
        }
        return null;
    }

    @Override
    public Void visitFormalParameterList(JavaParser.FormalParameterListContext ctx) {
        List<JavaParser.FormalParameterContext> formalParameters = ctx.formalParameter();
        for (int i = 0; i < formalParameters.size(); i++) {
            methodBodyTxt.add(formalParameters.get(i).variableDeclaratorId().identifier().getText());
            if(formalParameters.size() > 1) {
                methodBodyTxt.add(",");
            }
        }
        return null;
    }

    @Override
    public Void visitClassBody(JavaParser.ClassBodyContext ctx){
        // define child classBodyDeclaration list
        List<JavaParser.ClassBodyDeclarationContext> classBodyDecList = ctx.classBodyDeclaration();
        // get methods with call super antipatern using child
        List<String> apMethodNames = classInfoObj.getApMethodNames(currentClassName, detectorResult);

        for (int i = 0; i < classBodyDecList.size(); i++) {
            String name = classBodyDecList.get(i).memberDeclaration().methodDeclaration().identifier().getText();
            if(apMethodNames.contains(name)){
                codeTxt.add("@Override");
                codeTxt.add("protected");
                visitMethodDeclaration(classBodyDecList.get(i).memberDeclaration().methodDeclaration());
            }
            else {
                traverse(classBodyDecList.get(i));
            }
        }
        return null;
    }
    @Override
    public Void visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        String methodType = ctx.typeTypeOrVoid().getText();
        String methodName = ctx.identifier().getText();
        String str = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        ParseTree subtree = ctx.formalParameters();
        codeTxt.add(methodType);
        codeTxt.add("do"+str);
        traverse(subtree);
        visitMethodBody(ctx.methodBody());
        return null;
    }

    @Override
    public Void visitMethodBody(JavaParser.MethodBodyContext ctx){
        codeTxt.add("{");
        List<JavaParser.BlockStatementContext> blockStatementsList = ctx.block().blockStatement();
        for (int i = 0; i < blockStatementsList.size(); i++) {
            if(!blockStatementsList.get(i).getText().contains("super.")){
                traverse(blockStatementsList.get(i));
            }
        }
        codeTxt.add("}");
        return null;
    }
}
