package opengl.main;

import java.io.File;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import interfaces.TextInterface;
import rendering.TextRenderer;
import text.font.FontAtlas;
import text.font.FontAtlasGenerator;
import text.font.LetterLoadOptions;
import tools.LoadNatives;

public class Main {

	private static final int FONT_SIZE = 44;
	public static final int EMPTY_SPACING = 1;
	
	public static void main(String[] args) {
		if(ClassLoader.getSystemResource("opengl/main/Main.class").toString().startsWith("jar:")){
			LoadNatives.loadAll();
		} else{
			System.setProperty("org.lwjgl.librarypath", new File("src/lib/jars/windows").getAbsolutePath());
		}
		
		DisplayManager.createDisplay();
		
		FontAtlasGenerator generator = new FontAtlasGenerator();
		FontAtlas fontAtlas = generator.generateFontAtlas(FONT_SIZE, Main.class.getResourceAsStream("/res/fonts/BASKVILL.ttf"), LetterLoadOptions.EXTENDED_LETTER_GROUP);
		String text="Line one.\n"
				+ "Second line that is pretty long and has lots of content.\n"
				+ "Reflective surfaces work as a gate between those two dimensions.\n"
				+ "Strong spirits are capable of interacting with other spirits that are\n"
				+ "trapped between those two dimensions, like yourself.\n"
				+ "So you better stay cautious.";
		TextInterface textInterface = new TextInterface(fontAtlas, text, 0.1f, new Vector3f(1f, 0f, 0f));
		
		/*
		 * Note! Enable blend after fonts have been generated. When generating fonts - disable blend.
		 */
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		
		TextRenderer textRenderer = new TextRenderer();
		while(!Display.isCloseRequested()) {
			DisplayManager.updateDisplay();
			GL11.glClearColor(1, 1, 1, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			textRenderer.render(textInterface);
		}
	}
	
	public static void getError() {
		int error;
		if((error = GL11.glGetError()) != 0) {
			System.out.println("OpenGL error: " + error);
		}
	}

}
