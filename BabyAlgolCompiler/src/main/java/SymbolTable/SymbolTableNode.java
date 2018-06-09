package SymbolTable;

/**
 * [Class Overview]:
 * This is the basic store unit of the symbol table.
 * With the increase demands for lexical analyzer, this class may expand.
 * For now, it stores 4 types of information.
 * 1. Hash code;
 * 2. Identifier name(variable name);
 * 3. Type;
 * 4. Offset.
 * <p>
 * Hash code is automatically generated when the identifier name is stored in this class.
 * That means, this hash code is also means the identifier name's hash code.
 *
 * @author Chengzhi Hu
 * @category For CS4110 Assignment 1 - Symbol Table - As the basic symbol store unit.
 * Also for Assignment 4 - Parser with ST - As part of Symbol Table class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 * @since Jan.22.2017
 * Last modified on Feb.15.2017
 */
public class SymbolTableNode {


  //1. Hash code
  private int hashValue;


  //2. Identifier name(variable name)
  private String lexeme;


  //3. Type
  private String type;


  //4. Offset position
  private int offset;


  public SymbolTableNode() {
    this.hashValue = -1;
    this.lexeme = new String();
    this.type = new String();
    this.setOffset(0);
  }


  public SymbolTableNode(char type, String lexeme, int offset) {
    this();
    this.setType(type);
    this.setLexemeAndHashValue(lexeme);
    this.offset = offset;
  }


  public String getLexeme() {
    return lexeme;
  }


  //Set the variable name. And also the hash code at same time.
  public void setLexemeAndHashValue(String lexeme) {
    this.lexeme = lexeme;
    this.hashValue = lexeme.hashCode();
  }


  public int getHashValue() {
    return hashValue;
  }


  @Override
  public String toString() {
    String summary = new String();
    //summary=this.getLexeme()+"\n";
    summary = this.getLexeme() + "\t" + this.getType() + "\t" + this.getOffset() + "\n";
    return summary;
  }


  //May have to expand this method if the class is going to expand.
  //For now, just judge by if the 2 class variables are initial values.
  public boolean isEmpty() {
    return ((this.getLexeme().isEmpty()) || (this.hashValue == -1));
  }


  public int getOffset() {
    return this.offset;
  }


  public void setOffset(int offset) {
    this.offset = offset;
  }


  public String getType() {
    return type;
  }


  public char getTypeInChar() {
    return type.charAt(0);
  }

  public void setType(char type) {
    switch (type) {
      case 'i':
      case 'I':
        this.type = "INTEGER";
        break;
      case 's':
      case 'S':
        this.type = "STRING";
        break;
      case 'l':
      case 'L':
        this.type = "LOGICAL";
        break;
    }
  }
}
