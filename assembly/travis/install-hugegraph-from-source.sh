#!/bin/bash

set -ev

if [[ $# -ne 1 ]]; then
    echo "Must pass commit id of hugegraph repo"
    exit 1
fi

TRAVIS_DIR=`dirname $0`

echo `git version`

COMMIT_ID=$1
HUGEGRAPH_GIT_URL="https://github.com/starhugegraph/hugegraph.git"

#git clone ${HUGEGRAPH_GIT_URL}
#git checkout -b gh-dis-release origin/gh-dis-release
git clone -b gh-dis-release --depth 20 ${HUGEGRAPH_GIT_URL}
cd hugegraph

mvn package -DskipTests
mv hugegraph-*.tar.gz ../
cd ../
rm -rf hugegraph
tar -zxvf hugegraph-*.tar.gz
cd hugegraph-*/

cp -rf ../$TRAVIS_DIR/conf/log4j2.xml conf/

REST_SERVER_CONFIG="conf/rest-server.properties"
GREMLIN_SERVER_CONFIG="conf/gremlin-server.yaml"

# config gremlin-server
echo "
authentication: {
  authenticator: com.baidu.hugegraph.auth.StandardAuthenticator,
  authenticationHandler: com.baidu.hugegraph.auth.WsAndHttpBasicAuthHandler,
  config: {tokens: conf/rest-server.properties}
}" >> $GREMLIN_SERVER_CONFIG

sed -i 's/#auth.authenticator=/auth.authenticator=com.baidu.hugegraph.auth.StandardAuthenticator/' ${REST_SERVER_CONFIG}


# start HugeGraphServer with https protocol
bin/start-hugegraph.sh
cd ../
