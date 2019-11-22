package text.font;

public class FontFace {

	public static final int LOAD_GLYPH_DEFAULT = 0;
	public static final int LOAD_GLYPH_WITH_RENDER = 4; //FT_LOAD_RENDER
	public static final int LOAD_GLYPH_VALUES_ONLY = 4194304; //FT_LOAD_BITMAP_METRICS_ONLY
	
	@SuppressWarnings("unused")
	private final long pointer;
	private long previousCharacter;
	private boolean fontSizeSet = false;
	@SuppressWarnings("unused")
	private final long bufferPointer;
	@SuppressWarnings("unused")
	private byte[] data;
	
	public FontFace(long pointer, long bufferPointer, byte[] data) {
		this.pointer = pointer;
		this.bufferPointer = bufferPointer;
		this.data = data;
	}
	
	public void loadCharacter(char character, int type) throws FontSizeNotSetException {
		if(!fontSizeSet) {
			throw new FontSizeNotSetException("Font size has not been set. Make sure to call setFontSize() before loading characters.");
		}
		FontAtlasGenerator.loadCharacterToFontFace(this, character, type);
	}
	
	public void setFontSize(int fontSize) {
		FontAtlasGenerator.setFontSize(this, 0, fontSize);
		fontSizeSet = true;
	}
	
	public void setFontSize(int width, int height) {
		FontAtlasGenerator.setFontSize(this, width, height);
		fontSizeSet = true;
	}
	
	public GlyphData getLetterData() {
		return FontAtlasGenerator.getLetterData(this);
	}
	
	public GlyphData loadCharacterAndGetLetterData(char character, int type) throws FontSizeNotSetException {
		if(!fontSizeSet) {
			throw new FontSizeNotSetException("Font size has not been set. Make sure to call setFontSize() before loading characters.");
		}
		return FontAtlasGenerator.loadCharacterToFontFaceAndGetData(this, character, type);
	}
	
	public void delete() {
		FontAtlasGenerator.freeFontFace(this);
	}
	
	public int getLineSpacing() {
		return FontAtlasGenerator.getLineSpacing(this);
	}
	
	public class FontSizeNotSetException extends Exception{
		private static final long serialVersionUID = -2083609768810715761L;

		private FontSizeNotSetException() {
			super();
		}
		
		private FontSizeNotSetException(String string) {
			super(string);
		}
		
	}
}
