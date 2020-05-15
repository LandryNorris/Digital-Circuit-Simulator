import java.io.IOException;

public class Application {

	static Settings settings = new Settings();
	
	static {
		try {
			System.out.println("loading settings");
			settings.loadFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
