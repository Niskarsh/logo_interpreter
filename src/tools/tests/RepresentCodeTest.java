package logo.tools.tests;

import logo.parser.Expr;
import logo.lexer.Token;
import logo.lexer.TokenType;
import logo.tools.prettyPrinter.AstPrinter;

public class RepresentCodeTest {

	public static void main(String[] args) {                 
	    Expr expression = new Expr.Binary(                     
	        new Expr.Unary(                                    
	            new Token(TokenType.MINUS, "-", null, 1),      
	            new Expr.Literal(123)),                        
	        new Token(TokenType.STAR, "*", null, 1),           
	        new Expr.Grouping(                                 
	            new Expr.Literal(45.67)));

	    System.out.println(new AstPrinter().print(expression));
	} 
}