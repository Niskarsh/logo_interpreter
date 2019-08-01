import java.io.BufferedReader;                               
import java.io.IOException;                                  
import java.io.InputStreamReader;                            
import java.nio.charset.Charset;                             
import java.nio.file.Files;                                  
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;                                                 
import java.util.Map;  
import java.lang.Math;

public class Logo {

	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false; //Static marker for parser errors
	static boolean hadRuntimeError = false; //Static parser for Runtime errors
	public static boolean hasGraphics = false; //Checks for Statements pertaining graphics
	public static List<Object> graphicSets = new ArrayList<>();
	public static boolean pd = true;
	public static boolean turtle = true;
	public static double color = 0;
	public static double pensize = 1;

	public static void main(String[] args) throws IOException {
	    
		run (args[0]);
	    // if (args.length > 1) {                                   
		   //  System.out.println("Usage: jlox [script]");            
		   //  System.exit(64); 
	    // } else if (args.length == 1) {                           
	    //  	runFile(args[0]);                                      
	    // } else {                                                 
	    // 	runPrompt();                                           
	    // }                                                        
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
	      run(reader.readLine().toLowerCase());
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

	private static List<Object> run (String source) {

		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		// Expr expression = parser.parse();

		// if (hadError) return;

		interpreter.interpret(statements);

		if (hasGraphics) {
			System.out.println(graphicSets);
		}

		return graphicSets;
		// System.out.println(new Interpreter().print(expression));

	}

	private static List<Object> getGraphicSets () {
		return graphicSets;
	}

}


// Compiled

enum TokenType {
	LEFT_BRACE, RIGHT_BRACE, COMMA, MINUS, PLUS, SLASH, STAR, DOT, COLON,

	BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

	IDENTIFIER,

	STRING, NUMBER,

	TRUE, FALSE,

	IF, TEST, IFELSE, IFTRUE, IFFALSE, STOP,
	FOR, UNTIL, DOUNTIL, WHILE, DOWHILE, REPEAT,
	RUN, 
	FOREVER, REPCOUNT,
	MOUSEPOS, CLICKPOS, BACKGROUND, BG, GETSCREENCOLOR, GETSC,
	FORWARD, BACK, RIGHT, LEFT,
	CS, CLEARSCREEN, HIDE_TURTLE, SHOW_TURTLE, PU, PENUP, PD, PENDOWN, TO, END, PENMODE, PAINT, ERASE, REVERSE, SETPENCOLOR,
	SETPENSIZE, SETBACKGROUND, SETSCREENCOLOR, SETSC,
	SETPOS, SETXY, SETX, SETY, AND, OR, NOT, XOR,
	SHOW, MAKE, LOCAL,

	RANDOM,

	NULL,

	EOF
};

//Token info  	

class Token {

  public final TokenType type;                                           
  public final String lexeme;                                            
  public final Object literal;                                           
  public final int line; 

  public Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;                                             
    this.lexeme = lexeme;                                         
    this.literal = literal;                                       
    this.line = line;                                             
  }                                                               

  public String toString() {                                      
    return type + " " + lexeme + " " + literal;                   
  }                                                               
}       



//Scanner class
class Scanner {

  private static final Map<String, TokenType> keywords;
  private final String source;                                            
  private List<Token> tokens = new ArrayList<>();
  private int start = 0;                               
  private int current = 0;                             
  private int line = 1;

