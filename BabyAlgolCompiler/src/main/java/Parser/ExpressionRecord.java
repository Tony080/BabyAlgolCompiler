package Parser;

/**
 * [Class Overview]
 * <p>
 * A class to store the literal token's position and type in the stack.
 * This class is the simplified version of SymbolTableNode class.
 * All things store in this class means it will not store in the SymbolTable.
 *
 * @author Chengzhi Hu
 * @category For Assignment 5&6 - Code Generation - As literal position and type store class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 * @since Feb.27.2017
 */
public class ExpressionRecord {

  //1. Type of temporary variable
  //Possible types:
  //a. I - INTEGER
  //b. L - LOGICAL
  //c. S - STRING
  //d. E - ERROR
  //e. U - UNDEFIEND
  private char type;


  //2. Memory stack location
  private int offset;

  private String labelName;

  public ExpressionRecord() {
    this.type = 'U';
    this.offset = 1;
    this.setLabelName(new String());
  }


  //Copy constructor. Treat the parameter's record as source.
  //And the newly created expression record as destination.
  public ExpressionRecord(ExpressionRecord source) {
    this.type = source.getType();
    this.offset = source.getOffset();
    this.labelName = source.getLabelName();
  }

  public char getType() {
    return type;
  }


  public void setType(char type) {
    this.type = type;
  }


  public int getOffset() {
    return offset;
  }


  public void setOffset(int offset) {
    this.offset = offset;
  }


  public String getLabelName() {
    return labelName;
  }


  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }
}
