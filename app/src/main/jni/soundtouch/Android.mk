# Copyright (C) 2010 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# $Id: Android.mk 216 2015-05-18 15:28:41Z oparviai $

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# *** Remember: Change -O0 into -O2 in add-applications.mk ***

LOCAL_MODULE    := soundtouch
LOCAL_SRC_FILES := soundtouch-jni.cpp $(LOCAL_PATH)/source/SoundTouch/AAFilter.cpp  $(LOCAL_PATH)/source/SoundTouch/FIFOSampleBuffer.cpp \
                $(LOCAL_PATH)/source/SoundTouch/FIRFilter.cpp $(LOCAL_PATH)/source/SoundTouch/cpu_detect_x86.cpp \
                $(LOCAL_PATH)/source/SoundTouch/sse_optimized.cpp $(LOCAL_PATH)/source/SoundStretch/WavFile.cpp \
                $(LOCAL_PATH)/source/SoundTouch/RateTransposer.cpp $(LOCAL_PATH)/source/SoundTouch/SoundTouch.cpp \
                $(LOCAL_PATH)/source/SoundTouch/InterpolateCubic.cpp $(LOCAL_PATH)/source/SoundTouch/InterpolateLinear.cpp \
                $(LOCAL_PATH)/source/SoundTouch/InterpolateShannon.cpp $(LOCAL_PATH)/source/SoundTouch/TDStretch.cpp \
                $(LOCAL_PATH)/source/SoundTouch/BPMDetect.cpp $(LOCAL_PATH)/source/SoundTouch/PeakFinder.cpp 

# for native audio
LOCAL_SHARED_LIBRARIES += -lgcc 
# --whole-archive -lgcc 
# for logging
LOCAL_LDLIBS    += -llog
# for native asset manager
#LOCAL_LDLIBS    += -landroid

# Custom Flags: 
# -fvisibility=hidden : don't export all symbols
LOCAL_CFLAGS += -fvisibility=hidden -fdata-sections -ffunction-sections -I$(LOCAL_PATH)/include

# OpenMP mode : enable these flags to enable using OpenMP for parallel computation 
#LOCAL_CFLAGS += -fopenmp
#LOCAL_LDFLAGS += -fopenmp


# Use ARM instruction set instead of Thumb for improved calculation performance in ARM CPUs	
LOCAL_ARM_MODE := arm

#LOCAL_C_INCLUDES := include

include $(BUILD_SHARED_LIBRARY)
