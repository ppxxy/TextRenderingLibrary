package interfaces;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import opengl.objects.VAO;
import text.font.FontAtlas;
import text.mesh.TextMeshCreator;
import text.mesh.TextMeshData;

public class TextInterface extends VAO{

	public static Vector4f DEFAULT_TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
	public static float DEFAULT_FONT_SIZE = 0.2f;
	private static Matrix4f DEFAULT_TRANSFORMATION_MATRIX = new Matrix4f();
	
	private Vector4f color = DEFAULT_TEXT_COLOR;
	private float fontSize = DEFAULT_FONT_SIZE;
	private Matrix4f transformationMatrix = DEFAULT_TRANSFORMATION_MATRIX;
	private FontAtlas fontAtlas;
	private String content;
	
	private int vertexCount;
	private boolean previousContent = false;
	
	static {
		DEFAULT_TRANSFORMATION_MATRIX.translate(new Vector2f(0f, 0f));
		DEFAULT_TRANSFORMATION_MATRIX.scale(new Vector3f(1f, 1f, 1f));
	}
	
	public TextInterface(FontAtlas fontAtlas, String text, float fontSize, Vector3f color) {
		this.fontAtlas = fontAtlas;
		this.content = text;
		this.fontSize = fontSize;
		this.color = new Vector4f(color.x, color.y, color.z, 1f);
		generateText();
	}
	
	public TextInterface(FontAtlas fontAtlas, String text, float fontSize, Vector4f color) {
		this.fontAtlas = fontAtlas;
		this.content = text;
		this.fontSize = fontSize;
		this.color = color;
		generateText();
	}
	
	public TextInterface(FontAtlas fontAtlas, String text, float fontSize) {
		this.fontAtlas = fontAtlas;
		this.content = text;
		this.fontSize = fontSize;
		generateText();
	}
	
	public TextInterface(FontAtlas fontAtlas, String text) {
		this.fontAtlas = fontAtlas;
		this.content = text;
		generateText();
	}
	
	private void generateText() {
		if(previousContent) {
			//Clear previous data
		}
		TextMeshCreator textMeshCreator = new TextMeshCreator(fontAtlas);
		TextMeshData mesh = textMeshCreator.createTextMesh(this.content, this.fontSize, TextMeshCreator.CENTER, TextMeshCreator.CENTER);
		this.bind();
		this.createAttribute(0, mesh.getVertexPositions(), 2);
		this.createAttribute(1, mesh.getTextureCoords(), 2);
		this.vertexCount = mesh.getVertexCount();
		this.unbind();
		previousContent = true;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public void setColor(Vector3f color) {
		this.color = new Vector4f(color.x, color.y, color.z, 1f);
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public Matrix4f getTransformationMatrix() {
		return this.transformationMatrix;
	}

	public void bindTextInterface() {
		this.fontAtlas.bindTexture();
		this.bind(0, 1);
	}
	
	public void unbindTextInterface() {
		this.unbind(0, 1);
	}
}
