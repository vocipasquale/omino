#include <jni.h>
#include <android/asset_manager_jni.h>
#include <GLES2/gl2.h>
#include <vector>
#include <string>
#include <map>
#include <android/log.h>

// stb_image per caricare PNG da memoria
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

// -----------------------------
// Shader base OpenGL ES 2.0
// -----------------------------
const char* vertexShaderSrc = R"(
attribute vec2 aPos;
attribute vec2 aTex;
varying vec2 vTex;
uniform vec2 uScreenSize;
void main() {
    vec2 pos = aPos / uScreenSize * 2.0 - 1.0;
    pos.y = -pos.y;
    gl_Position = vec4(pos, 0.0, 1.0);
    vTex = aTex;
}
)";

const char* fragmentShaderSrc = R"(
precision mediump float;
varying vec2 vTex;
uniform sampler2D uTexture;
void main() {
    gl_FragColor = texture2D(uTexture, vTex);
}
)";

GLuint compileShader(GLenum type, const char* src) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &src, nullptr);
    glCompileShader(shader);
    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char log[512];
        glGetShaderInfoLog(shader, 512, nullptr, log);
        __android_log_print(ANDROID_LOG_ERROR, "omino", "Shader error: %s", log);
    }
    return shader;
}

GLuint createProgram() {
    GLuint vs = compileShader(GL_VERTEX_SHADER, vertexShaderSrc);
    GLuint fs = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);
    GLuint prog = glCreateProgram();
    glAttachShader(prog, vs);
    glAttachShader(prog, fs);
    glLinkProgram(prog);
    glDeleteShader(vs);
    glDeleteShader(fs);
    return prog;
}

// -----------------------------
// Strutture dati di gioco
// -----------------------------
struct Texture {
    GLuint id;
    int width;
    int height;
};

struct Vec2 {
    float x, y;
};


struct Entity {
    Vec2 pos;
    std::string currentAn;
    int currentFrame; // Indice del frame corrente
    float animationTime; // Tempo trascorso per l'animazione
    float frameDuration; // Durata di ogni frame
};

struct Scala {
    Vec2 pos;
    std::string textureKey;
};


static Entity omino;
static std::vector<Entity> nemici;
static std::vector<Entity> mattoni;
static std::vector<Scala> scale;

// Gestione textures
static std::map<std::string, Texture> textures;

// Manager asset Android
static AAssetManager* g_assetMgr = nullptr;

// -----------------------------
// Shader program e attributi globali
// -----------------------------
static GLuint program = 0;
static GLint attrPos = -1;
static GLint attrTex = -1;
static GLint uniTex = -1;
static GLint uniScreen = -1;
static int screenWidth = 0;
static int screenHeight = 0;

// -----------------------------
// Funzione di utilità
// -----------------------------
static Texture loadTextureFromAsset(const std::string& filename) {
    //__android_log_print(ANDROID_LOG_DEBUG, "omino Cpp", "loadTextureFromAsset");

    AAsset* asset = AAssetManager_open(g_assetMgr, filename.c_str(), AASSET_MODE_BUFFER);
    if (!asset) {
        __android_log_print(ANDROID_LOG_ERROR, "omino", "Asset non trovato: %s", filename.c_str());
        return {0,0,0};
    }

    size_t len = AAsset_getLength(asset);
    std::vector<unsigned char> buffer(len);
    AAsset_read(asset, buffer.data(), len);
    AAsset_close(asset);

    int w, h, comp;
    unsigned char* data = stbi_load_from_memory(buffer.data(), buffer.size(), &w, &h, &comp, STBI_rgb_alpha);
    if (!data) {
        __android_log_print(ANDROID_LOG_ERROR, "omino", "Errore caricamento immagine: %s", filename.c_str());
        return {0,0,0};
    }

    GLuint texId;
    glGenTextures(1, &texId);
    glBindTexture(GL_TEXTURE_2D, texId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0,
                 GL_RGBA, GL_UNSIGNED_BYTE, data);

    stbi_image_free(data);

    return {texId, w, h};
}

