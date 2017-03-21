package Parser;

import java.io.IOException;
import LexicalAnalyzer.*;
import SymbolTable.*;

/**
 * [Class Overview]
 * Theory used: BNF.
 * Need class "LexicalAnalyzer" and class "Token" to return tokens.
 * Update on Feb.15.2017: Also need class "SymbolTable" and class "SymbolTableNode" to store identifiers.
 * Update on Feb.27.2017: Also need class "ExpressionRecord" to store literal token's information.
 * 
 * [Routine definitions]: - 31 routines & 17 nonterminals.
 * 1. programStart -> blockState '.'
 * 2. blockState -> BEGIN_tok statements END_tok
 * 3 & 4. statements -> singleStatement ';' statements | epsilon
 * 5. declare -> TYPE_tok ID_tok
 * 6 - 12. singleStatement -> assignState | declare | blockState | ifState |
 * 			loopState | ioState | epsilon
 * 13. assignState -> idReference ASSIGN_tok expression
 * 14. ifState -> IF_tok expression THEN_tok singleStatement
 * 15. loopState -> WHILE_tok expression DO_tok singleStatement
 * 16 & 17. ioState -> READ_tok '(' idReference ')' | WRITE_tok '(' expression ')'
 * 18. expression -> term expPrime
 * 19 & 20. expPrime -> ADD_operator_tok term expPrime | epsilon
 * 21. term -> relateFactor termPrime
 * 22 & 23. termPrime -> MUL_operator_tok relateFactor termPrime | epsilon 
 * 24. relateFactor -> factor factorPrime
 * 25 & 26. factorPrime -> RELATE_operator_tok factor | epsilon
 * 27-30. factor -> idReference | LITERAL_tok | '!' factor | '(' expression ')' 
 * 31. idReference -> ID_tok
 * 
 * 
 * @author Chengzhi Hu
 * @since Feb.07.2017
 * Last modified on Feb.27.2017
 * @category For CS4110 Assignment 3 - Parser - As a primary class.
 * Also for Assignment 4 - Parser with ST - As a primary function class.
 *  Also for Assignment 5&6 - Code Generation - As a primary function class.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 */
public class Parser {

	//Lexical Analyzer for finding tokens from file.
	private LexicalAnalyzer la;
	
	
	
	//A token to store current token returned from Lexical Analyzer.
	private Token token;
	
	
	
	
	//A Symbol Table to store all identifiers.
	private SymbolTable st;
	
	
	
	//An integer used to count how many routines have been printed on 1 line.
	private int printCounter;
	
	
	
	//A string to store all non fatal errors.
	private String nonFatalError;
	
	
	
	//A file writer to generate MIPS code.
	private CodeGenerator codeGenerator;
	

	
	//Store 1 expression record. Often changes. So, may need to pass to local variable in 
	//routine "expPrime" and "termPrime".
	//This record always stores the expression on the rhs. Or store the result expression
	//means(a<b, 3+5)will be stored in this variable.
	//If expression record on the lhs is needed, create a local variable in the specified routine.
	private ExpressionRecord expressionRecord;
	
	
	private boolean debugMode;
	public Parser(LexicalAnalyzer la,String outFileName) throws IOException
	{
		this.la=la;
		this.token=new Token();
		this.printCounter=0;
		this.st=new SymbolTable();
		this.nonFatalError=new String();
		this.codeGenerator=new CodeGenerator(outFileName);
		this.expressionRecord=new ExpressionRecord();
		this.debugMode=false;
	}
	
	
	
	//1. programStart -> blockState '.'
	public void programStart() throws IOException
	{
		print(1);
		codeGenerator.writeProlog();//Generate prolog code.
		blockState();
		match(18);//'.' TOKEN
		codeGenerator.writePostlog();//Generate postlog code.
	}
	
	
	
