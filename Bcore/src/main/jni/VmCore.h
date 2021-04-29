//
// Created by Milk on 4/9/21.
//

#ifndef VIRTUALM_VMCORE_H
#define VIRTUALM_VMCORE_H

#include <jni.h>

#define VMCORE_CLASS "top/niunaijun/blackbox/client/VMCore"

class VmCore {
public:
    static int getCallingUid(JNIEnv *env, int orig);
};


#endif //VIRTUALM_VMCORE_H
