package opengl.objects;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class Texture {

	private static Map<Integer, Integer> boundTextures = new HashMap<Integer, Integer>();
	
	private final int id;
	private final int target;
	
	public Texture(int target) {
		this.id = GL11.glGenTextures();
		this.target = target;
	}

	public void bind() {
		Integer id = boundTextures.get(this.target);
		if(id == null || id != this.id) {
			GL11.glBindTexture(this.target, this.id);
			boundTextures.put(this.target, this.id);
		}
	}
	
}