	//2. blockState -> BEGIN_tok statements END_tok
	private void blockState() throws IOException
	{
		int locOffsetCopy;
		print(2);
		match(7);//BEGIN TOKEN
		st.pushHashTable();//Meet BEGIN_tok, push the scope.
		locOffsetCopy=st.getOffset();
		statements();
		match(8);//END TOKEN
		st.popHashTable();//Meet END_tok, pop the scope.
		st.setOffset(locOffsetCopy);
	}
	
	
	
	//3. statements -> singleStatement ';' statements | epsilon
	private void statements() throws IOException
	{
		if(isStatement())
		{
			print(3);
			singleStatement();
			match(16);//';' TOKEN
			statements();
		}
		else
			print(4);
	}
	
	
	
	//4. declare -> TYPE_tok ID_tok
	private void declare()
	{
		print(5);
		char type=token.getLexeme().charAt(0);//Get type as a char. 'L' as LOGICAL, 'I' as INTEGER and etc..
		match(3);//TYPE TOKEN. Eat the token.
		if(st.findInCurrentScope(token.getLexeme()).isEmpty())//If undeclared, insert it into symbol table.
			st.insert(type, token.getLexeme());
		else
			nonFatalError(token.getLexeme()+" tries to decalre twice.");//If declared, report a non fatal error.
		match(1);//ID TOKEN
	}
	
	
	
	//5. singleStatement -> assignState | declare | blockState | ifState |
	// 						loopState | ioState | epsilon
	private void singleStatement() throws IOException {
		switch(token.getTokenNumber())
		{
		case 1://ID TOKEN
			print(6);
			assignState();
			break;
		case 3://DECLARE TOKEN
			print(7);
			declare();
			break;
		case 7://BEGIN TOKEN
			print(8);
			blockState();
			break;
		case 9://IF TOKEN
			print(9);
			ifState();
			break;
		case 11://LOOP TOKEN
			print(10);
			loopState();
			break;
		case 13://IO TOKEN
			print(11);
			ioState();
			break;
		default:
			print(12);
			break;
		}
	}

	
	
	//6. assignState -> idReference ASSIGN_tok expression
	private void assignState() throws IOException 
	{
		print(13);
		idReference();//ID state
		
		//Call copy constructor. Copy the id's record as left record
		ExpressionRecord expressionRecord_Left=new ExpressionRecord(expressionRecord);
		
		match(19);//ASSIGN TOKEN
		expression();		
		
		assignStateCodeGen(expressionRecord_Left);
	}
	
	
	
	//7. ifState -> IF_tok expression THEN_tok singleStatement
	private void ifState() throws IOException
	{
		print(14);
		match(9);//IF TOKEN
		expression();
		//Code generation
		//If expression is not a LOGICAL type, report a non fatal error.
		if(expressionRecord.getType()!='L')
			nonFatalError("If occurred a logical error.");
		String endIfLabel=codeGenerator.genNextEndIfLabel();
		codeGenerator.codeGen("lw $t0 "+expressionRecord.getOffset()+"($fp)");
		codeGenerator.codeGen("beq $t0 $zero "+endIfLabel);
		
		match(10);//THEN TOKEN
		singleStatement();
		
		//Write end if label at the bottom of statements.
		codeGenerator.codeGen(endIfLabel+":");
	}
	

	
	//8. loopState -> WHILE_tok expression DO_tok singleStatement
	private void loopState() throws IOException
	{
		print(15);
		match(11);//WHILE TOKEN
		
		
		String topWhileLabel=codeGenerator.genNextTopWhileLabel();
		codeGenerator.genComment("Remember the start position for re-looping.");
		codeGenerator.codeGen(topWhileLabel+":");
		
		expression();
		
		//expression must return a LOGICAL type result.
		if(expressionRecord.getType()!='L')
			nonFatalError("While occurred a Logical error.");
		String endWhileLabel=codeGenerator.genNextEndWhileLabel();
		codeGenerator.genComment("Check the expression to determine whether to execute the loop.");
		codeGenerator.codeGen("beq $t0 $zero "+endWhileLabel);
		
		match(12);//DO TOKEN
		singleStatement();
		

		codeGenerator.genComment("Loop in fact is the \"j\" to a circular expression.");
		codeGenerator.codeGen("j "+topWhileLabel);
		codeGenerator.genComment("The loop's break point starts from below label.");
		codeGenerator.codeGen(endWhileLabel+":");
	}
	
	
	
