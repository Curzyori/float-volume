#!/bin/bash
set -e

# Target directory: app/src/main/res
RES_DIR="app/src/main/res"

# Densities and their corresponding sizes
declare -A sizes
sizes[mdpi]=48
sizes[hdpi]=72
sizes[xhdpi]=96
sizes[xxhdpi]=144
sizes[xxxhdpi]=192

echo "Generating PNG launcher icons from logo.svg..."

for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
    size=${sizes[$density]}
    dir="$RES_DIR/mipmap-$density"
    mkdir -p "$dir"
    
    # 1. Standard Squircle Icon
    convert -background none -resize ${size}x${size} logo.svg "$dir/ic_launcher.png"
    
    # 2. Perfect Circle Round Icon
    convert logo.svg -resize 512x512 \
      \( +clone -threshold -1 -draw "circle 256,256 256,0" \) \
      -compose CopyOpacity -composite \
      -resize ${size}x${size} "$dir/ic_launcher_round.png"
      
    echo " -> Generated $density launcher icons (${size}x${size})"
done

echo "Successfully completed all launcher icon PNG generation!"
