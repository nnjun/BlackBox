//
// Created by Milk on 2021/5/5.
//

//
// Created by Milk on 5/5/21.
//

#include <cstring>
#include "VMClassLoaderHook.h"
#import "JniHook/JniHook.h"
static bool hideXposedClass = false;

HOOK_JNI(jstring, findLoadedClass, JNIEnv *env, jobject obj, jobject class_loader, jstring name) {
    const char * nameC = env->GetStringUTFChars(name, JNI_FALSE);
    if (hideXposedClass) {
        if (strstr(nameC, "de/robv/android/xposed/") ||
            strstr(nameC, "me/weishu/epic")) {
            return nullptr;
        }
    }
    // ALOGD("findLoadedClass: %s", nameC);
    jstring result = orig_findLoadedClass(env, obj, class_loader, name);
    env->ReleaseStringUTFChars(name, nameC);
    return result;
}

void VMClassLoaderHook::init(JNIEnv *env) {
    const char *className = "java/lang/VMClassLoader";
    JniHook::HookJniFun(env, className, "findLoadedClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;",
                        (void *) new_findLoadedClass,
                        (void **) (&orig_findLoadedClass), true);
}

void VMClassLoaderHook::hideXposed() {
    hideXposedClass = true;
}
