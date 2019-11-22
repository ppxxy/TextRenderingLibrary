package tools.packaging;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector2f;

import opengl.objects.Texture;
import text.font.FontAtlas;
import text.font.GlyphData;
import tools.formulas.MergeSortList;

public class Packaging {
	
	private int width, height;
	private float halfWidth, halfHeight;
	private float maxDistance;
	private Vector2f centerCoordinate;

	private final PackagingRenderer packagingRenderer;
	
	private int amountOfRectangles = 0;
	private List<Rectangle> rectangles;
	private Vector2f centerOfMass = new Vector2f(0f, 0f);
	private int totalWeight = 0;
	
	public float minX = 0, minY = 0, maxX = 0, maxY = 0;
	private int lineSpacing;
	
	public Packaging(int width, int height) {
		this.width = width;
		this.height = height;
		this.halfWidth = (float)width/2f;
		this.halfHeight = (float)height/2f;
		this.maxDistance = (float)Math.sqrt(this.halfWidth*this.halfWidth+this.halfHeight*this.halfHeight);
		this.packagingRenderer = new PackagingRenderer(this);
		this.centerCoordinate = new Vector2f(halfWidth, halfHeight);
		//this.maxDistance = squaredDistance(width, height);
		this.rectangles = new ArrayList<Rectangle>();
	}
	
	public void packElements(List<GlyphData> lettersData) {
		new MergeSortList<GlyphData>().sort(lettersData);
		
		boolean elementsPacked = true;
		do{
			elementsPacked = true;
			for(GlyphData l : lettersData) {
				if(!addShape(l.toRectangle())) {
					elementsPacked = false;
					doubleSize();
					break;
				}
			}
		} while(!elementsPacked);
	}
	
	public List<Rectangle> getRectangles(){
		return this.rectangles;
	}
	
	public void doubleSize() {
		this.halfWidth = this.width;
		this.halfHeight = this.height;
		this.width *= 2;
		this.height *= 2;
		this.maxDistance = (float)Math.sqrt(this.halfWidth*this.halfWidth+this.halfHeight*this.halfHeight);
		this.packagingRenderer.createFrameBufferObject();
		this.centerCoordinate = new Vector2f(halfWidth, halfHeight);
		this.rectangles.clear();
	}
	
	public boolean addShape(Rectangle rectangle) {
		if(++amountOfRectangles > 1) {
			//calculate values of surrounding pixels (pixelDistance to center of mass)
			//for every fitting point, place this element on top.
			Vector2f optimalLocation = packagingRenderer.renderArea(rectangles, rectangle);
			//place this at the best location
			rectangle.setPosition(optimalLocation);
			//calculate center of mass
			this.centerOfMass = calculateCenterOfMass();
			//add force to each rectangle towards center of mass. Rectangles will attempt to move in that direction (x & y).
			//calculate center of mass
			//this.centerOfMass = calculateCenterOfMass();
		} else {
			rectangle.setPosition(new Vector2f(0f, 0f));
		}
		totalWeight += rectangle.getWeight();
		rectangles.add(rectangle);
		return recalculateBoundaries(rectangle);
	}
	
	private boolean recalculateBoundaries(Rectangle rectangle) {
		float value;
		if((value = rectangle.getMinX()) < this.minX) {
			this.minX = value;
			if(value < -halfWidth) {
				return false;
			}
		}
		if((value = rectangle.getMaxX()) > this.maxX) {
			this.maxX = value;
			if(value > halfWidth) {
				return false;
			}
		}
		if((value = rectangle.getMinY()) < this.minY) {
			this.minY = value;
			if(value < -halfHeight) {
				return false;
			}
		}
		if((value = rectangle.getMaxY()) > this.maxY) {
			this.maxY = value;
			if(value > halfHeight) {
				return false;
			}
		}
		return true;
	}
	
	private Vector2f calculateCenterOfMass() {
		Vector2f centerOfMass = new Vector2f(0f, 0f);
		for(Rectangle r : rectangles) {
			Vector2f.add(centerOfMass, r.getCenterOfMass(), centerOfMass);
		}
		centerOfMass.x /= this.totalWeight;
		centerOfMass.y /= this.totalWeight;
		
		return centerOfMass;
	}

	public int getTexture(int attachment) {
		return packagingRenderer.getTexture(attachment);
	}
	
	public Texture getImageTexture() {
		byte[][] data = new byte[height][width];
		for(Rectangle r : rectangles) {
			r.addDataToArray(data, (int)halfWidth, (int)halfHeight);
		}
		Texture texture = new Texture(GL11.GL_TEXTURE_2D);
		texture.bind();
		ByteBuffer dataBuffer = ByteBuffer.allocateDirect(width*height);
		for(int y = 0; y < height; y++) {
			dataBuffer.put(data[y]);
		}
		dataBuffer.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, width, height, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, dataBuffer);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		return texture;
	}

	public void printContent() {
		for(Rectangle r : rectangles) {
			System.out.println(r.toString());
		}
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getHalfWidth() {
		return halfWidth;
	}

	public float getHalfHeight() {
		return halfHeight;
	}
	
	public float getMaxDistance() {
		return this.maxDistance;
	}

	public void setLineSpacing(int lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	public FontAtlas generateFontAtlas() {
		return new FontAtlas(getImageTexture(), lineSpacing, width, height, halfWidth, halfHeight, rectangles);
	}
}
