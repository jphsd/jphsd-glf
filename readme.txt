                    Graphic Layers Framework (GLF)
                          Release 1.0


                        WARRANTY LIMITS

   READ FOLLOWING AGREEMENT FOLLOWING BEFORE INSTALLING OR USING THE
   SOFTWARE PACKAGE.

   BY OPENING THIS SEALED SOFTWARE MEDIA PACKAGE OR DOWNLOADING THIS 
   SOFTWARE, YOU ACCEPT AND AGREE TO THE TERMS AND CONDITIONS BELOW.  IF YOU DO NOT 
   AGREE, DO NOT DOWNLOAD OR USE THE SOFTWARE.  

   The software media is distributed on an "As Is" basis, without warranty. 
   Neither the authors, the software developers, Sun Microsystems nor Prentice Hall 
   make any representation, or warranty, either express or implied, with respect 
   to the software programs, their quality, accuracy, or fitness for a specific purpose. 
   Therefore, neither the authors, the software developers nor Prentice Hall shall 
   have any liability to you or any other person or entity with respect to any liability, 
   loss, or damage caused or alleged to have been caused directly or indirectly by 
   programs contained on the media.  This includes, but is not limited to,
   interruption of service, loss of data, loss of classroom time, loss of 
   consulting or anticipatory profits, or consequential damages from the use of these programs.  


                           DISCLAIMER

     Please do not rely on this software for production-quality and/or
     mission-critical applications; the API is provided for demonstration
     purposes and is not a supported product.


Introduction
============

This software package contains the Graphic Layers Framework (GLF) Software. You need to 
follow the installation instructions and build the software before running the examples.

Contents
========

  * Requirements
  * Installing the Graphic Layers Framework
  * Building the Graphic Layers Framework
  * Running the example
  * Feedback
  * Important note

I Requirements
==============

You need to have the latest JDK release installed on your system.
At this time, the latest version is 1.2.2.


II Installing the Graphic Layers Framework
===========================================

Before installing the software, please make sure you read and agree the 
readAndAgree.txt file in the res/fonts subdirectory.

To install the Graphic Layers Framework you need to:

a. Save the glfWebAll.zip file into the directory you choose.
   for example: Save glf into c:\GLF

b. Unjar the glfWebAll.zip file.
   Command line: jar xvf glfWebAll.zip

   or use another zip utility to unpack the file.

   This expands the GLF distribution into your directory. After the file
   has been unzipped, you find the following files:

   + readme.txt (this file)
   + glfWebSrc.jar contains the source code for GLF
   + glfDoc: contains the javadoc for the GLF
   + res : contains various resource files (fonts, images) for the GLF
   + runDemo.bat and runDemo : scripts for running demos
   + glfbuild.bat and glfbuild : scripts for building GLF

c. Copy font files from the <installDir>/res/fonts directory to the 
   <JDK_DIR>/jre/lib/fonts directory, where <JDK_DIR> is the directory
   where the JDK is installed and <installDir> is the directory where
   GLF is installed.
   Remember that those fonts are for use with the GLF demos only and should
   not be used outside this context (as explained in the readAndAgree.txt in the
   <installDir>/res/fonts directory).

III Building the Graphic Layers Framework
=========================================

III.1. Unix

At the command prompt:

cd <installDir>
chmod +x glfbuild
glfbuild

III.2. Windows 

At the command prompt:

cd <installDir>
glfbuild

IV Running the examples
=======================

There are three examples coming with this distribution. Each is able to
produce a different output, and each can be configured to modify the image
created. When you start the examples as explained below, an application called
the Composition Studio starts that lets you decide the parameters (Colors, Fonts,
etc...) that the example should use. The default parameters can be used without
modification. Once you are satisfied with the parameter settings, you can click 
on the Preview button to see the output for the example. 
The three examples illustrate the type of sophisticated output you can create
with the Graphic Layers Framework. The first example, "Hello Layers" is a very
simple example that shows how to work with layers. The other two examples, demoOne
and demoTwo, illustrate various graphic features, such as gradient paint, masking, 
custom filters and custom composites.

IV.1. Unix

At the command prompt:

cd <installDir>
chmod +x runDemo
runDemo <demoName>

Where <demoName> can be either hello, demoOne or demoTwo. For example:

runDemo demoOne

IV.2 Windows

At the command prompt:

cd <installDir>
runDemo <demoName>

Where <demoName> can be either hello, demoOne or demoTwo. For example:

runDemo demoOne

V Feedback
==========

Your feedback, suggestions and comments are welcome. Please, feel free
to email me at: vincent.hardy@eng.sun.com or vincent.hardy@corp.sun.com

VI Important Note
=================

This Web distribution of the Graphic Layers Framework contains the core
packages of the framework as well as all the related utilities and three
examples. 
However, it does not contain all the examples that were developed with 
it for the purpose of the "Java 2D(tm) API Graphics" book, published by 
Prentice  Hall. The CD-ROM that comes with the book does contain all
the code examples discussed in the book.


