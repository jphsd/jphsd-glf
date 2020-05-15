# Java2D - Graphics Layers Framework

## Web version

This is a capture of the original Java2D Graphics Layers Framework developed by Vincent Hardy and published in 1999 at java.sun.com.

The project has been restructured as a Maven project to build jphsd-glf.jar and uses version 1.8 of Java. Given the age of the code, copious compilation warnings are generated.

Where changes were required to allow compilation, the original source file is preserved with a .orig extension.
The primary issue was the dependency on the old Sun JPEG codec, since replaced with the ImageIO class.

The three original demos are provided and can be run (once the source has been compiled) thus:

    $ java -cp target/jphsd-glf-1.0.jar com.sun.glf.util.CompositionStudio com.sun.glf.snippets.HelloLayers

    $ java -cp target/jphsd-glf-1.0.jar com.sun.glf.util.CompositionStudio res/com/sun/glf/beans/GLFWebDemoOne.ser.txt

    $ java -Dcom.sun.glf.getAllFonts=true -cp target/jphsd-glf-1.0.jar com.sun.glf.util.CompositionStudio ./res/com/sun/glf/beans/GLFWebDemoTwo.ser.txt

See the original readme.txt for more details.

## Book version

This adds the snippets, demos and gallery apps, and other assets from the CD-ROM that accompanied Hardy's book **Java 2D API Graphics**, Prentice Hall 2000 (ISBN 0-13-014266-2).

The apps have a demo harness to run them from that's invoked with:

    $ java -cp target/jphsd-glf-1.0.jar DemoRunner

The runner makes use of a shell script (Unix/Wondows), *runsnippet*, to launch the apps into their own process space.

See the original README_GFL_CD.TXT for more details.

Known issues:
- CMYKSave (Chapter 4): The ImageIO write call fails inside com.sun.imageio.plugins.jpeg.JPEGImageWriter.writeImage(Native Method)
