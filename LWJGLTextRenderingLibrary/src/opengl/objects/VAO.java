package opengl.objects;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * VertexArrayObject
 * {@linkplain https://www.khronos.org/opengl/wiki/Vertex_Specification}<br>
 * Wrapper for OpenGL VertexArrayObject. Contains references to all the
 * VertexBufferObjects contained within this VAO. Created by Kim on 16.3.2018.
 */

public class VAO {

	public static final int BYTES_PER_FLOAT = 4;

	/**
	 * This VAO's identifying index within OpenGL context.
	 */
	public final int id;

	/**
	 * {@code HashMap} where KEY = VBO's index and VALUE is the VBO.
	 */
	protected HashMap<Integer, AttributeVBO> vbos = new HashMap<>();
	/**
	 * {@link objects.VBO VBO} that stores object's indices.
	 */
	private VBO indexVBO;
	/**
	 * Number of indices within this object.
	 */
	private int indexCount;

	/**
	 * Default constructor that creates VAO on OpenGL.
	 */
	protected VAO() {
		this.id = GL30.glGenVertexArrays();
	}

	/**
	 * Public method to create VAO outside this class.
	 * 
	 * @return Newly created VAO.
	 */
	public static VAO create() {
		return new VAO();
	}

	/**
	 * Get the number of indices in this VAO. This is equal to the number of
	 * elements in VBO containing index data.<br>
	 * Equal to number of array elements in
	 * {@link objects.VAO#createIndexBuffer(int[]) createIndexBuffer(int[])}-method
	 * call.
	 * 
	 * @return Number of indices.
	 */
	public int getIndexCount() {
		return this.indexCount;
	}

	/**
	 * A method to create IndexBuffer for this VAO. This creates VBO and stores
	 * index data in it.
	 * 
	 * @param indices
	 *            Array containing index data.
	 */
	public void createIndexBuffer(int[] indices) {
		this.indexVBO = VBO.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
		indexVBO.bind();
		indexVBO.storeData(indices);
		this.indexCount = indices.length;
	}

	public void createAttribute(int attribute, float[] data, int attrSize) {
		AttributeVBO vbo = AttributeVBO.create(GL15.GL_ARRAY_BUFFER, attrSize, GL11.GL_FLOAT);
		vbo.bind();
		vbo.storeData(data);
		GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
		vbo.unbind();
		vbos.put(attribute, vbo);
	}

	public void createAttribute(int attribute, int[] data, int attrSize) {
		AttributeVBO vbo = AttributeVBO.create(GL15.GL_ARRAY_BUFFER, attrSize, GL11.GL_INT);
		vbo.bind();
		vbo.storeData(data);
		GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * BYTES_PER_FLOAT, 0);
		vbo.unbind();
		vbos.put(attribute, vbo);
	}

	public void bind(int... attributes) {
		GL30.glBindVertexArray(id);
		for (int i : attributes) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void bindWithPointer(int[] attributes, int[] targets) {
		if (indexVBO != null) {
			indexVBO.bind();
		}
		for (int i = 0; i < attributes.length; i++) {
			vbos.get(attributes[i]).bind();
			GL20.glEnableVertexAttribArray(attributes[i]);
			vbos.get(attributes[i]).setPointer(targets[i]);
		}
	}

	public void unbind(int... attributes) {
		for (int i : attributes) {
			GL20.glDisableVertexAttribArray(i);
		}
		GL30.glBindVertexArray(0);
	}

	public void delete() {
		GL30.glDeleteVertexArrays(id);
		deleteContent();
	}
	
	public void deleteContent() {
		for (VBO vbo : this.vbos.values()) {
			vbo.delete();
		}
		if(indexVBO != null) {
			indexVBO.delete();
		}
	}
}
