package opengl.objects;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

public class FrameBufferObject {

	private int width, height;

	protected final int id;
	private Map<Integer, RenderTarget> renderTargets = new HashMap<Integer, RenderTarget>();

	protected FrameBufferObject(int width, int height){
		this.width = width;
		this.height = height;
		this.id = GL30.glGenFramebuffers();
		bind();
	}

	public void bind(){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GL11.glViewport(0, 0, width, height);
	}

	public void unbind(){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static FrameBufferObject createFrameBuffer(int width, int height){
		return new FrameBufferObject(width, height);
	}

	/**
	 *
	 * @param width
	 * @param height
	 * @param format GL11.GL_FLOAT
	 * @param attachment GL30.GL_COLOR_ATTACHMENT0
	 * @return
	 */
	public static void createRenderBuffer(int width, int height, int format, int attachment) {
	}

	public void createTextureAttachment(int attachment, int internalFormat, int format, int type) {
		new TextureRenderTarget(attachment, internalFormat, format, type);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param format GL30.GL_RGB
	 * @param type GL11.GL_FLOAT
	 * @return
	 */
	public float[] getData(int x, int y, int width, int height, int format, int type) {
		FloatBuffer data = BufferUtils.createFloatBuffer(width*height);
		GL11.glReadPixels(x, y, width, height, format, type, data);
		float[] result = new float[width*height];
		data.get(result);
		return result;
	}

	public FloatBuffer getAllData(int format, int type) {
		FloatBuffer data = BufferUtils.createFloatBuffer(width * height);
		GL11.glReadPixels(0, 0, width, height, format, type, data);
		return data;
	}

	public void checkStatus(){
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if(status != GL30.GL_FRAMEBUFFER_COMPLETE){
			System.out.println("Faced an error on Framebuffer creation. Error code: " +status);
			System.exit(0);
		}
	}

	public int getAttachment(int attachment) {
		return this.renderTargets.get(attachment).getID();
	}

	public void delete() {
		for(RenderTarget r : renderTargets.values()) {
			r.delete();
		}
		renderTargets.clear();
		GL30.glDeleteFramebuffers(id);
	}

	private abstract class RenderTarget{

		protected final int id;

		protected RenderTarget(int attachment, int id) {
			this.id = id;
			renderTargets.put(attachment, this);
		}

		public int getID() {
			return this.id;
		}

		public abstract void delete();

	}

	private class TextureRenderTarget extends RenderTarget{

		/**
		 *
		 * @param attachment GL30.GL_COLOR_ATTACHMENT0
		 * @param internalFormat GL11.GL_RGBA
		 * @param format GL11.GL_RGBA
		 * @param type GL11.GL_FLOAT
		 */
		private TextureRenderTarget(int attachment, int internalFormat, int format, int type) {
			super(attachment,  GL11.glGenTextures());
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
			FloatBuffer buffer = BufferUtils.createFloatBuffer(width * height);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, buffer);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, this.id, 0);
		}

		@Override
		public void delete() {
			GL11.glDeleteTextures(this.id);
		}

	}

	private class RenderBufferRenderTarget extends RenderTarget{

		private RenderBufferRenderTarget(int attachment, int format) {
			super(attachment, GL30.glGenRenderbuffers());
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.id);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format, width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, this.id);
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		}

		@Override
		public void delete() {
			GL30.glDeleteRenderbuffers(this.id);
		}

	}
}
