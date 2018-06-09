import LexicalAnalyzer.LexicalAnalyzer;
import Parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * [Class Overview]
 * Entrance of the Baby Algol Compiler(BAC).
 * Un-implement functions:
 * 1. No functions in this compiler.
 * 2. No string value assign in this compiler.
 * 3. No REAL type in this compiler.
 * <p>
 * TODO - modify the parser, to accept functions maybe later.
 *
 * @author Chengzhi Hu
 * @category For CS4110 Final project - As the entrance of the compiler.
 * @since Mar.14.2017
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 */
public class BabyAlgolCompiler {

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Invalid input/output file parameter!");
      System.exit(0);
    }
    String inFileName = args[0];
    String outFileName = args[1];

    Scanner inFileScanner = new Scanner(new File(inFileName));
    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inFileScanner);
    Parser parser = new Parser(lexicalAnalyzer, outFileName);
    parser.findToken();//Start lexical Analyzer
    parser.programStart();
    int finalTokenNum = parser.getToken().getTokenNumber();
    if (finalTokenNum != -2)//If the last token isn't EOF_tok, it's a fatal error.
      parser.fatalError(parser.getToken().getLexeme());
    System.out.println("\nEntire ST:");
    System.out.println("Lexeme\tType\tOffset");
    System.out.println(parser.getSymbolTable().printEntireTable());
    System.out.println("\nNon fatal errors:");
    System.out.println(parser.printNonFatalError());
    System.out.println("\nLexical errors:");
    System.out.println(parser.getLexicalError());
  }


}
