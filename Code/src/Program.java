import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Program {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input1, input2, command;
        AST ast = new AST();
        GenerateParser parser = new GenerateParser();
        JavaParser javaParser;
        JavaParser.CompilationUnitContext parserContext;

        System.out.println("---------------------------------------------------");
        System.out.println("     Which anti-pattern do you want to check?");
        System.out.println("1.Call Super");
        System.out.println("2.Constant Interface");
        input1 = scanner.next();
        System.out.println("---------------------------------------------------");
        System.out.println("      Which operation do you want to perform?");
        System.out.println("1.Draw source AST");
        System.out.println("2.Detecting");
        input2 = scanner.next();
        command = input1.concat(input2);

        switch (command) {
            // Call Supper - AST
            case "11":
                ast.DrawTree(parser.Create("CallSuper-src.txt"));
                break;

            // Call Supper - Detect and Solve
            case "12":
                javaParser = parser.Create("CallSuper-src.txt");
                CallSuperDetecting callSuperDetector = new CallSuperDetecting();
                boolean result = callSuperDetector.detector(javaParser);
                if(result){
                    System.out.println("Do you want to refactor detected 'Call Super' anti-patterns?(y/n)");
                    switch (scanner.next()) {
                        case "y":
                            System.out.println("Wait to refactoring...");
                            javaParser = parser.Create("CallSuper-src.txt");
                            CallSuperSolving callSuperSolver = new CallSuperSolving();
                            callSuperSolver.detectorResult = callSuperDetector.classInfo;
                            callSuperSolver.solve(javaParser);
                            Desktop desktop = Desktop.getDesktop();
                            desktop.open(new File("Call Super Output.java"));
                        case "n":
                            System.exit(0);
                    }
                }
                break;

            // Constant Interface - AST
            case "21":
                ast.DrawTree(parser.Create("ConstantInterface-src.txt"));
                break;

            // Constant Interface - Detect and Solve
            case "22":
                javaParser = parser.Create("ConstantInterface-src.txt");
                ConstantInterfaceDetecting detector2 = new ConstantInterfaceDetecting();
                ConstantInterfaceSolving solver2 = new ConstantInterfaceSolving();
                Map<String, List<String>> detectorResult = detector2.detector(javaParser);

                if(detectorResult.size() > 0){
                    for (Map.Entry<String, List<String>> item : detectorResult.entrySet()) {
                        List<String> itemValues = item.getValue();
                        for (int i = 0; i < itemValues.size(); i++) {
                            String[] values = itemValues.get(i).split(",");
                            if (values[1].equals("true")) {
                                solver2.trueValuesInterfaceList.add(values[0]);
                            }
                        }
                    }
                    if (solver2.trueValuesInterfaceList.size() > 0) {
                        System.out.println("Do you want to refactor detected 'Constant Interface' anti-patterns?(y/n)");
                        switch (scanner.next()) {
                            case "y":
                                System.out.println("Wait to refactoring...");
                                javaParser = parser.Create("ConstantInterface-src.txt");
                                solver2.detectorResult = detectorResult;
                                solver2.solve(javaParser);
                                Desktop desktop = Desktop.getDesktop();
                                desktop.open(new File("Constant Interface Output.java"));

                            case "n":
                                System.exit(0);
                        }
                    }
                }
                else{
                    System.out.println(ANSI_YELLOW + "None of the interfaces have constants!" + ANSI_RESET);
                    System.out.println(ANSI_YELLOW + "Class with implementing interface not found!" + ANSI_RESET);
                }
                break;
        }
    }
}