  static {
    keywords = new HashMap<>();
    keywords.put("false", TokenType.FALSE);
    keywords.put("if", TokenType.IF);
    keywords.put("ifelse", TokenType.IFELSE);
    keywords.put("test", TokenType.TEST);
    keywords.put("iftrue", TokenType.IFTRUE);
    keywords.put("ift", TokenType.IFTRUE);
    keywords.put("iffalse", TokenType.IFFALSE);
    keywords.put("iff", TokenType.IFFALSE);
    keywords.put("stop", TokenType.STOP);
    keywords.put("for", TokenType.FOR);
    keywords.put("until", TokenType.UNTIL);
    keywords.put("dountil", TokenType.DOUNTIL);
    keywords.put("while", TokenType.WHILE);
    keywords.put("dowhile", TokenType.DOWHILE);
    keywords.put("repeat", TokenType.REPEAT);
    keywords.put("run", TokenType.RUN);
    keywords.put("forever", TokenType.FOREVER);
    keywords.put("repcount", TokenType.REPCOUNT);
    keywords.put("true", TokenType.TRUE);
    keywords.put("mousepos", TokenType.MOUSEPOS);
    keywords.put("clickpos", TokenType.CLICKPOS);
    keywords.put("background", TokenType.BACKGROUND);
    keywords.put("bg", TokenType.BG);
    keywords.put("getscreencolor", TokenType.GETSCREENCOLOR);
    keywords.put("getsc", TokenType.GETSC);
    keywords.put("fd", TokenType.FORWARD);
    keywords.put("forward", TokenType.FORWARD);
    keywords.put("bk", TokenType.BACK);
    keywords.put("back", TokenType.BACK);
    keywords.put("rt", TokenType.RIGHT);
    keywords.put("right", TokenType.RIGHT);
    keywords.put("lt", TokenType.LEFT);
    keywords.put("left", TokenType.LEFT);
    keywords.put("cs", TokenType.CLEARSCREEN);
    keywords.put("clearscreen", TokenType.CLEARSCREEN);
    keywords.put("ht", TokenType.HIDE_TURTLE);
    keywords.put("hideturtle", TokenType.HIDE_TURTLE);
    keywords.put("st", TokenType.SHOW_TURTLE);
    keywords.put("showturtle", TokenType.SHOW_TURTLE);
    keywords.put("pu", TokenType.PENUP);
    keywords.put("penup", TokenType.PENUP);
    keywords.put("pd", TokenType.PENDOWN);
    keywords.put("pendown", TokenType.PENDOWN);
    keywords.put("to", TokenType.TO);
    keywords.put("end", TokenType.END);
    keywords.put("penmode", TokenType.PENDOWN);
    keywords.put("paint", TokenType.PAINT);
    keywords.put("erase", TokenType.ERASE);
    keywords.put("reverse", TokenType.REVERSE);
    keywords.put("setpencolor", TokenType.SETPENCOLOR);
    keywords.put("spc", TokenType.SETPENCOLOR);
    keywords.put("pc", TokenType.SETPENCOLOR);
    keywords.put("setpc", TokenType.SETPENCOLOR);
    keywords.put("setpensize", TokenType.SETPENSIZE);
    keywords.put("setps", TokenType.SETPENSIZE);
    keywords.put("pt", TokenType.SETPENSIZE);
    keywords.put("setbackground", TokenType.SETBACKGROUND);
    keywords.put("setscreencolor", TokenType.SETBACKGROUND);
    keywords.put("setsc", TokenType.SETSC);
    keywords.put("setpos", TokenType.SETPOS);
    keywords.put("setx", TokenType.SETX);
    keywords.put("sety", TokenType.SETY);
    keywords.put("&&", TokenType.AND);
    keywords.put("||", TokenType.OR);
    keywords.put("!", TokenType.NOT);
    keywords.put("and", TokenType.AND);
    keywords.put("or", TokenType.OR);
    keywords.put("not", TokenType.NOT);
    keywords.put("xor", TokenType.XOR);
    keywords.put("show", TokenType.SHOW);
    keywords.put("make", TokenType.MAKE);
    keywords.put("local", TokenType.LOCAL);
    keywords.put("random", TokenType.RANDOM);
    keywords.put("null", TokenType.NULL);

// //Maximum munch
  }

	public Scanner (String source) {
		this.source = source;
	}


	public List<Token> scanTokens () {

    	while (!isAtEnd()) {                            
      // We are at the beginning of the next lexeme.
	      start = current;                              
	      scanToken();                                  
	    }

	    tokens.add(new Token(TokenType.EOF, "", null, line));     
	    return tokens;                                  
  	}

	private boolean isAtEnd () {
		return current>=source.length();
	}

	private char advance () {
		current++;
		return source.charAt(current-1);
	}

	private void addToken (TokenType type) {                
    	addToken(type, null);                                
	}                                                      

  private void addToken(TokenType type, Object literal) {
 	  String text = source.substring(start, current);      
  	tokens.add(new Token(type, text, literal, line));    
  }  

  private boolean match (char expected) {
  	if(source.charAt(current)!=expected) {
  		return false;
  	}
  	current++;
  	return true;
  }

  private char peek () {
  	if(isAtEnd()) {
  		return '\0';
  	}
  	return source.charAt(current);
  }

