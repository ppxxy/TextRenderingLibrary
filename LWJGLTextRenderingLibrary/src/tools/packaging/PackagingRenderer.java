package tools.packaging;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.ARBTextureRg;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import opengl.objects.FrameBufferObject;
import opengl.objects.VAO;
import rendering.ShaderProgram;
import rendering.uniforms.UniformFloat;
import rendering.uniforms.UniformInteger;
import rendering.uniforms.UniformMatrix;
import rendering.uniforms.UniformSampler;
import rendering.uniforms.UniformVec2;
import rendering.uniforms.UniformVec4;

public class PackagingRenderer {

	private PackagingShader shader;
	private static final Vector4f OCCUPIED_COLOR = new Vector4f(0f, 0f, 0f, 1f);
	
	private static final VAO vao = VAO.create();

	private FrameBufferObject frameBufferObject;

	
	private final Packaging packaging;
	
	static {
		vao.bind();
		vao.createAttribute(0, new float[] { -1, 1, -1, -1, 1, 1, 1, -1 }, 2);
		vao.unbind();
	}
	
	public PackagingRenderer(Packaging packaging) {
		this.packaging = packaging;
		this.shader = new PackagingShader();
		createFrameBufferObject();
	}
	
	public void createFrameBufferObject() {
		if(frameBufferObject != null) {
			frameBufferObject.delete();
		}
		frameBufferObject = FrameBufferObject.createFrameBuffer(packaging.getWidth(), packaging.getHeight());
		frameBufferObject.createTextureAttachment(GL30.GL_COLOR_ATTACHMENT0, ARBTextureRg.GL_R16, GL11.GL_RED, GL11.GL_FLOAT);
		frameBufferObject.createTextureAttachment(GL30.GL_COLOR_ATTACHMENT1, ARBTextureRg.GL_R16, GL11.GL_RED, GL11.GL_FLOAT);
		frameBufferObject.checkStatus();
		frameBufferObject.unbind();
	}
	
	public Vector2f renderArea(List<Rectangle> rectangles, Rectangle rectangleToAdd) {
		frameBufferObject.bind();
		//clear color
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.translate(new Vector2f((packaging.minX + packaging.maxX)/packaging.getWidth(), (packaging.minY + packaging.maxY)/packaging.getHeight()));
		transformationMatrix.scale(new Vector3f((packaging.maxX - packaging.minX + 2*rectangleToAdd.getWidth())/packaging.getWidth(), (packaging.maxY - packaging.minY + 2*rectangleToAdd.getHeight())/packaging.getHeight(), 1f));
		vao.bind(0);
		shader.bindColorShader(new Vector4f(1f, 1f, 1f, 1f));
		shader.transformationMatrixColor.loadMatrix(transformationMatrix);
		//Pixels within given boundaries get value 1f
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		shader.color.loadVec4(OCCUPIED_COLOR);
		//Pixels that are occupied get color 0f
		for(Rectangle r : rectangles) {
			transformationMatrix.setIdentity();
			transformationMatrix.translate(new Vector2f(r.getX()/packaging.getHalfWidth(), r.getY()/packaging.getHalfHeight()));
			transformationMatrix.scale(new Vector3f((float)r.getWidth()/packaging.getWidth(), (float)r.getHeight()/packaging.getHeight(), 1f));
			shader.transformationMatrixColor.loadMatrix(transformationMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		}
		shader.unbindShader();
		GL11.glFinish();
		//render area
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT1);
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		shader.bindDistanceShader();
		transformationMatrix.setIdentity();
		shader.transformationMatrixDistance.loadMatrix(transformationMatrix);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBufferObject.getAttachment(GL30.GL_COLOR_ATTACHMENT0));
		//Every 1f is replaced by distance.
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		shader.unbindShader();
		GL11.glFinish();
		
