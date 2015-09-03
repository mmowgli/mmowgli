#!/bin/bash
#
# Searches the deployment directory for likely javascript files, and
# writes a javascript file that contains an array of the files found.
# This can be included in other javascript files.
#
# @author DMcG

JS_FILES=`find widgetsets/com.vaadin.DefaultWidgetSet -name \*.js -print`

JS_FILES_2=`find widgetsets/edu.nps.moves.mmowgli.widgetset.Mmowgli2Widgetset -name \*.js -print`

JS_FILES_3=`ls vaadin*.js`

echo var externalJavascriptResources = [ > javascriptResources.js

for A_FILE in $JS_FILES
do
  echo "     " \"$A_FILE\", >> javascriptResources.js
done

for A_FILE in $JS_FILES_2
do
  echo "     " \"$A_FILE\", >> javascriptResources.js
done

for A_FILE in $JS_FILES_3
do
  echo "     " \"$A_FILE\", >> javascriptResources.js
done

echo "];" >> javascriptResources.js
