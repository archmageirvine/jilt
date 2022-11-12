#!/bin/bash

LOGNAME=${LOGNAME:-$USER}
export SCRIPTS=${SCRIPTS:-${HOME}/java/cvscheck}
if [ ! "${HOME}" ]; then
    export HOME=/home/${LOGNAME}
fi

export JAVA=/usr/local/java/jdk
export SYSNAME=cvscheck_jilt

export WEB=${HOME}/public_html/cvscheck_jilt

export CODEHOME=${HOME}/jilt

export MAIN_MODULE="."
export SRC_MODULES="src"
export LIB_MODULES="lib"
export TEST_MODULES="test"
export RES_MODULES=""
export JAVADOC_MODULES="src"
export DOCUMENT_CHECKERS="classdocs spelldocs2008"
export JAVAC_OPTS="-source 1.8 -Xlint -Xlint:-serial -proc:none"
export COMPILERS="javacall"
export JAVADOC_OPTS="-source 1.8"
export JUMBLE_OPTS=""
export JUMBLE_ALLOCATION="0"
export DISABLE_JUMBLE=1
export CHECKSTYLE_SUPPRESSIONS_FILE=${CODEHOME}/.cvscheck/checkstyle_suppressions.xml
export LEVEL=4
export STYLE_EXCLUDES=""
export PATH=${HOME}/java_apps/findbugs/bin:${PATH}
export STYLE_CHECKERS="asciiall checkstyleall findbugsall javacpd"
export CPD_TOKENS=250
export PLOT_PACKAGE_DEPENDENCIES_OPTS='jilt 2 2'
export LINKAGE_OPTS="--alltests"
export FINDBUGS_OPTS="-pluginList ${HOME}/java_apps/spotbugs-3.1.0-RC5/lib/fb-contrib-7.0.5.sb.jar"

COLUMN_SEQUENCE=('cleancodedirs' 'compilemodules' 'localtests')
export COLUMN_SEQUENCE

if [ "$1" ]; then
    source "${SCRIPTS}/cvscheck.sh" "$@"
else
    source "${SCRIPTS}/cvscheck.sh" cvscheck_main 2>&1
fi >& /dev/null
