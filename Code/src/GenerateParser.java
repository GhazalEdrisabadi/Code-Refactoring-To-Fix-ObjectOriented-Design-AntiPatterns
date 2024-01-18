import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class GenerateParser {
    public JavaParser Create(String fileName) throws IOException, URISyntaxException {
        File root = new File(Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("")).toURI());
        File resource = new File(root, fileName);
        CharStream inputStream = CharStreams.fromFileName(resource.getPath());
        JavaLexer lexer = new JavaLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        return new JavaParser(commonTokenStream);
    }
}
