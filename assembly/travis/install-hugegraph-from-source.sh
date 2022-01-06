#!/bin/bash

set -ev

if [[ $# -ne 1 ]]; then
    echo "Must pass commit id of hugegraph repo"
    exit 1
fi

echo `git version`

COMMIT_ID=$1
HUGEGRAPH_GIT_URL="https://github.com/starhugegraph/hugegraph.git"

#git clone ${HUGEGRAPH_GIT_URL}
#git checkout -b gh-dis-release origin/gh-dis-release
git clone -b gh-dis-release --depth 20 ${HUGEGRAPH_GIT_URL}
cd hugegraph

# install lib
LIB_DIR="hugegraph-dist/src/assembly/travis/lib"
mvn install:install-file -DgroupId=com.baidu.hugegraph -DartifactId=hugegraph-common -Dversion=1.8.10 -Dpackaging=jar -Dfile=$LIB_DIR/hugegraph-common-1.8.10.jar -DpomFile=$LIB_DIR/common-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-pd-client-1.0-SNAPSHOT.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-pd-client -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DpomFile=$LIB_DIR/hg-pd-client-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-pd-common-1.0-SNAPSHOT.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-pd-common -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DpomFile=$LIB_DIR/hg-pd-common-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-pd-grpc-1.0-SNAPSHOT.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-pd-grpc -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DpomFile=$LIB_DIR/hg-pd-grpc-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-store-client-1.0-SNAPSHOT.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-store-client -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DpomFile=$LIB_DIR/hg-store-client-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-store-grpc-1.0-SNAPSHOT.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-store-grpc -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DpomFile=$LIB_DIR/hg-store-grpc-pom.xml
mvn install:install-file -Dfile=$LIB_DIR/hg-store-term-1.0.1.jar -DgroupId=com.baidu.hugegraph -DartifactId=hg-store-term -Dversion=1.0.1 -Dpackaging=jar -DpomFile=$LIB_DIR/hg-store-term-pom.xml


mvn package -DskipTests
mv hugegraph-*.tar.gz ../
cd ../
rm -rf hugegraph
tar -zxvf hugegraph-*.tar.gz
cd hugegraph-*/

REST_SERVER_CONFIG="conf/rest-server.properties"
GREMLIN_SERVER_CONFIG="conf/gremlin-server.yaml"

# config gremlin-server
echo "
authentication: {
  authenticator: com.baidu.hugegraph.auth.StandardAuthenticator,
  authenticationHandler: com.baidu.hugegraph.auth.WsAndHttpBasicAuthHandler,
  config: {tokens: conf/rest-server.properties}
}" >> $GREMLIN_SERVER_CONFIG

sed -i 's/#auth.authenticator=com.baidu.hugegraph.auth.StandardAuthenticator/auth.authenticator=com.baidu.hugegraph.auth.StandardAuthenticator/' ${REST_SERVER_CONFIG}


# start HugeGraphServer with https protocol
bin/start-hugegraph.sh
cd ../
