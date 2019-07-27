package logo.interpreter;

public class Graphics {

	public double move=0, turn=0;
	public boolean pd = true;
	public int color = 0;
	public int pensize = 1;
	public Graphics (double move, double turn, boolean pd, int color, int pensize) {
		this.move = move;
		this.turn = turn;
		this.pd = pd;
		this.color = color;
		this.pensize = pensize;
	}

	public String toString() {                                      
    	return move + " " + turn + " " + pd+ " " + color+ " " + pensize;                   
  	}
}