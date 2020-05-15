@echo off
@set INSTALL_DIR=.
@set SNIPPET=%1%
@set CLASSPATH_SAV=%CLASSPATH%
@set CLASSPATH=%INSTALL_DIR%\glf.jar
@set FONT_PATH=-Dcom.sun.glf.getAllFonts=true

rem
rem Part I code samples
rem

@if "%SNIPPET%" == "AffineTransformAttribute" goto imageSnippet
@if "%SNIPPET%" == "AffineTransformTypes" goto noArguments
@if "%SNIPPET%" == "AlphaCompositeRules" goto noArguments
@if "%SNIPPET%" == "BasicStrokeControls" goto noArguments
@if "%SNIPPET%" == "ClipAttribute" goto noArguments
@if "%SNIPPET%" == "ClippingUsage" goto imageSnippet
@if "%SNIPPET%" == "CMYKSave" goto cmykSave
@if "%SNIPPET%" == "ColorTransparency" goto colorTransparency
@if "%SNIPPET%" == "CompositeAttribute" goto imageSnippet
@if "%SNIPPET%" == "CustomGlyphLayout" goto glyphLayoutSnippet
@if "%SNIPPET%" == "DukeShapeDemo" goto noArguments
@if "%SNIPPET%" == "FontAttribute" goto noArguments
@if "%SNIPPET%" == "FontFlip" goto noArguments
@if "%SNIPPET%" == "FontLister" goto noArguments
@if "%SNIPPET%" == "FontTransform" goto noArguments
@if "%SNIPPET%" == "HelloRenderingModel" goto noArguments
@if "%SNIPPET%" == "ImageFilters" goto imageSnippet
@if "%SNIPPET%" == "ImageLoad" goto imageSnippet
@if "%SNIPPET%" == "JustifiedTextBlock" goto textBlock
@if "%SNIPPET%" == "OffscreenBufferRendering" goto offscreenSnippet
@if "%SNIPPET%" == "PaintAttribute" goto noArguments
@if "%SNIPPET%" == "PaintTypes" goto imageSnippet
@if "%SNIPPET%" == "PrinterRendering" goto imageSnippet
@if "%SNIPPET%" == "RenderingHintsAttribute" goto noArguments
@if "%SNIPPET%" == "ScreenRendering" goto imageSnippet
@if "%SNIPPET%" == "ShapeClasses" goto noArguments
@if "%SNIPPET%" == "ShapeRendering" goto noArguments
@if "%SNIPPET%" == "ShapeUsage" goto noArguments
@if "%SNIPPET%" == "SimpleTextLayoutRendering" goto noArguments
@if "%SNIPPET%" == "SimpleTextRendering" goto noArguments
@if "%SNIPPET%" == "StrokeAttribute" goto noArguments
@if "%SNIPPET%" == "StyledTextRendering" goto fontSnippet
@if "%SNIPPET%" == "TexturePaintControls" goto textureSnippet

rem
rem Part II code samples
rem

rem Chapter 6
@if "%SNIPPET%" == "HelloLayers" goto compositionFactory

rem Chapter 7
@if "%SNIPPET%" == "UsingLayers" goto textCompositionFactory
@if "%SNIPPET%" == "LayerMarginsUsage" goto compositionFactory
@if "%SNIPPET%" == "AnchorPlacement" goto anchorPlacement
@if "%SNIPPET%" == "AdjustedAnchorPlacement" goto anchorPlacement
@if "%SNIPPET%" == "AnchorPlacementTransform" goto anchorPlacement
@if "%SNIPPET%" == "EiffelFill" goto noArguments
@if "%SNIPPET%" == "EiffelStroke" goto noArguments
@if "%SNIPPET%" == "EiffelComposite" goto noArguments
@if "%SNIPPET%" == "TextLayerControls" goto textCompositionFactory
@if "%SNIPPET%" == "ImagePlacement" goto textCompositionFactory
@if "%SNIPPET%" == "HelloLayersReuse" goto compositionFactory
@if "%SNIPPET%" == "CombiningLayers" goto textCompositionFactory

rem Chapter 8
@if "%SNIPPET%" == "UsingCompositionComponent" goto noArguments

rem Chapter 9
@if "%SNIPPET%" == "GradientPaintExtUsage" goto noArguments
@if "%SNIPPET%" == "RadialGradientPaintUsage" goto noArguments
@if "%SNIPPET%" == "RadialGradientPaintExtUsage" goto noArguments
@if "%SNIPPET%" == "CompositeStrokeUsage" goto noArguments
@if "%SNIPPET%" == "ControlStrokeUsage" goto noArguments
@if "%SNIPPET%" == "ShapeStrokeUsage" goto noArguments
@if "%SNIPPET%" == "WaveStrokeUsage" goto noArguments
@if "%SNIPPET%" == "TextStrokeUsage" goto noArguments
@if "%SNIPPET%" == "ColorCompositeUsage" goto colorCompositeUsage
@if "%SNIPPET%" == "LightOpUsage1" goto imageSnippet
@if "%SNIPPET%" == "LightOpUsage2" goto imageSnippet
@if "%SNIPPET%" == "LightOpUsage3" goto lightOpUsage3
@if "%SNIPPET%" == "SpotLightUsage" goto compositionFactory
@if "%SNIPPET%" == "LightOpUsage4" goto lightOpUsage4
@if "%SNIPPET%" == "ElevationMapUsage" goto textCompositionFactory
@if "%SNIPPET%" == "LightOpUsage5" goto lightOpUsage5
@if "%SNIPPET%" == "GetSunLightUsage" goto textCompositionFactory
@if "%SNIPPET%" == "GetSpotLightUsage" goto textCompositionFactory
@if "%SNIPPET%" == "GetLightRampUsage" goto textCompositionFactory
@if "%SNIPPET%" == "GetHotSpotLightRampUsage" goto textCompositionFactory
@if "%SNIPPET%" == "ToneAdjustmentOpUsage" goto textCompositionFactory
@if "%SNIPPET%" == "WaveTransformUsage" goto noArguments
@if "%SNIPPET%" == "BumpTransformUsage" goto noArguments

