import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.io.*;
import java.util.*;

public class ConstantInterfaceSolving extends JavaParserBaseVisitor<Object> implements JavaParserVisitor<Object>{

    public List<String> codeTxt = new ArrayList<>();
    List<String> impTxt = new ArrayList<>();
    List<String> trueValuesInterfaceList = new ArrayList<>(); // list of interfaces with anti-pattern
    Map<String, List<String>> detectorResult;
    File file;
    Writer w;
    BufferedWriter bw;
    private String name;

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

    public void solve(JavaParser javaParser) throws IOException {

        JavaParser.CompilationUnitContext parserContext = javaParser.compilationUnit();
        List<String> visitCompilationUnitResult = visitCompilationUnit(parserContext);

        // remove duplicates from list
        Set<String> set = new HashSet<>(trueValuesInterfaceList);
        trueValuesInterfaceList.clear();
        trueValuesInterfaceList.addAll(set);

        file = new File("Constant Interface Output.java"); // Specify the filename
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

        for (int i = 0; i < impTxt.size(); i++) {
            bw.write(impTxt.get(i));
            if(impTxt.get(i).equals(";")){
                bw.newLine();
            }
        }

        for (int i = 0; i < visitCompilationUnitResult.size(); i++) {
            bw.write(visitCompilationUnitResult.get(i));
            bw.write(" ");
            if(visitCompilationUnitResult.get(i).equals("{")
                    || visitCompilationUnitResult.get(i).equals("}")
                    || visitCompilationUnitResult.get(i).equals(";")){
                bw.newLine();
            }
        }
        bw.close();
    }

    @Override
    public List<String> visitCompilationUnit(JavaParser.CompilationUnitContext ctx){

        List<JavaParser.TypeDeclarationContext> typeDecList = ctx.typeDeclaration(); // get typeDeclaration list
        JavaParser.InterfaceDeclarationContext intDecItem; // define interfaceDeclaration item
        JavaParser.ClassDeclarationContext classDecItem; // define classDeclaration item
        List<JavaParser.ClassOrInterfaceModifierContext> classOrIntModList; // define classOrInterfaceModifier List

        if (typeDecList.size() > 0) {
            for (int i = 0; i < typeDecList.size(); i++) {

                // get all classOrInterfaceModifiers of this typeDeclaration
                classOrIntModList = typeDecList.get(i).classOrInterfaceModifier();

                if(classOrIntModList.size() > 0){
                    for (int j = 0; j < classOrIntModList.size(); j++) {
                        // add all classOrInterfaceModifiers to write in a file
                        codeTxt.add(classOrIntModList.get(j).getText());
                    }
                }

                classDecItem = typeDecList.get(i).classDeclaration();
                intDecItem = typeDecList.get(i).interfaceDeclaration();

                if (classDecItem != null) {
                    List<String> interfaces = new ArrayList<>();
                    name = classDecItem.identifier().getText(); // get class name

                    codeTxt.add("class");
                    codeTxt.add(name);

                    for (Map.Entry<String, List<String>> item : detectorResult.entrySet()){
                        if(item.getKey().equals(name)){
                            List<String> itemValues = item.getValue();
                            for (int j = 0; j < itemValues.size(); j++) {
                                String[] values = itemValues.get(j).split(",");
                                if (values[1].equals("false")) {
                                    interfaces.add(values[0]);
                                }
                            }
                            break;
                        }
                    }
                    if(interfaces.size() > 0){
                        codeTxt.add("implements");
                        for (int j = 0; j < interfaces.size()-1; j++) {
                            codeTxt.add(interfaces.get(i));
                            codeTxt.add(",");
                        }
                        codeTxt.add(interfaces.get(interfaces.size()-1));
                    }
                    JavaParser.ClassBodyContext classBodyItem = classDecItem.classBody();
                    List<JavaParser.ClassBodyDeclarationContext> classBodyDeclarationList =
                            classDecItem.classBody().classBodyDeclaration();
                    codeTxt.add(classBodyItem.getChild(0).getText()); // add { to codeTxt
                    for (int j = 0; j < classBodyDeclarationList.size(); j++) {
                        ParseTree tree = classBodyDeclarationList.get(j);
                        traverse(tree);
                    }
                    codeTxt.add(classBodyItem.getChild(classBodyItem.getChildCount() - 1).getText()); // add } to codeTxt
                }
                else if (intDecItem != null) {
                    name = intDecItem.identifier().getText(); // get interface name

                    // apply changes only to interfaces with anti-patterns
                    if(trueValuesInterfaceList.contains(name)){

                        impTxt.add("import static "+name+".*");
                        impTxt.add(";");
                        codeTxt.add("final class");
                        codeTxt.add(name);

                        visitInterfaceBody(intDecItem.interfaceBody());
                    }
                    // this interface has not anti-pattern
                    else {
                        codeTxt.add(intDecItem.getText());
                    }
                }
            }
        }
        return codeTxt;
    }

    @Override
    public Void visitInterfaceBody (JavaParser.InterfaceBodyContext ctx){

        // get interfaceBodyDeclaration list
        List<JavaParser.InterfaceBodyDeclarationContext> intBodyDecList = ctx.interfaceBodyDeclaration();

        // define interfaceMemberDeclaration item
        JavaParser.InterfaceMemberDeclarationContext intMemDecItem;

        codeTxt.add(ctx.getChild(0).getText()); // add { to codeTxt

        codeTxt.add("private");
        codeTxt.add(name);
        codeTxt.add("()");
        codeTxt.add("{");
        codeTxt.add("}");


        for (int i = 0; i < intBodyDecList.size(); i++) {

            intMemDecItem = intBodyDecList.get(i).interfaceMemberDeclaration(); // get interfaceMemberDeclaration item

            if(intMemDecItem != null){

                if (intMemDecItem.constDeclaration() != null) {
                    visitConstDeclaration(intMemDecItem.constDeclaration());
                }
                else {
                    ParseTree tree = intBodyDecList.get(i);
                    traverse(tree);
                }
            }
            else{
                codeTxt.add(intBodyDecList.get(i).getText());
            }
        }
        codeTxt.add(ctx.getChild(ctx.getChildCount()-1).getText()); // add } to codeTxt
        return null;
    }

    @Override
    public Void visitConstDeclaration (JavaParser.ConstDeclarationContext ctx){

        codeTxt.add("public static final");

        // add constantDeclaration contents to codeTxt
        for (int i = 0; i < ctx.getChildCount(); i++) {
            codeTxt.add(ctx.getChild(i).getText());
        }
        return null;
    }
}
