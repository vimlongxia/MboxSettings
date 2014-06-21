LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := MboxSetting
LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := libksoap2
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
 
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libksoap2:libs/ksoap2.jar
include $(BUILD_MULTI_PREBUILT)


include $(call all-makefiles-under,$(LOCAL_PATH))

