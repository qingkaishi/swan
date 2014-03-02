#!/bin/bash

# configure
if [ -f .conftest ]; then
    rm .conftest
fi
java -version 2>>.conftest
javaversion=`head -1 .conftest | cut -d" " -f 3 | cut -d. -f 2`
if [ -z "$javaversion" ];then
    echo "Cannot parse java version string. please report bug!"
    exit 1
elif [ "$javaversion" != 6 ];then
    echo "Java version: $javaversion. Please use Java 6."
    exit 1
fi
rm .conftest

if [ -d /tmp ];then
    touch /tmp/.swan.args 2>/dev/null
else
    echo "Directory /tmp does not exist! Is it a linux OS?"
    exit 1
fi

# clean and compile
mvn install:install-file -Dfile=/home/ise/swan/third-party/commons-cli-1.2.jar -DartifactId=commons-cli -DgroupId=commons-cli -Dversion=1.2 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=/home/ise/swan/third-party/soot-2.4.0.jar -DartifactId=soot -DgroupId=soot -Dversion=2.4.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=/home/ise/swan/third-party/asm-3.1.jar -DartifactId=asm -DgroupId=asm -Dversion=3.1 -Dpackaging=jar -DgeneratePom=true

mvn clean
mvn package

cd swan
mvn assembly:assembly
cd ..

# install
cp ./swan/target/swan-1.0-SNAPSHOT-jar-with-dependencies.jar ./bin/swan.jar
cp ./libtransform/target/libtransform-1.0-SNAPSHOT.jar ./bin/libtransform.jar
cp ./libgen/target/libgen-1.0-SNAPSHOT.jar ./bin/libgen.jar
cp ./libmonitor/target/libmonitor-1.0-SNAPSHOT.jar ./bin/libmonitor.jar
