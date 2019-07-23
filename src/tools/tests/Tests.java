package logo.tools.tests;

import java.io.IOException;
import logo.lexer.Scanner;

class Tests {
	public static void main(String args[])throws IOException {
		Scanner chk = new Scanner("to tree :size\nif :size < 5 [forward :size back :size stop]\nforward :size/3\nleft 30 tree :size*2/3 right 30\nforward :size/6\nright 25 tree :size/2 left 25\nforward :size/3\nright 25 tree :size/2 left 25\nforward :size/6\nback :size\nend\nclearscreen\ntree 150");
		// var chk = new Scanner("show \"aa \"s")
 		System.out.println(chk.scanTokens());
	}
}