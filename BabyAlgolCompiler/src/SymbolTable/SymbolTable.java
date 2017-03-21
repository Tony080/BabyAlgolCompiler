package SymbolTable;

import java.util.*;

/**
 * [Class Overview]:
 * Data structure used: Hash table, Stack.
 * 
 * Symbol table routines:
 * 1. Insert: Node inserts into hash table - As current scope.
 * 2. Push: Hash table pushes into stack - As open scopes.
 * 3. Pop: Pop the closed scope.
 * 4. Display: Display currently current scope and open scopes.
 * 5. Print all scopes: Print all nodes in every scopes(both open and close) on the screen.
 * @author Chengzhi Hu
 * @since Jan.22.2017 
 * Last modified on Feb.15.2017
 * @category For CS4110 Assignment 1 - Symbol Table - As primary function class. 
 * Also for Assignment 4 - Parser with ST - As part of Symbol Table class. 
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 */
public class SymbolTable {

	//Define the hash table size.
	//Since no const in java, use final instead.
	public static final int hashTableSize=19;


	
	//A hash table to store the current scope.
	private Hashtable<Integer,SymbolTableNode> currentScope;

	
	
	//A stack to store the every open scopes.
	//Doesn't care about what happened in an closed scope.
	private Stack<Hashtable<Integer,SymbolTableNode>> openScopes;
	
	
	
	//A offset to determine the node's offset value. -4 after each insertion.
	private int offset;
	
	
	//Viable to store every scopes' nodes information.
	private String entireTable;
	
	
	
	//A mark to control the format of entireTable output.
	//If meet a new scope, insert a "\n" to make the output more clear to read.
	private boolean newScopeMark;

	
	
	public SymbolTable()
	{
		this.openScopes=new Stack<Hashtable<Integer,SymbolTableNode>>();
		this.currentScope=new Hashtable<Integer,SymbolTableNode>(SymbolTable.hashTableSize);
		this.offset=0;
		this.entireTable=new String();
		this.newScopeMark=false;
	}
	

	
	//Create a node for insertion.
	//Store the node's information in the String variable entireTable.
	//Offset -4 after insertion.
	public boolean insert(char type, String lexeme)
	{
		SymbolTableNode stn=new SymbolTableNode(type, lexeme,this.offset);
		if(this.newScopeMark)
		{
			this.entireTable+="\n";
			this.newScopeMark=false;
		}
		this.entireTable+=stn.toString();
		try
		{
			this.currentScope.put(stn.getHashValue(),stn);
			this.getNextOffset();
			return true;
		}
		catch(NullPointerException e)
		{
			return false;
		}	
	}
	

	
	//Display every node in current scope and open scopes.
	public String display()
	{
		String result=new String();
		result="Current Scope : \n"+hashTableToString(this.currentScope)+"\n\n";
		result+="Open Scopes : \n";
		ListIterator<Hashtable<Integer,SymbolTableNode>> li= this.openScopes.listIterator();
		while(li.hasNext())
		{
			Hashtable<Integer, SymbolTableNode> liScope=li.next();
			result+=hashTableToString(liScope);
		}
		result+="\n\n";
		return result;
	}
	

	
	//Find node by it's hash value in current scope.(present as hash table)
	//Return an empty symbol table node if nothing found.
	//Otherwise return the node value it found.
	public SymbolTableNode findInCurrentScope(String variableName)
	{
		if(this.currentScope.containsKey(variableName.hashCode()))
		{
			return this.currentScope.get(variableName.hashCode());
		}
		else
		{
			return new SymbolTableNode();
		}
	}
	

	
	//Find node by it's hash value in every open scopes.(present as stack)
	//Return an empty symbol table node if nothing found.
	//Otherwise, return the node matched in last open scope.
	public SymbolTableNode findInOpenScopes(String variableName)
	{
		ListIterator<Hashtable<Integer,SymbolTableNode>> li= this.openScopes.listIterator();
		SymbolTableNode stn=new SymbolTableNode();
		
		//Traverse and return the last node matched in stack.
		while(li.hasNext())
		{
			Hashtable<Integer, SymbolTableNode> liScope=li.next();
			if(liScope.containsKey(variableName.hashCode()))
			{
				stn=liScope.get(variableName.hashCode());
			}
		}
		return stn;
	}
	

	
	//Call this method when meets a begin token.
	public void pushHashTable()
	{
		this.currentScope=new Hashtable<Integer,SymbolTableNode>(SymbolTable.hashTableSize);
		this.openScopes.push(currentScope);
		this.newScopeMark=true;
	}
	

	
	//Call this method when meets an end token.
	public Hashtable<Integer,SymbolTableNode> popHashTable()
	{
		Hashtable<Integer,SymbolTableNode> result= this.openScopes.pop();
		if(this.openScopes.size()>0)
			this.currentScope=this.openScopes.peek();
		this.newScopeMark=true;
		return result;
	}
	
	
	
	//Give a hash table and call this method to return an briefly organized String with 
	//hash table's information. 
	public String hashTableToString(Hashtable<Integer,SymbolTableNode> hashtable)
	{
		String result=new String();
		Set<Integer> keys=hashtable.keySet();
		Iterator<Integer> li=keys.iterator();
		while(li.hasNext())
		{
			Integer key=li.next();
			result+=hashtable.get(key).toString();
		}
		return result;
	}
	
	
	
	public String printEntireTable()
	{
		return this.entireTable;
	}



	public int getOffset() 
	{
		return this.offset;
	}

	
	
	//Call this method in parser. Record the position will be used for literal tokens.
	public int getNextOffset()
	{
		int offset=this.offset;
		this.offset-=4;
		return offset;
	}


	public void setOffset(int offset) 
	{
		this.offset = offset;
	}
	
}
