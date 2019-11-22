#include <jni.h>
#include <stdio.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include "text_font_FontAtlasGenerator.h"

void JNICALL Java_text_font_FontAtlasGenerator_initFreeType(JNIEnv *env, jobject obj){
	FT_Library* ft = (FT_Library*) malloc(sizeof(FT_Library));
	FT_Error initFreeType = FT_Init_FreeType(ft);
	if (initFreeType){
		printf("Couldn't init freetype. %s\n", FT_Error_String(initFreeType));
	} else{
		jclass fontAtlasGeneratorClass = (*env)->GetObjectClass(env, obj);
		jfieldID pointerField = (*env)->GetFieldID(env, fontAtlasGeneratorClass, "ftLibraryPointer", "J");
		(*env)->SetLongField(env, obj, pointerField, (intptr_t)ft);
	}
}

FT_Library* getLibrary(JNIEnv *env, jobject obj){
	jclass fontAtlasGeneratorClass = (*env)->GetObjectClass(env, obj);
	jfieldID pointerField = (*env)->GetFieldID(env, fontAtlasGeneratorClass, "ftLibraryPointer", "J");
	jlong jpointer = (*env)->GetLongField(env, obj, pointerField);
	if((void*) jpointer <= 0){
		return 0;
	}
	return (FT_Library*) jpointer;
}

FT_Face* getFontFace(JNIEnv *env, jobject obj){
	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jfieldID pointerField = (*env)->GetFieldID(env, fontFaceClass, "pointer", "J");
	jlong jpointer = (*env)->GetLongField(env, obj, pointerField);
	if((void*) jpointer <= 0){
		printf("FontFace is null!\n");
		fflush(stdout);
		return 0;
	}
	return (FT_Face*) jpointer;
}

jobject JNICALL Java_text_font_FontAtlasGenerator_generateFontFace(JNIEnv *env, jobject obj, jbyteArray array){
	FT_Library *ft = getLibrary(env, obj);
	if(!ft){
		printf("FreeType library has not been initialized.\n");
		return 0;
	}

	FT_Face* face = (FT_Face*) malloc(sizeof(FT_Face));

	jbyte* bufferPtr = (*env)->GetByteArrayElements(env, array, NULL);
	jsize lengthOfArray = (*env)->GetArrayLength(env, array);
	FT_Error initFace = FT_New_Memory_Face(*ft, bufferPtr, lengthOfArray, 0, face);
	//(*env)->ReleaseByteArrayElements(env, array, bufferPtr, 0);

	if(initFace){
		printf("Couldn't generate face from the given input. %d\n", initFace);
		fflush(stdout);
		return 0;
	}

	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jmethodID fontFaceConstructor = (*env)->GetMethodID(env, fontFaceClass, "<init>", "(JJ[B)V");
	jobject fontFaceObject = (*env)->NewObject(env, fontFaceClass, fontFaceConstructor, (intptr_t)face, (intptr_t)bufferPtr, array);
	return fontFaceObject;
}

void JNICALL Java_text_font_FontAtlasGenerator_setFontSize(JNIEnv *env, jclass clazz, jobject obj, jint width, jint height){
	FT_Face *face = getFontFace(env, obj);

	if(!face){
		printf("FontFace has not been initialized.\n");
		fflush(stdout);
		return;
	}

	FT_Set_Pixel_Sizes(*face, width, height);
}

void JNICALL Java_text_font_FontAtlasGenerator_loadCharacterToFontFace(JNIEnv *env, jclass clazz, jobject obj, jlong character, jint flags){
	FT_Face face = *getFontFace(env, obj);

	if(!face){
		printf("FontFace has not been initialized.\n");
		return;
	}

	FT_Error error = FT_Load_Char(face, character, flags);
	if(error){
		printf("FreeType error loading character. Error code %d for character %d.\n", error, character);
		fflush(stdout);
		return;
	}

	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jfieldID previousCharacterField = (*env)->GetFieldID(env, fontFaceClass, "previousCharacter", "J");
	(*env)->SetLongField(env, obj, previousCharacterField, character);
}

