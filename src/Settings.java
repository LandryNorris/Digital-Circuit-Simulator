import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Settings {
	//global properties
	String workspaceDirectory = "";
	boolean shouldAutoSave = false;
	int autoSaveTime = 60000; //milliseconds
	boolean allowKeyCreate = true; //allow user to create components using the keyboard.
	
	//last state properties
	int screenWidth = 0;
	int screenHeight = 0;
	
	String path = "appSettings.properties";
	
	Settings loadFromFile() throws IOException {
		File file = new File(path);
		if(!file.exists()) {
			System.err.println(path + " doesn't currently exist. Creating now. This is expected when the app is first run.");
			file.createNewFile();
		}
		InputStream in = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(in);
		
		workspaceDirectory = properties.getProperty("workspaceDirectory", "");
		shouldAutoSave = Boolean.parseBoolean(properties.getProperty("shouldAutoSave", "false"));
		autoSaveTime = Integer.parseInt(properties.getProperty("autoSaveTime", "60000"));
		allowKeyCreate = Boolean.parseBoolean(properties.getProperty("allowKeyCreate", "false"));
		
		screenWidth = Integer.parseInt(properties.getProperty("screenWidth", "1000"));
		screenHeight = Integer.parseInt(properties.getProperty("screenHeight", "500"));
		return this;
	}
	
	void writeToFile() throws IOException {
		File file = new File(path);
		if(!file.exists()) {
			System.err.println(path + " doesn't currently exist. Creating now. This is unexpected and may be due to moving the exe or appSettings file.");
			file.createNewFile();
		}
		OutputStream out = new FileOutputStream(file);
		Properties properties = new Properties();
		properties.setProperty("workspaceDirectory", workspaceDirectory);
		properties.setProperty("shouldAutoSave", ""+shouldAutoSave);
		properties.setProperty("autoSaveTime", ""+autoSaveTime);
		properties.setProperty("allowKeyCreate", ""+allowKeyCreate);

		properties.setProperty("screenWidth", ""+screenWidth);
		properties.setProperty("screenHeight", ""+screenHeight);
		
		properties.store(out, "Settings for DigitalCircuitSimulator");
	}
	
	//for debugging purposes
	void print() {
		Properties properties = new Properties();
		properties.setProperty("workspaceDirectory", workspaceDirectory);
		properties.setProperty("shouldAutoSave", ""+shouldAutoSave);
		properties.setProperty("autoSaveTime", ""+autoSaveTime);
		properties.setProperty("allowKeyCreate", ""+allowKeyCreate);

		properties.setProperty("screenWidth", ""+screenWidth);
		properties.setProperty("screenHeight", ""+screenHeight);
		
		try {
			properties.store(System.out, "Settings for DigitalCircuitSimulator");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
