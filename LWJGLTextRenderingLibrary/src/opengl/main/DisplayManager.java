package opengl.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static final int FPS_CAP = 120;
	private static final String TITLE = "Display";

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(){

		ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true).withProfileCore(true);

		try {
			DisplayMode displayMode = Display.getDisplayMode();
			Display.setDisplayMode(displayMode);
			Display.setResizable(true);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(TITLE);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GL11.glClearColor(0f, 0f, 0f, 1f);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		lastFrameTime = getCurrentTime();
	}

	public static boolean updateDisplay(){
		boolean resized = false;
		if(Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			resized = true;
		}
		Display.sync(FPS_CAP);
		Display.update();
		long currentTime = getCurrentTime();
		delta = (float)(currentTime - lastFrameTime) / 1000f;
		lastFrameTime = currentTime;
		return resized;
	}

	public static void closeDiplay(){
		Display.destroy();
	}

	public static float getFrameTime() {
		return delta;
	}

	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}