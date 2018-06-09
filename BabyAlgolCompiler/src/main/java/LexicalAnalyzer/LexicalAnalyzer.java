package LexicalAnalyzer;

import java.util.Arrays;
import java.util.Scanner;


/**
 * [Class Overview]
 * Theory used : DFA, Binary search.
 * Data structure used : Sorted simple array.
 * <p>
 * [Important Things]
 * a. It's allowed to have multiple lines of COMMENT;
 * b. It's also allowed to have multiple lines of Strings;
 * c. It's not allowed to have a double quote inside a string.
 * d. It's not case sensitive. All identifiers & keywords will be converted into upper case. But not literals.
 * <p>
 * [Token Number Definition]
 * -2 - EOF$(Represent for end of file)
 * (-1 - Default token number, illegal of course.)
 * 1 - Identifiers
 * 2 - Literals
 * 3 - Types
 * 4 - Addition operators(+,-,"OR")
 * 5 - Multiply operators(*,/,"DIV","REM","AND")
 * 6 - Relational operators(=,!=,<,>)
 * 7 - "BEGIN"
 * 8 - "END"
 * 9 - "IF"
 * 10 - "THEN"
 * 11 - "WHILE"
 * 12 - "DO"
 * 13 - "READ" | "WRITE" | "WRITELN"
 * 14 - "("
 * 15 - ")"
 * 16 - ";"
 * 17 - "!"
 * 18 - "."
 * 19 - ":="
 * <p>
 * HINT: "COMMENT" is not a token, anything between it and ";" will be ignored.
 *
 * @author Chengzhi Hu
 * @category For CS4110 Assignment 2 - Lexical Analyzer - As a primary class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 * @since Jan.23 2017
 */
public class LexicalAnalyzer {

  //Array for key words. Sorted array.
  public static final String[] KEYWORDS = {"AND", "BEGIN", "DIV", "DO", "END",
      "FALSE", "IF", "INTEGER", "LOGICAL", "OR",
      "READ", "REM", "STRING", "THEN", "TRUE",
      "WHILE", "WRITE", "WRITELN"};


  //Array for key word's token numbers
  //its values is matches with KEYWORDS array.
  public static final int[] KEYWORDSTOKENS = {5, 7, 5, 12, 8,
      2, 9, 3, 3, 4,
      13, 5, 3, 10, 2,
      11, 13, 13};


  //A string variable to store potential lexical error messages.
  private String lexicalErrorMessages;


  //A buffer reader to read the file and get a char.
  private BufferReader bufferReader;


  public LexicalAnalyzer(Scanner inFileScanner) {
    this.lexicalErrorMessages = new String();
    bufferReader = new BufferReader(inFileScanner);
  }


  //Core method for Lexical Analyzer.
  //Use DFA theory to analyze the input char.
  public Token findToken() {
    char a = bufferReader.getChar();
    Token token = new Token();
    while (Character.isWhitespace(a))//White space, ignore and read next char.
      a = bufferReader.getChar();
    if (Character.isLetter(a))//Letter char goes to idOrKeywordToken() method. Maybe identifier, keyword, comment or EOF.
      return idOrKeywordToken(a);
    else if (a == '0')//'0' treat as a single literal token
    {
      token.setTokenNumber(2);
      token.setLexeme('0');
      return token;
    } else if (Character.isDigit(a))//Other digits goes to digitToken() method.
      return digitToken(a);
    else if (a == ':')//Every ':' should follow a '='.
    {
      return valueSignToken(a);
    } else if (a == '!')//Maybe ! or !=. Depends on next char.
      return booleanNotToken();
    else if (a == '\"')//Starting with a double quote, must end with a double quote to finish the DFA.
      return stringValueToken();
    else//Other case are quite simple. Just set the char as the lexeme, and set different token number.
    {
      token.setLexeme(a);
      switch (a) {
        case '+':
        case '-':
          token.setTokenNumber(4);
          return token;
        case '*':
        case '/':
          token.setTokenNumber(5);
          return token;
        case '(':
          token.setTokenNumber(14);
          return token;
        case ')':
          token.setTokenNumber(15);
          return token;
        case ';':
          token.setTokenNumber(16);
          return token;
        case '.':
          token.setTokenNumber(18);
          return token;
        case '=':
        case '<':
        case '>':
          token.setTokenNumber(6);
          return token;
        default:
          recordLexicalError(a);//If not a legal char, record the error and continue the DFA.
          return findToken();
      }
    }
  }


