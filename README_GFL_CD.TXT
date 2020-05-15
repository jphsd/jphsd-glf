                    Graphic Layers Framework
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

Contents

  * Requirements
  * Installing the Graphic Layers Framework
  * Running the example
  * Feedback

I Requirements
==============

You will need to have the latest JDK release installed on your system.
At the time this software was packaged, the latest version was 1.2.2.

The 'java' executable is expected to be in your PATH environment variable.

To set that variable:

[UNIX], C-Shell:
----------------
setenv PATH ${PATH}:<JDK_DIR>/bin

[Windows], Command line:
------------------------
set PATH=%PATH%;<JDK_DIR>\bin

Where <JDK_DIR> is the directory where you have installed the JDK. 

IMPORTANT NOTE, WINDOWS 95 Users
---------------------------------

Make sure there is enough memory allocated in the command line environment.
To modify the amount of memory for the initial environment, open an MS-DOS console,
and select 'Properties' from the system menu. Select the 'Memory' tab and select
enough memory in the 'Initial Environment' combo box. If there is not enough 
memory allocated, the scripts for running demos will not work.

IMPORTANT NOTE, Linux Users
---------------------------

The software has been tested on Linux. However, the version of the JDK supported 
on Linux was older than the one available on Solaris and Windows. 
Some of the sample programs do not work well on Linux. However, the majority of 
the examples work well.


II Installing the Graphic Layers Framework
===========================================

Before installing the software, please make sure you read and agree the 
readAndAgree.txt file in the res/fonts subdirectory.

To install the Graphic Layers Framework you need to:

a. Save the glfAll.jar file into the directory you chose.
   for example: Save glf into c:\GLF

b. Unjar the glfAll.jar file.
   Command line: jar xvf glfAll.jar

   This will expand the GLF into you directory. After the file
   has been unjared, you will find the following files:
   + readme.txt (this file)
   + glfSrc.jar contains the source code for GLF
   + glfDoc: contains the javadoc for the GLF
   + res : contains resource various files (fonts, images) for the GLF
   + runsnippet, runsnippet.bat, demorunner, and demorunner.bat : scripts for running demos

c. Copy font files from the <installDir>/res/fonts directory to the 
   <JDK_DIR>/jre/lib/fonts directory, where <JDK_DIR> is the directory
   where the JDK is installed and <installDir> is the directory where
   GLF is installed.
   Remember that those fonts are for use with the GLF demos only and should
   not be used outside this context (as explained in the readAndAgree.txt in the
   <installDir>/res/fonts directory.

III Running the examples
========================

There are multiples ways you can use to run the examples:

a. From the command line, as explained in the book. For example:
   > java -classpath glf.jar com.sun.glf.util.CompositionStudio res\com\sun\glf\beans\UsingLayers.ser.txt

   Note that you can also set the CLASSPATH environment variable to include glf.jar

b. Use the runsnippet script with the example name as an input parameter (see exlanations
   below)

c. Use the DemoRunner application (see explanations below)

III.1. Using runsnippet on Unix

At the command prompt:

cd <installDir>
chmod +x runsnippet
runsnippet <demoName>

Where <demoName> is defined in the book. For example:

runsnippet HelloLayers

III.2 Using runsnippet on Windows

Windows 95 users, see IMPORTANT NOTE at the begining of this document.

At the command prompt:

cd <installDir>
runsnippet <demoName>

Where <demoName> is defined in the book. For example:

runsnippet HelloLayers

III.3 Using DemoRunner

III.3.1 Unix

At the command prompt:

cd <installDir>
chmod +x runsnippet
chmod +x demorunner
demorunner

III.3.2. Windows

Windows 95 users, see IMPORTANT NOTE at the begining of this document.

At the command prompt:

cd <installDir>
demorunner

V Feedback

Your feedback, suggestions and comments are welcome. Please, feel free
to email me at: vincent.hardy@eng.sun.com.

