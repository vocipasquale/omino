#include <jni.h>
#include <android/asset_manager_jni.h>
#include <GLES2/gl2.h>
#include <vector>
#include <string>
#include <map>
#include <android/log.h>
#include <unordered_map>

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

struct Porta {
    Vec2 pos;
    std::string textureKey;
};

struct Cassa {
    Vec2 pos;
    std::string textureKey;
};

static Entity omino;
static std::unordered_map<int, Entity> nemici;
//static std::vector<Entity> nemici;
static std::unordered_map<int, Entity> mattoni;
static std::vector<Scala> scale;
static Porta porta;
static std::vector<Cassa> casse;

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
static void addNemico(int id, const Entity& nemico) {
    nemici[id] = nemico; // Aggiungi o aggiorna il nemico con la chiave id
}

static Entity* getNemico(int id) {
    auto it = nemici.find(id);
    if (it != nemici.end()) {
        return &it->second; // Restituisci un puntatore all'Entity
    }
    return nullptr; // Restituisci nullptr se non trovato
}

static void removeNemico(int id) {
    nemici.erase(id); // Rimuovi il nemico dalla mappa
}

static void addMattone(int id, const Entity& mattone) {
    mattoni[id] = mattone; // Aggiunge o aggiorna il mattone con la chiave id
}

static Entity* getMattone(int id) {
    auto it = mattoni.find(id);
    if (it != mattoni.end()) {
        return &it->second; // Restituisce un puntatore all'Entity
    }
    return nullptr; // Restituisci nullptr se non trovato
}

static void removeMattone(int id) {
    mattoni.erase(id); // Rimuove il mattone dalla mappa
}


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

static void changeFrameSet(int maxIdx, Entity& entity) {
   entity.animationTime += 0.016f; // Supponendo un frame rate di circa 60 FPS
   if (entity.animationTime >= entity.frameDuration) {
       if(entity.currentFrame < maxIdx){
           entity.currentFrame++;
       }else {
           entity.currentFrame = 1;
       }
       entity.animationTime = 0.0f; // Resetta il tempo
   }
}

static void disegnaOmino(){
    std::string key = "";
    if(omino.currentAn == "omino_run_dx"
        || omino.currentAn == "omino_run_sx" ){
       changeFrameSet(3, omino);
       key = omino.currentAn + "_" + std::to_string(omino.currentFrame);
    }else if(omino.currentAn == "omino_run_up"
        || omino.currentAn == "omino_run_down" ){
           changeFrameSet(2, omino);
           key = omino.currentAn + "_" + std::to_string(omino.currentFrame);
    }else { //omino_idle_dx; omino_idle_sx; omino_falling;
           key = omino.currentAn;
    }

    // Disegna il frame corrente dell'animazione
    drawQuad(textures[key], omino.pos.x, omino.pos.y);
}

static void disegnaNemici(){
std::string key = "";
for (auto& pair : nemici) {
        int id = pair.first;          // La chiave (id del nemico)
        Entity& n = pair.second; // L'oggetto Entity

            if(n.currentAn == "nemico_run_dx"
                    || n.currentAn == "nemico_run_sx" ){
                    changeFrameSet(3, n);
                    key = n.currentAn + "_" + std::to_string(n.currentFrame);
            }else if(n.currentAn == "nemico_run_up"
                    || n.currentAn == "nemico_run_down" ){
                    changeFrameSet(2, n);
                    key = n.currentAn + "_" + std::to_string(n.currentFrame);
            }else {
                    key = n.currentAn;
            }
            //__android_log_print(ANDROID_LOG_ERROR, "disegna nemico", "texture: %s", key.c_str());
            drawQuad(textures[key], n.pos.x, n.pos.y);
    }
}

static void disegnaScale(){
    for (auto& s : scale)
            drawQuad(textures["scala"], s.pos.x, s.pos.y);
}

