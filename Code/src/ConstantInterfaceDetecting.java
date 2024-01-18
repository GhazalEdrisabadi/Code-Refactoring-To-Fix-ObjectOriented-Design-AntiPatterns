import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class ConstantInterfaceDetecting extends JavaParserBaseVisitor<Object> implements JavaParserVisitor<Object>{

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    /* Returns Map<String,List<String>> Object */
    public Map<String,List<String>> detector(JavaParser javaParser){

        // a CompilationUnitContext to start detecting
        JavaParser.CompilationUnitContext parserContext = javaParser.compilationUnit();

        // a dictionary of interfaces along with their constants and classes with implemented interfaces
        Map<Object,Object> visitCompilationUnitResult = visitCompilationUnit(parserContext);

        // a list of interfaces along with their constants
        List<Map.Entry<Object,Object>> interfaceList = new ArrayList<>();

        // a list of classes with implemented interfaces
        List<Map.Entry<Object,Object>> classList = new ArrayList<>();

        // key list of each item in classList
        List<String> classKey = new ArrayList<>();

        // a dict to store method output
        Map<String,List<String>> resultDict = new HashMap<>();

        String add = "+";
        String sub = "-";
        String mul = "*";
        String div = "/";
        String rem = "%";
        String eq = "=";

        // separating interfaces from classes
        for (Map.Entry<Object,Object> item : visitCompilationUnitResult.entrySet()) {
            if (item.getKey() instanceof String) {
                interfaceList.add(item);
            }
            else{
                classList.add(item);
            }
        }

        // loop on each item in class list, classList:[[key=value]]
        // classList = [[className, interfaceImplementedName1, interfaceImplementedName2, ....]=[classBody], ...]
        for (int i = 0; i < classList.size(); i++) {

            // A temp list to help generate output
            List<String> temp = new ArrayList<>();

            // get key of each class list item,
            // classKey = [className, interfaceImplementedName1, interfaceImplementedName2, ....]
            classKey = (List)classList.get(i).getKey();

            System.out.println(ANSI_CYAN + "Class: " + classKey.get(0) + ANSI_RESET);

            for (int j = 1; j < classKey.size(); j++) { // loop on each item in classDictKey

                System.out.println(ANSI_CYAN + "Interface: " + classKey.get(j) + ANSI_RESET);


                if(interfaceList.size() > 0){

                    // loop on each item in interface list, interfaceList:[[key=value]]
                    // interfaceList = [interfaceName=[constant1, constant2, ...], ...]
                    for (int k = 0; k < interfaceList.size(); k++) {
                        String interfaceName = interfaceList.get(k).getKey().toString();

                        // check interface implemented by class is an interface with constants or not
                        if(classKey.get(j).equals(interfaceName)){

                            temp.add(interfaceName.concat(",").concat("true"));

                            ParseTree t = (ParseTree)classList.get(i).getValue(); // get classBody
                            String t2 = t.getText(); // get classBody values

                            // loop on values of interfaceDict ~ loop on constants
                            for (Object s : (List)interfaceList.get(k).getValue()) {
                                String str = s.toString();
                                System.out.println(ANSI_CYAN + ">>> Constant: " + str + ANSI_RESET);

                                // search for:
                                // constant+ , +constant , constant- , -constant , constant= , =constant
                                // constant* , *constant , constant/ , /constant , constant% , %constant
                                if(t2.contains(str.concat(add)) || t2.contains(add.concat(str))
                                        || t2.contains(str.concat(sub)) || t2.contains(sub.concat(str))
                                        || t2.contains(str.concat(mul)) || t2.contains(mul.concat(str))
                                        || t2.contains(str.concat(div)) || t2.contains(div.concat(str))
                                        || t2.contains(str.concat(rem)) || t2.contains(rem.concat(str))
                                        || t2.contains(str.concat(eq)) || t2.contains(eq.concat(str))){

                                    System.out.println("The "+str+" constant in "+classKey.get(0)+" class has been changed.");
                                } else {
                                    System.out.println("The "+str+" constant in "+classKey.get(0)+" not used.");
                                }

                            }
                            System.out.println(ANSI_RED + "'Constant Interface' anti-pattern detected!" + ANSI_RESET);
                        }
                        else {
                            System.out.println(ANSI_GREEN + "No 'Constant Interface' anti-pattern detected." + ANSI_RESET);
                            temp.add(interfaceName.concat(",").concat("false"));
                        }
                    }
                }
                else{
                    System.out.println(ANSI_GREEN + "No 'Constant Interface' anti-pattern detected." + ANSI_RESET);
                    temp.add(classKey.get(j).concat(",").concat("false"));
                }
            }
            // remove duplicates from list
            Set<String> set = new HashSet<>(temp);
            temp.clear();
            temp.addAll(set);
            resultDict.put(classKey.get(0),temp);
            System.out.println("-----------------------------------------------------------------------");
        }
        return resultDict;
    }

    /* Returns a dictionary of interfaces along with their constants and classes that implement these interfaces */
    /* Hierarchy : CompilationUnit */
    @Override
    public Map<Object,Object> visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {

        List<JavaParser.TypeDeclarationContext> typeDecList = ctx.typeDeclaration(); // get typeDeclaration list
        JavaParser.ClassDeclarationContext classDecItem; // define classDeclaration item
        JavaParser.InterfaceDeclarationContext intDecItem; // define interfaceDeclaration item
        Map<Object,Object> resultDict =  new HashMap<>(); // define a dict for method output
        String interfaceName;

        if (typeDecList.size() > 0) {
            for (int i = 0; i < typeDecList.size(); i++) { // loop on each item in typeDeclaration list
                classDecItem = typeDecList.get(i).classDeclaration(); // get classDeclaration item
                intDecItem = typeDecList.get(i).interfaceDeclaration(); // get interfaceDeclaration item
                if (classDecItem != null) {

                    // if the class implements an interface, add it
                    if(visitClassDeclaration(classDecItem).size() > 0){
                        resultDict.putAll(visitClassDeclaration(classDecItem)); // call visitClassDeclaration method
                    }
                }
                else if (intDecItem != null) {
                    interfaceName = intDecItem.identifier().getText();
                    if (intDecItem.interfaceBody() != null) { // check interface has body or not

                        // if interface has constant, add it
                        if(visitInterfaceBody(intDecItem.interfaceBody()).size() > 0){
                            // HashMap<String,<List<String>> Object
                            // KEY:      interface name
                            // Value:    constants' name
                            resultDict.put(interfaceName, visitInterfaceBody(intDecItem.interfaceBody()));
                        }
                    }
                    else{
                        System.out.println(ANSI_YELLOW + "'InterfaceBody' of interface " + interfaceName +" not found! " +
                                "Check your source code and try again." + ANSI_RESET);
                        System.exit(0);
                    }
                }
            }
        }
        else{
            System.out.println(ANSI_YELLOW + "'TypeDeclaration' rule not found! " +
                    "Check your source code and try again." + ANSI_RESET);
            System.exit(0);
        }
        return resultDict;
    }

    /* Returns Map<<List<String>,ParseTree> Object
     * KEY:      class name , implemented interfaces name
     * Value:    class body parse tree
     */
    /* Hierarchy : CompilationUnit <-- TypeDeclaration <-- ClassDeclaration */
    @Override
    public Map<List<String>, ParseTree> visitClassDeclaration (JavaParser.ClassDeclarationContext ctx){

        String child;
        String className = "";
        List<String> implementedInterfacesList = new ArrayList<>();
        ParseTree classBodyTree;
        Map<List<String>, ParseTree> resultDict = new HashMap<>(); // define a dict for method output
        JavaParser.TypeListContext typeListContext; // define TypeListContext item

        // get class name and implemented interfaces list by class
        for (int i = 0; i < ctx.getChildCount(); i++) {
            child = ctx.getChild(i).getText(); // get ClassDeclaration child text
            if (child.equals("class")) {
                className = ctx.getChild(i + 1).getText();
            }
            else if (child.equals("implements")) {
                typeListContext =  (JavaParser.TypeListContext)ctx.getChild(i + 1);
                implementedInterfacesList = visitTypeList(typeListContext); // call visitTypeList method
            }
        }
        if (ctx.classBody() != null) { // check class has body or not
            classBodyTree = ctx.getChild(ctx.getChildCount() - 1); // get classBody tree

            // if the class implements an interface, add it
            if(implementedInterfacesList.size() > 0){
                implementedInterfacesList.add(0, className); // add class name to the start of implementedInterfacesList
                resultDict.put(implementedInterfacesList, classBodyTree); // add above items to dictionary
            }
        }
        else{
            System.out.println(ANSI_YELLOW + "'ClassBody' of class " + className +" not found! " +
                    "Check your source code and try again." + ANSI_RESET);
            System.exit(0);
        }
        return resultDict;
    }

    /* Returns the list of constants' name of an interface */
    /* Hierarchy : CompilationUnit <-- TypeDeclaration <-- InterfaceDeclaration <-- InterfaceBody */
    @Override
    public List<String> visitInterfaceBody (JavaParser.InterfaceBodyContext ctx){
        // get interfaceBodyDeclaration list
        List<JavaParser.InterfaceBodyDeclarationContext> intBodyDecList = ctx.interfaceBodyDeclaration();
        List<String> tempList;
        List<String> constantsNameList = new ArrayList<>();
        JavaParser.InterfaceMemberDeclarationContext intMemDecItem; // define InterfaceMemberDeclarationContext item

        if (intBodyDecList.size() > 0) {
            // call visitConstDeclaration and add constants' name for an interface to the list
            for (int i = 0; i < intBodyDecList.size(); i++) {
                intMemDecItem = intBodyDecList.get(i).interfaceMemberDeclaration(); // get interfaceMemberDeclaration item
                if (intMemDecItem != null) {
                    if (intMemDecItem.constDeclaration() != null) { // check interface has constants or not
                        tempList = visitConstDeclaration(intMemDecItem.constDeclaration());
                        constantsNameList.addAll(tempList); // add all constants' name of interface to list
                    }
                    else{
                        constantsNameList.clear();
                    }
                }
            }
        }
        else{
            System.out.println(ANSI_YELLOW + "'InterfaceBodyDeclaration' of interface not found! " +
                    "Check your source code and try again." + ANSI_RESET);
            System.exit(0);
        }
        return constantsNameList;
    }

    /* Returns the list of constants' name of an InterfaceMemberDeclaration */
    /* Hierarchy : CompilationUnit <-- TypeDeclaration <-- InterfaceDeclaration <-- InterfaceBody
     *          <-- InterfaceBodyDeclaration <-- InterfaceMemberDeclaration <-- ConstDeclaration */
    @Override
    public List<String> visitConstDeclaration (JavaParser.ConstDeclarationContext ctx){

        List<String> constantsNameList = new ArrayList<>(); // define a list for method output
        List<JavaParser.ConstantDeclaratorContext> consDec = ctx.constantDeclarator(); // get constantDeclarator list

        for (int i = 0; i < consDec.size(); i++) { // loop on each item in a ConstantDeclarator
            // add constants' name from an ConstantDeclarator to the list
            constantsNameList.add(consDec.get(i).identifier().getText());
        }
        return constantsNameList;
    }

    /* Returns the names of all interfaces that a class implements */
    /* Hierarchy : CompilationUnit <-- TypeDeclaration <-- ClassDeclaration <-- TypeList */
    @Override
    public List<String> visitTypeList (JavaParser.TypeListContext ctx){

        List<JavaParser.TypeTypeContext> ttList = ctx.typeType(); // define TypeTypeContext list
        List<String> interfacesNameList = new ArrayList<>(); // define a list for method output

        for (int i = 0; i < ttList.size(); i++) { // loop on each item in a TypeList
            // add interfaces' name from an TypeType to the list
            interfacesNameList.add(ttList.get(i).classOrInterfaceType().identifier(0).getText());
        }
        return interfacesNameList;
    }
}

