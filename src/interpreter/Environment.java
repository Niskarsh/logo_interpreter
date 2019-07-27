package logo.interpreter;

import java.util.HashMap;
import java.util.Map;
import logo.lexer.Token;
import logo.errorHandlers.RuntimeError;

public class Environment {

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