	//9. ioState -> READ_tok '(' idReference ')' | WRITE_tok '(' expression ')'
	private void ioState() throws IOException
	{
		if(token.getLexeme().equals("READ"))//READ TOKEN
		{
			print(16);
			match(13);//READ TOKEN
			match(14);//'(' TOKEN
			idReference();//ID state
			match(15);//')' TOKEN
			
			read_TokCodeGen();
		}
		else if(token.getTokenNumber()==13)//WRITE TOKEN
		{
			print(17);
			//The difference between WRITE and WRITELN is just WRITELN is WRITE plus a carriage return.
			boolean carriageReturnFlag=false;
			if(token.getLexeme().equals("WRITELN"))
				carriageReturnFlag=true;
			
			match(13);//WRITE or WRITELN TOKEN
			match(14);//'(' TOKEN
			expression();
			match(15);//')' TOKEN
			
			write_TokCodeGen(carriageReturnFlag);
		}
		else
			fatalError(token.getLexeme());
	}

	

	//10. expression -> term expPrime
	private void expression() throws IOException
	{
		print(18);
		term();
		expPrime();
	}
	
	
	
	//11. expPrime -> ADD_operator_tok term expPrime | epsilon
	private void expPrime() throws IOException
	{
		if(token.getTokenNumber()==4)//ADD TOKEN
		{
			print(19);
			
			//Call copy constructor.
			//Record previous record as left record. And "expressionRecord" will
			//represent for right record after term and expPrime routines.
			ExpressionRecord expressionRecord_Left=new ExpressionRecord(expressionRecord);
			//Record the specific operator it will have.
			String operator=token.getLexeme();
			
			match(4);//Eat operator token
			term();
			expPrime();
			
			//After met the right literal token, do code generation.
			expPCodeGen(operator, expressionRecord_Left);
		}
		else
			print(20);
	}
	
	
	
	//12. term -> relateFactor termPrime
	private void term() throws IOException
	{
		print(21);
		relateFactor();
		termPrime();
	}
	
	
	
	//13. termPrime -> MUL_operator_tok relateFactor termPrime | epsilon 
	private void termPrime() throws IOException
	{
		
		if(token.getTokenNumber()==5)//MULTI TOKEN
		{
			print(22);
			
			//Call copy constructor.
			//Record previous record as left record. And "expressionRecord" will
			//represent for right record after relateFactor and termPrime routines.
			ExpressionRecord expressionRecord_Left=new ExpressionRecord(expressionRecord);
	
			//Record the specific operator it will have.
			String operator=token.getLexeme();
			
			match(5);
			relateFactor();
			termPrime();
			
			//After met the right literal token, do code generation.
			termPCodeGen(operator, expressionRecord_Left);
		}
		else
			print(23);
	}
	
	
	
	//14. relateFactor -> factor factorPrime
	private void relateFactor() throws IOException
	{
		print(24);
		factor();
		factorPrime();
	}
	
	
	
	//15. factorPrime -> RELATE_operator_tok factor | epsilon
	private void factorPrime() throws IOException
	{

		if(token.getTokenNumber()==6)//RELATION TOKEN
		{
			print(25);
			//Call copy constructor. Copy the id's record as left record
			ExpressionRecord expressionRecord_Left=new ExpressionRecord(expressionRecord);
			String operator=token.getLexeme();
			
			match(6);
			factor();
			
			//Generate all code at the end of this routine.
			factorPCodeGen(operator, expressionRecord_Left);
		}
		else
			print(26);
	}
	
	
	
