package text.font;

import java.util.Collection;
import java.util.HashMap;

import opengl.objects.Texture;
import tools.packaging.Rectangle;

public class FontAtlas {
	
	private final HashMap<Long, LetterData> content;
	private final Texture texture;
	private final int lineSpacing;
	
	public FontAtlas(Texture texture, int lineSpacing, int packagingWidth, int packagingHeight, float halfPackagingWidth, float halfPackagingHeight, Collection<Rectangle> rectangles) {
		this.texture = texture;
		this.lineSpacing = lineSpacing;
		content = new HashMap<Long, LetterData>();
		LetterData l;
		for(Rectangle r : rectangles) {
			l = new LetterData(r, packagingWidth, packagingHeight, halfPackagingWidth, halfPackagingHeight);
			content.put(l.getCharacter(), l);
		}
	}
	
	public LetterData getLetterData(long character) {
		if(!content.containsKey(character)) {
			System.out.println("FontAtlas doesn't contain mapping for character " +character +".");
		}
		return content.get(character);
	}
	
	public Texture getTexture() {
		return this.texture;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void bindTexture() {
		this.texture.bind();
	}
}
