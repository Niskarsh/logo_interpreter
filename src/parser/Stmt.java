package logo.parser;

import logo.parser.Expr;
import logo.lexer.Token;
import java.util.List;

public abstract class Stmt {

	public interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitPrintStmt(Print stmt);
		R visitVarStmt(Var stmt);	
		R visitBlockStmt(Block stmt);
		R visitIfStmt(If stmt);
		R visitIfElseStmt(IfElse stmt);
		R visitRepeatStmt (Repeat stmt);
		R visitWhileStmt (While stmt);
		R visitForwardsStmt (Forwards stmt);
		R visitRightStmt (Right stmt);
		R visitPenStmt (Pen stmt);
	}

	public abstract <R> R accept (Visitor<R> visitor);

	public static class Expression extends Stmt {

		public final Expr expression;
		public Expression (Expr expression) {
			this.expression = expression;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

	}

	public static class Print extends Stmt {
		
		public final Expr expression;
		public Print (Expr expression) {
			this.expression = expression;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	public static class Var extends Stmt {

		public final Token name;
		public final Expr initializer;
		public Var (Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}


	}

	public static class Block extends Stmt {

		public final List<Stmt> statements;

		public Block (List<Stmt> statements) {
			this.statements = statements;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

	}

	public static class If extends Stmt {

		public final List<Stmt> thenBlock;
		public final Expr condition;

		public If (Expr condition, List<Stmt> thenBlock) {
			this.condition = condition;
			this.thenBlock = thenBlock;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

	}

	public static class IfElse extends Stmt {

		public final List<Stmt> thenBlock, elseBlock;
		public final Expr condition;

		public IfElse (Expr condition, List<Stmt> thenBlock, List<Stmt> elseBlock) {
			this.condition = condition;
			this.thenBlock = thenBlock;
			this.elseBlock = elseBlock;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitIfElseStmt(this);
		}

	}

	public static class Repeat extends Stmt {

		public final List<Stmt> block;
		public final Expr condition;

		public Repeat (Expr condition, List<Stmt> block) {
			this.condition = condition;
			this.block = block;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitRepeatStmt(this);
		}

	}

	public static class While extends Stmt {

		public final List<Stmt> block;
		public final Expr condition;

		public While (Expr condition, List<Stmt> block) {
			this.condition = condition;
			this.block = block;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

	}

	public static class Forwards extends Stmt {

		public final Expr value;
		public boolean forward = true;

		public Forwards (Expr value, boolean forward) {
			this.value = value;
			this.forward = forward;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitForwardsStmt(this);
		}
	}

	public static class Right extends Stmt {

		public final Expr value;
		public boolean forward = true;

		public Right (Expr value, boolean forward) {
			this.value = value;
			this.forward = forward;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitRightStmt(this);
		}
	}

	public static class Pen extends Stmt {

		public boolean pd;

		public Pen (boolean pd) {
			this.pd = pd;
		}

		public <R> R accept (Visitor<R> visitor) {
			return visitor.visitPenStmt(this);
		}
	}
}