// -----------------------------
// Rendering base
// -----------------------------
static void drawQuad(const Texture& tex, float x, float y) {
    if (tex.id == 0) return;

    GLfloat vertices[] = {
        x, y, 0.0f, 0.0f,
        x + tex.width, y, 1.0f, 0.0f,
        x, y + tex.height, 0.0f, 1.0f,
        x + tex.width, y + tex.height, 1.0f, 1.0f
    };

    glUseProgram(program);
    glBindTexture(GL_TEXTURE_2D, tex.id);
    glUniform1i(uniTex, 0);
    glUniform2f(uniScreen, (float)screenWidth, (float)screenHeight);

    glEnableVertexAttribArray(attrPos);
    glVertexAttribPointer(attrPos, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), vertices);

    glEnableVertexAttribArray(attrTex);
    glVertexAttribPointer(attrTex, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), vertices + 2);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
}

static std::string jstringToString(JNIEnv* env, jstring jStr) {
           const char* chars = env->GetStringUTFChars(jStr, nullptr);
           std::string str(chars);
           env->ReleaseStringUTFChars(jStr, chars);
           return str;
}

// -----------------------------
// JNI
// -----------------------------
extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeInit(JNIEnv* env, jclass, jobject assetManager) {
    g_assetMgr = AAssetManager_fromJava(env, assetManager);

    textures["omino_idle_dx"] = loadTextureFromAsset("sprites/omino_dx_1.png");
    textures["omino_idle_sx"] = loadTextureFromAsset("sprites/omino_sx_1.png");
    textures["omino_idle_up"] = loadTextureFromAsset("sprites/omino_idle_up.png");
    textures["omino_falling"] = loadTextureFromAsset("sprites/omino_falling.png");

    textures["omino_run_dx_1"] = loadTextureFromAsset("sprites/omino_dx_1.png");
    textures["omino_run_dx_2"] = loadTextureFromAsset("sprites/omino_dx_2.png");
    textures["omino_run_dx_3"] = loadTextureFromAsset("sprites/omino_dx_3.png");
    textures["omino_run_dx_4"] = loadTextureFromAsset("sprites/omino_dx_4.png");
    textures["omino_run_dx_5"] = loadTextureFromAsset("sprites/omino_dx_5.png");
    textures["omino_run_dx_6"] = loadTextureFromAsset("sprites/omino_dx_6.png");
    textures["omino_run_dx_7"] = loadTextureFromAsset("sprites/omino_dx_7.png");
    textures["omino_run_dx_8"] = loadTextureFromAsset("sprites/omino_dx_8.png");

    textures["omino_run_sx_1"] = loadTextureFromAsset("sprites/omino_sx_1.png");
    textures["omino_run_sx_2"] = loadTextureFromAsset("sprites/omino_sx_2.png");
    textures["omino_run_sx_3"] = loadTextureFromAsset("sprites/omino_sx_3.png");
    textures["omino_run_sx_4"] = loadTextureFromAsset("sprites/omino_sx_4.png");
    textures["omino_run_sx_5"] = loadTextureFromAsset("sprites/omino_sx_5.png");
    textures["omino_run_sx_6"] = loadTextureFromAsset("sprites/omino_dx_6.png");
    textures["omino_run_sx_7"] = loadTextureFromAsset("sprites/omino_sx_7.png");
    textures["omino_run_sx_8"] = loadTextureFromAsset("sprites/omino_sx_8.png");

    textures["omino_run_up_1"] = loadTextureFromAsset("sprites/omino_up_1.png");
    textures["omino_run_up_2"] = loadTextureFromAsset("sprites/omino_up_2.png");
    textures["omino_run_down_1"] = loadTextureFromAsset("sprites/omino_up_2.png");
    textures["omino_run_down_2"] = loadTextureFromAsset("sprites/omino_up_1.png");

    textures["nemico_idle_dx"] = loadTextureFromAsset("sprites/nemico_dx_1.png");

    textures["mattone"]    = loadTextureFromAsset("tiles/mattone.png");
    textures["scala"]      = loadTextureFromAsset("tiles/scala.png");

    //init Omino
    omino = { {0.0f, 0.0f}, "omino_idle_dx", 1, 0.0f, 0.1f };

    glEnable(GL_BLEND);
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    program = createProgram();
    attrPos = glGetAttribLocation(program, "aPos");
    attrTex = glGetAttribLocation(program, "aTex");
    uniTex = glGetUniformLocation(program, "uTexture");
    uniScreen = glGetUniformLocation(program, "uScreenSize");

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeResize(JNIEnv*, jclass, jint width, jint height) {
    screenWidth = width;
    screenHeight = height;
    glViewport(0, 0, width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetOminoPositions(JNIEnv* env, jclass, jfloat x, jfloat y, jstring currentAnimation) {
       //__android_log_print(ANDROID_LOG_DEBUG, "omino", "nativeSetOminoPositions", "");

       const char* chars = env->GetStringUTFChars(currentAnimation, nullptr);

       omino.pos.x = x;
       omino.pos.y = y;
       omino.currentAn = std::string(chars);

       env->ReleaseStringUTFChars(currentAnimation, chars); // Rilascia la memoria
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetNemiciPositions(JNIEnv* env, jclass, jobjectArray positions) {
    jsize length = env->GetArrayLength(positions);
    nemici.clear(); // Pulisci il vettore esistente

    for (jsize i = 0; i < length; i++) {
        jobject nemicoData = env->GetObjectArrayElement(positions, i);
        jclass nemicoClass = env->GetObjectClass(nemicoData);

        // Ottieni i campi x, y e str
        jfieldID xField = env->GetFieldID(nemicoClass, "x", "F");
        jfieldID yField = env->GetFieldID(nemicoClass, "y", "F");
        jfieldID strField = env->GetFieldID(nemicoClass, "currentAn", "Ljava/lang/String;");

        // Estrai i valori
        float x = env->GetFloatField(nemicoData, xField);
        float y = env->GetFloatField(nemicoData, yField);
        jstring str = (jstring) env->GetObjectField(nemicoData, strField);

        // Converti la stringa in std::string
        const char* strChars = env->GetStringUTFChars(str, nullptr);
        std::string currentAnimation(strChars);
        env->ReleaseStringUTFChars(str, strChars); // Rilascia la memoria

        // Crea un nuovo Entity e aggiungilo al vettore nemici
        Entity newNemico = {{x, y}, currentAnimation, 1, 0.0f, 0.1f}; // Imposta i valori di default
        nemici.push_back(newNemico);

        // Rilascia il riferimento all'oggetto nemico
        env->DeleteLocalRef(nemicoData);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetMattonePositions(JNIEnv* env, jclass, jfloatArray arr) {
    jsize len = env->GetArrayLength(arr);
    jfloat* data = env->GetFloatArrayElements(arr, nullptr);
    mattoni.clear();
    for (int i = 0; i < len; i += 2) {
        mattoni.push_back({data[i], data[i+1]});
    }
    env->ReleaseFloatArrayElements(arr, data, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetScalaPositions(JNIEnv* env, jclass, jfloatArray arr) {
    jsize len = env->GetArrayLength(arr);
    jfloat* data = env->GetFloatArrayElements(arr, nullptr);
    scale.clear();
    for (int i = 0; i < len; i += 2) {
        scale.push_back({data[i], data[i+1]});
    }
    env->ReleaseFloatArrayElements(arr, data, 0);
}




static void changeFrameSetOmino(int maxIdx) {
   omino.animationTime += 0.016f; // Supponendo un frame rate di circa 60 FPS
   if (omino.animationTime >= omino.frameDuration) {
       if(omino.currentFrame < maxIdx){
           omino.currentFrame++;
       }else {
           omino.currentFrame = 1;
       }
       omino.animationTime = 0.0f; // Resetta il tempo
   }
}


extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeRender(JNIEnv* env, jclass) {
    glClear(GL_COLOR_BUFFER_BIT);

    for (auto& m : mattoni)
        drawQuad(textures["mattone"], m.pos.x, m.pos.y);

    for (auto& s : scale)
        drawQuad(textures["scala"], s.pos.x, s.pos.y);

    for (auto& n : nemici)
            drawQuad(textures["nemico_idle_dx"], n.pos.x, n.pos.y);


    std::string key = "";
    if(omino.currentAn == "omino_run_dx"
        || omino.currentAn == "omino_run_sx" ){
        changeFrameSetOmino(8);
        key = omino.currentAn + "_" + std::to_string(omino.currentFrame);

    }else if(omino.currentAn == "omino_run_up"
        || omino.currentAn == "omino_run_down" ){
            changeFrameSetOmino(2);
            key = omino.currentAn + "_" + std::to_string(omino.currentFrame);
    }else { //omino_idle_dx; omino_idle_sx; omino_falling;
            key = omino.currentAn;
    }
    //__android_log_print(ANDROID_LOG_DEBUG, "omino", "texture key: (%s)", key.c_str());

    // Disegna il frame corrente dell'animazione
    drawQuad(textures[key], omino.pos.x, omino.pos.y);
}

