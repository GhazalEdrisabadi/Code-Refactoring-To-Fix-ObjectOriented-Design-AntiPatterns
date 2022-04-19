using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Tree;
using System.IO;

namespace Final_Project
{
    class Program
    {
        static void Main(string[] args)
        {
            StreamWriter w1 = new StreamWriter(@"C:\Javalib\test.txt");
            StreamWriter w2 = new StreamWriter(@"C:\Javalib\error.txt");
            String input = "int";
            ICharStream stream2 = new AntlrInputStream(input);
            ITokenSource lexer = new CSharpLexer(stream2);
            ITokenStream tokens = new CommonTokenStream(lexer);
            CSharpParser parser = new CSharpParser(tokens);
            parser.BuildParseTree = true;
            CSharpParser.LiteralContext result = parser.literal();
            Console.Write(result.ToStringTree());
            Console.ReadKey();
        }
    }
}