		//Lastly for each point that is not 0f we calculate average.
		shader.bindShapeShader(rectangleToAdd.getWidth(), rectangleToAdd.getHeight());
		transformationMatrix.setIdentity();
		shader.transformationMatrixShape.loadMatrix(transformationMatrix);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBufferObject.getAttachment(GL30.GL_COLOR_ATTACHMENT1));
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		
		//Then we read all the available options
		FloatBuffer buffer = frameBufferObject.getAllData(GL11.GL_RED, GL11.GL_FLOAT);
		
		vao.unbind(0);
		frameBufferObject.unbind();
		
		//and choose the one with the highest value.
		float highest = 0f;
		float f;
		int currentIndex = 0;
		int highestIndex = 0;
		while(buffer.hasRemaining()) {
			if((f = buffer.get()) > highest) {
				highest = f;
				highestIndex = currentIndex;
			}
			currentIndex++;
		}
		Vector2f optimal = new Vector2f(highestIndex%packaging.getWidth()+rectangleToAdd.getWidth()/2f-packaging.getHalfWidth(), highestIndex/packaging.getWidth()+rectangleToAdd.getHeight()/2f-packaging.getHalfHeight());
		//System.out.println("Highest is " +highest +" at index " +highestIndex +", coordinates: " + optimal.toString());
		return optimal;
	}
	
	public int getTexture(int attachment) {
		return frameBufferObject.getAttachment(attachment);
	}
	
	public FrameBufferObject getFrameBufferObject() {
		return this.frameBufferObject;
	}
	
	private class PackagingShader{

		private static final String VERTEX_FILE = "/res/shaders/TransformedQuadVertexShader.glsl";
		private static final String COLOR_FRAGMENT_FILE = "/res/shaders/colorFragmentShader.glsl";
		private static final String DISTANCE_FRAGMENT_FILE = "/res/shaders/distFragmentShader.glsl";
		private static final String SHAPE_FRAGMENT_FILE = "/res/shaders/shapeFragmentShader.glsl";
		
		private final int colorShaderID, distanceShaderID, shapeShaderID;
		
		private UniformMatrix transformationMatrixColor = new UniformMatrix("transformationMatrix");
		private UniformMatrix transformationMatrixDistance = new UniformMatrix("transformationMatrix");
		private UniformMatrix transformationMatrixShape = new UniformMatrix("transformationMatrix");
		private UniformVec4 color = new UniformVec4("color");
		private UniformVec2 sizeDistance = new UniformVec2("size");
		private UniformVec2 sizeShape = new UniformVec2("size");
		private UniformVec2 centerCoordinates = new UniformVec2("centerCoordinates");
		private UniformFloat maxDistance = new UniformFloat("maxDistance");
		private UniformSampler textureDistance = new UniformSampler("texture");
		private UniformSampler textureShape = new UniformSampler("texture");
		private UniformInteger width = new UniformInteger("width");
		private UniformInteger height = new UniformInteger("height");
		
		public PackagingShader() {
			int vertexShaderID = ShaderProgram.loadShader(VERTEX_FILE, GL20.GL_VERTEX_SHADER);
			int colorFragmentShaderID = ShaderProgram.loadShader(COLOR_FRAGMENT_FILE, GL20.GL_FRAGMENT_SHADER);
			int distanceFragmentShaderID = ShaderProgram.loadShader(DISTANCE_FRAGMENT_FILE, GL20.GL_FRAGMENT_SHADER);
			int shapeFragmentShaderID = ShaderProgram.loadShader(SHAPE_FRAGMENT_FILE, GL20.GL_FRAGMENT_SHADER);
			colorShaderID = GL20.glCreateProgram();
			GL20.glAttachShader(colorShaderID, vertexShaderID);
			GL20.glAttachShader(colorShaderID, colorFragmentShaderID);
			GL20.glBindAttribLocation(colorShaderID, 0, "position");
			GL20.glLinkProgram(colorShaderID);
			getLinkStatus(colorShaderID);
			GL20.glDetachShader(colorShaderID, vertexShaderID);
			GL20.glDetachShader(colorShaderID, colorFragmentShaderID);
			GL20.glDeleteShader(colorFragmentShaderID);
			
			transformationMatrixColor.storeUniformLocation(colorShaderID);
			color.storeUniformLocation(colorShaderID);
			GL20.glValidateProgram(colorShaderID);
			
			distanceShaderID = GL20.glCreateProgram();
			GL20.glAttachShader(distanceShaderID, vertexShaderID);
			GL20.glAttachShader(distanceShaderID, distanceFragmentShaderID);
			GL20.glBindAttribLocation(distanceShaderID, 0, "position");
			GL20.glLinkProgram(distanceShaderID);
			getLinkStatus(distanceShaderID);
			GL20.glDetachShader(distanceShaderID, vertexShaderID);
			GL20.glDetachShader(distanceShaderID, distanceFragmentShaderID);
			GL20.glDeleteShader(distanceFragmentShaderID);
			
			transformationMatrixDistance.storeUniformLocation(distanceShaderID);
			sizeDistance.storeUniformLocation(distanceShaderID);
			centerCoordinates.storeUniformLocation(distanceShaderID);
			maxDistance.storeUniformLocation(distanceShaderID);
			textureDistance.storeUniformLocation(distanceShaderID);
			GL20.glValidateProgram(distanceShaderID);
			
			shapeShaderID = GL20.glCreateProgram();
			GL20.glAttachShader(shapeShaderID, vertexShaderID);
			GL20.glAttachShader(shapeShaderID, shapeFragmentShaderID);
			GL20.glBindAttribLocation(shapeShaderID, 0, "position");
			GL20.glLinkProgram(shapeShaderID);
			getLinkStatus(shapeShaderID);
			GL20.glDetachShader(shapeShaderID, vertexShaderID);
			GL20.glDetachShader(shapeShaderID, shapeFragmentShaderID);
			GL20.glDeleteShader(shapeFragmentShaderID);
			GL20.glDeleteShader(vertexShaderID);
			
			transformationMatrixShape.storeUniformLocation(shapeShaderID);
			sizeShape.storeUniformLocation(shapeShaderID);
			width.storeUniformLocation(shapeShaderID);
			height.storeUniformLocation(shapeShaderID);
			textureShape.storeUniformLocation(shapeShaderID);
			GL20.glValidateProgram(shapeShaderID);
		}
		
		private void bindColorShader(Vector4f color) {
			GL20.glUseProgram(colorShaderID);
			this.color.loadVec4(color);
		}
		
		private void bindDistanceShader() {
			GL20.glUseProgram(distanceShaderID);
			this.sizeDistance.loadVec2(new Vector2f(packaging.getWidth(), packaging.getHeight()));
			this.centerCoordinates.loadVec2(new Vector2f(packaging.getHalfWidth(), packaging.getHalfHeight()));
			this.maxDistance.loadFloat(packaging.getMaxDistance());
			this.textureDistance.loadTexUnit(0);
		}
		
		private void bindShapeShader(int width, int height) {
			GL20.glUseProgram(shapeShaderID);
			this.sizeShape.loadVec2(new Vector2f(packaging.getWidth(), packaging.getHeight()));
			this.width.loadInteger(width);
			this.height.loadInteger(height);
			this.textureShape.loadTexUnit(0);
		}
		
		private void getLinkStatus(int shaderID) {
			int linkStatus = GL20.glGetProgrami(shaderID, GL20.GL_LINK_STATUS);
			if (linkStatus == 0) {
				int infoLength = GL20.glGetProgrami(shaderID, GL20.GL_INFO_LOG_LENGTH);
				String errorMessage = GL20.glGetProgramInfoLog(shaderID, infoLength);
				throw new RuntimeException("Could not link shaders. " + errorMessage);
			}
		}
		
		private void unbindShader() {
			GL20.glUseProgram(0);
		}
		
		private void delete() {
			GL20.glDeleteProgram(colorShaderID);
			GL20.glDeleteProgram(distanceShaderID);
			GL20.glDeleteProgram(shapeShaderID);
		}
		
	}
	
	public void delete() {
		frameBufferObject.delete();
		shader.delete();
		vao.delete();
	}
	
}
