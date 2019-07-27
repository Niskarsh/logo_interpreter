package logo.lexer;

import java.util.ArrayList;                                               
import java.util.HashMap;                                                 
import java.util.List;                                                    
import java.util.Map;  

//Scanner class
public class Scanner {

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

// var chk = new Scanner("to tree :size\nif :size < 5 [forward :size back :size stop]\nforward :size/3\nleft 30 tree :size*2/3 right 30\nforward :size/6\nright 25 tree :size/2 left 25\nforward :size/3\nright 25 tree :size/2 left 25\nforward :size/6\nback :size\nend\nclearscreen\ntree 150")

// var chk = new Scanner("show \"aa \"s")
// console.log(chk.scanTokens())