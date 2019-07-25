package logo.parser;

import logo.lexer.Token;
import logo.lexer.TokenType;
import logo.lexer.Scanner;
import logo.parser.Expr;
import logo.errorHandlers.ParseError;
import logo.parser.Stmt;
import logo.Logo;
import java.util.List;
import java.util.ArrayList;


public class Parser {
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
		if (match(TokenType.SHOW)) return printStatement();
		if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());


		return expressionStatement();
	}

	private List<Stmt> block () {
		List<Stmt> statements = new ArrayList<>();
		while(!check(TokenType.RIGHT_BRACE)&&!isAtEnd()) {
			statements.add (declarations());
		}

		consume (TokenType.RIGHT_BRACE, "Expected ] at closing of the block");
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
		return equality();
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
				case FD:
				case FORWARD:
				case BK:
				case BACK:
				case RT:
				case RIGHT:
				case LT:
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