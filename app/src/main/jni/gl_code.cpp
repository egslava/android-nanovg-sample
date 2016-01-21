/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// OpenGL ES 2.0 code

#include <jni.h>
#include <android/log.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define  LOG_TAG    "libgl2jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define NANOVG_GLES2_IMPLEMENTATION   // Use GL2 es implementation.
//#include "nanovg/nanovg.h"
#include "nanovg/nanovg.h"
#include "nanovg/nanovg_gl.h"

float verticeX(float angle);
float verticeY(float angle);

static void printGLString(const char *name, GLenum s) {
    const char *v = (const char *) glGetString(s);
    LOGI("GL %s = %s\n", name, v);
}

static void checkGlError(const char* op) {
    for (GLint error = glGetError(); error; error
            = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}


struct NVGcontext* vg;

int _w, _h;

float rotation,             // final value
        currentRotation;    // current animated value
float r,g,b,a;      // bg
int w, h;
float x, y;

bool setupGraphics(int _w, int _h) {
    w = _w; h = _h;

    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extensions", GL_EXTENSIONS);

    LOGI("setupGraphics(%d, %d)", w, h);
    vg = nvgCreateGLES2(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);

    glViewport(0, 0, w, h);
    checkGlError("glViewport");

    glClearColor(r,g,b,a);
    checkGlError("glClearColor");
    return true;
}

void renderFrame() {
    currentRotation = currentRotation*0.9f + rotation * 0.1f;

    glStencilMask(0xff);
    checkGlError("glStencilMask");
    glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    checkGlError("glClear");

    nvgBeginFrame(vg, w, h, w/h);
    nvgBeginPath(vg);
    nvgMoveTo(vg, verticeX(90.f), verticeY(90.f));
    nvgLineTo(vg, verticeX(90.f + 120.0f), verticeY(90.0f + 120.0f));
    nvgLineTo(vg, verticeX(90.f + 240.f), verticeY(90.0f + 240.0f) );
    nvgClosePath(vg);
    nvgFillColor(vg, nvgRGBA(255,192,0,255));
    nvgFill(vg);
    nvgFillColor(vg, nvgRGBA(255,192,0,255));
    nvgEndFrame(vg);

}

//inline float sqr(float x){return x*x};
float len(){
    return sqrt( 0.25f*w*w + 0.25f*h*h) * 0.5f; // 0.75 of 0.5w * 0.5w + 0.5h*0.5h
}
float verticeX(float angle){
    return x + w/2.0f + cos(nvgDegToRad(angle + currentRotation) ) * len();
}

float verticeY(float angle){
    return y + h/2.0f - sin(nvgDegToRad(angle + currentRotation) ) * len();
}

extern "C" {
    JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_init(JNIEnv * env, jobject obj,  jint width, jint height);
    JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_step(JNIEnv * env, jobject obj);
    JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_setRotation(JNIEnv *env, jclass type, jfloat _rotation);
    JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_setBackgroundColor(JNIEnv *env, jclass type, jfloat R, jfloat G,
                                                             jfloat B, jfloat A);
    JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_setTranslation(JNIEnv *env, jclass type, jfloat _x, jfloat _y);
};

JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_init(JNIEnv * env, jobject obj,  jint width, jint height)
{
    setupGraphics(width, height);
}

JNIEXPORT void JNICALL Java_ru_egslava_hello_1nanovg_JNI_step(JNIEnv * env, jobject obj)
{
    renderFrame();
}

JNIEXPORT void JNICALL
Java_ru_egslava_hello_1nanovg_JNI_setRotation(JNIEnv *env, jclass type, jfloat _rotation) {
    rotation = _rotation;
}

JNIEXPORT void JNICALL
Java_ru_egslava_hello_1nanovg_JNI_setBackgroundColor(JNIEnv *env, jclass type, jfloat R, jfloat G,
                                                     jfloat B, jfloat A) {

    r = R; g = G; b = B; a = A;     // if method was called before initialization, save params and defer...
    glClearColor(r,g,b,a);      // if method is called after GL initialization
    checkGlError("glClearColor");
}

JNIEXPORT void JNICALL
Java_ru_egslava_hello_1nanovg_JNI_setTranslation(JNIEnv *env, jclass type, jfloat _x, jfloat _y) {
    x = _x; y=_y;
}