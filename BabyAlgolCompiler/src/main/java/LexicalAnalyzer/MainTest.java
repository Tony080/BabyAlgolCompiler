package LexicalAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * [Class overview]
 * This class is the test entrance of the lexical analyzer class.
 * It gets 1 input file and 2 output files.
 * Once it gets a token, print it on screen and append it to output file.
 * Once it read a new line(in memory operation, but not the file operation)
 * print the original code with line number on another output file called "echo" here.
 * After the entire file has been analyzed, it will print all errors to the echo output file.
 *
 * @author Chengzhi Hu
 * @category For CS4110 Assignment 2 - Lexical Analyzer - As the tester class.
 * @since Jan.23 2017
 */
public class MainTest {

  public static final String inputFileName = "D:\\CSUEB\\CS4110\\FinalProject\\test3.txt";
  public static final String outputFileName = "D:\\CSUEB\\CS4110\\FinalProject\\out3.txt";
  public static final String echoFileName = "D:\\CSUEB\\CS4110\\FinalProject\\echo3.txt";

  public static void main(String[] args) throws Exception {
    Scanner scannerForInputFile = new Scanner(new File(MainTest.inputFileName));
    Scanner scannerForEcho = new Scanner(new File(MainTest.inputFileName));
    FileWriter outputFileWriter = new FileWriter(MainTest.outputFileName, true);
    FileWriter echoFileWriter = new FileWriter(MainTest.echoFileName, true);
    Token token = new Token();
    LexicalAnalyzer la = new LexicalAnalyzer(scannerForInputFile);


    //read the first line to the echo output file.
    int i = 1;
    while (scannerForEcho.hasNextLine())
      echoFileWriter.write((i++) + "\t" + scannerForEcho.nextLine() + "\n");

    int j = 0;
    do {
      if (j++ % 3 == 0)
        System.out.println();
      token = la.findToken();
      System.out.print(token.toString() + "\t\t");
      outputFileWriter.write(token.toString());
      outputFileWriter.write("\n");
    } while (token.getTokenNumber() != -2);

    //After the entire file read, print all errors to echo output file.
    echoFileWriter.write(la.printLexicalError());
    System.out.println("\n\n" + la.printLexicalError());
    scannerForInputFile.close();
    scannerForEcho.close();
    outputFileWriter.close();
    echoFileWriter.close();
  }
}
