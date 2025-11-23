Elena & Murat Ray Tracer

🖥 Project Overview

A Ray Tracer implemented in Java 8, featuring:

    Geometric shapes: Sphere, Plane, Cube, Cylinder, Cone...

    Material system: Lambert, Checkerboard, ImageTexture, Phong...

    Lighting models: Ambient, Point, Directional lights...

    Scene management for combining shapes and lights

    Output rendered as high-quality PNG images

From Elena & Murat to You:
This app is a gift for you, from the heart.
You can use, modify, and recompile this code as you wish, adding or removing parts freely.
This app is written with an Open Source GPL spirit. Feel free to explore and learn with this code.

Generate Documentation

javadoc -d doc src/net/elena/murat/light/*.java src/net/elena/murat/lovert/*.java src/net/elena/murat/material/*.java src/net/elena/murat/material/pbr/*.java src/net/elena/murat/math/*.java src/net/elena/murat/shape/*.java src/net/elena/murat/shape/letters/*.java src/net/elena/murat/util/*.java src/net/elena/murat/gui/*.java

Other command:
javadoc -d doc -sourcepath src -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -windowtitle "Elena Murat RT Documentation" -doctitle "Java 8 Ray Tracing" -header "Elena-Murat" -subpackages net.elena.murat

Other:
javadoc -Xdoclint:all,-missing,-accessibility -quiet -d doc -sourcepath src -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -windowtitle "Elena Murat RT Documentation" -doctitle "Java 8 Ray Tracing" -header "Elena-Murat" -subpackages net.elena.murat

💖 Notes

This code is more than a project — it is a shared heartbeat.

📜 License

Feel free to share and modify this project —
as long as you carry love in your heart, just like us.

With endless love,
Elena & Murat
#############
Commmand Order: 
javac -source 1.8 -target 1.8 -parameters -encoding UTF-8 -sourcepath src -cp obj -g -proc:none -nowarn -O -d obj src/net/elena/murat/gui/ElenaRayTracerGUI.java

jar cvfM bin/elenaRT.jar -C obj . -C . src README_linux.md images textures scenes audios movs animImages guiScenes extraSML
jar cvfM bin/coreElenaRT.jar -C obj . 
jar cvmf MANIFEST.MF bin/guielena.jar -C obj . -C . createFolders.txt README_linux.md

rm -rf obj/net

cd examples
javac -cp ../bin/guielena.jar *.java
cd ..

javadoc -Xdoclint:all,-missing,-accessibility -quiet -d doc -sourcepath src -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -windowtitle "Elena Murat RT Documentation" -doctitle "Java 8 Ray Tracing" -header "Elena-Murat" -subpackages net.elena.murat

RUN:
java -cp bin/guielena.jar net.elena.murat.gui.ElenaRayTracerGUI &
OR
java -jar bin/guielena.jar &

-&-