jobject JNICALL Java_text_font_FontAtlasGenerator_getLetterData(JNIEnv *env, jclass clazz, jobject obj){
	FT_Face *face = getFontFace(env, obj);

	if(!face){
		printf("FontFace has not been initialized.\n");
		fflush(stdout);
		return 0;
	}

	jclass letterDataClass = (*env)->FindClass(env, "text/font/LetterData");
	jmethodID letterDataConstructor = (*env)->GetMethodID(env, letterDataClass, "<init>", "(JIIIII[B)V");

	FT_GlyphSlot glyph = (*face)->glyph;
	int size = (glyph->bitmap.width) * (glyph->bitmap.rows);
	jbyteArray imageData = (*env)->NewByteArray(env, size);
	(*env)->SetByteArrayRegion(env, imageData, 0, size, (jbyte*) glyph->bitmap.buffer);
	//(*env)->DeleteLocalRef(env, imageData);
	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jfieldID previousCharacterField = (*env)->GetFieldID(env, fontFaceClass, "previousCharacter", "J");
	jlong previousCharacter = (*env)->GetLongField(env, obj, previousCharacterField);

	jobject letterDataObject = (*env)->NewObject(env, letterDataClass, letterDataConstructor,
			previousCharacter,
			glyph->bitmap.width, glyph->bitmap.rows,
			glyph->bitmap_left, glyph->bitmap_top,
			glyph->advance.x, imageData);
	return letterDataObject;
}

jint JNICALL Java_text_font_FontAtlasGenerator_getLineSpacing(JNIEnv *env, jclass clazz, jobject obj){
	FT_Face face = *getFontFace(env, obj);

	if(!face){
		printf("FontFace has not been initialized.\n");
		return 0;
	}

	return face->size->metrics.height;
}

jobject JNICALL Java_text_font_FontAtlasGenerator_loadCharacterToFontFaceAndGetData(JNIEnv *env, jclass clazz, jobject obj, jlong character, jint flags){
	FT_Face face = *getFontFace(env, obj);

	if(!face){
		printf("FontFace has not been initialized.\n");
		return 0;
	}

	FT_Error error = FT_Load_Char(face, character, flags);
	if(error){
		printf("FreeType error loading character. Error code %d for character %d.\n", error, character);
		fflush(stdout);
		return 0;
	}

	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jfieldID previousCharacterField = (*env)->GetFieldID(env, fontFaceClass, "previousCharacter", "J");
	(*env)->SetLongField(env, obj, previousCharacterField, character);

	jclass glyphDataClass = (*env)->FindClass(env, "text/font/GlyphData");
	jmethodID letterDataConstructor = (*env)->GetMethodID(env, glyphDataClass, "<init>", "(JIIIII[B)V");

	FT_GlyphSlot glyph = face->glyph;
	int size = (glyph->bitmap.width) * (glyph->bitmap.rows);
	jbyteArray imageData = (*env)->NewByteArray(env, size);
	(*env)->SetByteArrayRegion(env, imageData, 0, size, (jbyte*) glyph->bitmap.buffer);

	jobject letterDataObject = (*env)->NewObject(env, glyphDataClass, letterDataConstructor,
			character,
			glyph->bitmap.width, glyph->bitmap.rows,
			glyph->bitmap_left, glyph->bitmap_top,
			glyph->advance.x, imageData);
	return letterDataObject;
}

void JNICALL Java_text_font_FontAtlasGenerator_freeFontFace(JNIEnv *env, jclass clazz, jobject obj){
	FT_Face *face = getFontFace(env, obj);

	jclass fontFaceClass = (*env)->FindClass(env, "text/font/FontFace");
	jfieldID bufferPointerField = (*env)->GetFieldID(env, fontFaceClass, "bufferPointer", "J");
	jbyte* bufferPtr = (jbyte*)((*env)->GetLongField(env, obj, bufferPointerField));
	jfieldID arrayField = (*env)->GetFieldID(env, fontFaceClass, "data", "[B");
	jbyteArray array = (jbyteArray)((*env)->GetObjectField(env, obj, arrayField));

	(*env)->ReleaseByteArrayElements(env, array, bufferPtr, JNI_ABORT);

	if(FT_Done_Face(*face)){
		printf("Couldn't free face.\n");
		fflush(stdout);
	}

	free(face);
}
