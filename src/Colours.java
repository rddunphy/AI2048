import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Colours {
	
	private Map<Integer, Color> colours;

	public Colours() {
		colours = new HashMap<>();
		colours.put(2, new Color(133, 193, 233));
		colours.put(4, new Color(52, 152, 219));
		colours.put(8, new Color(40, 116, 166));
		colours.put(16, new Color(20, 143, 119));
		colours.put(32, new Color(17, 122, 101));
		colours.put(64, new Color(22, 160, 133));
		colours.put(128, new Color(115, 198, 182));
		colours.put(256, new Color(125, 206, 160));
		colours.put(512, new Color(130, 224, 170));
		colours.put(1024, new Color(46, 204, 113));
		colours.put(2048, new Color(35, 155, 86));
		colours.put(4096, new Color(183, 149, 11));
		colours.put(8192, new Color(241, 196, 15));
		colours.put(16384, new Color(243, 156, 18));
		colours.put(32768, new Color(230, 126, 34));
		colours.put(65536, new Color(211, 84, 0));
		colours.put(131072, new Color(192, 57, 43));
	}
	
	public Color getColour(int n) {
		if (colours.containsKey(n))
			return colours.get(n);
		return Color.BLACK;
	}
	
}
