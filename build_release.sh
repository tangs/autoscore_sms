#!/bin/bash

version=`cat app/build.gradle | egrep "versionName (.+)" | awk -F" "  {'print$2'} | sed 's/"//g'`
dest_name=auto_score_sms_v$version.apk
# echo $dest_name

if ./gradlew connectedAndroidTest 
then
	echo Android test succ.
	if ./gradlew assembleRelease
	then
		echo make apk succ.
		apksigner sign --ks /Users/tangs/Documents/jks/1.jks --ks-pass pass:336699 --out app/build/outputs/apk/release/$dest_name app/build/outputs/apk/release/app-release-unsigned.apk
	else
		echo make apk fail.
	fi	
else
	echo Android test fail, checkout code please.
fi