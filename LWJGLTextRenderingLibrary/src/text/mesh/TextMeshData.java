package text.mesh;

public class TextMeshData {

	private float[] vertexPositions;
	private float[] textureCoords;
	private float width;
	private float height;

	protected TextMeshData(float[] vertexPositions, float[] textureCoords, float width, float height) {
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
		this.width = width;
		this.height = height;
	}

	public float[] getVertexPositions() {
		return this.vertexPositions;
	}

	public float[] getTextureCoords() {
		return this.textureCoords;
	}

	public int getVertexCount() {
		return this.vertexPositions.length / 2;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight(){
		return this.height;
	}
}
