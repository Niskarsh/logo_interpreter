package logo;

import java.io.BufferedReader;                               
import java.io.IOException;                                  
import java.io.InputStreamReader;                            
import java.nio.charset.Charset;                             
import java.nio.file.Files;                                  
import java.nio.file.Paths;
import java.util.List;
import logo.lexer.*;
import logo.parser.*;
import logo.tools.prettyPrinter.AstPrinter;
import logo.interpreter.Interpreter;
import logo.errorHandlers.RuntimeError;


public class Logo {

	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false; //Static marker for parser errors
	static boolean hadRuntimeError = false; //Static parser for Runtime errors

	public static void main(String[] args) throws IOException {
	    if (args.length > 1) {                                   
		    System.out.println("Usage: jlox [script]");            
		    System.exit(64); 
	    } else if (args.length == 1) {                           
	     	runFile(args[0]);                                      
	    } else {                                                 
	    	runPrompt();                                           
	    }                                                        
  	}

  	private static void runFile(String path) throws IOException {
	    byte[] bytes = Files.readAllBytes(Paths.get(path));        
	    run(new String(bytes, Charset.defaultCharset()));
	    if (hadError) System.exit(65);                   
		if (hadRuntimeError) System.exit(70);      
  	}

  	private static void runPrompt() throws IOException {         
	    InputStreamReader input = new InputStreamReader(System.in);
	    BufferedReader reader = new BufferedReader(input);

	    for (;;) { 
	      System.out.print("loGo >> ");                                  
	      run(reader.readLine());
	      hadError=false;
	      hadRuntimeError=false;                                  
	    }                                                          
  	}                                                            

  	public static void runtimeError (RuntimeError error) {

  		System.err.println (error.getMessage()+"\n[line "+error.token.line+"]");
  		hadRuntimeError=true;

  	}

	public static void error(Token token, String message) {              
	    if (token.type == TokenType.EOF) {                          
   		    report(token.line, " at end", message);
	    } else {
    		report(token.line, " at '" + token.lexeme + "'", message);
	    }                                                           
  	}

	private static void report(int line, String where, String message) {
		System.err.println(
		"[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

	private static void run (String source) {

		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		// Expr expression = parser.parse();

		if (hadError) return;

		interpreter.interpret(statements);
		// System.out.println(new Interpreter().print(expression));

	}

}