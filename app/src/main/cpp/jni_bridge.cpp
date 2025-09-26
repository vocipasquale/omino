#include <jni.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>

extern "C" {

JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_00024Companion_nativeInit(JNIEnv* env, jclass clazz, jobject /*assetManager*/);

// Backward compatibility if methods are declared static directly in class (no Companion)
JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeInit(JNIEnv* env, jclass /*clazz*/, jobject /*assetManager*/) {
    // no-op for stub; real init will be added later
}

JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeResize(JNIEnv* /*env*/, jclass /*clazz*/, jint /*width*/, jint /*height*/) {
    // no-op
}

JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeUpdate(JNIEnv* /*env*/, jclass /*clazz*/) {
    // no-op
}

//JNIEXPORT void JNICALL
//Java_com_game_omino_render_GameRenderer_nativeRender(JNIEnv* /*env*/, jclass /*clazz*/) {
    // no-op
//}

JNIEXPORT void JNICALL
Java_com_game_omino_input_TouchInput_nativeTouch(JNIEnv* /*env*/, jclass /*clazz*/, jfloat /*x*/, jfloat /*y*/, jint /*action*/) {
    // no-op
}

}