	//16. factor -> idReference | LITERAL_tok | '!' factor | '(' expression ')' 
	private void factor() throws IOException
	{
		switch(token.getTokenNumber())
		{
		case 1://ID TOKEN
			print(27);
			idReference();
			break;
		case 2://LITERAL TOKEN
			print(28);
			literalCodeGen();
			match(2);
			break;
		case 17://'!' TOKEN
			print(29);
			match(17);
			factor();
			
			//check type & code generation.
			if(expressionRecord.getType()!='L')
				nonFatalError("'!' can only be used with a LOGICAL type.");
			codeGenerator.codeGen("lw $t0 "+expressionRecord.getOffset()+"($fp)");
			codeGenerator.codeGen("xor $t0 $t0 1");
			codeGenerator.codeGen("sw $t0 "+expressionRecord.getOffset()+"($fp)");
			break;
		case 14://'(' TOKEN
			print(30);
			match(14);
			expression();
			match(15);
			break;
		default:
			fatalError(token.getLexeme());
		}
	}
	
	//17. idReference -> ID_tok
	private void idReference()
	{
		print(31);
		
		//Read the symbol table, check the variable.
		SymbolTableNode tempNode=st.findInOpenScopes(token.getLexeme());
		if(tempNode.isEmpty())//If not found in any open scope. Report a non fatal error.
			nonFatalError(token.getLexeme()+" is refered without declaration.");
		else//If found, record the id from last open scope 's type and location(offset).
		{
			this.expressionRecord.setType(tempNode.getTypeInChar());
			this.expressionRecord.setOffset(tempNode.getOffset());
		}
		match(1);//ID TOKEN
	}
	
	
	
	public SymbolTable getSymbolTable() {
		return st;
	}



	private void match(int expect)
	{
		int tokenNum=this.token.getTokenNumber();
		if(tokenNum==expect)
			this.token=la.findToken();
		else
			fatalError(token.getLexeme());
	}
	
	
	
	//Call this method only once to start parsing.
	public void findToken()
	{
		this.token=this.la.findToken();
	}
	
	
	
	//Call this method in main to see if the last token is EOF$.
	public Token getToken()
	{
		return this.token;
	}
	
	
	//Print both error routine and the entire symbol table when occurred a fatal error.
	//And stop parsing immediately.
	//And delete the incorrect MIPS file immediately.
	public void fatalError(String tokenLexeme)
	{
		System.out.println("Terminating parse on line "+getCurrentLineNumber()+" on lexeme "+tokenLexeme);
		System.out.println("\nEntire ST:");
		System.out.println("Lexeme\tType\tOffset");
		System.out.println(getSymbolTable().printEntireTable());
		codeGenerator.deleteCode();
		System.exit(0);
	}
	
	
	
	//May call this method when non fatal errors occurred.
	//Define error detail in different parse routine.
	//The generated MIPS code will be deleted at the end of parsing.
	private void nonFatalError(String errorDetail)
	{
		this.nonFatalError+=errorDetail+"On line "+getCurrentLineNumber()+"\n";
		codeGenerator.discardCode();
	}
	
	
	
	//May call this method once meet a type mismatch.
	//The generated MIPS code will be deleted at the end of parsing.
	private void nonFatalError(ExpressionRecord expressionRecord_Left)
	{
		this.nonFatalError+="Type not match. LHS:"+expressionRecord_Left.getType()+"\t RHS:"
				+this.expressionRecord.getType()+". On line "+getCurrentLineNumber()+"\n";
		codeGenerator.discardCode();
	}
	
	
	
	//For debug use only. Do nothing on release.
	private void print(int routineNum)
	{
		if(debugMode)
		{
		if((printCounter++%40)==39)
			System.out.println();
		System.out.print(routineNum+" ");
		}
	}
	
	
	
