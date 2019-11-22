package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoadNatives {

	private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") +"lib\\";

	private static final String[] WINDOWS_NATIVES = new String[]{"jinput-dx8_64.dll", "jinput-dx8.dll",
			"jinput-raw_64.dll", "jinput-raw.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll"};
	private static final String[] LINUX_NATIVES = new String[]{"libjinput-linux.so", "libjinput-linux64.so",
			"liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so"};
	
	public static void loadAll() {
		File file = new File(TEMP_PATH);
		file.mkdirs();
		file.deleteOnExit();
		String os = System.getProperty("os.name").toLowerCase();
		String[] natives;
		String filePath;
		if(os.contains("windows")) {
			natives = WINDOWS_NATIVES;
			filePath = "/lib/jars/windows/";
		} else {
			natives = LINUX_NATIVES;
			filePath = "/lib/jars/linux/";
		}
		for(String s : natives) {
			loadNative(filePath, s);
		}
		System.setProperty("org.lwjgl.librarypath", TEMP_PATH);
	}
	
	private static void loadNative(String filePath, String name){
		try {
			InputStream in = LoadNatives.class.getResourceAsStream(filePath +name);
			File fileOut = new File(TEMP_PATH +name);
			FileOutputStream out = new FileOutputStream(fileOut);
			byte[] buf = new byte[1024];
		    int len;
		    while((len=in.read(buf))>0){
		    	out.write(buf,0,len);
		    }
			in.close();
			out.close();
			fileOut.deleteOnExit();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
