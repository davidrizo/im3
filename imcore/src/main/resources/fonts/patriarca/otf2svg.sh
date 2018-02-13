#!/bin/bash
FROM=Patriarca-Regular.otf
TO=Patriarca-Regular.svg
#Change fontforge path to your local computer path
/Applications/FontForge.app/Contents/Resources/opt/local/bin/fontforge -lang=ff -c 'Open($1); Generate($2)' "$FROM" "$TO"