rem 
rem Part III code samples
rem

rem Chapter 10
rem
rem Introduction: no examples

rem Chapter 11
@if "%SNIPPET%" == "ImageDropShadowComposition" goto ch11TextCompositionFactory
@if "%SNIPPET%" == "ShadowsComposition" goto ch11TextCompositionFactory
@if "%SNIPPET%" == "ShapeCastShadowComposition" goto ch11TextCompositionFactory
@if "%SNIPPET%" == "TextRecessedShadowComposition" goto ch11TextCompositionFactory

rem Chapter 12
@if "%SNIPPET%" == "BacklitGlowMenuComposition" goto ch12TextCompositionFactory
@if "%SNIPPET%" == "BacklitGlowMenuCompositionOff" goto ch12TextCompositionFactory
@if "%SNIPPET%" == "NeonGlowMenuComposition" goto ch12TextCompositionFactory
@if "%SNIPPET%" == "NeonGlowMenuCompositionCold" goto ch12TextCompositionFactory

rem Chapter 13
@if "%SNIPPET%" == "LightPaintingComposition" goto ch13TextCompositionFactory

rem Chapter 14
@if "%SNIPPET%" == "CircularLayoutComposition" goto ch14TextCompositionFactory
@if "%SNIPPET%" == "TriangularLayoutComposition" goto ch14TextCompositionFactory
@if "%SNIPPET%" == "ShapeLayoutComposition" goto ch14TextCompositionFactory
@if "%SNIPPET%" == "GlyphDecorationComposition" goto ch14TextCompositionFactory

rem Chapter 15
@if "%SNIPPET%" == "PostCardComposition" goto ch15TextCompositionFactory

rem Chapter 16
@if "%SNIPPET%" == "BarComposition" goto ch16TextCompositionFactory
@if "%SNIPPET%" == "CylinderComposition" goto ch16TextCompositionFactory
@if "%SNIPPET%" == "SphereComposition" goto ch16TextCompositionFactory
@if "%SNIPPET%" == "VolumeComposition" goto ch16TextCompositionFactory

rem Chapter 17
@if "%SNIPPET%" == "BrushedMetal" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "RecessedShadow" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "ShadowStandOut" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "GLFWebDemoTwo" goto textCompositionFactory
@if "%SNIPPET%" == "GLFWebDemoOne" goto textCompositionFactory
@if "%SNIPPET%" == "TextFlowers" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "SoftFocus" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "SunSet" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "ShapeSplatter" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "LookupParts" goto ch17TextCompositionFactory
@if "%SNIPPET%" == "Lights" goto ch17TextCompositionFactory

@goto notFound

:noArguments
java %FONT_PATH% com.sun.glf.snippets.%SNIPPET%
@goto end

:offscreenSnippet
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\vango09.jpg %2%
@goto end

:imageSnippet
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\vango09.jpg
@goto end

:textureSnippet
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\texture.jpg
@goto end

:colorTransparency
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\syberia30.jpg
@goto end

:cmykSave
@java %FONT_PATH% com.sun.glf.snippets.CMYKSave %INSTALL_DIR%\res\misc\cmyk.pf %INSTALL_DIR%\res\images\snippets\vango09.jpg %2% true
@goto end

:fontSnippet
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% serif 30
@goto end

:glyphLayoutSnippet
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% serif CrossWords
@goto end

:textBlock
@java %FONT_PATH% com.sun.glf.snippets.JustifiedTextBlock  %INSTALL_DIR%\res\text\apidoc.txt serif 30 500
@goto end

:ch11TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch11\%SNIPPET%.ser.txt
@goto end

:ch12TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch12\%SNIPPET%.ser.txt
@goto end

:ch13TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch13\%SNIPPET%.ser.txt
@goto end

:ch14TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch14\%SNIPPET%.ser.txt
@goto end

:ch15TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch15\%SNIPPET%.ser.txt
@goto end

:ch16TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch16\%SNIPPET%.ser.txt
@goto end

:ch17TextCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\ch17\%SNIPPET%.ser.txt
@goto end

:textCompositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio %INSTALL_DIR%\res\com\sun\glf\beans\%SNIPPET%.ser.txt
@goto end

:compositionFactory
@java %FONT_PATH% com.sun.glf.util.CompositionStudio com.sun.glf.snippets.%SNIPPET%
@goto end

:anchorPlacement
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\syberia84.jpg
@goto end

:colorCompositeUsage
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\vango09.jpg 255 0 0
@goto end

:lightOpUsage3
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\vango09.jpg 1.5  %INSTALL_DIR%\res\images\snippets\elevationMap.jpg
@goto end

:lightOpUsage4
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% doNotUseFile 4  %INSTALL_DIR%\res\images\snippets\elevationMap.jpg
@goto end

:lightOpUsage5
@java %FONT_PATH% com.sun.glf.snippets.%SNIPPET% %INSTALL_DIR%\res\images\snippets\elevationMap.jpg
@goto end

:notFound
@echo Unknow GLF example: %SNIPPET%

:end
set CLASSPATH=%CLASSPATH_SAV%