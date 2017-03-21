package LexicalAnalyzer;


/**
 * [Class overview]
 * This class intends to store both token number and lexeme at same time.
 * It has 3 ways to accept a lexeme variable.
 * It also override the toString() method in order to make this class easier to output.
 * @author Chengzhi Hu
 * @since Jan.23 2017
 * @category For CS4110 Assignment 2 - Lexical Analyzer - As the token structure class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 */
public class Token {

	private int tokenNumber;
	private String lexeme;

	
	public Token(int tokenNumber,String lexeme)
	{
		this.tokenNumber=tokenNumber;
		this.lexeme=lexeme;
	}
	
	
	//For a no parameter constructor, the default value of token number will be -1.
	//Warning: -1 is not a legal token number. It's only meaningful for debugging.
	public Token()
	{
		this(-1,"");
	}
	
	
	public int getTokenNumber() {
		return this.tokenNumber;
	}


	
	public void setTokenNumber(int tokenNumber) {
		this.tokenNumber = tokenNumber;
	}


	
	public String getLexeme() {
		return this.lexeme;
	}


	
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	
	public void setLexeme(char lexeme) {
		this.lexeme = String.valueOf(lexeme);
	}
	
	@Override
	public String toString()
	{
		String str=this.getTokenNumber()+"\t"+this.getLexeme();
		return str;
	}
	

}
