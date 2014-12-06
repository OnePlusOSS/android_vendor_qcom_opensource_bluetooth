ifneq ($(TARGET_USES_AOSP),true)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
src_dirs:= src/org/codeaurora/bluetooth/btcservice \
           src/org/codeaurora/bluetooth/ftp \
           src/org/codeaurora/bluetooth/dun \
           src/org/codeaurora/bluetooth/sap \
           src/org/codeaurora/bluetooth/pxpservice \
           src/org/codeaurora/bluetooth/a4wp

LOCAL_SRC_FILES := \
        $(call all-java-files-under, $(src_dirs)) \
        src/org/codeaurora/bluetooth/pxpservice/IPxpService.aidl

LOCAL_PACKAGE_NAME := BluetoothExt
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES := javax.obex
LOCAL_JAVA_LIBRARIES += mms-common
LOCAL_JAVA_LIBRARIES += telephony-common
LOCAL_JAVA_LIBRARIES += com.quicinc.wbc

LOCAL_STATIC_JAVA_LIBRARIES := com.android.vcard

LOCAL_REQUIRED_MODULES := bluetooth.default

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_MULTILIB:= 32
LOCAL_JNI_SHARED_LIBRARIES:= libbluetooth_jni

include $(BUILD_PACKAGE)


include $(call all-makefiles-under,$(LOCAL_PATH))

endif