	//If the token is ID, TYPE, BEGIN, IF, WHILE, READ/WRITE 
	//return true.
	private boolean isStatement()
	{
		return ((token.getTokenNumber()==1)||(token.getTokenNumber()==3)||
				(token.getTokenNumber()==7)||(token.getTokenNumber()==9)||
				(token.getTokenNumber()==11)||(token.getTokenNumber()==13));
	}
	
	
	//Return all non fatal errors as a single string.
	public String printNonFatalError()
	{
		if(this.nonFatalError.isEmpty())
			return new String("No errors.\n");
		return this.nonFatalError;
	}
	
	
	
	//Type judge by peek the first character of the token lexeme.
	//Only call this function when meets a literal token.
	//Start with:
	//1. Number - INTEGER(I)
	//2. Letter - LOGICAL(L)
	//3. "		- STRING(S)(Because all string is store with double quote. Example: "some thing inside")
	//Else, return 'E' as the error sign.
	private char getType(String tokenLexeme)
	{
		if(Character.isDigit(tokenLexeme.charAt(0)))
			return 'I';
		else if(Character.isLetter(tokenLexeme.charAt(0)))
			return 'L';
		else if(tokenLexeme.charAt(0)=='\"')
			return 'S';
		else
			return 'E';
	}
	
	
	
	//Only expPrime will call this function.
	//Generate MIPS code for expPrime(+,-,OR operation).
	private void expPCodeGen(String operator,ExpressionRecord expressionRecord_Left) throws IOException
	{
		typeCheck(expressionRecord_Left);
		int currentOffset=st.getNextOffset();
		String mipsOperator=new String();
		if(operator.equals("+"))
			mipsOperator="add";
		else if(operator.equals("-"))
			mipsOperator="sub";
		else//For "OR" operator. After the operation, the result will be changed to logical.
		{
			this.expressionRecord.setType('L');
			mipsOperator="or";		
		}
		codeGenerator.genComment("Value in $fp pos "+expressionRecord_Left.getOffset()+" "+operator+" value in pos "+expressionRecord.getOffset());
		codeGenerator.genComment("And store in $fp pos "+currentOffset);
		codeGenerator.codeGen("lw $t0 "+expressionRecord_Left.getOffset()+"($fp)");
		codeGenerator.codeGen("lw $t1 "+expressionRecord.getOffset()+"($fp)");
		codeGenerator.codeGen(mipsOperator+" $t0 $t0 $t1");
		codeGenerator.codeGen("sw $t0 "+currentOffset+"($fp)");
		//After the operation complete, the expression record should point to the result.
		this.expressionRecord.setOffset(currentOffset);
	}
	
	
	
