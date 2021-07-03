#!/bin/bash

# 普通包
sed -i "" "s/IS_MIANDIAN_2_1 = true/IS_MIANDIAN_2_1 = false/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sed -i "" "s/IS_MIANDIAN_3_1 = true/IS_MIANDIAN_3_1 = false/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sh ./build_release.sh "_normal"

# 缅甸2-1(只显示自己)
sed -i "" "s/IS_MIANDIAN_2_1 = false/IS_MIANDIAN_2_1 = true/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sed -i "" "s/IS_MIANDIAN_3_1 = true/IS_MIANDIAN_3_1 = false/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sh ./build_release.sh "_miandian2_x"

# 缅甸3-1(只显示自己)
sed -i "" "s/IS_MIANDIAN_2_1 = true/IS_MIANDIAN_2_1 = false/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sed -i "" "s/IS_MIANDIAN_3_1 = false/IS_MIANDIAN_3_1 = true/g" app/src/main/java/com/tangs/myapplication/BuildConfig.java
sh ./build_release.sh "_miandian3_x"