  //Once meet a char start with letter, call this method.
  //Loop until meet a char is not a digit, letter or '_'. And trace back a char.
  //If it's an EOF$, return EOF_tok.
  //If it's a comment, skip everything until meet a ';'. Then continue find token.
  //Else go to keyWordsJudge() method to let it determines if the string is a keyword or identifier.
  private Token idOrKeywordToken(char a) {
    String letter = new String();
    do {
      letter += a;
      a = bufferReader.getChar();
    } while (Character.isLetter(a) || Character.isDigit(a) || (a == '_'));
    bufferReader.backAChar();
    letter = letter.toUpperCase();
    if (letter.equals("EOF")) {
      if (bufferReader.getChar() == '$')
        return new Token(-2, "EOF$");
      else {
        bufferReader.backAChar();
        return new Token(1, "EOF");
      }
    } else if (letter.equals("COMMENT"))
      return skipComment();
    else
      return keyWordsJudge(letter);
  }


  //Skip everything until meet the first ';'.
  //Or, if meet the EOF, report an error and return EOF_tok.
  private Token skipComment() {
    char a;
    while (true) {
      a = bufferReader.getChar();
      if (a == ';')
        return findToken();
      if (a == 'E')
        if (bufferReader.getChar() == 'O')
          if (bufferReader.getChar() == 'F')
            if (bufferReader.getChar() == '$') {
              this.recordLexicalError('$');
              return new Token(-2, "EOF$");
            } else
              bufferReader.backAChar();
          else
            bufferReader.backAChar();
        else
          bufferReader.backAChar();

    }
  }


  //If meets a non-zero digit, call this method.
  //Loop until meet the char is not a digit.
  private Token digitToken(char a) {
    String digit = new String();
    do {
      digit += a;
      a = bufferReader.getChar();
    } while (Character.isDigit(a));
    bufferReader.backAChar();
    return new Token(2, digit);
  }


  //Call this method when meet a '!'.
  //Further judge whether it's just a '!' which means boolean not.
  //Or it followed by a '=' which will be a relational operator not equals.
  private Token booleanNotToken() {
    Token token = new Token();
    if (bufferReader.getChar() == '=') {
      token.setTokenNumber(6);
      token.setLexeme("!=");
      return token;
    } else {
      bufferReader.backAChar();
      token.setTokenNumber(17);
      token.setLexeme("!");
      return token;
    }
  }


  //If meet a ':', call this method.
  //Every ':' should immediately followed by a '='. Then treat ":=" as the ASSIGN_tok.
  //If the next char is not '=', then it's illegal, record the error and go back a char.
  private Token valueSignToken(char a) {
    Token token = new Token();
    if (bufferReader.getChar() == '=') {
      token.setTokenNumber(19);
      token.setLexeme(":=");
      return token;
    } else {
      recordLexicalError(a);
      bufferReader.backAChar();
      return findToken();
    }
  }


  //Call this method once meets a quote sign.
  //Push everything into the variable str until meet another quote sign.
  //No double quote sign in the inner str allowed.
  private Token stringValueToken() {
    String str = new String("\"");
    Token token = new Token();

    char a = bufferReader.getChar();
    while (a != '\"') {
      str += a;
      a = bufferReader.getChar();
    }
    str += a;
    token.setTokenNumber(2);
    token.setLexeme(str);
    return token;
  }


  //Judge whether the word is a key word.
  //Use binary search in global variable KEYWORDS.
  //If it isn't, it will set the token as an identifier.
  //Otherwise, if it is, it will return proper token number and token lexeme.
  private Token keyWordsJudge(String letter) {
    //Do binary search
    //Make sure the backup word is all upper case.
    //The position will return as an integer.
    int location = Arrays.binarySearch(LexicalAnalyzer.KEYWORDS, letter);
    Token tokenForJudge = new Token();
    if (location >= 0) {
      tokenForJudge.setTokenNumber(LexicalAnalyzer.KEYWORDSTOKENS[location]);
      tokenForJudge.setLexeme(LexicalAnalyzer.KEYWORDS[location]);
    } else {
      tokenForJudge.setTokenNumber(1);
      tokenForJudge.setLexeme(letter);
    }
    return tokenForJudge;
  }


  //If error occurs, record the line and position in global variable lexicalErrorMessages.
  //The message will shown like this:
  //At Line 1 Column 2 : Illegal char ':'
  private void recordLexicalError(char a) {
    String prompt = "At Line " + (bufferReader.getCurrentLineNumber()) + " Column " + (bufferReader.getBufferPos() + 1) + " : Illegal char \'" + a + "\' \n";
    this.lexicalErrorMessages += prompt;
  }


  //Like a getter of the lexical error variable.
  //But if the lexical error variable has no error stored, it will return string "No error occurs".
  public String printLexicalError() {
    String str = this.lexicalErrorMessages;
    if (str.isEmpty())
      str = "No lexical error occurs.";
    return str;
  }


  //May call this in Parser to indicate the error occurred line number.
  public int getCurrentLineNumber() {
    return this.bufferReader.getCurrentLineNumber();
  }
}