static void disegnaMattoni(){
    std::string key = "";

    for (auto& pair : mattoni) {
            int id = pair.first;          // La chiave (id del nemico)
            Entity& m = pair.second; // L'oggetto Entity

            if(m.currentAn == "mattone_erasing_dx" || m.currentAn == "mattone_erasing_sx" ){
               changeFrameSet(7, m);
               key = m.currentAn + "_" + std::to_string(m.currentFrame);
               if(m.currentFrame==6){
                    m.currentFrame=5;
               }

               if(m.currentFrame<=4){
              // __android_log_print(ANDROID_LOG_ERROR, "mattone", "texture: %s %f %f", key.c_str(), m.pos.x, (m.pos.y-64));
                drawQuad(textures[key], m.pos.x, (m.pos.y-64));

              //  __android_log_print(ANDROID_LOG_ERROR, "mattone", "texture: mattone %f %f", m.pos.x, m.pos.y);
                drawQuad(textures["mattone"], m.pos.x, m.pos.y);
               }

            }else {
               key = m.currentAn;
               m.currentFrame=1;
               drawQuad(textures[key], m.pos.x, m.pos.y);
            }
    }
}

static void disegnaPorta(){
    drawQuad(textures[porta.textureKey], porta.pos.x, porta.pos.y);
}

