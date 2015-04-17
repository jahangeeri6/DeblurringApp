LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC
include C:\OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk


LOCAL_MODULE    := DeblurringAPP
LOCAL_SRC_FILES := Darken.cpp
LOCAL_SRC_FILES += GaussianBlurFilter.cpp
LOCAL_SRC_FILES += AverageSmoothFilter.cpp
LOCAL_SRC_FILES += SoftGlowFilter.cpp
LOCAL_SRC_FILES += LightFilter.cpp
LOCAL_SRC_FILES += LomoAddBlackRound.cpp
LOCAL_SRC_FILES += NeonFilter.cpp
LOCAL_SRC_FILES += OilFilter.cpp
LOCAL_SRC_FILES += SketchFilter.cpp
LOCAL_SRC_FILES += TvFilter.cpp
LOCAL_SRC_FILES += SharpenFilter.cpp
LOCAL_SRC_FILES += ReliefFilter.cpp
LOCAL_SRC_FILES += PixelateFilter.cpp
LOCAL_SRC_FILES += BlockFilter.cpp
LOCAL_SRC_FILES += GammaCorrectionFilter.cpp
LOCAL_SRC_FILES += MotionBlurFilter.cpp
LOCAL_SRC_FILES += BrightContrastFilter.cpp
LOCAL_SRC_FILES += ColorTranslator.cpp
LOCAL_SRC_FILES += HueSaturationFilter.cpp
LOCAL_SRC_FILES += GothamFilter.cpp
LOCAL_SRC_FILES += HDRFilter.cpp
LOCAL_SRC_FILES += cn_Ragnarok_NativeFilterFunc.cpp


LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
