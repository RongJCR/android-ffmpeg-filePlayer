LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := Myffmpeg_codec
LOCAL_SRC_FILES := com_h264_decode2_FFmpegNative.c

LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_SHARED_LIBRARIES:= libavformat libavcodec libavutil libswscale libswresample
include $(BUILD_SHARED_LIBRARY)
$(call import-module,ffmpeg-2.7.2/android/arm)
