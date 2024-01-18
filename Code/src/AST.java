
import org.antlr.v4.gui.TreeViewer;
import java.util.Arrays;

public class AST {
    public void DrawTree(JavaParser parser){
        JavaParser.CompilationUnitContext parserContext = parser.compilationUnit();
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()),parserContext);
        viewer.open();
    }
}

