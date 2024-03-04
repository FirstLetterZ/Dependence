#!/bin/sh
#modelName="global"
#modelName="api"
#modelName="views"
modelName="dataparser"
#modelName="process"
#modelName="permission"
#modelName="appstack"
#modelName="file"
#modelName="fingerprint"
#modelName="fragmentManager"
#modelName="wheelpicker"
#modelName="version"
#modelName="toolkit"
#modelName="compatFragmentManager"
#modelName="compatPermission"
#modelName="frame"

./gradlew :$modelName:clean --info
./gradlew :$modelName:build --info
./gradlew :$modelName:publish --info