  private void handleString () {
  	String st="";
  	while(peek()!=' ' && !isAtEnd()) {
  		if (peek()=='\n') {
  			line++;
  		}
      //escape character '\'
      if (peek()!='\\') {
        //fill logic

      }
  		advance();
  	}
    start++;
  	st = source.substring(start, current);
    if (tokens.get(tokens.size()-1).type==TokenType.MAKE) {
      addToken(TokenType.IDENTIFIER, ":"+st);
      return;
    }
  	addToken(TokenType.STRING, st);
  }

  private boolean isDigit (char c) {
    return (c >= '0' && c <= '9')   ;
  } 

  private void number() {                                     
    while (isDigit(peek())) {
    	advance();
    }

    // Look for a fractional part.                            
    if (peek() == '.' && isDigit(peekNext())) {               
      // Consume the "."                                      
      advance();                                 

      while (isDigit(peek())) {
      	advance();                    
      }
    }                                                         

    addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
  } 

  private char peekNext() {                         
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);              
  } 

  private boolean isColon (char c) {
  	if(c==':') {
  		return true;
  	}
		return false;
  }

  private boolean isAlphaNumeric (char c) {
    return this.isAlpha(c) || this.isDigit(c);      
  }

  private boolean isAlpha(char c) {       
    return ((c >= 'a' && c <= 'z') ||      
               (c >= 'A' && c <= 'Z') ||      
                c == '_');               
  }

  private void identifier () {
  	while (isAlphaNumeric(peek())) {
  		advance();
  	}
  	String text = this.source.substring(this.start, this.current);
		addToken(TokenType.IDENTIFIER, text);
  }

	private void scanToken () {
		char c = advance();
		switch (c) {
			case '[': addToken(TokenType.LEFT_BRACE); break;
			case ']': addToken(TokenType.RIGHT_BRACE); break;
			case ',': addToken(TokenType.COMMA); break;          
	    case '-': addToken(TokenType.MINUS); break;          
	    case '+': addToken(TokenType.PLUS); break;           
	    case '*': addToken(TokenType.STAR); break; 

	    case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;      
	    case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;    
	    case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;      
	    case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
      case '&': addToken(match('&') ? TokenType.AND : null); break;
      case '|': addToken(match('|') ? TokenType.OR : null); break;

	    case '/':                                                       
	        if (match('/')) {
	          // A comment goes on until the end of the line.                
	          while (peek() != '\n' && !isAtEnd()) {
	          	advance();
	          }
	        } else {                                                      
	          addToken(TokenType.SLASH);
	        }                                                             
      break;

      case ' ':                                    
      case '\r':                                   
      case '\t':                                   
        // Ignore whitespace,tabs
      break;

      case '\n':                                   
        line++;                       
      break;  

      case '"':
      	handleString();
    	break;

    	default:                                     
				if (isDigit(c)) {                          
          number();
        } else if (isColon(c)) {
        	identifier();                                   
        } else {
        	while (isAlphaNumeric(peek())) {
			  		advance();
			  	}
			  	String text = source.substring(start, current);
			  	TokenType type = keywords.get(text);
			  	if(type!=null) {
			  		addToken(type);
			  	} else {
              addToken(TokenType.IDENTIFIER, text);
          }
        }                                   
        break;

		}

	}
}

class ParseError extends RuntimeException {

}

class RuntimeError extends RuntimeException {
	public final Token token;

	public RuntimeError (Token token, String message) {
		super(message);
		this.token = token;
	}
}


//Visitor design pattern implemented
abstract class Expr {
//Interface with generic
	public interface Visitor<R> {
		R visitBinaryExpr (Binary expr);
		R visitGroupingExpr (Grouping expr);
		R visitLiteralExpr (Literal expr);
		R visitUnaryExpr (Unary expr);
		R visitVariableExpr (Variable expr);
		R visitLogicalExpr (Logical expr);
		R visitRandomExpr (Random expr);
	}

	public abstract <R> R accept (Visitor<R> visitor);

	public static class Binary extends Expr {


		public final Expr left, right;
		public final Token operator;

	    public Binary(Expr left, Token operator, Expr right) {
	    	this.left = left;
	    	this.operator = operator;
	    	this.right = right;
	    }

