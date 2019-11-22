/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class text_font_FontAtlasGenerator */

#ifndef _Included_text_font_FontAtlasGenerator
#define _Included_text_font_FontAtlasGenerator
#ifdef __cplusplus
extern "C" {
#endif
#undef text_font_FontAtlasGenerator_MIN_WIDTH
#define text_font_FontAtlasGenerator_MIN_WIDTH 512L
#undef text_font_FontAtlasGenerator_MIN_HEIGHT
#define text_font_FontAtlasGenerator_MIN_HEIGHT 512L
/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    initFreeType
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_text_font_FontAtlasGenerator_initFreeType
  (JNIEnv *, jobject);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    generateFontFace
 * Signature: ([B)Ltext/font/FontFace;
 */
JNIEXPORT jobject JNICALL Java_text_font_FontAtlasGenerator_generateFontFace
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    setFontSize
 * Signature: (Ltext/font/FontFace;II)V
 */
JNIEXPORT void JNICALL Java_text_font_FontAtlasGenerator_setFontSize
  (JNIEnv *, jclass, jobject, jint, jint);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    freeFontFace
 * Signature: (Ltext/font/FontFace;)V
 */
JNIEXPORT void JNICALL Java_text_font_FontAtlasGenerator_freeFontFace
  (JNIEnv *, jclass, jobject);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    loadCharacterToFontFace
 * Signature: (Ltext/font/FontFace;JI)V
 */
JNIEXPORT void JNICALL Java_text_font_FontAtlasGenerator_loadCharacterToFontFace
  (JNIEnv *, jclass, jobject, jlong, jint);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    getLetterData
 * Signature: (Ltext/font/FontFace;)Ltext/font/GlyphData;
 */
JNIEXPORT jobject JNICALL Java_text_font_FontAtlasGenerator_getLetterData
  (JNIEnv *, jclass, jobject);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    loadCharacterToFontFaceAndGetData
 * Signature: (Ltext/font/FontFace;JI)Ltext/font/GlyphData;
 */
JNIEXPORT jobject JNICALL Java_text_font_FontAtlasGenerator_loadCharacterToFontFaceAndGetData
  (JNIEnv *, jclass, jobject, jlong, jint);

/*
 * Class:     text_font_FontAtlasGenerator
 * Method:    getLineSpacing
 * Signature: (Ltext/font/FontFace;)I
 */
JNIEXPORT jint JNICALL Java_text_font_FontAtlasGenerator_getLineSpacing
  (JNIEnv *, jclass, jobject);

#ifdef __cplusplus
}
#endif
#endif