package text.mesh;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import text.font.FontAtlas;
import text.font.LetterData;

public class TextMeshCreator {

	public static final int MINIMUM = 0, CENTER = 1, MAXIMUM = 2;
	public static float verticalPerPixelSize = 0.01f;

	private int verticalOrigin = MINIMUM;
	private int horizontalOrigin = MINIMUM;
	public final FontAtlas atlas;

	public TextMeshCreator(FontAtlas atlas) {
		this.atlas = atlas;
	}

	public void setVerticalOrigin(int origin) {
		this.verticalOrigin = origin;
	}

	public void setHorizontalOrigin(int origin) {
		this.horizontalOrigin = origin;
	}

	public TextMeshData createTextMesh(String text, float fontSize) {
		LetterData[] characters = loadLettersData(text);
		TextMeshData data = createQuadVertices(fontSize, characters, this.horizontalOrigin, this.verticalOrigin);
		return data;
	}

	public TextMeshData createTextMesh(String text, float fontSize, int horizontalOrigin, int verticalOrigin) {
		LetterData[] characters = loadLettersData(text);
		TextMeshData data = createQuadVertices(fontSize, characters, horizontalOrigin, verticalOrigin);
		return data;
	}

	private LetterData[] loadLettersData(String text) {
		char[] characterArray = text.toCharArray();
		LetterData[] characters = new LetterData[text.length()];
		for (int i = 0, n = characterArray.length; i < n; i++) {
			characters[i] = atlas.getLetterData(characterArray[i]);
		}
		return characters;
	}

	private TextMeshData createQuadVertices(float fontSize, LetterData[] characters, int horizontalOrigin,
			int verticalOrigin) {
		List<Vector2f> vertices = new ArrayList<Vector2f>();
		List<Float> textureCoords = new ArrayList<Float>();
		float viewportWidth = Display.getWidth();
		float viewportHeight = Display.getHeight(); 
		float aspectRatio = viewportWidth/viewportHeight;
		float verticalPPS = verticalPerPixelSize * fontSize;
		float horizontalPPS = verticalPPS/aspectRatio;
		float width = 0f;
		Vector2f cursor = new Vector2f(0f, 0f);
		int amountOfMovedVertices = 0;
		int numberOfLines = 1;
		for (LetterData c : characters) {
			if (c.getCharacter() == '\n') {
				numberOfLines++;
				if(cursor.x > width) {
					width = cursor.x;
				}
				if (horizontalOrigin != MINIMUM) {
					float delta = horizontalOrigin == CENTER ? cursor.x/2f : cursor.x;
					for (; amountOfMovedVertices < vertices.size(); amountOfMovedVertices++) {
						vertices.get(amountOfMovedVertices).x -= delta;
					}
				}
				cursor.y -= verticalPPS * atlas.getLineSpacing();
				cursor.x = 0f;
			} else {
				addVerticesForCharacter(cursor, c, fontSize, vertices, horizontalPPS, verticalPPS);
				addTexCoords(textureCoords, c.getXTextureCoord(), c.getYTextureCoord(), c.getTextureWidth(),
						c.getTextureHeight());
				// addData(cursor, c, fontSize, vertices, textureCoords, indices, horizontalPPS,
				// verticalPPS);
				cursor.x += c.getxAdvance(horizontalPPS)/2f;
			}
		}
		if(cursor.x > width) {
			width = cursor.x;
		}
		if (horizontalOrigin != MINIMUM) {
			float delta = horizontalOrigin == CENTER ? cursor.x/2f : cursor.x;
			for (; amountOfMovedVertices < vertices.size(); amountOfMovedVertices++) {
				vertices.get(amountOfMovedVertices).x -= delta;
			}
		}
		/*if (verticalOrigin != MAXIMUM) {
			float delta = (2f - verticalOrigin) * verticalPPS * numberOfLines * atlas.getLineSpacing();
			for (Vector2f v : vertices) {
				v.y += delta;
			}
		}*/
		return new TextMeshData(verticeListToArray(vertices), listToArray(textureCoords), width, cursor.y+verticalPPS * atlas.getLineSpacing());
	}

	private void addVerticesForCharacter(Vector2f cursor, LetterData character, float fontSize,
			List<Vector2f> vertices, float horizontalPPS, float verticalPPS) {
		float x = cursor.getX() + character.getxOffset(horizontalPPS);
		float maxY = cursor.getY() + character.getyOffset(verticalPPS);
		float maxX = x + character.getQuadWidth(horizontalPPS);
		float y = maxY - character.getQuadHeight(verticalPPS);
		addVertices(vertices, x, y, maxX, maxY);
	}

	/*public float getLineHeight(float fontSize) {
		return Settings.getVerticalPPS() * fontSize * loader.getLineHeight();
	}*/

	/*
	 * private void addData(Vector2f cursor, CharacterData character, float
	 * fontSize, List<Vector2f> vertices, List<Vector2f> textureCoords,
	 * List<Integer> indices, float horizontalPPS, float verticalPPS) { float x =
	 * cursor.getX() + character.getxOffset(horizontalPPS); float y = cursor.getY()
	 * + character.getyOffset(verticalPPS); float maxX = x +
	 * character.getQuadWidth(horizontalPPS); float maxY = y +
	 * character.getQuadHeight(verticalPPS); vertices.add(new Vector2f(x, y));
	 * indices.add(vertices.size()); vertices.add(new Vector2f(x, maxY));
	 * indices.add(vertices.size()); }
	 */

	private static void addVertices(List<Vector2f> vertices, float x, float y, float maxX, float maxY) {
		vertices.add(new Vector2f(x, y));
		vertices.add(new Vector2f(x, maxY));
		vertices.add(new Vector2f(maxX, maxY));
		vertices.add(new Vector2f(maxX, maxY));
		vertices.add(new Vector2f(maxX, y));
		vertices.add(new Vector2f(x, y));
	}

	private static void addTexCoords(List<Float> texCoords, float x, float y, float width, float height) {
		float maxX = x + width;
		float maxY = y + height;
		texCoords.add(x);
		texCoords.add(maxY);
		texCoords.add(x);
		texCoords.add(y);
		texCoords.add(maxX);
		texCoords.add(y);
		texCoords.add(maxX);
		texCoords.add(y);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(x);
		texCoords.add(maxY);
	}

	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0, n = array.length; i < n; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

	private static float[] verticeListToArray(List<Vector2f> vertices) {
		float[] array = new float[vertices.size() * 2];
		int index = 0;
		for (Vector2f v : vertices) {
			array[index++] = v.x;
			array[index++] = v.y;
		}
		return array;
	}
}
