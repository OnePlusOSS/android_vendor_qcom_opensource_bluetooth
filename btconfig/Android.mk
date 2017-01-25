#ifeq ($(BOARD_HAVE_BLUETOOTH),true)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := system/bt/hci/include

LOCAL_SRC_FILES:= \
              btconfig.c

LOCAL_MULTILIB := 32
LOCAL_MODULE_TAGS := debug optional
LOCAL_MODULE :=btconfig

LOCAL_SHARED_LIBRARIES += libcutils   \
                          libutils    \
                          libdl

include $(BUILD_EXECUTABLE)
#endif
