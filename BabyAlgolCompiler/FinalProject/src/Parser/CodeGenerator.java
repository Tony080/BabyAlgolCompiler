package Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * [Class Overview]
 * This class is an i/o helper class. It creates a file and write intermediate code into it.
 * Must bind with a parser in order to generate proper code. 
 * 
 * [Label Definition]
 * 1. CR - As Carriage Return. Actually is "\n". 
 * 2. TL - As logical TRUE.
 * 3. FL - As logical FALSE.
 * 4. SLabel - Store Strings in the label starts from 0. So the first legal SLabel is SLabel0.
 * 				Example: SLabel0 .asciiz "This is a string"
 * 5. EndIfLabel - Record the position once the if block meets its end. So if the statement is false,
 * 					some parts will be skipped. And the location it skips to is the EndIfLabel.
 * 					Also starts from 0. So the first legal EndIfLabel is EndIfLabel0.
 * 6. TopWhileLabel - Mark the start position of loop statement. The first legal TopWhileLabel is TopWhileLabel0.
 * 7. EndWhileLabel - Mark the end position of loop statement. The first legal EndWhileLabel is EndWhileLabel0.
 * @author Chengzhi Hu
 * @since Feb.22.2017
 * @category For CS4110 Assignment 5 - Code Generation - As code generate & file input class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 *
 */
public class CodeGenerator {

	//Generated label name can be modified here.
	public static final String STRING_LABEL="SLabel";
	public static final String END_IF_LABEL="EndIfLabel";
	public static final String TOP_WHILE_LABEL="TopWhileLabel";
	public static final String END_WHILE_LABEL="EndWhileLabel";
	
	
	private FileWriter codeWriter;
	
	
	//Open file for codeWriter, and delete generated code if any error occurred in code generation.
	private File file;
	
	
	//A string buffer to store all string labels which will write to buffer nearly at the 
	//end of MIPS code.
	private String strLabels;
	
	
	//Counter to store how many string labels have been stored.
	private int strLabelCounts;

	
	private int endIfLabelCounts;
	
	private int topWhileCounts;

	
	private int endWhileCounts;
	
	
	//If code not OK, delete the code file(*.s) after code generation.
	private boolean codeOK;
	
	
	
	public CodeGenerator(String fileName) throws IOException
	{
		this.file=new File(fileName);
		this.codeWriter=new FileWriter(file);
		this.strLabels=new String();
		this.strLabelCounts=0;
		this.endIfLabelCounts=0;
		this.topWhileCounts=0;
		this.endWhileCounts=0;
		this.codeOK=true;
	}
	
	
	
	//Call this function only at the beginning of the programStart routine in parser.
	public void writeProlog() throws IOException
	{
		this.codeWriter.write("#Prolog:\n");
		this.codeWriter.write(".text\n");
		this.codeWriter.write(".globl main\n");
		this.codeWriter.write("main:\n");
		this.codeWriter.write("move $fp $sp\n");
		//this.codeWriter.write("la $a0 ProgStart\n");  Debug use
		//this.codeWriter.write("li $v0 4\n");
		//this.codeWriter.write("syscall\n");
		this.codeWriter.write("#End of Prolog\n");
	}
	
	
	
	
	//Call this function only at the end of programStart routine in parser.
	public void writePostlog() throws IOException
	{
		this.codeWriter.write("#PostLog:\n");
		//this.codeWriter.write("la $a0 ProgEnd\n");  Debug use
		//this.codeWriter.write("li $v0 4\n");
		//this.codeWriter.write("syscall\n");
		this.codeWriter.write("li $v0 10\n");
		this.codeWriter.write("syscall\n");
		this.codeWriter.write(".data\n");
		genAllLabels();
		genASCIIZLabel("CR","\\n");//Generate CR label
		genASCIIZLabel("TL","TRUE");
		genASCIIZLabel("FL","FALSE");

		this.codeWriter.close();
		if(!codeOK)
			deleteCode();
	}
	
	
	
	
	//Call this function multi-times to form a really MIPS program.
	public void codeGen(String param) throws IOException
	{
		this.codeWriter.write(param+"\n");
	}
	
	
	
	//Generate a single line of comment in MIPS
	public void genComment(String comment) throws IOException
	{
		this.codeWriter.write("# "+comment+"\n");
	}
	
	
	
	private void genAllLabels() throws IOException
	{
		this.codeWriter.write(strLabels);
	}
	
	
	
	//Label generation for default necessary strings in program.
	//Such as TRUE/FALSE, CR, and etc..
	private void genASCIIZLabel(String labelName,String contents) throws IOException
	{
		this.codeWriter.write(labelName+":\t.asciiz \""+contents+"\"\n");
	}
	
	

	//For STRING variable label gen.
	//Not generate immediately, but store information in memory. Not generated until 
	//at the end of code generation. Thus, these labels could all stay with other string labels
	//in .data section in one piece.
	public String bufferStrLabel(String contents) throws IOException
	{
		int counts=strLabelCounts++;
		String currentLabelName=STRING_LABEL+counts;
		this.strLabels+=currentLabelName+":\t.asciiz \""+contents+"\"\n";
		return currentLabelName;
	}
	
	
	
	public String genNextEndIfLabel()
	{	
		return END_IF_LABEL+this.endIfLabelCounts++;
	}
	
	
	
	public String genNextTopWhileLabel()
	{
		return TOP_WHILE_LABEL+this.topWhileCounts++;
	}
	
	
	
	public String genNextEndWhileLabel()
	{
		return END_WHILE_LABEL+this.endWhileCounts++;
	}
	
	
	
	//If non fatal error occurred in parser, call this method.
	public void discardCode()
	{
		this.codeOK=false;
	}
	
	
	//If fatal error occurred in parser, call this method to terminate code generation immediately.
	public void deleteCode()
	{
		this.file.delete();
	}
}
