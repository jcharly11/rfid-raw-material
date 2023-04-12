//
// Created by hgallardo on 11/04/2023.
//
#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_checkpoint_rfid_1raw_1material_security_jwt_JWTDecoder_getKey(JNIEnv *env, jobject thiz) {
    std::string b64Key = "Y2Y2MTI3OTVjMTQxYmFhZTczNGJiMjgxZDcwMTM5NTUwMTZlNmYxZjE5MTY2NzkyZGU3YTAzYmQzNGNhOTUyZg==";
    return env->NewStringUTF(b64Key.c_str());}