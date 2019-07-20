const { TokenType } = require("./constants.js")

export const TokenType = {
	LEFT_BRACE, RIGHT_BRACE, COMMA, MINUS, PLUS, SLASH, STAR, BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
	LESS, LESS_EQUAL, IDENTIFIER, STRING, NUMBER, ELSE, FALSE, IF, FOR,UNTIL, DOUNTIL, WHILE, DOWHILE, REPEAT, RUN, 
	FOREVER, REPCOUNT, TRUE, MOUSEPOS, CLICKPOS, BACKGROUND, BG, GETSCREENCOLOR, GETSC, FD, FORWARD, BK, BACKGROUND,
	RT, RIGHT, LT, LEFT, CS, CLEARSCREEN, PU, PENUP, PD, PENDOWN, TO, END, PENMODE, PAINT, ERASE, REVERSE, SETPENCOLOR,
	SETPENSIZE, SETBACKGROUND, SETSCREENCOLOR, SETSC, SETPOS, SETXY, SETX, SETY, AND, OR, NOT, EOF
}
//Token info  	
class Token {
	
	var type, lexeme, literal, line

	Token(type, lexeme, literal, line) {
    	this.type = type;                                             
    	this.lexeme = lexeme;                                         
    	this.literal = literal;                                       
    	this.line = line;                                             
	}

	toString() {
		return (type+" "+lexeme+" "+literal)
	}
}

//Scanner class
class Scanner {
	var source="", tokens=[], start = 0, current = 0, line = 1

	Scanner (source) {
		this.source = source
	}


	scanTokens () {

    	while (!isAtEnd()) {                            
      // We are at the beginning of the next lexeme.
	      start = current;                              
	      scanToken();                                  
	    }

	    tokens.push(new Token(EOF, "", null, line));     
	    return tokens;                                  
  	}

	isAtEnd () {
		return current>=source.length
	}

	advance () {
		return source.charAt(current++)
	}

	addToken (type) {                
    	addToken(type, null);                                
  	}                                                      

    addToken(TokenType type, Object literal) {
   	    String text = source.substring(start, current);      
    	tokens.add(new Token(type, text, literal, line));    
  }  

	scanToken () {
		var c = advance()
		switch (c) {
			case '[': addToken(TokenType.LEFT_BRACE); break;
			case ']': addToken(TokenType.RIGHT_BRACE); break;
			case ',': addToken(TokenType.COMMA); break;          
		    case '-': addToken(TokenType.MINUS); break;          
		    case '+': addToken(TokenType.PLUS); break;           
		    case '*': addToken(TokenType.STAR); break; 

		}

	}
}