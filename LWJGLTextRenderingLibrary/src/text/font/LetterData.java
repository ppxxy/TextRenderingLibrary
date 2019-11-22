package text.font;

import tools.packaging.Rectangle;

public class LetterData {

	/**
	 * Character's keycode.
	 */
	private final long id;
	
	/**
	 * Character's x-coordinate in relation to atlas's width.<br>
	 * This value is passed to shader.
	 */
	private final float xTextureCoord;
	
	/**
	 * Character's y-coordinate in relation to atlas's height.<br>
	 * This value is passed to shader.
	 */
	private final float yTextureCoord;
	
	/**
	 * Character's width in relation to atlas's width.<br>
	 * This value is passed to shader.
	 */
	private final float textureWidth;
	
	/**
	 * Character's height in relation to atlas's height.<br>
	 * This value is passed to shader.
	 */
	private final float textureHeight;
	
	/**
	 * Character's width in pixels. This value is used to calculate mesh size.
	 */
	private final int width;
	
	/**
	 * Character's height in pixels. This value is used to calculate mesh size.
	 */
	private final int height;
	
	/**
	 * Character's offset in pixels.<br>
	 * This value is used to calculate the offset when creating mesh.
	 */
	private final int xBearing;
	
	/**
	 * Character's offset in pixels.<br>
	 * This value is used to calculate the offset when creating mesh.
	 */
	private final int yBearing;
	
	/**
	 * The amount of pixels that the pointer should move to the right after this character.
	 */
	private final int xAdvance;
	
	public LetterData(long id, float xTextureCoord, float yTextureCoord, float textureWidth, float textureHeight,
			int width, int height, int xBearing, int yBearing, int xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.width = width;
		this.height = height;
		this.xBearing = xBearing;
		this.yBearing = yBearing;
		this.xAdvance = xAdvance;
	}
	
	public LetterData(Rectangle storedCharacter, int packagingWidth, int packagingHeight, float halfPackagingWidth, float halfPackagingHeight) {
		GlyphData glyphData = storedCharacter.getContent();
		this.id = glyphData.character;
		this.xTextureCoord = (storedCharacter.getMinX()+halfPackagingWidth)/packagingWidth;
		this.yTextureCoord = (storedCharacter.getMinY()+halfPackagingHeight)/packagingHeight;
		this.width = glyphData.getWidth();
		this.height = glyphData.getHeight();
		this.textureWidth = (float) this.width / packagingWidth;
		this.textureHeight = (float) this.height / packagingHeight;
		this.xBearing = glyphData.bearingX;
		this.yBearing = glyphData.bearingY;
		this.xAdvance = glyphData.advance;
	}
	
	public long getCharacter() {
		return this.id;
	}
	
	public float getXTextureCoord() {
		return this.xTextureCoord;
	}
	
	public float getYTextureCoord() {
		return this.yTextureCoord;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public float getTextureWidth() {
		return this.textureWidth;
	}
	
	public float getTextureHeight() {
		return this.textureHeight;
	}
	
	public int getBearingX() {
		return this.xBearing;
	}
	
	public int getBearingY() {
		return this.yBearing;
	}
	
	public float getQuadWidth(float horizontalPerPixelSize) {
		return width * horizontalPerPixelSize;
	}

	public float getQuadHeight(float verticalPerPixelSize) {
		return height * verticalPerPixelSize;
	}

	public float getxOffset(float horizontalPerPixelSize) {
		return xBearing * horizontalPerPixelSize;
	}

	public float getyOffset(float verticalPerPixelSize) {
		return yBearing * verticalPerPixelSize;
	}

	public float getxAdvance(float horizontalPerPixelSize) {
		return xAdvance * horizontalPerPixelSize;
	}
	
	public int getAdvanceX() {
		return this.xAdvance;
	}
}
