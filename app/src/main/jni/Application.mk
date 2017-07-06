# $Id: Application.mk 212 2015-05-15 10:22:36Z oparviai $
#
# Build library bilaries for all supported architectures
#

# attention : 64bit not use optimize
APP_ABI := armeabi-v7a armeabi  x86 arm64-v8a x86_64
#APP_OPTIM := release
APP_STL := gnustl_static
APP_CPPFLAGS := -fexceptions # -D SOUNDTOUCH_DISABLE_X86_OPTIMIZATIONS

