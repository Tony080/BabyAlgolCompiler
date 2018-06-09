package LexicalAnalyzer;

import java.util.Scanner;

/**
 * [Class Overview]
 * Read file into buffer. Only 1 line of file in the memory at most.
 * The buffer size is equals to the char counts in a single line.
 * Once the buffer has been all read, read next line in file.
 * If meets the end of file, fill the buffer with "EOF$".
 *
 * @author Chengzhi Hu
 * @category For CS4110 Assignment 2 - Lexical Analyzer - As buffer reader.
 * Compiler: Eclipse Neon.2 Release (4.6.2)
 * Language: Java 8
 * @since Feb.13.2017
 */
public class BufferReader {

  //read file per line into memory.
  private String stringBuffer;


  //The current char position to string buffer.
  private int bufferPos;

  //A track with the line number(also the position to array list).
  private int currentLineNumber;

  //A scanner to read from file
  private Scanner inFileScanner;

  public BufferReader(Scanner inFileScanner) {
    this.currentLineNumber = 0;
    this.bufferPos = 0;
    this.inFileScanner = inFileScanner;
  }


  //Getter of current line number.
  public int getCurrentLineNumber() {
    return this.currentLineNumber;
  }

  //Read the file per line. And backup the last line.
  //If meet the end of file, return "EOF".
  private void fillBuffer() {
    if (this.inFileScanner.hasNextLine()) {
      this.stringBuffer = this.inFileScanner.nextLine();
      this.currentLineNumber++;
    } else
      this.stringBuffer = "EOF$";
  }


  //Get next char.
  //If meet the EOLN, call function to read next line into buffer.
  //Then get next char in the buffer.
  public char getChar() {
    //If try to read char before the 1st element,
    //Just return the 1st element.
    if (this.bufferPos < 0) {
      this.bufferPos = 0;
    }
    //If try to read char at next line.
    //Read a new line from file. And set the first char to space.
    //Because BufferReader doesn't have the authority to eat a EOLN(white space).
    //But to represent it as another white space - space.
    else if (this.bufferPos >= this.stringBuffer.length()) {
      this.fillBuffer();
      this.bufferPos = 0;
      return ' ';
    }
    //Or just return the char in current buffer.
    return this.stringBuffer.charAt(this.bufferPos++);
  }


  //Method to back a position of buffer.
  public void backAChar() {
    this.bufferPos--;
  }


  public int getBufferPos() {
    return this.bufferPos;
  }
}