	    public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitBinaryExpr(this);
	    }		

	}

	public static class Grouping extends Expr {

		public final Expr expression;

		public Grouping(Expr expression) {
	  		this.expression = expression;
  		}		

  		public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitGroupingExpr(this);
	    }
	}

	public static class Literal extends Expr {

		public final Object value;
		
		public Literal(Object value) {
		  	this.value = value;
	 	}	

	 	public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitLiteralExpr(this);
	    }
	}

	public static class Unary extends Expr {

		public final Token operator;
		public final Expr right;

		
		public Unary(Token operator, Expr right) {
		  	this.operator = operator;
		  	this.right = right;
		}

		public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitUnaryExpr(this);
	    }	
	}

	public static class Variable extends Expr {

		public final Token name;

		public Variable (Token name) {
			this.name = name;
		}

		public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitVariableExpr(this);
	    }
	}

	public static class Logical extends Expr {

		public final Token operator;
		public final Expr left, right;

		public Logical (Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public <R> R accept (Visitor<R> visitor) {
	    	return visitor.visitLogicalExpr(this);
	    }
	}

	public static class Random extends Expr {

		public final Expr limit;

		public Random (Expr limit) {
			this.limit = limit;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitRandomExpr(this);
		}
	}

	


}


abstract class Stmt {

