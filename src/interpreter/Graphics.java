package logo.interpreter;

public class Graphics {

	public double move=0, turn=0;
	public boolean pd = true;
	public boolean turtle = true;
	public double color = 0;
	public double pensize = 1;
	public Graphics (double move, double turn, boolean pd, boolean turtle, double color, double pensize) {
		this.move = move;
		this.turn = turn;
		this.pd = pd;
		this.turtle = turtle;
		this.color = color;
		this.pensize = pensize;
	}

	public String toString() {                                      
    	return move + " " + turn + " " + pd+ " " + turtle+ " " + color+ " " + pensize;                   
  	}
}