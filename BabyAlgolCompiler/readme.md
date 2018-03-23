1. **Introduction**<br>
This compiler is called baby algol compiler(bac). Written in java language. In order to run the compiler, java virtual machine is needed. Some of grammars in algol are not implemented, and that's why call it "baby algol". Further details on 3. Language definition. 
This compiler only generates MIPS code. To run it like a program, you may also need to install some MIPS simulator like QtSpim: http://spimsimulator.sourceforge.net/
2. **Install & use**
    1. You need to install Java runtime environment (JRE) at first. http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
    2. After the JRE installed, open Command Prompt (For windows), enter<br> 
       **"java -jar bac.jar inFilePath outFilePath"**<br>
       to run the compiler.<br>
       **inFilePath** - The source code file written in **baby algol**.<br>
       **outFilePath** - The intermediate code file the compiler will generate in **MIPS**. 
       [example on windows](https://raw.githubusercontent.com/Tony-Hu/BabyAlgolCompiler/master/BabyAlgolCompiler/example.png)
    3. The symbol table will always present.
    4. As picture shown above, if there are no errors in all sections, the MIPS code will be generated as outFilepath. If any error occurs, the compiler will prompt you. And **NO** MIPS code file will be generated.

3. **Language definition**
    1. The basic rules of algol goes here: https://en.wikipedia.org/wiki/ALGOL
    2. The implemented keywords are shown below.
    [check list](https://raw.githubusercontent.com/Tony-Hu/BabyAlgolCompiler/master/BabyAlgolCompiler/check_list.png)
    3. What didn't implemented in this compiler?
       1. No function allowed in this compiler.
       2. No REAL type implemented.<br>
       Sorry about that. But this compiler just done in 10 weeks on my compiler class.