	//Only termPrime will call this function.
	//Generate MIPS code for termPrime(*,/,DIV,REM,AND operation).
	private void termPCodeGen(String operator,ExpressionRecord expressionRecord_Left) throws IOException
	{
		typeCheck(expressionRecord_Left);
		int currentOffset=st.getNextOffset();
		String mipsOperator=new String();
		boolean modFlag=false;
		if(operator.equals("AND"))
		{
			codeGenerator.genComment("Spcial code gen for \"and\" operator.");
			codeGenerator.genComment("The result could be stored to any temporary register instead of lo or hi.");
			codeGenerator.codeGen("lw $t0 "+expressionRecord_Left.getOffset()+"($fp)");
			codeGenerator.codeGen("lw $t1 "+expressionRecord.getOffset()+"($fp)");
			codeGenerator.codeGen("and $t0 $t0 $t1");
			codeGenerator.codeGen("sw $t0 "+currentOffset+"($fp)");
			//After the operation complete, the expression record should point to the result.
			//And the result will be changed to logical.
			this.expressionRecord.setType('L');
			this.expressionRecord.setOffset(currentOffset);
			return;
		}
		if(operator.equals("*"))
			mipsOperator="mult";
		else if(operator.equals("/") || operator.equals("DIV"))
			mipsOperator="div";
		else if(operator.equals("REM"))
		{
			modFlag=true;
			mipsOperator="div";
		}
		codeGenerator.genComment("Value in $fp pos "+expressionRecord_Left.getOffset()+" "+operator+" value in pos "+expressionRecord.getOffset());
		if(modFlag)
			codeGenerator.genComment("And read hi into $t0, then store in $fp pos "+currentOffset);
		else
			codeGenerator.genComment("And read lo into $t0, then store in $fp pos "+currentOffset);
		codeGenerator.codeGen("lw $t0 "+expressionRecord_Left.getOffset()+"($fp)");
		codeGenerator.codeGen("lw $t1 "+expressionRecord.getOffset()+"($fp)");
		codeGenerator.codeGen(mipsOperator+" $t0 $t1");
		if(modFlag)
			codeGenerator.codeGen("mfhi $t0");//REM result stores in hi register.
		else
			codeGenerator.codeGen("mflo $t0");// DIV/TIMES result stores in lo register.
		codeGenerator.codeGen("sw $t0 "+currentOffset+"($fp)");
		//After the operation complete, the expression record should point to the result.
		this.expressionRecord.setOffset(currentOffset);
	}
	
	
	
	//Only factorPrime will call this function.
	//Generate MIPS code for factorPrime(<,>,=,!= operation).
	private void factorPCodeGen(String operator,ExpressionRecord expressionRecord_Left ) throws IOException
	{
		typeCheck(expressionRecord_Left);
		int currentOffset=st.getNextOffset();
		String mipsOperator=new String();
		if(operator.equals("<"))
			mipsOperator="slt";
		else if(operator.equals(">"))
			mipsOperator="sgt";
		else if(operator.equals("="))
			mipsOperator="seq";
		else	//operator is "!="
			mipsOperator="sne";
		codeGenerator.genComment("Value in fp "+expressionRecord_Left.getOffset()+ " and in fp "+expressionRecord.getOffset());
		codeGenerator.genComment("will make a comparasion("+operator+") and store the logical result in t0");
		codeGenerator.codeGen("lw $t0 "+expressionRecord_Left.getOffset()+"($fp)");
		codeGenerator.codeGen("lw $t1 "+expressionRecord.getOffset()+"($fp)");
		codeGenerator.codeGen(mipsOperator+" $t0 $t0 $t1");
		codeGenerator.codeGen("sw $t0 "+currentOffset+"($fp)");
		//After the operation complete, the expression record should point to the result.
		this.expressionRecord.setOffset(currentOffset);
		this.expressionRecord.setType('L');	
		
	}


	
	//Only assignState will call this function.
	private void assignStateCodeGen(ExpressionRecord expressionRecord_Left) throws IOException
	{
		typeCheck(expressionRecord_Left);
		codeGenerator.genComment("Assign operation");
		codeGenerator.codeGen("lw $t0 "+expressionRecord.getOffset()+"($fp)");
		codeGenerator.codeGen("sw $t0 "+expressionRecord_Left.getOffset()+"($fp)");
	}



	//Only ioState will call this function.
	//NOTICE: Read for this parser can only read and generate code for integer.
	//TODO - May add more types to read - LOGICAL, STRING, and...
	private void read_TokCodeGen() throws IOException
	{
		if(expressionRecord.getType()!='I')
			nonFatalError("You can only read for Integer.");
		codeGenerator.genComment("Read something into $fp pos "+expressionRecord.getOffset());
		codeGenerator.codeGen("li $v0 5");
		codeGenerator.codeGen("syscall");
		codeGenerator.codeGen("sw $v0 "+expressionRecord.getOffset()+"($fp)");
	}