	public interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitPrintStmt(Print stmt);
		R visitVarStmt(Var stmt);	
		R visitBlockStmt(Block stmt);
		R visitIfStmt(If stmt);
		R visitIfElseStmt(IfElse stmt);
		R visitRepeatStmt (Repeat stmt);
		R visitWhileStmt (While stmt);
		R visitForwardsStmt (Forwards stmt);
		R visitRightStmt (Right stmt);
		R visitPenStmt (Pen stmt);
		R visitTurtleStmt (Turtle stmt);
		R visitPenColorStmt (PenColor stmt);
		R visitPenSizeStmt (PenSize stmt);
	}

	public abstract <R> R accept (Visitor<R> visitor);

	public static class Expression extends Stmt {

		public final Expr expression;
		public Expression (Expr expression) {
			this.expression = expression;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

	}

	public static class Print extends Stmt {
		
		public final Expr expression;
		public Print (Expr expression) {
			this.expression = expression;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	public static class Var extends Stmt {

		public final Token name;
		public final Expr initializer;
		public Var (Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}


	}

	public static class Block extends Stmt {

		public final List<Stmt> statements;

		public Block (List<Stmt> statements) {
			this.statements = statements;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

	}

	public static class If extends Stmt {

		public final List<Stmt> thenBlock;
		public final Expr condition;

		public If (Expr condition, List<Stmt> thenBlock) {
			this.condition = condition;
			this.thenBlock = thenBlock;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

	}

	public static class IfElse extends Stmt {

		public final List<Stmt> thenBlock, elseBlock;
		public final Expr condition;

		public IfElse (Expr condition, List<Stmt> thenBlock, List<Stmt> elseBlock) {
			this.condition = condition;
			this.thenBlock = thenBlock;
			this.elseBlock = elseBlock;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitIfElseStmt(this);
		}

	}

	public static class Repeat extends Stmt {

		public final List<Stmt> block;
		public final Expr condition;

		public Repeat (Expr condition, List<Stmt> block) {
			this.condition = condition;
			this.block = block;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitRepeatStmt(this);
		}

	}

	public static class While extends Stmt {

		public final List<Stmt> block;
		public final Expr condition;

		public While (Expr condition, List<Stmt> block) {
			this.condition = condition;
			this.block = block;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

	}

	public static class Forwards extends Stmt {

		public final Expr value;
		public boolean forward = true;

		public Forwards (Expr value, boolean forward) {
			this.value = value;
			this.forward = forward;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitForwardsStmt(this);
		}
	}

	public static class Right extends Stmt {

		public final Expr value;
		public boolean forward = true;

		public Right (Expr value, boolean forward) {
			this.value = value;
			this.forward = forward;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitRightStmt(this);
		}
	}

	public static class Pen extends Stmt {

		public boolean pd;

		public Pen (boolean pd) {
			this.pd = pd;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPenStmt(this);
		}
	}

	public static class Turtle extends Stmt {

		public boolean tl;

		public Turtle (boolean tl) {
			this.tl = tl;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitTurtleStmt(this);
		}
	}

	public static class PenColor extends Stmt {

		public Expr color;

		public PenColor (Expr color) {
			this.color = color;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPenColorStmt(this);
		}
	}

	public static class PenSize extends Stmt {

		public Expr pensize;

		public PenSize (Expr pensize) {
			this.pensize = pensize;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPenSizeStmt(this);
		}
	}
}


class Parser {
	private final List<Token> tokens;
	private int current =0; 


	public Parser (List<Token> tokens) {
		this.tokens = tokens;
	}

	private boolean match(TokenType ...types) {
		for (TokenType type : types) {           
	      if (check(type)) {                     
	        advance();                           
	        return true;                         
	      }                                      
    	}

    return false;  
	}

	private boolean check (TokenType type) {
		if(isAtEnd()) {
			return false;
		}
		if(peek().type==type) {
			return true;
		}
		return false;
	}

	private boolean isAtEnd () {
		return peek().type==TokenType.EOF;
	}

	private Token advance () {
		if (!isAtEnd()) {
			++current;
		}
		return previous();
	}

	private Token peek () {
		return tokens.get(current);
	}

	private Token previous () {
		return tokens.get(current-1);
	}

	//fns defined according to precedence and associativity


	public List<Stmt> parse() {

		List<Stmt> statements = new ArrayList<>();
		while (!isAtEnd()) {
			statements.add(declarations());


			// statements.add(statement());
		}

		return statements;
		// try {
		// 	return expression();
		// } catch (ParseError error) {
		// 	return null;
		// }
	}

	private Stmt declarations () {
		try {
			if (match(TokenType.MAKE)) return varDeclaration(TokenType.MAKE);
			if (match(TokenType.LOCAL)) return varDeclaration(TokenType.LOCAL);

			return statement ();

		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt  varDeclaration (TokenType type) {

		Token name = consume(TokenType.IDENTIFIER, "Expect variable name");

		Expr initializer = null;

		if (type==TokenType.MAKE) {
			initializer = expression();
		}

		return new Stmt.Var(name, initializer);
	}


	private Stmt statement () {

		if (match(TokenType.FORWARD)) return forwardStatement(true);
		if (match(TokenType.BACK)) return forwardStatement(false);
		if (match(TokenType.RIGHT)) return rightStatement(true);
		if (match(TokenType.LEFT)) return rightStatement(false);
		if (match(TokenType.PENUP)) return penStatement(false);
		if (match(TokenType.PENDOWN)) return penStatement(true);
		if (match(TokenType.HIDE_TURTLE)) return turtleStatement(false);
		if (match(TokenType.SHOW_TURTLE)) return turtleStatement(true);
		if (match(TokenType.SETPENSIZE)) return pensizeStatement();
		if (match(TokenType.SETPENCOLOR)) return penColorStatement();
		if (match(TokenType.REPEAT)) return repeatStatement();
		if (match(TokenType.WHILE)) return whileStatement();
		if (match(TokenType.IF)) return ifStatement();
		// if (match(TokenType.TEST)) return testStatement();
		if (match(TokenType.IFELSE)) return ifElseStatement();
		if (match(TokenType.SHOW)) return printStatement();
		if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());


		return expressionStatement();
	}

	private Stmt penColorStatement() {
		Expr value = expression();
		return new Stmt.PenColor(value);
	}

	private Stmt pensizeStatement() {
		Expr value = expression();
		return new Stmt.PenSize(value);
	}

	private Stmt turtleStatement(boolean tl) {
		return new Stmt.Turtle(tl);
	}

	private Stmt penStatement(boolean pd) {
		return new Stmt.Pen(pd);
	}

	private Stmt rightStatement(boolean forward) {
		Expr value = expression();
		return new Stmt.Right(value, forward);
	}

	private Stmt forwardStatement(boolean forward) {
		Expr value = expression();
		return new Stmt.Forwards(value, forward);
	}

	private Stmt repeatStatement () {
		Expr condition = expression();
		consume (TokenType.LEFT_BRACE, "Expected '[ before block'");
		List<Stmt> block = block();
		return new Stmt.Repeat(condition, block);
	}

	private Stmt whileStatement () {
		Expr condition;
		if (match (TokenType.LEFT_BRACE)) {
			condition = expression();
			consume (TokenType.RIGHT_BRACE, "Expected ']' at end of condition");
		} else {
			condition = expression();
		}

		consume (TokenType.LEFT_BRACE, "Expected '[' before block");
		List<Stmt> block = block();
		return new Stmt.While(condition, block);
	}



	private Stmt ifStatement () {
		Expr condition;
		if (match (TokenType.LEFT_BRACE)) {
			condition = expression();
			consume (TokenType.RIGHT_BRACE, "Expected ']' at end of condition");
		} else {
			condition = expression();
		}

		consume (TokenType.LEFT_BRACE, "Expected '[' before true block");
		List<Stmt> thenBlock = block();
		return new Stmt.If(condition, thenBlock);

	}

	private Stmt ifElseStatement () {
		Expr condition;
		
		if (match (TokenType.LEFT_BRACE)) {
			condition = expression();
			consume (TokenType.RIGHT_BRACE, "Expected ']' at end of condition");
		} else {
			condition = expression();
		}

		consume (TokenType.LEFT_BRACE, "Expected '[' before true block");
		List<Stmt> thenBlock = block();
		consume (TokenType.LEFT_BRACE, "Expected '[' before false block");
		List<Stmt> elseBlock = block();
		return new Stmt.IfElse(condition, thenBlock, elseBlock);

	}

	private List<Stmt> block () {
		List<Stmt> statements = new ArrayList<>();
		while(!check(TokenType.RIGHT_BRACE)&&!isAtEnd()) {
			statements.add (declarations());
		}

		consume (TokenType.RIGHT_BRACE, "Expected ']' at closing of the block");
		return statements;
	}

	//printing work
	private Stmt printStatement() {
		Expr value = expression();
		//blank space after it ends
		return new Stmt.Print(value);
	}

	//default statement
	private Stmt expressionStatement() {
		Expr value = expression();
		//blank space after it ends
		return new Stmt.Expression(value);
	}

	//expression = equality
	private Expr expression () {
		return random();
	}

	private Expr random () {
		Expr expr = or();
		while (match(TokenType.RANDOM)) {
			expr = new Expr.Random (expr);
		}
		return expr;
	}
	private Expr or () {
		Expr expr = and();

		while (match (TokenType.OR)) {
			Token operator = previous();
			Expr right = and();
			expr =  new Expr.Logical (expr, operator, right);
		}

		return expr;
	}

	private Expr and () {
		Expr expr = equality();

		while (match (TokenType.AND)) {
			Token operator = previous();
			Expr right = equality();
			expr =  new Expr.Logical (expr, operator, right);
		}

		return expr;

	}

	//equality/=comparision((!=|==)comparision)*
	private Expr equality () {
		Expr expr = comparision ();
		
		while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparision();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	//comparison = addition ( ( ">" | ">=" | "<" | "<=" ) addition )*

	private Expr comparision () {
		Expr expr = addition ();

		while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
			Token operator = previous();
			Expr right = addition();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	// addition=multiplication ( ( "-" | "+" ) multiplication )* ;
	
	private Expr addition () {
		Expr expr = multiplication ();

		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = multiplication();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}	
	
	//multiplication = unary ( ( "/" | "*" ) unary )* ;

	private Expr multiplication () {
		Expr expr = unary ();

		while (match(TokenType.SLASH, TokenType.STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}
	
	//unary= ( "!" | "-" ) unary | primary ;

	private Expr unary () {                     

	    if (match(TokenType.BANG, TokenType.MINUS)) {                
			Token operator = previous();           
			Expr right = unary();                  
			return new Expr.Unary(operator, right);
	    }

	    return primary();                        
  	}
	
	//primary = NUMBER | STRING | "false" | "true" | "null" | "(" expression ")" | IDENTIFIER 

	private Expr primary () {
		if (match(TokenType.FALSE)) {
			return new Expr.Literal(false);
		}
		if (match(TokenType.TRUE)) {
			return new Expr.Literal(true);
		}
		if (match(TokenType.NULL)) {
			return new Expr.Literal(null);
		}
		if (match(TokenType.NUMBER, TokenType.STRING)) {
			return new Expr.Literal(previous().literal);
		}
		if (match(TokenType.LEFT_BRACE)) {
			Expr expr = expression ();
			consume (TokenType.RIGHT_BRACE, "Expecting ] after expression");
			return new Expr.Grouping(expr);
		}
		//for identifiers
		if (match(TokenType.IDENTIFIER)) {
			return new Expr.Variable(previous());
		}

		if (match(TokenType.RANDOM)) {
			return new Expr.Random(expression());
		}

		throw error(peek(), "Expect expression");
	}

	private Token consume ( TokenType type, String errorMessage) {

		if(check(type)) {
			return advance();
		}
		throw error(peek(), errorMessage);
	}

	private ParseError error (Token token, String errorMessage) {
		Logo.error(token, errorMessage);
		return new ParseError();
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().line != peek().line ) {
				return;
			}

			switch (peek().type) {
				case IF:
				case FOR:
				case UNTIL:
				case DOUNTIL:
				case WHILE:
				case DOWHILE:
				case REPEAT:
				case RUN:
				case FOREVER:
				case FORWARD:
				case BACK:
				case RIGHT:
				case LEFT:
				case CS:
				case CLEARSCREEN:
				case TO:
				case END:
				case SHOW:
				case MAKE:
					return;
			}

			advance();
		}
	}

}


class Environment {

	private final Map<String, Object> values = new HashMap<>();
	private Environment enclosing;

	public Environment() {
		this.enclosing = null;
	}

	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	public void define (String name, Object value) {
		values.put (name, value);
	}

	public Object get (Token name) {
		if (values.containsKey(name.lexeme.substring(1))) {
			return values.get(name.lexeme.substring(1));
		}

		if (enclosing!=null) return enclosing.get(name);

		throw new RuntimeError (name, "Undefined variable :" + name.lexeme.substring(1));
	}

	public void assign(Token name, Object value) {            
	
	    if (values.containsKey(name.lexeme.substring(1))) {           
	    	values.put(name.lexeme.substring(1), value);                
	    	return;                                        
	    }

	    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
}


class Graphics {

	public double move=0, turn=0;
	public boolean pd = true;
	public boolean turtle = true;
	public double color = 0;
	public double pensize = 1;
	public Graphics (double move, double turn, boolean pd, boolean turtle, double color, double pensize) {
		this.move = move;
		this.turn = turn;
		this.pd = pd;
		this.turtle = turtle;
		this.color = color;
		this.pensize = pensize;
	}

	public String toString() {                                      
    	return move + " " + turn + " " + pd+ " " + turtle+ " " + color+ " " + pensize;                   
  	}
}

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	private Environment environment = new Environment();

	public Object print (Expr expr) {
		return expr.accept(this);
	}

	//wrapper around over-riding methods to provide exception handling
	public void interpret (List<Stmt> statements) {
		try {

			for(Stmt statement: statements) {
				execute(statement);
			}
			// Object value = evaluate (expression);
			// System.out.println(stringify(value));
		} catch (RuntimeError error) {
			Logo.runtimeError(error);
		}

	}

	private void execute (Stmt stmt) {
		stmt.accept(this);
	}

	private String stringify (Object value) {
		if (value == null) return "null";
		if (value instanceof Double) {
			String text = value.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length()-2);
			}
			return text;
		}

		return value.toString();
	}

	//Overriding Expression interface methods
	@Override
	public Object visitLiteralExpr (Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitGroupingExpr (Expr.Grouping expr) {
		return evaluate(expr.expression);
	}	

	private Object evaluate (Expr expr) {
		return expr.accept(this);
	}

	@Override
	public Object visitUnaryExpr (Expr.Unary expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double)right;
			case BANG:
				return !isTruthy(right);
		}

		//for java syntax
		return null;
	}

	//Logical operators
	@Override
	public Object visitLogicalExpr (Expr.Logical expr) {
		Object left = evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			if (isTruthy(left)) return left;
		} else {
			if (!isTruthy (left)) return left;
		}

		return evaluate(expr.right);
	}


	@Override
	public Object visitRandomExpr (Expr.Random expr) {
		
		double limit = (double)evaluate(expr.limit);
		limit = Math.random()*limit;



		return Math.floor(limit);

	}

	private void checkNumberOperand (Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number");

	}

	private boolean isTruthy(Object object) {

		if(object==null) return false;
		if(object instanceof Boolean) return (boolean)object;
		return true;

	}

	@Override
	public Object visitBinaryExpr (Expr.Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left-(double)right;
			case PLUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left+(double)right;
			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double)left/(double)right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double)left*(double)right;
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double)left>(double)right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left>=(double)right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left<(double)right;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left<=(double)right;
			case BANG_EQUAL: return !isEqual(left, right);
      		case EQUAL_EQUAL: return isEqual(left, right);

		}

		//Unreachable
		return null;

	}

	@Override
	public Object visitVariableExpr (Expr.Variable expr) {
		return environment.get (expr.name);

	}

	private void checkNumberOperands(Token operator, Object left, Object right) {   
	    if (left instanceof Double && right instanceof Double) return;
	    
	    throw new RuntimeError(operator, "Operands must be numbers");
  	} 

	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null) return true;
		if (left==null) return false;

		return left.equals(right);
	}


	//Overriding Statements interface methods

	//Evalutes expressions
	@Override
	public Void visitExpressionStmt (Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;

	}

	// Evalutes printing statements
	@Override
	public Void visitPrintStmt (Stmt.Print stmt) {

		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
		
	}


	//Stores variables
	@Override
	public Void visitVarStmt (Stmt.Var stmt) {

		Object value = null;
		if (stmt.initializer !=null) {
			value = evaluate (stmt.initializer);
		}

		environment.define(stmt.name.lexeme, value);
		return null;
	}

	//excutes block and introduces new scope
	@Override
	public Void visitBlockStmt (Stmt.Block stmt) {

		executeBlock(stmt.statements, new Environment(environment));
 		return null;
	}

	void executeBlock (List<Stmt> statements, Environment environment) {

		Environment previous = this.environment;
		try {
			this.environment = environment;
			for (Stmt statement: statements) {
				execute (statement);
			}
		} finally {
			this.environment = previous;
		}

	}

	@Override
	public Void visitIfStmt (Stmt.If stmt) {

		if (isTruthy(evaluate(stmt.condition))) {
			executeBlock (stmt.thenBlock, new Environment(environment));
		}

		return null;

	}

	@Override
	public Void visitIfElseStmt (Stmt.IfElse stmt) {

		if (isTruthy(evaluate(stmt.condition))) {
			executeBlock (stmt.thenBlock, new Environment(environment));
		} else {
			executeBlock (stmt.elseBlock, new Environment(environment));
		}

		return null;

	}

	@Override
	public Void visitWhileStmt (Stmt.While stmt) {
		
		while (isTruthy(evaluate(stmt.condition))) {
	    	executeBlock(stmt.block, new Environment(environment));                       
	    }                                           
	    return null; 

	}

	@Override
	public Void visitRepeatStmt (Stmt.Repeat stmt) {
		double i=0;
		double condtn = (double)evaluate(stmt.condition);
		while (i<condtn) {
			executeBlock (stmt.block, new Environment(environment));
			++i;
		}

		return null;

	}

	@Override
	public Void visitForwardsStmt (Stmt.Forwards stmt) {
		Logo.hasGraphics = true;
		if (stmt.forward) {
			Logo.graphicSets.add (new Graphics((double)evaluate(stmt.value), 0, Logo.pd, Logo.turtle, Logo.color, Logo.pensize));	
		} else {
			Logo.graphicSets.add (new Graphics(-(double)evaluate(stmt.value), 0, Logo.pd, Logo.turtle, Logo.color, Logo.pensize));
		}
		
		return null;
	}

	@Override
	public Void visitRightStmt (Stmt.Right stmt) {
		Logo.hasGraphics = true;
		if (stmt.forward) {
			Logo.graphicSets.add (new Graphics(0,(double)evaluate(stmt.value), Logo.pd, Logo.turtle, Logo.color, Logo.pensize));	
		} else {
			Logo.graphicSets.add (new Graphics(0,-(double)evaluate(stmt.value), Logo.pd, Logo.turtle, Logo.color, Logo.pensize));
		}
		
		return null;
	}

	@Override
	public Void visitPenStmt (Stmt.Pen stmt) {
		Logo.hasGraphics = true;
		Logo.pd = stmt.pd;
		
		return null;
	}

	@Override
	public Void visitTurtleStmt (Stmt.Turtle stmt) {
		Logo.hasGraphics = true;
		Logo.turtle = stmt.tl;
		
		return null;
	}

	@Override
	public Void visitPenColorStmt (Stmt.PenColor stmt) {
		Logo.hasGraphics = true;
		Logo.color = (double)evaluate(stmt.color);
		
		return null;
	}

	@Override
	public Void visitPenSizeStmt (Stmt.PenSize stmt) {
		Logo.hasGraphics = true;
		Logo.pensize = (double)evaluate(stmt.pensize);
		
		return null;
	}

	

}