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
cur=`pwd`
self=$cur/$0
selfdir=${self%\/*}/

mvn install:install-file -Dfile=$selfdir/third-party/commons-cli-1.2.jar -DartifactId=commons-cli -DgroupId=commons-cli -Dversion=1.2 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=$selfdir/third-party/soot-2.4.0.jar -DartifactId=soot -DgroupId=soot -Dversion=2.4.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=$selfdir/third-party/asm-3.1.jar -DartifactId=asm -DgroupId=asm -Dversion=3.1 -Dpackaging=jar -DgeneratePom=true

mvn clean
mvn package

cd $selfdir/swan
mvn assembly:assembly
cd $cur

# install
test -d $selfdir/bin || mkdir $selfdir/bin

cp $selfdir/swan/target/swan-1.0-SNAPSHOT-jar-with-dependencies.jar $selfdir/bin/swan.jar
cp $selfdir/libtransform/target/libtransform-1.0-SNAPSHOT.jar $selfdir/bin/libtransform.jar
cp $selfdir/libgen/target/libgen-1.0-SNAPSHOT.jar $selfdir/bin/libgen.jar
cp $selfdir/libmonitor/target/libmonitor-1.0-SNAPSHOT.jar $selfdir/bin/libmonitor.jar
cp -r $selfdir/third-party/ $selfdir/bin
