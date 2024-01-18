import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    public String parentClassName;
    public String childClassName;
    public List<JavaParser.ClassBodyDeclarationContext> overriddenMethods;
    public List<JavaParser.ClassBodyDeclarationContext> methodsWithAntipatern;

    public ClassInfo(){

    }
    public ClassInfo(String parent, String child, List<JavaParser.ClassBodyDeclarationContext> overridden, List<JavaParser.ClassBodyDeclarationContext> antipatern){
        this.parentClassName = parent;
        this.childClassName = child;
        this.overriddenMethods = overridden;
        this.methodsWithAntipatern = antipatern;

    }

    public List<String> getAllParents(List<ClassInfo> info){
        List<String> parents = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            if(!parents.contains(info.get(i).parentClassName)){
                parents.add(info.get(i).parentClassName);
            }
        }
        return parents;
    }

    public List<JavaParser.ClassBodyDeclarationContext> getApMethods(String parent, List<ClassInfo> info){
        List<JavaParser.ClassBodyDeclarationContext> ApMethods = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            if(info.get(i).parentClassName.equals(parent)){
                ApMethods.addAll(info.get(i).methodsWithAntipatern);
            }
        }
        return ApMethods;
    }

    public List<JavaParser.ClassBodyDeclarationContext> getApMethodsFromChild(String child, List<ClassInfo> info){
        List<JavaParser.ClassBodyDeclarationContext> ApMethods = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            if(info.get(i).childClassName.equals(child)){
                ApMethods.addAll(info.get(i).methodsWithAntipatern);
            }
        }
        return ApMethods;
    }

    public List<String> getApMethodNames(String child, List<ClassInfo> info){
        List<JavaParser.ClassBodyDeclarationContext> ApMethods = new ArrayList<>();
        List<String> ApMethodNames = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            if(info.get(i).childClassName.equals(child)){
                ApMethods = info.get(i).methodsWithAntipatern;
                for (int j = 0; j < ApMethods.size(); j++) {
                    ApMethodNames.add(ApMethods.get(j).memberDeclaration().methodDeclaration().identifier().getText());
                }
            }
        }
        return ApMethodNames;
    }
}
