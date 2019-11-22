package rendering;

import org.lwjgl.opengl.GL11;
import interfaces.TextInterface;
import opengl.objects.VAO;
import rendering.uniforms.UniformMatrix;
import rendering.uniforms.UniformSampler;
import rendering.uniforms.UniformVec4;

/**
 * Tekstielementtien renderöimiseen käytetty olio.
 * @author Kim Rautio
 *
 */
public class TextRenderer{

	private ShaderProgram shader;
	
	public TextRenderer() {
		this.shader = new TextShader();
	}

	public void render(VAO vao, int texture, int vertexCount) {
		shader.start();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		vao.bind(0, 1);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		vao.unbind(0, 1);

		shader.stop();
	}
	
	public void render(TextInterface textInterface) {
		shader.start();
		
		TextShader shader = (TextShader) this.shader;
		textInterface.bindTextInterface();
		shader.transformationMatrix.loadMatrix(textInterface.getTransformationMatrix());
		shader.textColor.loadVec4(textInterface.getColor());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, textInterface.getVertexCount());
		textInterface.unbindTextInterface();
		
		shader.stop();
	}

	private class TextShader extends ShaderProgram {

		private static final int SAMPLER_TEX_UNIT = 0;

		private static final String VERTEX_FILE = "/res/shaders/textVertex.glsl";
		private static final String FRAGMENT_FILE = "/res/shaders/textFragment.glsl";

		private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
		private UniformVec4 textColor = new UniformVec4("textColor"); 
		private UniformSampler sampler = new UniformSampler("sampler");

		public TextShader() {
			super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords");
			super.storeUniformLocations(transformationMatrix, textColor, sampler);
			start();
			sampler.loadTexUnit(SAMPLER_TEX_UNIT);
			stop();
		}

	}

}
