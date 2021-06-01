#!/bin/bash

# 普通包
sed -i "" "s/IS_MIANDIAN_13 = true/IS_MIANDIAN_13 = false/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sh ./build_release.sh "_normal"

# 缅甸13(只显示自己)
sed -i "" "s/IS_MIANDIAN_13 = false/IS_MIANDIAN_13 = true/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sh ./build_release.sh "_miandian13"
