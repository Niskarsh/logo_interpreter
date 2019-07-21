
//Token info  	
class Token {
	
	constructor(type, lexeme, literal, line) {
    	this.type = type;                                             
    	this.lexeme = lexeme;                                         
    	this.literal = literal;                                       
    	this.line = line;                                             
	}

	toString() {
		return (this.type+" "+this.lexeme+" "+this.literal)
	}
}

// var b = new Token('a','a', 2,4)
// console.log(b.toString())
exports.Token = Token