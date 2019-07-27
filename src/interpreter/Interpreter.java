package logo.interpreter;

import logo.parser.Expr;
import logo.parser.Stmt;
import logo.lexer.TokenType;
import logo.lexer.Token;
import logo.errorHandlers.RuntimeError;
import logo.interpreter.Environment;
import logo.interpreter.Graphics;
import logo.Logo;
import java.util.List;
import java.lang.Math;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

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