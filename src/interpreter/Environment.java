package logo.interpreter;

import java.util.HashMap;
import java.util.Map;
import logo.lexer.Token;
import logo.errorHandlers.RuntimeError;

public class Environment {

	private final Map<String, Object> values = new HashMap<>();

	public void define (String name, Object value) {
		values.put (name, value);
	}

	public Object get (Token name) {
		if (values.containsKey(name.lexeme.substring(1))) {
			return values.get(name.lexeme.substring(1));
		}

		throw new RuntimeError (name, "Undefined variable :" + name.lexeme.substring(1));
	}
}
