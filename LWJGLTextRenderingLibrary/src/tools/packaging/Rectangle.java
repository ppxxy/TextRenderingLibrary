package tools.packaging;

import org.lwjgl.util.vector.Vector2f;

import opengl.main.Main;
import text.font.GlyphData;

public class Rectangle implements Comparable<Rectangle>{

	private Vector2f centerOfMass = new Vector2f(0f, 0f);
	
	private Vector2f position;
	private float minX, minY, maxX, maxY;
	private final int width, height;
	private final float halfWidth, halfHeight;
	private final int weight;
	
	private GlyphData content;
	
	public Rectangle(GlyphData content, Vector2f position, int width, int height) {
		this(content, width, height);
		this.setPosition(position);
	}
	
	public Rectangle(GlyphData content, int width, int height) {
		this(content, width, height, width * height);
	}
	
	public Rectangle(GlyphData content, int width, int height, int weight) {
		this.content = content;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.halfWidth = width/2f;
		this.halfHeight = height/2f;
		this.position = new Vector2f(0f, 0f);
	}

	public int getWeight() {
		return this.weight;
	}
	
	public float getX() {
		return this.position.x;
	}
	
	public float getY() {
		return this.position.y;
	}
	
	public float getMinX() {
		return this.minX;
	}
	
	public float getMinY() {
		return this.minY;
	}
	
	public float getMaxX() {
		return this.maxX;
	}
	
	public float getMaxY() {
		return this.maxY;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public GlyphData getContent() {
		return this.content;
	}
	
	public void setPosition(Vector2f position) {
		this.position.set(position);
		this.minX = this.position.x - halfWidth;
		this.maxX = this.position.x + halfWidth;
		this.minY = this.position.y - halfHeight;
		this.maxY = this.position.y + halfHeight;
		this.centerOfMass.set(position).scale(weight);
	}
	
	public void setBottomLeftToCoordinates(Vector2f coordinates) {
		this.setPosition(new Vector2f(coordinates.x+halfWidth, coordinates.y+halfHeight));
	}
	
	public Vector2f getCenterOfMass() {
		return this.centerOfMass;
	}
	
	public String toString() {
		return new String(this.minX +", " +this.maxX + " and " +this.minY +", " +this.maxY);
	}
	
	public void addDataToArray(byte[][] data, int halfWidth, int halfHeight) {
		int x = (int) this.minX;
		int y = (int) this.minY;
		for(byte b : content.imageData) {
			data[halfHeight + y][halfWidth + x] = b;
			if(++x >= this.maxX - Main.EMPTY_SPACING) {
				x = (int)this.minX;
				y++;
			}
		}
	}

	@Override
	public int compareTo(Rectangle compare) {
		return this.weight > compare.weight ? -1 : this.weight < compare.weight ? 1 : 0;
	}
}
