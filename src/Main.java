import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {

		Board b = new Board();
		System.out.println(b);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			String s;
			try {
				System.out.print(">");
				s = br.readLine().trim();
				switch (s) {
				case "w":
					b.move(Direction.UP);
					break;
				case "a":
					b.move(Direction.LEFT);
					break;
				case "s":
					b.move(Direction.DOWN);
					break;
				case "d":
					b.move(Direction.RIGHT);
					break;
				case "exit":
					System.out.println("Bye!");
					System.exit(0);
					break;
				default:
					System.out.println("Enter w, a, s, d, or exit.");
				}
				System.out.println();
				System.out.println(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
