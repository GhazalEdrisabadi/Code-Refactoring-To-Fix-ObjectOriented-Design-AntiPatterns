import java.util.ArrayList;
import java.util.List;

public class CallSuperDetecting extends JavaParserBaseVisitor<Object> implements JavaParserVisitor<Object>{

    // define colors of console
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    List<ClassInfo> classInfo = new ArrayList<>();

    public Boolean detector(JavaParser javaParser){

        Boolean flag = false; // The source code has at least one antipattern or not
        JavaParser.CompilationUnitContext parserContext = javaParser.compilationUnit(); // a CompilationUnitContext to start detecting
        JavaParser.MethodBodyContext methodBodyItem; // define child overridden methods body
        List<JavaParser.ClassBodyDeclarationContext> methods; // define child overridden methods list
        String methodName = ""; // define child overridden method name
        String str = "";

        visitCompilationUnit(parserContext);

        for (int i = 0; i < classInfo.size(); i++) { // loop on each item in classInfo list
            // print superclass and subclass names
            System.out.println(ANSI_CYAN + "Superclass: " +  classInfo.get(i).parentClassName + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Subclass: " + classInfo.get(i).childClassName + ANSI_RESET);
            methods = classInfo.get(i).overriddenMethods; // get child overridden methods list

            if(methods.size() > 0){
                for (int j = 0; j < methods.size(); j++) { // loop on each item in child overridden methods list
                    // get child overridden method name
                    methodName = methods.get(j).memberDeclaration().methodDeclaration().identifier().getText();
                    // print overridden method name
                    System.out.println(ANSI_CYAN + ">>> Overridden Method: " + methodName + ANSI_RESET);
                    // get child overridden method body
                    methodBodyItem = methods.get(j).memberDeclaration().methodDeclaration().methodBody();
                    str = "super" + "." + methodName;

                    if (methodBodyItem.getText().contains(str)) { // antipattern detection in method body
                        // add methods with antipatern to classInfo object
                        classInfo.get(i).methodsWithAntipatern.add(methods.get(j));
                        System.out.println(ANSI_RED + "'Call Super' anti-pattern detected!" + ANSI_RESET);
                        flag = true;
                    }
                    else{
                        System.out.println(ANSI_GREEN + "No 'Call Super' anti-pattern detected." + ANSI_RESET);
                    }
                }
                System.out.println("-----------------------------------------------------------------------");
            }
            else{
                System.out.println(ANSI_YELLOW + "Overridden method not found!" + ANSI_RESET);
            }
        }
        return flag;
    }

    @Override
    public Void visitCompilationUnit(JavaParser.CompilationUnitContext ctx){

        List<JavaParser.TypeDeclarationContext> typeDecList = ctx.typeDeclaration(); // get typeDeclaration list
        JavaParser.ClassDeclarationContext classDecItem; // define classDeclaration item

        if(typeDecList.size() > 0) {
            for (int i = 0; i < typeDecList.size(); i++) { // loop on each item in typeDeclaration list
                classDecItem = typeDecList.get(i).classDeclaration(); // get classDeclaration item
                if (classDecItem != null) {
                    visitClassDeclaration(classDecItem);
                }
            }
        }
        else{
            System.out.println(ANSI_YELLOW + "'TypeDeclaration' rule not found! " +
                    "Check your source code and try again." + ANSI_RESET);
            System.exit(0);
        }
        return null;
    }

    /* returns a list of class methods */
    @Override
    public Void visitClassDeclaration(JavaParser.ClassDeclarationContext ctx){
        String secondChild  = ctx.getChild(2).getText(); // parent:class methods , child:extends
        String className = "";
        /* Assume : The source code only contains child and parent classes */
        // child class
        if(secondChild.equals("extends")){
            String parentName = ctx.getChild(3).getText();
            className = ctx.getChild(1).getText();
            classInfo.add(new ClassInfo(parentName,className,visitClassBody(ctx.classBody()),new ArrayList<>()));
        }
        return null;
    }

    /* Returns a list of overridden methods in the child class */
    /* Hierarchy : CompilationUnit <--  <--  <-- ClassBody */
    @Override
    public List<JavaParser.ClassBodyDeclarationContext> visitClassBody(JavaParser.ClassBodyContext ctx){

        // get classBodyDeclaration list
        List<JavaParser.ClassBodyDeclarationContext> classBodyDecList = ctx.classBodyDeclaration();
        // define modifiers list
        List<JavaParser.ModifierContext> modifiersList;
        // define a result list for method output
        List<JavaParser.ClassBodyDeclarationContext> result = new ArrayList<>();

        for(int i = 0; i < classBodyDecList.size(); i++){ // loop on each item in classBodyDeclaration list
            modifiersList = classBodyDecList.get(i).modifier(); // get modifiers of a classBodyDeclaration
            for (int j = 0; j < modifiersList.size(); j++) { // loop on each item in modifiers list
                //  check ClassBodyDeclaration method has @Override or not
                if(modifiersList.get(j).getText().equals("@Override")){
                    // add this classBodyDeclaration item to the list
                    result.add(classBodyDecList.get(i));
                }
            }
        }
        return result;
    }
}