	//Only ioState will call this function.
	//NOTICE: WRITE can only directly print a string, but not a STRING type variable.
	//TODO - Implements print string variable later.
	private void write_TokCodeGen(boolean crFlag) throws IOException
	{
		switch(expressionRecord.getType())
		{
		case 'I'://INTEGER
			codeGenerator.genComment("Print value from $fp pos "+expressionRecord.getOffset());
			codeGenerator.codeGen("lw $a0 "+expressionRecord.getOffset()+"($fp)");
			codeGenerator.codeGen("li $v0 1");
			codeGenerator.codeGen("syscall");
			break;
		case 'L'://LOGICAL
			codeGenerator.genComment("Print logical from $fp pos "+expressionRecord.getOffset());
			codeGenerator.codeGen("lw $t0 "+expressionRecord.getOffset()+"($fp)");
			codeGenerator.codeGen("la $a0 FL");//Set the value is false by default.
			String endIfLabel=codeGenerator.genNextEndIfLabel();
			codeGenerator.codeGen("beq $t0 $zero "+endIfLabel);//If the logical value is 0, jump to the end If label.
			codeGenerator.codeGen("la $a0 TL");//If logical value is 1, change it to TL(true label).
			codeGenerator.codeGen(endIfLabel+":");
			codeGenerator.codeGen("li $v0 4");
			codeGenerator.codeGen("syscall");
			break;
		case 'S'://STRING
			codeGenerator.genComment("Print string from "+expressionRecord.getLabelName());
			codeGenerator.codeGen("la $a0 "+expressionRecord.getLabelName());
			codeGenerator.codeGen("li $v0 4");
			codeGenerator.codeGen("syscall");
			break;
		}
		if(crFlag)//Generate a carriage return print call.
		{
			codeGenerator.genComment("Print a carriage return.");
			codeGenerator.codeGen("la $a0 CR");
			codeGenerator.codeGen("li $v0 4");
			codeGenerator.codeGen("syscall");
		}	
	}



	//Check the type. May complain a type not match error.
	private void typeCheck(ExpressionRecord expressionRecord_Left)
	{
		if(expressionRecord_Left.getType()!=expressionRecord.getType())
			nonFatalError(expressionRecord_Left);
	}
	
	
	
	private void literalCodeGen() throws IOException
	{

		String lexeme=token.getLexeme();
		char type=getType(lexeme);
		this.expressionRecord.setType(type);
		if(type=='I')
		{
			int currentOffset=st.getNextOffset();
			this.expressionRecord.setOffset(currentOffset);
			codeGenerator.genComment("Read "+lexeme+" into $fp pos "+currentOffset);
			codeGenerator.codeGen("li $t0 "+lexeme);
			codeGenerator.codeGen("sw $t0 "+this.expressionRecord.getOffset()+"($fp)");
		}
		else if (type=='L')
		{
			int currentOffset=st.getNextOffset();
			this.expressionRecord.setOffset(currentOffset);
			codeGenerator.genComment("Read "+lexeme+" into $fp pos "+currentOffset);
			codeGenerator.genComment("1 as TRUE and 0 as FALSE");
			int value=0;
			if(lexeme.charAt(0)=='T')
				value=1;
			codeGenerator.codeGen("li $t0 "+value);
			codeGenerator.codeGen("sw $t0 "+this.expressionRecord.getOffset()+"($fp)");
		}
		else if(type=='S')//String, buffer in code generator, and get the string label for further reference. 
		{
			//TODO Only directly used for WRITE/WRITELN. To implement more about STRING type, modify from here.
			String strValue=lexeme.substring(1, lexeme.length()-1);//Get out the double quote.
			String labelName=codeGenerator.bufferStrLabel(strValue);
			this.expressionRecord.setLabelName(labelName);
		}
	}
	
	
	
	//In order to indicate error occurred line.
	private int getCurrentLineNumber()
	{
		return this.la.getCurrentLineNumber();
	}
	
	
	
	public String getLexicalError()
	{
		return la.printLexicalError();
	}
}
