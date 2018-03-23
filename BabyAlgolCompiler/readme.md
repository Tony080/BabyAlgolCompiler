# 1. **Introduction**<br>
This compiler is called baby algol compiler(bac). Written in java language. In order to run the compiler, java virtual machine is needed. Some of grammars in algol are not implemented, and that's why call it "baby algol". Further details on [3. Language definition](#3.-**language-definition**<br>). 
This compiler only generates MIPS code. To run it like a program, you may also need to install some MIPS simulator like QtSpim: http://spimsimulator.sourceforge.net/<br>
**Architecture**<br>
![UML](https://raw.githubusercontent.com/Tony-Hu/BabyAlgolCompiler/master/diagram.gv.png)

# 2. **Install & use**<br>
1. You need to install Java runtime environment (JRE) at first. http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
2. After the JRE installed, you can open it in an IDE. And just go to BabyAlgolCompiler.java -> main function to run the compiler.<br>
Note that you have to add the **inFilePath** and **outFilePath** in your program argument.
3. Or, you can pack it as a jar file. Say you packed it named "bac.jar". Open Command Prompt (For windows), enter<br> 
   **"java -jar bac.jar inFilePath outFilePath"**<br>
   to run the compiler.<br>
   **inFilePath** - The source code file written in **baby algol**.<br>
   **outFilePath** - The intermediate code file the compiler will generate in **MIPS**. <br>
   ![example on windows](https://raw.githubusercontent.com/Tony-Hu/BabyAlgolCompiler/master/BabyAlgolCompiler/example.png)
3. The symbol table will always present.
4. As picture shown above, if there are no errors in all sections, the MIPS code will be generated as outFilepath. If any error occurs, the compiler will prompt you. And **NO** MIPS code file will be generated.

# 3. **Language definition**<br>
1. The basic rules of algol goes here: https://en.wikipedia.org/wiki/ALGOL
2. Notice that by this compiler definition, algol here is **NOT case-sensitive**.
3. The implemented keywords are shown below.<br>
![check list](https://raw.githubusercontent.com/Tony-Hu/BabyAlgolCompiler/master/BabyAlgolCompiler/check_list.png)
4. What didn't implemented in this compiler?
   1. No function allowed in this compiler.
   2. No REAL type implemented.<br>
   Sorry about that. But this compiler just done in 10 weeks on my compiler class.

# 4. **Example**<br>
Given the input in algol source code format. This is a program to test if the given input is prime.<br>
```
begin
Integer a;
integer b;
integer c;
logicAl isPrime;
isPrime:=true;
writeln("Input 1 positive integer for testing if prime");
read(a);
b:=2;
c:=b*b;
while !(c>a) do
      begin
      integer result;
      result:=a rem b;
      if result=0 then
      	 begin
	 writeln("Not a prime!");
	 isPrime:=false;
	 end;
      b:=b+1;
      c:=b*b;
      end;
if isprime then
   writeln("Is a prime!");
end.
```
We save it as a file.<br>
After we run the given code over the baby algol compiler, we got an output in MIPS code format.<br>
```mipsasm
#Prolog:
.text
.globl main
main:
move $fp $sp
#End of Prolog
# Read TRUE into $fp pos -16
# 1 as TRUE and 0 as FALSE
li $t0 1
sw $t0 -16($fp)
# Assign operation
lw $t0 -16($fp)
sw $t0 -12($fp)
# Print string from SLabel0
la $a0 SLabel0
li $v0 4
syscall
# Print a carriage return.
la $a0 CR
li $v0 4
syscall
# Read something into $fp pos 0
li $v0 5
syscall
sw $v0 0($fp)
# Read 2 into $fp pos -20
li $t0 2
sw $t0 -20($fp)
# Assign operation
lw $t0 -20($fp)
sw $t0 -4($fp)
# Value in $fp pos -4 * value in pos -4
# And read lo into $t0, then store in $fp pos -24
lw $t0 -4($fp)
lw $t1 -4($fp)
mult $t0 $t1
mflo $t0
sw $t0 -24($fp)
# Assign operation
lw $t0 -24($fp)
sw $t0 -8($fp)
# Remember the start position for re-looping.
TopWhileLabel0:
# Value in fp -8 and in fp 0
# will make a comparasion(>) and store the logical result in t0
lw $t0 -8($fp)
lw $t1 0($fp)
sgt $t0 $t0 $t1
sw $t0 -28($fp)
lw $t0 -28($fp)
xor $t0 $t0 1
sw $t0 -28($fp)
# Check the expression to determine whether to execute the loop.
beq $t0 $zero EndWhileLabel0
# Value in $fp pos 0 REM value in pos -4
# And read hi into $t0, then store in $fp pos -36
lw $t0 0($fp)
lw $t1 -4($fp)
div $t0 $t1
mfhi $t0
sw $t0 -36($fp)
# Assign operation
lw $t0 -36($fp)
sw $t0 -32($fp)
# Read 0 into $fp pos -40
li $t0 0
sw $t0 -40($fp)
# Value in fp -32 and in fp -40
# will make a comparasion(=) and store the logical result in t0
lw $t0 -32($fp)
lw $t1 -40($fp)
seq $t0 $t0 $t1
sw $t0 -44($fp)
lw $t0 -44($fp)
beq $t0 $zero EndIfLabel0
# Print string from SLabel1
la $a0 SLabel1
li $v0 4
syscall
# Print a carriage return.
la $a0 CR
li $v0 4
syscall
# Read FALSE into $fp pos -48
# 1 as TRUE and 0 as FALSE
li $t0 0
sw $t0 -48($fp)
# Assign operation
lw $t0 -48($fp)
sw $t0 -12($fp)
EndIfLabel0:
# Read 1 into $fp pos -48
li $t0 1
sw $t0 -48($fp)
# Value in $fp pos -4 + value in pos -48
# And store in $fp pos -52
lw $t0 -4($fp)
lw $t1 -48($fp)
add $t0 $t0 $t1
sw $t0 -52($fp)
# Assign operation
lw $t0 -52($fp)
sw $t0 -4($fp)
# Value in $fp pos -4 * value in pos -4
# And read lo into $t0, then store in $fp pos -56
lw $t0 -4($fp)
lw $t1 -4($fp)
mult $t0 $t1
mflo $t0
sw $t0 -56($fp)
# Assign operation
lw $t0 -56($fp)
sw $t0 -8($fp)
# Loop in fact is the "j" to a circular expression.
j TopWhileLabel0
# The loop's break point starts from below label.
EndWhileLabel0:
lw $t0 -12($fp)
beq $t0 $zero EndIfLabel1
# Print string from SLabel2
la $a0 SLabel2
li $v0 4
syscall
# Print a carriage return.
la $a0 CR
li $v0 4
syscall
EndIfLabel1:
#PostLog:
li $v0 10
syscall
.data
SLabel0:	.asciiz "Input 1 positive integer for testing if prime"
SLabel1:	.asciiz "Not a prime!"
SLabel2:	.asciiz "Is a prime!"
CR:	.asciiz "\n"
TL:	.asciiz "TRUE"
FL:	.asciiz "FALSE"

```


