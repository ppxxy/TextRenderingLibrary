package opengl.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

/**
 * Created by Kim on 16.3.2018.
 */

public class VBO {

	private int id;
	/**
	 * Blabla
	 */
	private int type;

	protected VBO(int id, int type) {
		this.id = id;
		this.type = type;
	}

	protected VBO(int type) {
		this.id = GL15.glGenBuffers();
		this.type = type;
	}

	public static VBO create(int type) {
		return new VBO(type);
	}

	public void bind() {
		GL15.glBindBuffer(type, id);
	}

	public void unbind() {
		GL15.glBindBuffer(type, 0);
	}

	public void storeData(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}

	public void storeData(IntBuffer buffer) {
		GL15.glBufferData(type, buffer, GL15.GL_STATIC_DRAW);
	}

	public void storeData(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}

	public void storeData(FloatBuffer buffer) {
		GL15.glBufferData(type, buffer, GL15.GL_STATIC_DRAW);
	}

	public void delete() {
		GL15.glDeleteBuffers(id);
	}
}