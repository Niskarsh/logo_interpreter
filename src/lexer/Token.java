package logo.lexer;

//Token info  	

public class Token {

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

// var b = new Token('a','a', 2,4)
// console.log(b.toString())