static void disegnaCasse(){
    for (auto& c : casse){
            drawQuad(textures["cassa"], c.pos.x, c.pos.y);
        }
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
    textures["omino_run_sx_1"] = loadTextureFromAsset("sprites/omino_sx_1.png");
    textures["omino_run_sx_2"] = loadTextureFromAsset("sprites/omino_sx_2.png");
    textures["omino_run_sx_3"] = loadTextureFromAsset("sprites/omino_sx_3.png");
    textures["omino_run_up_1"] = loadTextureFromAsset("sprites/omino_up_1.png");
    textures["omino_run_up_2"] = loadTextureFromAsset("sprites/omino_up_2.png");
    textures["omino_run_down_1"] = loadTextureFromAsset("sprites/omino_up_2.png");
    textures["omino_run_down_2"] = loadTextureFromAsset("sprites/omino_up_1.png");

    textures["nemico_idle_sx"] = loadTextureFromAsset("sprites/nemico_sx_1.png");
    textures["nemico_idle_dx"] = loadTextureFromAsset("sprites/nemico_dx_1.png");
    textures["nemico_idle_up"] = loadTextureFromAsset("sprites/nemico_idle_up.png");
    textures["nemico_falling"] = loadTextureFromAsset("sprites/nemico_falling.png");
    textures["nemico_run_dx_1"] = loadTextureFromAsset("sprites/nemico_dx_1.png");
    textures["nemico_run_dx_2"] = loadTextureFromAsset("sprites/nemico_dx_2.png");
    textures["nemico_run_dx_3"] = loadTextureFromAsset("sprites/nemico_dx_3.png");
    textures["nemico_run_sx_1"] = loadTextureFromAsset("sprites/nemico_sx_1.png");
    textures["nemico_run_sx_2"] = loadTextureFromAsset("sprites/nemico_sx_2.png");
    textures["nemico_run_sx_3"] = loadTextureFromAsset("sprites/nemico_sx_3.png");
    textures["nemico_run_up_1"] = loadTextureFromAsset("sprites/nemico_up_1.png");
    textures["nemico_run_up_2"] = loadTextureFromAsset("sprites/nemico_up_2.png");
    textures["nemico_run_down_1"] = loadTextureFromAsset("sprites/nemico_up_2.png");
    textures["nemico_run_down_2"] = loadTextureFromAsset("sprites/nemico_up_1.png");

    textures["mattone"]    = loadTextureFromAsset("tiles/mattone.png");

    textures["mattone_erasing_dx_1"] = loadTextureFromAsset("tiles/mattone_erasing_dx_1.png");
    textures["mattone_erasing_dx_2"] = loadTextureFromAsset("tiles/mattone_erasing_dx_2.png");
    textures["mattone_erasing_dx_3"] = loadTextureFromAsset("tiles/mattone_erasing_dx_3.png");
    textures["mattone_erasing_dx_4"] = loadTextureFromAsset("tiles/mattone_erasing_dx_4.png");

    textures["mattone_erasing_sx_1"] = loadTextureFromAsset("tiles/mattone_erasing_sx_1.png");
    textures["mattone_erasing_sx_2"] = loadTextureFromAsset("tiles/mattone_erasing_sx_2.png");
    textures["mattone_erasing_sx_3"] = loadTextureFromAsset("tiles/mattone_erasing_sx_3.png");
    textures["mattone_erasing_sx_4"] = loadTextureFromAsset("tiles/mattone_erasing_sx_4.png");

    textures["cassa"]    = loadTextureFromAsset("tiles/cassa.png");

    textures["porta_chiusa"]    = loadTextureFromAsset("tiles/porta_chiusa.png");
    textures["porta_aperta"]    = loadTextureFromAsset("tiles/porta_aperta.png");

    textures["scala"]      = loadTextureFromAsset("tiles/scala.png");

    textures["void_texture"]    = loadTextureFromAsset("tiles/void_texture.png");

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
Java_com_game_omino_render_GameRenderer_nativeSetOminoPositionsAndAnimation(JNIEnv* env, jclass, jfloat x, jfloat y, jstring currentAnimation) {
       //__android_log_print(ANDROID_LOG_DEBUG, "omino", "nativeSetOminoPositions", "");

       const char* chars = env->GetStringUTFChars(currentAnimation, nullptr);

       omino.pos.x = x;
       omino.pos.y = y;
       omino.currentAn = std::string(chars);

       env->ReleaseStringUTFChars(currentAnimation, chars); // Rilascia la memoria
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetNemiciPositionsAndAnimation(JNIEnv* env, jclass, jobjectArray positions) {
    jsize length = env->GetArrayLength(positions);

    for (jsize i = 0; i < length; i++) {
        jobject nemicoData = env->GetObjectArrayElement(positions, i);
        jclass nemicoClass = env->GetObjectClass(nemicoData);

        // Ottieni i campi x, y e str
        jfieldID idField = env->GetFieldID(nemicoClass, "id", "I");
        jfieldID xField = env->GetFieldID(nemicoClass, "x", "F");
        jfieldID yField = env->GetFieldID(nemicoClass, "y", "F");
        jfieldID strField = env->GetFieldID(nemicoClass, "currentAn", "Ljava/lang/String;");

        // Estrai i valori
        int id = env->GetIntField(nemicoData, idField);
        float x = env->GetFloatField(nemicoData, xField);
        float y = env->GetFloatField(nemicoData, yField);
        jstring str = (jstring) env->GetObjectField(nemicoData, strField);

        // Converti la stringa in std::string
        const char* strChars = env->GetStringUTFChars(str, nullptr);
        std::string currentAnimation(strChars);
        env->ReleaseStringUTFChars(str, strChars); // Rilascia la memoria

        // Crea un nuovo Entity e aggiungilo al mappa nemici
        Entity* nemico = getNemico(id);
        if (nemico == nullptr) { // Controlla se il nemico è stato trovato
            addNemico(id, {{x, y}, currentAnimation, 1, 0.0f, 0.1f}); // Imposta i valori di default
        }else{
            addNemico(id, {{x, y}, currentAnimation, nemico->currentFrame, nemico->animationTime, nemico->frameDuration}); //aggiorna x,y e currentAn
        }

        // Rilascia il riferimento all'oggetto nemico
        env->DeleteLocalRef(nemicoData);
    }
}


extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetMattonePositions(JNIEnv* env, jclass, jobjectArray positions) {
    jsize length = env->GetArrayLength(positions);

        for (jsize i = 0; i < length; i++) {
            jobject mattoneData = env->GetObjectArrayElement(positions, i);
            jclass mattoneClass = env->GetObjectClass(mattoneData);

            // Ottieni i campi x, y e str
            jfieldID idField = env->GetFieldID(mattoneClass, "id", "I");
            jfieldID xField = env->GetFieldID(mattoneClass, "x", "F");
            jfieldID yField = env->GetFieldID(mattoneClass, "y", "F");
            jfieldID strField = env->GetFieldID(mattoneClass, "currentAn", "Ljava/lang/String;");

            // Estrai i valori
            int id = env->GetIntField(mattoneData, idField);
            float x = env->GetFloatField(mattoneData, xField);
            float y = env->GetFloatField(mattoneData, yField);
            jstring str = (jstring) env->GetObjectField(mattoneData, strField);

            // Converti la stringa in std::string
            const char* strChars = env->GetStringUTFChars(str, nullptr);
            std::string currentAnimation(strChars);
            env->ReleaseStringUTFChars(str, strChars); // Rilascia la memoria

         //   __android_log_print(ANDROID_LOG_ERROR, "mattone", "texture: %s", currentAnimation.c_str());

            // Crea un nuovo Entity e aggiungilo al mappa mattoni
            addMattone(id, {{x, y}, strChars, 1, 0.0f, 0.1f}); // Imposta i valori di default

            // Rilascia il riferimento all'oggetto nemico
            env->DeleteLocalRef(mattoneData);
        }
}




extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetMattoneAnimations(JNIEnv* env, jclass, jobjectArray positions) {
    jsize length = env->GetArrayLength(positions);

            for (jsize i = 0; i < length; i++) {
                jobject mattoneData = env->GetObjectArrayElement(positions, i);
                jclass mattoneClass = env->GetObjectClass(mattoneData);

                // Ottieni i campi x, y e str
                jfieldID idField = env->GetFieldID(mattoneClass, "id", "I");
                jfieldID xField = env->GetFieldID(mattoneClass, "x", "F");
                jfieldID yField = env->GetFieldID(mattoneClass, "y", "F");
                jfieldID strField = env->GetFieldID(mattoneClass, "currentAn", "Ljava/lang/String;");

                // Estrai i valori
                int id = env->GetIntField(mattoneData, idField);
                float x = env->GetFloatField(mattoneData, xField);
                float y = env->GetFloatField(mattoneData, yField);
                jstring str = (jstring) env->GetObjectField(mattoneData, strField);

                // Converti la stringa in std::string
                const char* strChars = env->GetStringUTFChars(str, nullptr);
                std::string currentAnimation(strChars);
                env->ReleaseStringUTFChars(str, strChars); // Rilascia la memoria

                Entity* mattone = getMattone(id);
                if (mattone == nullptr) { // NON DOVREBBE ACCADERE!!!
                    addMattone(id, {{x, y}, currentAnimation, 1, 0.0f, 0.1f});
                }else{
                    addMattone(id, {{x, y}, currentAnimation, mattone->currentFrame, mattone->animationTime, mattone->frameDuration}); //aggiorna x,y e currentAn
                }

                // Rilascia il riferimento all'oggetto nemico
                env->DeleteLocalRef(mattoneData);
            }
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


extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetCassaPositions(JNIEnv* env, jclass, jfloatArray arr) {
    jsize len = env->GetArrayLength(arr);
    jfloat* data = env->GetFloatArrayElements(arr, nullptr);
    casse.clear();
    for (int i = 0; i < len; i += 2) {
        casse.push_back({data[i], data[i+1]});
    }
    env->ReleaseFloatArrayElements(arr, data, 0);
}


extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeSetPortaPositionsAndAnimation(JNIEnv* env, jclass, jfloat x, jfloat y, jstring currentAnimation) {
       const char* chars = env->GetStringUTFChars(currentAnimation, nullptr);

       porta.pos.x = x;
       porta.pos.y = y;
       porta.textureKey = std::string(chars);

       env->ReleaseStringUTFChars(currentAnimation, chars); // Rilascia la memoria
}

extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeClearPorta(JNIEnv* env, jclass){
    porta.textureKey = "void_texture";
}


extern "C" JNIEXPORT void JNICALL
Java_com_game_omino_render_GameRenderer_nativeRender(JNIEnv* env, jclass) {
    glClear(GL_COLOR_BUFFER_BIT);

    disegnaScale();
    disegnaMattoni();
    disegnaCasse();
    disegnaPorta();
    disegnaOmino();
    disegnaNemici();

}

