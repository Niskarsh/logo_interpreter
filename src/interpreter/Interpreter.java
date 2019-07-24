package logo.interpreter;

import logo.parser.Expr;
import logo.lexer.TokenType;

public class Interpreter implements Expr.Visitor<Object> {


	public Object print (Expr expr) {
		return expr.accept(this);
	}
	//Overriding interface methods
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
				return -(double)right;
			case BANG:
				return !isTruthy(right);
		}

		//for java syntax
		return null;
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
				return (double)left-(double)right;
			case PLUS:
				return (double)left+(double)right;
			case SLASH:
				return (double)left/(double)right;
			case STAR:
				return (double)left*(double)right;
			case GREATER:
				return (double)left>(double)right;
			case GREATER_EQUAL:
				return (double)left>=(double)right;
			case LESS:
				return (double)left<(double)right;
			case LESS_EQUAL:
				return (double)left<=(double)right;
			case BANG_EQUAL: return !isEqual(left, right);
      		case EQUAL_EQUAL: return isEqual(left, right);

		}
		
		//Unreachable
		return null;

	}

	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null) return true;
		if (left==null) return false;

		return left.equals(right);
	}

}