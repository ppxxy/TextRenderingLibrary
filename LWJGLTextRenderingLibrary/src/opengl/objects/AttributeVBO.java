package opengl.objects;

import org.lwjgl.opengl.GL20;

/**
 * A class extending {@link objects.VBO VBO}.<br>
 * This class is used to store data for {@link objects.VAO VAO}.<br>
 * Elements of this class also contain information of Attribute's
 * {@link objects.AttributeVBO#attrSize size} and
 * {@link objects.AttributeVBO#dataType dataType}.<br>
 * Created by Kim on 16.3.2018.
 */

public class AttributeVBO extends VBO {

	/**
	 * Number of values defining one attribute for one vertex.<br>
	 * Examples:<br>
	 * <ul>
	 * <li>For 3D {@code normals} this is 3; ({@code x}, {@code y},
	 * {@code z})-coordinates.</li>
	 * <li>For texture coordinates this is 2; ({@code u},
	 * {@code v})-coordinates.</li>
	 * </ul>
	 */
	private int attrSize;
	/**
	 * Variable defining the dataType of this attribute. Default value is
	 * {@link org.lwjgl.opengl.GL11.GL_FLOAT GL11.GL_FLOAT}.
	 */
	private int dataType;

	/**
	 * {@code Constructor} creating VBO with attributeSize and dataType included.
	 * 
	 * @param type
	 *            {@link objects.VBO#type Type}value of this VBO.
	 *            {@link org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER GL15.GL_ARRAY_BUFFER}
	 *            for ordinary VBO.
	 * @param attrSize
	 *            Number of values defining one attribute for one vertex.
	 * @param dataType
	 *            Variable defining the dataType of this attribute.
	 */
	private AttributeVBO(int type, int attrSize, int dataType) {
		super(type);
		this.attrSize = attrSize;
		this.dataType = dataType;
	}

	/**
	 * Public method used to create {@code AttributeVBO}.
	 * 
	 * @param type
	 *            {@link objects.VBO#type Type}value of this VBO.
	 *            {@link org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER GL15.GL_ARRAY_BUFFER}
	 *            for ordinary VBO.
	 * @param attrSize
	 *            Number of values defining one attribute for one vertex.
	 * @param dataType
	 *            Variable defining the dataType of this attribute.
	 * @return Created {@link objects.AttributeVBO AttributeVBO}
	 */
	public static AttributeVBO create(int type, int attrSize, int dataType) {
		return new AttributeVBO(type, attrSize, dataType);
	}

	/**
	 * Method to bind vertex attribute pointer with only one parameter.
	 * 
	 * @param attribute
	 *            Index of the vertex attribute.
	 */
	public void setPointer(int attribute) {
		GL20.glVertexAttribPointer(attribute, attrSize, dataType, false, 0, 0);
	}
}
