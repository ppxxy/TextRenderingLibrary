package text.font;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import text.font.FontFace.FontSizeNotSetException;
import tools.packaging.Packaging;

/**
 * FontAtlasGenerator is library that makes it possible to use FreeType font library from Java.
 * Usage:
 * <ol>
 * <li>Initialize FreeType by creating {@code FontAtlasGenerator} object {@link text.font.FontAtlasGenerator#FontAtlasGenerator() FontAtlasGenerator()}.</li>
 * <li>Generate {@code FontFace} by either calling native methods 
 * {@link text.font.FontAtlasGenerator#generateFontFace(byte[]) generateFontFace(byte[])} or 
 * {@link text.font.FontAtlasGenerator#generateFontFace(ByteBuffer) generateFontFace(ByteBuffer)} or 
 * if you have {@link java.io.InputStream InputStream}-object pointing to .tff-file {@link text.font.FontAtlasGenerator#generateFontFace(InputStream) generateFontFace(InputStream)}.</li>
 * <li>After {@code FontFace} has been generated, you <b>have to</b> set it's font size by calling {@link text.font.FontFace#setFontSize(int) FontFace.setFontSize(int)} or
 *  {@link text.font.FontFace#setFontSize(int, int) setFontSize(int, int)}.</li>
 * <li>Once font size has been set, you can load glyphs on the {@code FontFace} with methods
 * {@link text.font.FontAtlasGenerator#loadCharacterToFontFace(FontFace, long, int) loadCharacterToFontFace(FontFace, long, int)} and 
 * {@link text.font.FontFace#loadCharacter(char, int) FontFace.loadCharacter(char, int)}.</li>
 * <li>To fetch loaded data use methods {@link text.font.FontFace#getLetterData() FontFace.getLetterData()} and {@link text.font.FontAtlasGenerator#getLetterData(FontFace)}</li>
 * <li>Lastly don't forget to delete FontFace by calling {@link text.font.FontFace#delete() FontFace.delete()}.</li>
 * </ol>
 * 
 * @author Kim Rautio
 */
public class FontAtlasGenerator {

	public static final int MIN_WIDTH = 512, MIN_HEIGHT = 512;
	
	/**
	 * Loads required libraries to use FreeType and FontAtlasGenerator native methods.
	 */
	static {
		System.load(new File("src/lib/freetype/windows/freetype.dll").getAbsolutePath());
		System.load(new File("src/text/font/FontAtlasGenerator.dll").getAbsolutePath());
	}
	
	/**
	 * Pointer to FreeType libraryobject's memoryspace.
	 * This value should only be edited and accessed from C.
	 */
	private long ftLibraryPointer;
	
	/**
	 * Public constructor that initializes FreeType library.<br>
	 * {@code FontAtlasGenerator} works as a linking object to FreeType library.
	 */
	public FontAtlasGenerator() {
		initFreeType();
	}
	
	public FontAtlas generateFontAtlas(int fontSize, InputStream fontFileStream, LetterLoadOptions letters) {
		FontFace face = generateFontFace(fontFileStream);
		face.setFontSize(fontSize);
		int lineSpacing = face.getLineSpacing()/64;
		List<GlyphData> lettersData = new ArrayList<GlyphData>();
		for(char c : letters) {
			try {
				lettersData.add(face.loadCharacterAndGetLetterData(c, FontFace.LOAD_GLYPH_WITH_RENDER));
			} catch (FontSizeNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		face.delete();
		
		Packaging packaging = new Packaging(MIN_WIDTH, MIN_HEIGHT);
		packaging.setLineSpacing(lineSpacing);
		packaging.packElements(lettersData);
		
		return packaging.generateFontAtlas();
	}
	
	private native void initFreeType();
	
	public FontFace generateFontFace(InputStream input) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[2048];

		try {
			while ((nRead = input.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return generateFontFace(buffer.toByteArray());
	}
	
	public native FontFace generateFontFace(byte[] fontFaceData);
	
	/**
	 * A method to set {@link text.font.FontFace FontFace}'s width and height in pixels.<br>
	 * <br>
	 * You can specify both {@code width} and {@code height} to control the shape of the glyphs.<br>
	 * Or you can let FreeType calculate correct values by only specifying one of the two values,<br>
	 * leaving the other one to be 0.<br>
	 * <br>
	 * Most <b>commonly font sizes are defined by height value.</b><br>
	 * Meaning that font size 14 would be {@code width = 0, height = 14}.<br>
	 * @param fontFace {@link text.font.FontFace FontFace}-object, whose size we want to specify.
	 * @param width Width of the glyphs, or 0 to match height.
	 * @param height Height of the glyphs, or 0 to match width.
	 */
	public static native void setFontSize(FontFace fontFace, int width, int height);
	
	/**
	 * A method to free {@link text.font.FontFace FontFace} after it's not used anymore.<br>
	 * @param face {@link text.font.FontFace FontFace} to be freed.
	 */
	public static native void freeFontFace(FontFace face);
	
	/**
	 * Loads the specified character to {@link text.font.FontFace FontFace}'s memory. <br><br>
	 * 
	 * The way that the character is loaded can be controlled by method's {@code type}-parameter.<br>
	 * {@link text.font.FontFace#LOAD_GLYPH_WITH_RENDER FontFace.LOAD_GLYPH_WITH_RENDER} will load character and render it.<br>
	 * {@link text.font.FontFace#LOAD_GLYPH_VALUES_ONLY FontFace.LOAD_GLYPH_VALUES_ONLY} will load the character without rendering.<br>
	 * <br>
	 * After character is loaded to the memory, it's data can be accessed using methods
	 * 
	 * @param fontFace {@link text.font.FontFace FontFace} that will load the specified character.
	 * @param character Character to be loaded. Character values of up to 8 bits can be used.
	 * @param type Defines how the character is supposed to be loaded.<br>
	 */
	public static native void loadCharacterToFontFace(FontFace fontFace, long character, int type);
	
	public static native GlyphData getLetterData(FontFace fontFace);
	
	/*
	 * .tff file loaded into buffer.
	 * font size
	 * bold/italic/bold, italic
	 * underline/middleline
	 * included characters
	 */

	public static native GlyphData loadCharacterToFontFaceAndGetData(FontFace fontFace, long character, int type);

	public static native int getLineSpacing(FontFace fontFace);
}
