package text.font;

import opengl.main.Main;
import tools.packaging.Rectangle;

public class GlyphData implements Comparable<GlyphData>{

	public final long character;
	public final byte[] imageData;
	public final int width, height, bearingX, bearingY, advance, weight;

	public GlyphData(long character, int width, int height, int bearingX, int bearingY, int advance, byte[] imageData) {
		this.character = character;
		this.width = width;
		this.height = height;
		this.weight = width*height;
		this.bearingX = bearingX;
		this.bearingY = bearingY;
		this.advance = advance/32;
		this.imageData = imageData;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBearingX() {
		return bearingX;
	}

	public int getBearingY() {
		return bearingY;
	}

	public int getAdvance() {
		return advance;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public String toString() {
		return new String("Character: " +character +", Width: " +width +", Height: " +height +", Bearing X: " +bearingX +", Bearing Y: " +bearingY +", Advance: " +advance);
	}
	
	public Rectangle toRectangle() {
		return new Rectangle(this, this.width+Main.EMPTY_SPACING, this.height+Main.EMPTY_SPACING, this.getWeight());
	}

	@Override
	public int compareTo(GlyphData compare) {
		return this.weight > compare.weight ? -1 : this.weight < compare.weight ? 1 : 0;
	}}
