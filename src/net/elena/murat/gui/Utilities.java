// Murat Inan
package net.elena.murat.gui;

// JAVA
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

// CUSTOM
import net.elena.murat.light.*;
import net.elena.murat.material.*;
import net.elena.murat.material.pbr.*;
import net.elena.murat.shape.*;
import net.elena.murat.shape.letters.*;
import net.elena.murat.math.*;
import net.elena.murat.lovert.*;
import net.elena.murat.util.*;

public class Utilities extends Object {
	private static JComponent pane = new JTextField("");
	
	protected static java.util.List<EMShape> shapes = new ArrayList<>();
    protected static java.util.List<Light> lights = new ArrayList<>();
    
    // Shape types — CustomShape ve Letter3D çıkarıldı
    public static final String[] SHAPE_TYPES = {
        "Plane", "Sphere", "Box", 
        "Cone", "Cylinder", "EmojiBillboard",
        "Torus", "TorusKnot", "Triangle", 
        "Ellipsoid", "Crescent", "Cube",  
        "Hyperboloid", "Rectangle3D", 
        "UnionCSG", "IntersectionCSG", "DifferenceCSG",
        "CustomShape"
    };

    // Material types
    public static final String[] MATERIAL_TYPES = {
    "CheckerboardMaterial", "EmissiveMaterial", "ImageTextureMaterial",
    "TextureMaterial", "TextDielectricMaterial", "SphereWordTextureMaterial", 
    "LambertMaterial", "TransparentPNGMaterial", "AfricanKenteMaterial", 
    "AmberMaterial", "AnisotropicMetalMaterial", "IsotropicMetalTextMaterial", 
    "AnodizedMetalMaterial", "AnodizedTextMaterial", "AuroraCeramicMaterial", 
    "BaklavaMaterial", "BlackHoleMaterial", "BrightnessMaterial", 
    "BrunostCheeseMaterial", "CalligraphyRuneMaterial", "CarpetTextureMaterial", 
    "CeramicTilePBRMaterial", "CiniMaterial", "CircleTextureMaterial", 
    "ChromePBRMaterial", "CoffeeFjordMaterial", "ContrastMaterial", 
    "CopperMaterial", "CopperPBRMaterial", "CrystalClearMaterial", 
    "CrystalMaterial", "DamaskCeramicMaterial", "DewDropMaterial", 
    "DiagonalCheckerMaterial", "DiamondMaterial", "DielectricMaterial", 
    "DiffuseMaterial", "EdgeLightColorMaterial", "EmeraldMaterial", 
    "FjordCrystalMaterial", "FractalBarkMaterial", "FractalFireMaterial", 
    "GhostTextMaterial", "GlassicTilePBRMaterial", "GlassMaterial", 
    "GoldMaterial", "GoldPBRMaterial", "GradientChessMaterial", 
    "GradientImageTextMaterial", "GradientTextMaterial", "GraniteMaterial", 
    "HamamSaunaMaterial", "HexagonalHoneycombMaterial", "HokusaiMaterial", 
    "HologramDataMaterial", "HolographicDiffractionMaterial", "HolographicPBRMaterial", 
    "HotCopperMaterial", "HybridTextMaterial", "InvertLightColorMaterial", 
    "KilimRosemalingMaterial", "LavaFlowMaterial", "LightningMaterial", 
    "LinearGradientMaterial", "MarbleMaterial", "MarblePBRMaterial", 
    "MetallicMaterial", "MirrorMaterial", "MoonSurfaceMaterial", 
    "MosaicMaterial", "MultiMixMaterial", "NazarMaterial", 
    "NeutralMaterial", "NonScaledTransparentPNGMaterial", "NordicWeaveMaterial", 
    "NordicWoodMaterial", "NorthernLightMaterial", "NorwegianRoseMaterial", 
    "OpticalIllusionMaterial", "OrbitalMaterial", "PhongElenaMaterial", 
    "PhongMaterial", "PixelArtMaterial", "PlasticPBRMaterial", 
    "PlatinumMaterial", "PolkaDotMaterial", "ProceduralCloudMaterial", 
    "ProceduralFlowerMaterial", "PureWaterMaterial", "QuantumFieldMaterial", 
    "RadialGradientMaterial", "RandomMaterial", "ReflectiveMaterial", 
    "RosemalingMaterial", "RubyMaterial", "RuneStoneMaterial", 
    "SalmonMaterial", "SandMaterial", "SilverMaterial", 
    "SilverPBRMaterial", "SimitMaterial", "SolidCheckerboardMaterial", 
    "SolidColorMaterial", "SquaredMaterial", "StainedGlassMaterial", 
    "StarfieldMaterial", "StarryNightMaterial", "StripedMaterial", 
    "SultanKingMaterial", "TelemarkPatternMaterial", "TexturedCheckerboardMaterial", 
    "TexturedPhongMaterial", "TransparentEmissivePNGMaterial", "TransparentEmojiMaterial",
    "TriangleMaterial", "TulipFjordMaterial", "TurkishTileMaterial", 
    "TurkishDelightMaterial", "VikingMetalMaterial", "VikingRuneMaterial", 
    "WaterfallMaterial", "WaterPBRMaterial", "WoodMaterial", 
    "WoodPBRMaterial", "WoodGrainMaterial", "WordMaterial", 
    "XRayMaterial", "CustomMaterial"
   };

    public static final String[] LIGHT_TYPES = {
        "ElenaMuratAmbientLight", "MuratPointLight",
        "ElenaDirectionalLight", "SpotLight",
		"AreaLight", "SphereLight",
		"TubeLight", "BioluminescentLight", 
		"BlackHoleLight", "FractalLight",
		"PulsatingPointLight", "CustomLight"
    };
	
	public static final String WARNLIGHT = "\n\nWarning = Animation mode may not work with some materials.\nThe most compatible lights for animation are:\nthe main ambient, point and directional light classes.\nIf animation doesn't work with your selected light,\nplease don't worry and try another light or the mentioned ambient - point - directional lights.";
	public static final String SHAPE_CHANGE_TEXT = "[0 0.1 0]*[0 0 0]*[1 1 1]:[0 -0.1 0]*[0 0 0]*[1 1 1]";
	public static final String SHAPE_CHANGE_TEXT_TR = "[0 0 0]*[0 0 0]*[1 1 1]";
	public static final String VAL_SHAPE_STR = "<html><body><font color=\"#00DD00\" size=\"5\"><b>Enter anim incdec transform vals in this format-><p>[tx ty tz]*[rx ry rz]*[sx sy sz]:<p>[tx ty tz]*[rx ry rz]*[sx sy sz]</b></font></body></html>";          
    public static final String VAL_SHAPE_STR_TR = "<html><body><font color=\"#00DD00\" size=\"5\"><b>Enter transform vals in this format-><p>[tx ty tz]*[rx ry rz]*[sx sy sz]</b></font></body></html>";          
	public static final String USER_INPUT = "User input";
    
    private static final Material CHECKER = new CheckerboardMaterial(
          Color.RED, Color.WHITE, 5.0, 0.3, 0.6, 0.2, 32.0, 
          Color.RED, 0.0, 1.0, 0.0, Matrix4.identity()
    );
    
    // Public constructor
    public Utilities() {
		super();
    }
    
    public static void setPane(JComponent c) {
		pane = c;
	}
	
    public static boolean isMaterialLine(String line) {
		line = line.toLowerCase();
        return line.contains("material") && 
               !line.contains("renderer settings") && 
               !line.contains("camera");
    }
    
 public static boolean isShapeLine(String line) {
    String cleanLine = line;
  
    for (String type : SHAPE_TYPES) {
        if (cleanLine.startsWith(type)) return true;
    }
    return false;
}

	public static String getShapeName(String shapeDesc) {
    // Shape description --> "Sphere sphere_001 { ... }" -> "sphere_001"
    String[] lines = shapeDesc.split("\n");
    if (lines.length > 0) {
        String firstLine = lines[0].trim();
        String[] parts = firstLine.split("\\s+");
        if (parts.length >= 2) {
            return parts[1];
        }
    }
    return "unknown_shape";
  }
	
	public static String getShortName(String old) {
		int index = old.indexOf("{");
		if (index < 0) return old;
		
		final int oldlen = old.length();
		if (oldlen <= 96) {
			return (old.replaceAll("\n", " "));
		}
		if (oldlen > 96) {
			return (old.substring(0, 96).replaceAll("\n", " "));
		}
		
		String shpName = old.substring(0, index);
		shpName = shpName.trim();
		
		return shpName;
	}

	public static final String getNameFromMaterialShape(String oldstr) {
		int index = oldstr.indexOf("{");
		if (index < 0) return "Unknown";
		
		String str = oldstr.substring(0, index);
		str = str.trim();
		str = str.replaceAll("  ", " ");
		String[] split = str.split(" ");
		if (split == null) return "Unknown";
		if (split.length < 2) return "Unknown";
		
		return split[1].trim();
		// Material material_001 {
	}
	
  public static final String[] parseXYZTransform(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        
        // ":" split
        String[] parts = input.split(":");
        if (parts.length != 2) {
            return new String[]{"", ""};
        }
        
        String firstPart = parts[0].trim();
        String secondPart = parts[1].trim();
        
        // Remove ";"
        secondPart = secondPart.replace(";", "").trim();
        
        // First parse
        String firstTransform = formatTransform(firstPart, "first");
        // Second parse
        String secondTransform = formatTransform(secondPart, "second");
        
        return new String[]{firstTransform, secondTransform};
    }
    
    public static final String parseSingleXYZTransform(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "nullTransform";
        }
 
        // Remove ";"
        input = input.replace(";", "").trim();
        
        // First parse
        String fTransform = formatTransform(input, "Shape transform");
      
        return fTransform;
    }

    private static final String formatTransform(String transformData, String partName) {
        if (transformData.isEmpty()) {
            return "";
        }
        
        try {
            // remove "*" and "[]"
            String cleanData = transformData.replace("[", "").replace("]", "").replace("*", " ");
            
            // Split empty char
            String[] values = cleanData.trim().split("\\s+");
            
            if (values.length != 9) {
                return "// Error: " + partName + " must be 9 values.";
            }
            
            // Convert values to double
            double tx = Double.parseDouble(values[0]);
            double ty = Double.parseDouble(values[1]);
            double tz = Double.parseDouble(values[2]);
            double rx = Double.parseDouble(values[3]);
            double ry = Double.parseDouble(values[4]);
            double rz = Double.parseDouble(values[5]);
            double sx = Double.parseDouble(values[6]);
            double sy = Double.parseDouble(values[7]);
            double sz = Double.parseDouble(values[8]);
            
            // Formatted String
            return String.format(java.util.Locale.US, "transform = translate(%.3f, %.3f, %.3f) * rotate(%.3f, %.3f, %.3f) * scale(%.3f, %.3f, %.3f)",
                                tx, ty, tz, rx, ry, rz, sx, sy, sz);
            
        } catch (NumberFormatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    // ——————— TEMPLATES ———————
    public static String getLightTemplate(String type) {
        switch (type) {
            case "ElenaMuratAmbientLight": return "color = #404040;\nintensity = 0.5;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
			case "MuratPointLight": return "position = P(5.000, 5.000, 5.000);\ncolor = #FFFFFF;\nintensity = 1.0;\nconstantAttenuation = 1.0;\nlinearAttenuation = 0.0;\nquadraticAttenuation = 0.0;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
            case "ElenaDirectionalLight": return "direction = V(-1,-1,-1);\ncolor = #FFFFFF;\nintensity = 1.0;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
            case "SpotLight": return "position = P(5,5,5);\ndirection = V(-1,-1,-1);\ncolor = #FFFF00;\nintensity = 1.0;\ninnerConeAngle = 10.0;\nouterConeAngle = 20.0;\nconstantAttenuation = 1.0;\nlinearAttenuation = 0.09;\nquadraticAttenuation = 0.032;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
			case "SphereLight": return "position = P(5.000, 5.000, 5.000);\ncolor = #FFFFFF;\nintensity = 1.0;\nradius = 0.5;\nsampleCount = 8;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
			case "TubeLight": return "position = P(5.000, 5.000, 5.000);\ncolor = #FFFFFF;\nintensity = 1.0;\nstartPoint = P(4.000, 5.000, 5.000);\nendPoint = P(6.000, 5.000, 5.000);\nradius = 0.1;\nlength = 2.0;\nsampleCount = 6;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
			case "AreaLight": return "position = P(5.000, 5.000, 5.000);\ncolor = #FFFFFF;\nintensity = 1.0;\nnormal = V(0.000, -1.000, 0.000);\nwidth = 2.0;\nheight = 1.5;\nsamplesU = 4;\nsamplesV = 3;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;";
			case "BioluminescentLight": return "positions = [P(0,1,0)];\ncolor = #00FF00;\npulseSpeed = 0.5;\nbaseIntensity = 1.0;\nattenuationFactor = 0.1;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
			case "BlackHoleLight": return "singularity = P(0,0,0);\nradius = 1.0;\ncolor = #000000;\nintensity = 1.0;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
			case "PulsatingPointLight": return "initialPosition = P(5,5,5);\nbaseColor = #FF0000;\nbaseIntensity = 1.0;\npulsationSpeed = 0.5;\nmovementSpeed = 0.1;\nmovementAmplitude = 1.0;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
            case "FractalLight": return "position = P(5,5,5);\ncolor = #FF00FF;\nintensity = 1.0;\noctaves = 4;\npersistence = 0.5;\nfrequency = 2.0;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
            case "CustomLight": return "className = extraSML/TestLight.class;\nfirstAnimationIntensity = 0.0;\nsecondAnimationIntensity = 0.0;" + WARNLIGHT;
            default: return "";
        }
    }

    public static String getShapeTemplate(String type) {
        switch (type) {
            case "Box": return "width = 2.0;\nheight = 2.0;\ndepth = 2.0;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Cone": return "radius = 1.0;\nheight = 2.0;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Crescent": return "radius = 1.0;\ncutRadius = 0.5;\ncutDistance = 0.2;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Cube": return "sideLength = 2.0;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Cylinder": return "radius = 1.0;\nheight = 2.0;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Ellipsoid": return "center = P(0,0,0);\na = 1.0;\nb = 0.8;\nc = 0.6;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "EmojiBillboard": return "width = 20.0;\nheight = 10.0;\nisRectangle = false;\nisVisible = false;\nimagePath = \"textures/turkeyFlag.png\";\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Hyperboloid": return "a = 1.0;\nb = 1.0;\nc = 1.0;\nheight = 2.0;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Plane": return "pointOnPlane = P(0,0,0);\nnormal = V(0,1,0);\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "TransparentPlane": return "pointOnPlane = P(0,0,0);\nnormal = V(0,0,1);\nthickness = 0.1;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Rectangle3D": return "p1 = P(-1,-1,0);\np2 = P(1,1,0);\nthickness = 0.1;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "RectangularPrism": return "width = 2.0;\nheight = 1.0;\ndepth = 0.5;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Sphere": return "radius = 1.3;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Torus": return "majorRadius = 2.0;\nminorRadius = 0.5;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "TorusKnot": return "R = 2.0;\nr = 0.5;\np = 2;\nq = 3;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "Triangle": return "v0 = P(-1,0,0);\nv1 = P(1,0,0);\nv2 = P(0,1,0);\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            //case "Image3D": return "imagePath = \"textures/turkeyFlag.png\";\nbaseSize = 256;\nwidthScale = 1.0;\nheightScale = 1.0;\nthickness = 0.1;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            case "UnionCSG": return "firstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\n ";
            case "IntersectionCSG": return "firstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\n ";
            case "DifferenceCSG": return "firstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\n ";
            case "CustomShape": return "className = extraSML/TestShape.class;\nfirstAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);\nsecondAnim_transform = translate(0, 0, 0) * rotate(0, 0, 0) * scale(1, 1, 1);";
            default: return "";
        }
    }

    public static String getMaterialTemplate(String type) {
        switch (type) {
            case "MultiMixMaterial": return "materials = [mat1, mat2];\nratios = [0.7, 0.3];";
            case "AfricanKenteMaterial": return "";
            case "AmberMaterial": return "";
            case "AnisotropicMetalMaterial": return "metalColor = #B3B3BF;\nanisotropy = 0.8;\nroughnessX = 0.1;\nroughnessY = 0.4;";
            case "AnodizedMetalMaterial": return "baseColor = #4A90E2;";
			case "AnodizedTextMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nword = \"METAL\";\ntextColor = #FFFFFF;\ngradientColor = #00000000;\ngradientType = \"horizontal\";\nbgColor = #00000000;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 48;\nuOffset = 0;\nvOffset = -50;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = -50;\nbaseColor = #4A90E2;";
            case "AuroraCeramicMaterial": return "baseColor = #FFFFFF;\nauroraColor = #00FF00;\nauroraIntensity = 1.0;";
            case "BaklavaMaterial": return "pastryColor = #FFDF00;\nsyrupColor = #D2B48C;\nnutColor = #8B4513;\nlayers = 12.0;\nsyrupiness = 0.8;";
			case "BlackHoleMaterial": return "singularity = P(0,0,0);";
            case "BrightnessMaterial": return "baseColor = #808080;\nbrightness = 1.0;\nuseLightColor = false;";
            case "BrunostCheeseMaterial": return "cheeseColor = #D2691E;\ncaramelColor = #FFA500;\ncaramelAmount = 0.5;";
            case "CalligraphyRuneMaterial": return "parchmentColor = #F5DEB3;\ninkColor = #000000;\ngoldLeafColor = #FFD700;\nwritingIntensity = 1.0;";
            case "CarpetTextureMaterial": return "baseColor = #8B4513;\npatternColor = #FFD700;";
			case "CheckerboardMaterial": return "color1 = #FFFFFF;\ncolor2 = #FF0000;\nsize = 1.0;\nambientCoeff = 0.4;\ndiffuseCoeff = 0.7;\nspecularCoeff = 0.2;\nshininess = 32.0;\nspecularColor = #FFFFFF;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
			case "CiniMaterial": return "baseColor = #FFFFFF;\npatternColor = #0090EE;\nglossiness = 0.8;";
			case "CircleTextureMaterial": return "solidColor = #FF0000;\npatternColor = #00FF00;\npatternSize = 0.5;";
            case "ContrastMaterial": return "baseColor = #808080;\ncontrast = 1.0;\nuseLightColor = false;";
            case "CopperMaterial": return "";
            case "CrystalClearMaterial": return "glassTint = #00FFFF;\nclarity = 1.0;\nior = 1.5;\ndispersion = 0.044;";
            case "CrystalMaterial": return "baseColor = #0000FF;\ncrystalColor = #00FFFF;";
            case "DamaskCeramicMaterial": return "primary = #FF6347;\nsecondary = #4682B4;\nshininess = 50.0;\nambient = 0.2;\nspecular = 0.8;";
			case "DewDropMaterial": return "baseColor = #87CEEB;\ndropColor = #FFFFFF;\ndropDensity = 0.5;\ndropSize = 0.1;\nambient = 0.3;\ndiffuse = 0.7;\nspecular = 0.9;\nshininess = 100.0;\nreflectivity = 0.8;\nior = 1.33;\ntransparency = 0.3;";
			case "DiagonalCheckerMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nscale = 1.0;\nambient = 0.2;\ndiffuse = 0.7;\nspecular = 0.5;\nshininess = 50.0;\nspecularColor = #FFFFFF;\nreflectivity = 0.3;\nior = 1.0;\ntransparency = 0.0;";
            case "DielectricMaterial": return "diffuseColor = #FFFFFF;\nior = 1.5;\ntransparency = 0.9;\nreflectivity = 0.1;";
            case "DiffuseMaterial": return "color = #FF0000;";
            //case "ElenaTextureMaterial": return "imagePath = \"textures/turkeyFlag.png\";";
            case "EmissiveMaterial": return "emissiveColor = #00FF00;\nemissiveStrength = 2.0;";
            case "FractalBarkMaterial": return "roughness = 0.5;";
            case "FractalFireMaterial": return "iterations = 5;\nchaos = 0.5;\nscale = 1.0;\nspeed = 0.1;";
            case "FjordCrystalMaterial": return "waterColor = #00CED1;\ncrystalColor = #40E0D0;\nclarity = 0.8;";
			case "GhostTextMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nword = \"GHOST\";\ntextColor = #FF0000;\ngradientColor = #00DD00;\ngradientType = \"horizontal\";\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 48;\nuOffset = 90;\nvOffset = 0;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 270;\ntransparency = 0.95;\nreflectivity = 0.4;\nior = 1.52;";            
            case "GoldMaterial": return "";
			case "GradientChessMaterial": return "baseColor1 = #FF0000;\nbaseColor2 = #0000FF;\nsquareSize = 1.0;\nambient = 0.2;\ndiffuse = 0.7;\nspecular = 0.5;\nshininess = 50.0;\nreflectivity = 0.3;\nior = 1.0;\ntransparency = 0.0;";			
			case "GradientImageTextMaterial": return "bgStart = #0000FF;\nbgEnd = #00FFFF;\ntextStart = #FF0000;\ntextEnd = #FFFF00;\nimagePath = \"textures/turkeyFlag.png\";\nbgAlpha = 1.0;\ntextAlpha = 1.0;\ntext = \"TEXT\";\nfont = Serif-1-100;\ndirection = HORIZONTAL;\nreflectivity = 0.2;\nior = 1.0;\ntransparency = 0.0;\nxOffset = -230;\nyOffset = 150;\nimgOffsetX = 100;\nimgOffsetY = 250;\nisWrap = false;";
			case "GradientTextMaterial": return "bgStart = #000000;\nbgEnd = #FFFFFF;\ntextStart = #FF0000;\ntextEnd = #0000FF;\ntext = \"TEXT\";\nfontName = Serif;\nfontStyle = 1;\nfontSize = 60;\ndirection = HORIZONTAL;\nreflectivity = 0.2;\nior = 1.0;\ntransparency = 0.0;\nxOffset = -130;\nyOffset = 10;";            
            case "GraniteMaterial": return "baseColor = #808080;\nroughness = 0.8;\nspecular = 0.1;\nreflectivity = 0.05;";
			case "HexagonalHoneycombMaterial": return "primary = #FFD700;\nsecondary = #8B4513;\nborderColor = #000000;\ncellSize = 1.0;\nborderWidth = 0.1;\nambientStrength = 0.3;\nspecularStrength = 0.5;\nshininess = 30.0;";            
            case "HamamSaunaMaterial": return "marbleColor = #F0F0F0;\nwoodColor = #8B4513;\nsteamColor = #B0E0E6;\nsteamIntensity = 0.5;";
            case "HokusaiMaterial": return "waterColor = #1A3380;\nfoamColor = #FFFFFF;\nscale = 4.0;\nmode = 0;\nradius = 1.0;";
            case "HologramDataMaterial": return "dataDensity = 0.5;\nresolution = 256;";
            case "HolographicDiffractionMaterial": return "reflectivity = 0.8;";
            case "HotCopperMaterial": return "copperColor = #B87333;\npatinaColor = #4A7B4A;\npatinaAmount = 0.3;";
			case "HybridTextMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nword = \"HYBRID\";\ntextColor = #FF0000;\ngradientColor = #00DD00;\ngradientType = \"horizontal\";\nbgColor = #100000bb;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 48;\nuOffset = 0;\nvOffset = 0;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 0;\ndiffuseColor = #FFFFFF;\nindexOfRefraction = 1.5;\ntransparency = 0.0;\nreflectivity = 0.0;\nfilterColorInside = #0000CC;\nfilterColorOutside = #0000CC;\nspecularColor = #FF0000;\nshininess = 32.0;\nambientCoefficient = 0.4;\ndiffuseCoefficient = 0.75;\nspecularCoefficient = 0.2;";			
			case "ImageTextureMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nuScale = 1.0;\nvScale = 1.0;\nuOffset = 0.0;\nvOffset = 0.0;\nambientCoefficient = 0.5;\ndiffuseCoefficient = 0.7;\nspecularCoefficient = 0.2;\nshininess = 32.0;\nreflectivity = 0.0;\nior = 1.0;";
			case "InvertLightColorMaterial": return "";
            case "KilimRosemalingMaterial": return "kilimColor = #FF6347;\nrosemalingColor = #4682B4;\naccentColor = #FFD700;\npatternIntensity = 1.0;";
            case "LambertMaterial": return "color = #FF0000;\nambient = 0.3;\ndiffuse = 1.0;";
            case "LavaFlowMaterial": return "hotColor = #FF4500;\ncoolColor = #FF8C00;\nflowSpeed = 0.1;";
            case "LightningMaterial": return "baseColor = #FFFF00;\nintensity = 2.0;";
			case "MarbleMaterial": return "baseColor = #F0F0F0;\nveinColor = #8B8B8B;\nscale = 1.0;\nveinDensity = 0.5;\nturbulence = 0.1;\nambient = 0.3;\ndiffuse = 0.7;\nspecular = 0.4;\nshininess = 60.0;\nreflectivity = 0.2;\nior = 1.0;\ntransparency = 0.0;";
            case "MetallicMaterial": return "metallicColor = #C0C0C0;\nspecularColor = #FFFFFF;\nreflectivity = 0.8;\nshininess = 50.0;\nambient = 0.3;\ndiffuse = 0.7;\nspecular = 0.5;";
            case "MosaicMaterial": return "baseColor = #FFFFFF;\ntileColor = #000000;\ntileSize = 0.5;\ngroutWidth = 0.1;\nrandomness = 0.1;";
            case "NazarMaterial": return "blueColor = #1E90FF;\nwhiteColor = #FFFFFF;\nglowIntensity = 0.3;\ntransparency = 0.25;\nreflectivity = 0.4;";
            case "NeutralMaterial": return "baseColor = #808080;\nreflectivity = 0.5;\ntransparency = 0.0;\nindexOfRefraction = 1.0;";
            case "NordicWoodMaterial": return "woodColor = #DEB887;\ngrainColor = #8B4513;\ngrainIntensity = 0.5;";
            case "NordicWeaveMaterial": return "primaryColor = #FF6347;\nsecondaryColor = #4682B4;\naccentColor = #FFD700;\npatternScale = 1.0;";
            case "NorthernLightMaterial": return "primaryAurora = #00FF00;\nsecondaryAurora = #0000FF;\nintensity = 1.0;";
			case "OpticalIllusionMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nfrequency = 10.0;\nsmoothness = 0.5;\nambient = 0.2;\ndiffuse = 0.6;\nspecular = 0.4;\nshininess = 40.0;\nreflectivity = 0.3;\nior = 1.0;\ntransparency = 0.0;";			
			case "CeramicTilePBRMaterial": return "tileColor = #FF6347;\ngroutColor = #000000;\ntileSize = 1.0;\ngroutWidth = 0.1;\ntileRoughness = 0.3;\ngroutRoughness = 0.8;\ntileSpecular = 0.5;\ngroutSpecular = 0.2;\nfresnelIntensity = 1.0;\nnormalMicroFacet = 0.1;\nreflectionSharpness = 0.8;\nenergyConservation = 0.95;";
            case "ChromePBRMaterial": return "baseReflectance = #C0C0C0;\nroughness = 0.1;\nanisotropy = 0.5;\nclearCoat = 0.2;\nedgeTint = #FFFFFF;";
            case "CoffeeFjordMaterial": return "coffeeColor = #6F4E37;\nfjordColor = #00CED1;\nblendIntensity = 0.5;";
            case "CopperPBRMaterial": return "baseColor = #B87333;\nroughness = 0.3;\noxidation = 0.2;";
            case "DiamondMaterial": return "baseColor = #B9F2FF;\nior = 2.42;\nreflectivity = 0.9;\ntransparency = 0.95;\ndispersionStrength = 0.044;\nfireEffect = 1.0;";
            case "EdgeLightColorMaterial": return "baseColor = #000000;\nedgeColor = #FFFFFF;\nedgeThreshold = 0.1;";
            case "EmeraldMaterial": return "baseColor = #50C878;\ndensity = 0.8;\nreflectivity = 0.7;";
            case "GlassicTilePBRMaterial": return "tileColor = #00CED1;\ngroutColor = #000000;\ntileSize = 1.0;\ngroutWidth = 0.1;\ntileRoughness = 0.1;\ngroutRoughness = 0.8;";
            case "GlassMaterial": return "baseColor = #00FFFF;\nior = 1.5;\nreflectivity = 0.1;\ntransparency = 0.9;";
            case "GoldPBRMaterial": return "albedo = #FFD700;\nroughness = 0.2;\nmetalness = 1.0;";
            case "HolographicPBRMaterial": return "baseColor = #0000FF;\nrainbowSpeed = 0.1;\nscanLineDensity = 10.0;\nglitchIntensity = 0.1;\ntimeOffset = 0.0;\ndistortionFactor = 0.1;\ndataDensity = 0.5;";
            case "IsotropicMetalTextMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nmetalColor = #B3B3BF;\nroughness = 0.2;\nreflectivity = 0.7;\nior = 1.0;\ntransparency = 0.0;\ntext = \"MERHABA\";\ntextColor = #FF0000;\ngradientColor = #00DD00;\ngradientType = \"horizontal\";\nbgColor = #00000000;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 52;\nuOffset = 0;\nvOffset = -130;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 0;";
            case "LinearGradientMaterial": return "topColor = #FF0000;\nbottomColor = #0000FF;";
            case "MarblePBRMaterial": return "baseColor = #F0F0F0;\nveinColor = #8B8B8B;\nveinScale = 1.0;\nveinContrast = 0.5;\nroughness = 0.3;\nreflectivity = 0.5;\nveinIntensity = 1.0;";
            case "MirrorMaterial": return "tintColor = #FFFFFF;\nreflectivity = 0.95;\nsharpness = 0.98;";
			case "MoonSurfaceMaterial": return "";
			case "NonScaledTransparentPNGMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nbillboardWidth = 20.0;\nbillboardHeight = 10.0;\nshadowAlphaThreshold = 0.1;\ngammaCorrection = 2.2;";
            case "NorwegianRoseMaterial": return "woodColor = #DEB887;\nroseColor = #FF6347;";
            case "OrbitalMaterial": return "centerColor = #FF0000;\norbitColor = #0000FF;\nringWidth = 0.1;\nringCount = 3;";
            case "PhongElenaMaterial": return "diffuseColor = #FF0000;\nreflectivity = 0.5;\nshininess = 50.0;\nambientCoefficient = 0.1;";
            case "PhongMaterial": return "diffuseColor = #FF0000;\nspecularColor = #FFFFFF;\nshininess = 50.0;\nambientCoefficient = 0.1;\ndiffuseCoefficient = 0.7;\nspecularCoefficient = 0.8;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
            case "PhongTextMaterial": return "word = \"PHONG\";\ntextColor = #FFFFFF;\ndiffuseColor = #FFFFFF;\nspecularColor = #FFFFFF;\nshininess = 50.0;";
			case "PixelArtMaterial": return "palette = [#000000, #FFFFFF, #FF0000, #00FF00, #0000FF];\npixelSize = 0.05;\nambient = 0.1;\ndiffuse = 0.7;\nspecular = 0.2;\nshininess = 10.0;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";            
            case "PlasticPBRMaterial": return "albedo = #FF0000;\nroughness = 0.5;\nreflectivity = 0.2;\nior = 1.4;\ntransparency = 0.0;";
            case "PlatinumMaterial": return "specularBalance = 0.5;";
            case "PolkaDotMaterial": return "baseColor = #FFFFFF;\ndotColor = #FF0000;\ndotSize = 0.1;\ndotSpacing = 0.2;";
            case "ProceduralCloudMaterial": return "baseColor = #87CEEB;\nhighlightColor = #FFFFFF;";
            case "ProceduralFlowerMaterial": return "petalCount = 5.0;\npetalColor = #FF6347;\ncenterColor = #FFD700;\nambientStrength = 0.5;";
            case "PureWaterMaterial": return "baseColor = #00CED1;\nflowSpeed = 0.1;";
            case "QuantumFieldMaterial": return "primary = #FF00FF;\nsecondary = #00FFFF;\nenergy = 1.0;";
            case "RadialGradientMaterial": return "centerColor = #FF0000;\nedgeColor = #0000FF;";
            case "RandomMaterial": return "";
            //case "RectangleCheckerMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nrectWidth = 1.0;\nrectHeight = 0.5;";
            case "ReflectiveMaterial": return "baseColor = #C0C0C0;\nreflectivity = 0.8;\nroughness = 0.1;";
            case "RosemalingMaterial": return "backgroundColor = #F0F0F0;\nflowerColor = #FF6347;\naccentColor = #FFD700;\npatternDensity = 1.0;";
            case "RoughMaterial": return "color = #808080;\nroughness = 0.8;\ndiffuseCoefficient = 0.7;\nreflectivity = 0.1;";
            case "RubyMaterial": return "baseColor = #E0115F;\ndensity = 0.8;\nreflectivity = 0.7;";
            case "RuneStoneMaterial": return "stoneColor = #8B8B8B;\nruneColor = #FFD700;\nruneDensity = 0.5;";
            case "SalmonMaterial": return "fleshColor = #FF91A4;\nskinColor = #C86450;\nfatColor = #FFDCB4;\nfreshness = 0.9;\noiliness = 0.7;";
			case "SandMaterial": return "baseSandColor = #C2B380;\ndarkSandColor = #A69461;\ngrainSize = 0.05;\nroughness = 0.8;";
            case "SilverMaterial": return "";
            case "SilverPBRMaterial": return "albedo = #F8F5E9;\nroughness = 0.15;\nmetalness = 1.0;";
            case "SimitMaterial": return "crustColor = #D2B48C;\nsesameColor = #F5DEB3;\nsoftColor = #FFF8DC;\ncrispiness = 0.8;\nsesameDensity = 0.7;";
			case "SmartGlassMaterial": return "color = #00CED1;\nclarity = 0.8;";
            case "SolidCheckerboardMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nsize = 1.0;\nambient = 0.1;\ndiffuse = 0.7;";
            case "SolidColorMaterial": return "color = #FF0000;";			
			case "SphereWordTextureMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nword = \"VANN\";\ntextColor = #FF0000;\ngradientColor = #00DD00;\ngradientType = \"horizontal\";\nbgColor = #00000000;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 100;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;\nuOffset = 0;\nvOffset = 0;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 0;";
			case "SquaredMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nscale = 1.0;\nambient = 0.1;\ndiffuse = 0.7;\nspecular = 0.2;\nshininess = 32.0;\nspecularColor = #FFFFFF;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
            case "StainedGlassMaterial": return "tint = #FF0000;\nroughness = 0.1;";
            case "StarfieldMaterial": return "nebulaColor = #000080;\nstarSize = 0.1;\nstarDensity = 1000.0;\ntwinkleSpeed = 0.1;";
			case "StarryNightMaterial": return "";
			case "StripedMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\nstripeSize = 5.0;\ndirection = HORIZONTAL;\nambientCoefficient = 0.1;\ndiffuseCoefficient = 0.8;\nspecularCoefficient = 0.1;\nshininess = 10.0;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
            case "SultanKingMaterial": return "goldColor = #FFD700;\nrubyColor = #E0115F;\nsapphireColor = #0F52BA;\nroyaltyIntensity = 1.0;";
            case "TelemarkPatternMaterial": return "baseColor = #FF6347;\npatternColor = #4682B4;\naccentColor = #FFD700;\npatternScale = 1.0;";
			case "TextDielectricMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nword = \"TEXT\";\ntextColor = #FF0000;\ngradientColor = #00DD00;\ngradientType = \"horizontal\";\nbgColor = #000000;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 50;\nuOffset = 0;\nvOffset = 0;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 0;\ndiffuseColor = #FF0000;\nior = 1.52;\ntransparency = 0.85;\nreflectivity = 0.15;\nfilterColorInside = #00BB00;\nfilterColorOutside = #00BB00;";
			case "TexturedCheckerboardMaterial": return "imagePath = \"textures/turkeyFlag.png\";\ncolor1 = #FF0000;\ncolor2 = #0000FF;\nsize = 1.0;\ntext = \"CHECKER\";\ntextColor = #FFFFFF;\ngradientColor = #000000;\ngradientType = \"HORIZONTAL\";\nbgColor = #00000000;\nfontFamily = \"Arial\";\nfontStyle = 1;\nfontSize = 72;\ntextUOffset = 150;\ntextVOffset = 0;\nimageWidth = 256;\nimageHeight = 256;\nimageUOffset = 0;\nimageVOffset = 0;\nambientCoeff = 0.4;\ndiffuseCoeff = 0.7;\nspecularCoeff = 0.2;\nshininess = 32.0;\nspecularColor = #FFFFFF;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
			case "TexturedPhongMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nbaseDiffuseColor = #FFFFFF;\nspecularColor = #FFFFFF;\nshininess = 32.0;\nambientCoefficient = 0.1;\ndiffuseCoefficient = 0.7;\nspecularCoefficient = 0.2;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;\nuOffset = 0.0;\nvOffset = 0.0;\nuScale = 1.0;\nvScale = 1.0;";
            case "TextureMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nisTile = true";
            case "ThresholdMaterial": return "baseColor = #808080;\nthreshold = 0.5;\naboveColor = #FFFFFF;\nbelowColor = #000000;\nuseLightColor = false;\ninvertThreshold = false;";
            //case "TransparentColorMaterial": return "transparency = 0.5;\nreflectivity = 0.0;\nior = 1.5;";
            case "TransparentEmojiMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nbillboardWidth = 20.0;\nbillboardHeight = 10.0;\ncheckerColor1 = #FF0000;\ncheckerColor2 = #FFFF00;\ncheckerSize = 0.1;\nuOffset = 0.0;\nvOffset = 0.0;\nuScale = 1.0;\nvScale = 1.0;\nisRepeatTexture = false;\nisMessy = false;";
            case "TransparentPNGMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nbillboardWidth = 20.0;\nbillboardHeight = 10.0;\nuOffset = 0.0;\nvOffset = 0.0;\nuScale = 1.0;\nvScale = 1.0;\nisRepeatTexture = false;";
			case "TransparentEmissivePNGMaterial": return "imagePath = \"textures/turkeyFlag.png\";\nbillboardWidth = 20.0;\nbillboardHeight = 10.0;\nuOffset = 0.0;\nvOffset = 0.0;\nuScale = 1.0;\nvScale = 1.0;\nisRepeatTexture = false;\nemissiveColor = #00FF00;\nemissiveStrength = 1.0;";
			case "TriangleMaterial": return "color1 = #FF0000;\ncolor2 = #0000FF;\ntriangleSize = 1.0;\nambientCoefficient = 0.1;\ndiffuseCoefficient = 0.7;\nspecularCoefficient = 0.2;\nshininess = 32.0;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;";
            case "TulipFjordMaterial": return "tulipColor = #FF6347;\nfjordColor = #00CED1;\nstemColor = #228B22;\nbloomIntensity = 1.0;";
            case "TurkishDelightMaterial": return "primaryColor = #FFB6C1;\npowderColor = #FFFAFA;\nsoftness = 0.8;\nsweetness = 0.3;\ntransparency = 0.2;";
			case "TurkishTileMaterial": return "baseColor = #FF6347;\npatternColor = #4682B4;\ntileSize = 1.0;";
            case "VikingMetalMaterial": return "baseColor = #C0C0C0;\nrustColor = #8B4513;\nrustDensity = 0.3;";
            case "VikingRuneMaterial": return "stoneColor = #8B8B8B;\nruneColor = #FFD700;\nruneDepth = 0.1;";
            case "WaterfallMaterial": return "baseColor = #00CED1;\nflowSpeed = 0.1;";
            case "WaterPBRMaterial": return "waterColor = #00CED1;\nroughness = 0.1;\nwaveIntensity = 0.5;\nmurkiness = 0.2;\nfoamThreshold = 0.8;";
            //case "WaterRippleMaterial": return "waterColor = #00CED1;\nwaveSpeed = 0.1;\nreflectivity = 0.5;";
			case "WoodGrainMaterial": return "";
			case "WoodMaterial": return "baseColor = #DEB887;\ngrainColor = #8B4513;\ngrainFrequency = 2.0;\nringVariation = 0.1;\nambientCoeff = 0.1;\ndiffuseCoeff = 0.7;\nspecularCoeff = 0.2;\nshininess = 32.0;\nreflectivity = 0.0;\nior = 1.0;\ntransparency = 0.0;\nobjectInverseTransform = null;";            
            case "WoodPBRMaterial": return "color1 = #DEB887;\ncolor2 = #8B4513;\ntileSize = 1.0;\nroughness = 0.5;\nspecularScale = 0.5;";
			case "WordMaterial": return "imagePath = \"textures/turkeyFlag.png\";\ntext = \"VANN\";\nforegroundColor = #FFFFFF;\nbackgroundColor = #00000000;\nfontName = \"Arial\";\nfontSize = 48;\nfontStyle = 1;\nuseGradient = true;\ngradientColor = #00000000;\nwidth = 300;\nheight = 80;";            
            case "XRayMaterial": return "baseColor = #00FFFF;\ntransparency = 0.8;\nreflectivity = 0.2;";
            case "CustomMaterial": return "className = extraSML/TestMaterial.class;";
            default: return "";
        }
    }

    // ——————— FACTORIES ———————

    public static EMShape createShapeFromText(String type, String text) {
        try {
            switch (type) {
                case "Box": {
                    double w = parseDouble(text, "width");
                    double h = parseDouble(text, "height");
                    double d = parseDouble(text, "depth");
                    EMShape shp = new net.elena.murat.shape.Box(w, h, d);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Cone": {
                    double radius = parseDouble(text, "radius");
                    double height = parseDouble(text, "height");
                    EMShape shp = new Cone(radius, height);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Crescent": {
                    double r = parseDouble(text, "radius");
                    double cutR = parseDouble(text, "cutRadius");
                    double cutD = parseDouble(text, "cutDistance");
                    EMShape shp = new Crescent(r, cutR, cutD);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Cube":  {
                    if (text.contains("sideLength")) {
                        double side = parseDouble(text, "sideLength");
                        EMShape shp = new Cube(side);
                        Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                        Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                        shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
						return shp;
                    } else {
                        Point3 min = parsePoint3(text, "min");
                        Point3 max = parsePoint3(text, "max");
                        EMShape shp = new Cube(min, max);
                        Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                        Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                        shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                        return shp;
                    }
                } case "Cylinder": {
                    if (text.contains("startPoint")) {
                        Point3 start = parsePoint3(text, "startPoint");
                        Point3 end = parsePoint3(text, "endPoint");
                        double rad = parseDouble(text, "radius");
                        double ht = parseDouble(text, "height");
                        EMShape shp = new Cylinder(start, end, rad, ht);
                        Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                        Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                        shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
						return shp;
                    } else {
                        double rad = parseDouble(text, "radius");
                        double ht = parseDouble(text, "height");
                        EMShape shp = new Cylinder(rad, ht);
                        Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                        Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                        shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                        return shp;
                    }
                } case "Ellipsoid": {
                    Point3 center = parsePoint3(text, "center");
                    double a = parseDouble(text, "a");
                    double b = parseDouble(text, "b");
                    double c = parseDouble(text, "c");
                    EMShape shp = new Ellipsoid(center, a, b, c);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "EmojiBillboard": {
                    double width = parseDouble(text, "width");
                    double height = parseDouble(text, "height");
                    boolean isRect = parseBoolean(text, "isRectangle");
                    boolean isVisible = parseBoolean(text, "isVisible");
                    String path = parseString(text, "imagePath");
                    BufferedImage img = loadImage(path);
                    EmojiBillboard ebil = new EmojiBillboard(width, height, isRect, isVisible, img);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
					ebil.setImagePath(path);
					ebil.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
					return ebil;
                } case "Hyperboloid": {
                    if (text.trim().isEmpty()) {
                        EMShape shp = new Hyperboloid();
                        shp.setAnimationTransforms(new Matrix4[]{new Matrix4(), new Matrix4()});
                        return shp;
                    } else {
                        double a = parseDouble(text, "a");
                        double b = parseDouble(text, "b");
                        double c = parseDouble(text, "c");
                        double height = parseDouble(text, "height");
                        EMShape shp = new Hyperboloid(a, b, c, height);
                        Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                        Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                        shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                        return shp;
                    }
                } case "Plane": {
                    Point3 point = parsePoint3(text, "pointOnPlane");
                    Vector3 normal = parseVector3(text, "normal");
                    EMShape shp = new Plane(point, normal);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "TransparentPlane": {
                    Point3 p = parsePoint3(text, "pointOnPlane");
                    Vector3 n = parseVector3(text, "normal");
                    double thickness = parseDouble(text, "thickness");
                    EMShape shp = new TransparentPlane(p, n, thickness);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Rectangle3D": {
                    Point3 p1 = parsePoint3(text, "p1");
                    Point3 p2 = parsePoint3(text, "p2");
                    float thickness = (float) parseDouble(text, "thickness");
                    EMShape shp = new Rectangle3D(p1, p2, thickness);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "RectangularPrism": {
                    double ww = parseDouble(text, "width");
                    double hh = parseDouble(text, "height");
                    double dd = parseDouble(text, "depth");
                    EMShape shp = new RectangularPrism(ww, hh, dd);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Sphere": {
                    double r = parseDouble(text, "radius");
                    EMShape shp = new Sphere(r);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Torus": {
                    double major = parseDouble(text, "majorRadius");
                    double minor = parseDouble(text, "minorRadius");
                    EMShape shp = new Torus(major, minor);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "TorusKnot": {
                    double R = parseDouble(text, "R");
                    double r = parseDouble(text, "r");
                    int p = parseInt(text, "p");
                    int q = parseInt(text, "q");
                    EMShape shp = new TorusKnot(R, r, p, q);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "Triangle": {
                    Point3 v0 = parsePoint3(text, "v0");
                    Point3 v1 = parsePoint3(text, "v1");
                    Point3 v2 = parsePoint3(text, "v2");
                    EMShape shp = new Triangle(v0, v1, v2);
                    Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform"); 
                    shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
                } case "CustomShape": {
					Matrix4 xanimTransform = parseAnimationTransform(text, "firstAnim_transform");
                    Matrix4 yanimTransform = parseAnimationTransform(text, "secondAnim_transform");
					String path = parseString(text, "className");
					EMShape shp = Utilities.loadCustomShape(path);
					shp.setAnimationTransforms(new Matrix4[]{xanimTransform, yanimTransform});
                    return shp;
				}
                default:
                    return new Sphere(1.0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(pane, "Shape parse error: " + e.getMessage());
            return null;
        }
    }

///////////
/**
 * Universal MultiMixMaterial parser for CSG - PROPERLY PARSES USER INPUT
 */
private static Material parseMultiMixMaterialForCSG(String materialType, String materialBlock) {
    try {
        System.out.println("=== UNIVERSAL MULTIMIX PARSER ===");
        
        // Find the materials array content
        int materialsStart = materialBlock.indexOf('[');
        int materialsEnd = materialBlock.indexOf(']', materialsStart);
        if (materialsStart == -1 || materialsEnd == -1) {
            throw new IllegalArgumentException("Invalid materials array format");
        }
        
        String materialsContent = materialBlock.substring(materialsStart + 1, materialsEnd).trim();
        
        // Find the ratios array content  
        int ratiosStart = materialBlock.indexOf('[', materialsEnd + 1);
        int ratiosEnd = materialBlock.indexOf(']', ratiosStart);
        if (ratiosStart == -1 || ratiosEnd == -1) {
            throw new IllegalArgumentException("Invalid ratios array format");
        }
        
        String ratiosContent = materialBlock.substring(ratiosStart + 1, ratiosEnd).trim();
        
        System.out.println("Materials content: " + materialsContent);
        System.out.println("Ratios content: " + ratiosContent);
        
        // Parse individual materials
        java.util.List<Material> materials = new ArrayList<>();
        String remainingMaterials = materialsContent;
        
        while (!remainingMaterials.trim().isEmpty()) {
            // Find next material type
            int materialTypeStart = -1;
            int materialTypeEnd = -1;
            
            for (int i = 0; i < remainingMaterials.length(); i++) {
                if (Character.isUpperCase(remainingMaterials.charAt(i))) {
                    materialTypeStart = i;
                    // Find the end of material type (until space or {)
                    for (int j = i + 1; j < remainingMaterials.length(); j++) {
                        char c = remainingMaterials.charAt(j);
                        if (c == ' ' || c == '{') {
                            materialTypeEnd = j;
                            break;
                        }
                    }
                    break;
                }
            }
            
            if (materialTypeStart == -1) break;
            
            String currentMaterialType = remainingMaterials.substring(materialTypeStart, materialTypeEnd).trim();
            System.out.println("Found material type: " + currentMaterialType);
            
            // Find the material block
            int blockStart = remainingMaterials.indexOf('{', materialTypeEnd);
            if (blockStart == -1) break;
            
            int blockEnd = findMatchingBracket(remainingMaterials, blockStart);
            if (blockEnd == -1) break;
            
            // Extract the complete material definition
            String materialDefinition = remainingMaterials.substring(materialTypeStart, blockEnd + 1).trim();
            System.out.println("Material definition: " + materialDefinition.substring(0, Math.min(100, materialDefinition.length())));
            
			// Parse this individual material
			Material material = null;
			if ("MultiMixMaterial".equals(currentMaterialType)) {
				// New recursive method
				material = parseMultiMixMaterialWithoutDialog(materialDefinition);
			} else {
				material = createMaterialFromText(currentMaterialType, materialDefinition);
			}
            
            if (material != null) {
                materials.add(material);
                System.out.println("Successfully parsed: " + currentMaterialType);
            } else {
                System.err.println("Failed to parse: " + currentMaterialType);
                materials.add(new DiffuseMaterial(Color.GRAY));
            }
            
            // Move to next material
            remainingMaterials = remainingMaterials.substring(blockEnd + 1).trim();
            // Skip comma if present
            if (remainingMaterials.startsWith(",")) {
                remainingMaterials = remainingMaterials.substring(1).trim();
            }
        }
        
        // Parse ratios
        String[] ratioStrings = ratiosContent.split(",");
        double[] ratios = new double[ratioStrings.length];
        
        for (int i = 0; i < ratioStrings.length; i++) {
            ratios[i] = Double.parseDouble(ratioStrings[i].trim());
        }
        
        // Validate counts
        if (materials.size() != ratios.length) {
            System.err.println("Warning: Materials count (" + materials.size() + ") doesn't match ratios count (" + ratios.length + ")");
            System.out.println("Returning Checkerboard Material...");
            return CHECKER;
            // Adjust to smaller count
            //int minCount = Math.min(materials.size(), ratios.length);
            //Material[] finalMaterials = new Material[minCount];
            //double[] finalRatios = new double[minCount];
            
            //System.arraycopy(materials.toArray(new Material[0]), 0, finalMaterials, 0, minCount);
            //System.arraycopy(ratios, 0, finalRatios, 0, minCount);
            
            // Normalize ratios
            //double total = 0;
            //for (double ratio : finalRatios) total += ratio;
            //for (int i = 0; i < finalRatios.length; i++) finalRatios[i] /= total;
            
            //materials = java.util.Arrays.asList(finalMaterials);
            //ratios = finalRatios;
        }
        
        MultiMixMaterial multiMix = new MultiMixMaterial(
            materials.toArray(new Material[0]), 
            ratios
        );
        
        System.out.println("=== UNIVERSAL MULTIMIX CREATED SUCCESSFULLY ===");
        System.out.println("Materials: " + materials.size() + ", Ratios: " + java.util.Arrays.toString(ratios));
        return multiMix;
        
    } catch (Exception e) {
        System.err.println("Universal MultiMix parse error: " + e.getMessage());
        e.printStackTrace();
        
        // Fallback: Create a simple MultiMix so the scene at least loads
        Material[] fallbackMaterials = {
            new DiffuseMaterial(Color.RED),
            new DiffuseMaterial(Color.BLUE)
        };
        double[] fallbackRatios = {0.5, 0.5};
        return new MultiMixMaterial(fallbackMaterials, fallbackRatios);
    }
}

private static int findMatchingBracket(String text, int startIndex) {
    if (startIndex < 0 || startIndex >= text.length()) return -1;
    
    int count = 1;
    for (int i = startIndex + 1; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c == '{') count++;
        if (c == '}') count--;
        if (count == 0) return i;
    }
    return -1;
}
//////////

    public static Material createMaterialFromText(String type, String text) {
        try {
            switch (type) {
                case "MultiMixMaterial": {
                    if (shapes.isEmpty()) {
                        JOptionPane.showMessageDialog(pane, "<html><body><font size=\"5\">Add shapes with materials first.</font></body></html>");
                        return new DiffuseMaterial(Color.GRAY);
                    }
                    java.util.List<Material> mats = new ArrayList<Material>();
                    java.util.List<String> names = new ArrayList<String>();
                    int idx = 1;
                    for (EMShape s : shapes) {
                        if (s.getMaterial() != null) {
                            mats.add(s.getMaterial());
                            names.add("mat" + (idx++));
                        }
                    }
                    if (mats.size() < 2) {
                        JOptionPane.showMessageDialog(pane, "<html><body><font size=\"5\">Need at least 2 materials in scene.</font></body></html>");
                        return new DiffuseMaterial(Color.GRAY);
                    }
                    String[] options = new String[mats.size()];
                    for (int i = 0; i < mats.size(); i++) {
                        options[i] = (i+1) + ": " + mats.get(i).getClass().getSimpleName();
                    }
                    JList<String> list = new JList<String>(options);
                    list.setForeground(Color.ORANGE.darker());
                    list.setFont(new Font("Serif", 1, 20));
                    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    JScrollPane scroll = new JScrollPane(list);
                    int res = JOptionPane.showConfirmDialog(pane, scroll,
                        "Select materials for MultiMix (min 2):",
                        JOptionPane.OK_CANCEL_OPTION);
                    if (res != JOptionPane.OK_OPTION) return new DiffuseMaterial(Color.GRAY);
                    int[] sel = list.getSelectedIndices();
                    if (sel.length < 2) {
                        JOptionPane.showMessageDialog(pane, 
                        "<html><body><font size=\"5\" color=\"blue\">Select at least 2 materials.</font></body></html>");
                        return new DiffuseMaterial(Color.GRAY);
                    }
                    Material[] selectedMats = new Material[sel.length];
                    double[] ratios = new double[sel.length];
                    
                    for (int i = 0; i < sel.length; i++) {
                        selectedMats[i] = mats.get(sel[i]);
                        String r = JOptionPane.showInputDialog(pane,
                            "<html><body><font size=\"5\" color=\"red\">Ratio for material " + (i+1) + " (0.0–1.0):</font></body></html>", "0.5");
                        ratios[i] = Double.parseDouble(r);
                    }
                    
                    //"materials = [mat1, mat2];\nratios = [0.7, 0.3];";
					StringBuffer sbk = new StringBuffer("[");
                    
                    final int sellen = sel.length;
                    
                    //for (int i = 0; i < sellen; i++) {
					//	String matname = defMaterials.get(sel[i]);
					//	sbk.append(matname);
					//	if (i == sellen-1) sbk.append("],\n[");
					//	else sbk.append(", ");
					//}
					for (int i = 0; i < sellen; i++) {
						double matr = ratios[i];
						sbk.append(Double.toString(matr));
						if (i == sellen-1) sbk.append("]");
						else sbk.append(", ");
					}
					//materialParamArea.setText(sbk.toString());
					
                    return new MultiMixMaterial(selectedMats, ratios);
                }
                case "AfricanKenteMaterial": return new AfricanKenteMaterial();
                case "AmberMaterial": return new AmberMaterial();
                case "AnisotropicMetalMaterial": {
					Color metalColor = parseColor(text, "metalColor");
					double anisotropy = parseDouble(text, "anisotropy");
					double roughnessX = parseDouble(text, "roughnessX");
					double roughnessY = parseDouble(text, "roughnessY");
					return new AnisotropicMetalMaterial(metalColor, anisotropy, roughnessX, roughnessY, Matrix4.identity());
				}
                case "AnodizedMetalMaterial": {
                    Color baseColor = parseColor(text, "baseColor");
                    return new AnodizedMetalMaterial(baseColor);
                } case "AnodizedTextMaterial": {
					String imagePath = parseString(text, "imagePath");
					String word = parseString(text, "word");
					Color textColor = parseColor(text, "textColor");
					Color gradientColor = parseColor(text, "gradientColor");
					String gradientType = parseString(text, "gradientType");
					Color bgColor = parseColor(text, "bgColor");
					String fontFamily = parseString(text, "fontFamily");
					int fontStyle = parseInt(text, "fontStyle");
					int fontSize = parseInt(text, "fontSize");
					int uOffset = parseInt(text, "uOffset");
					int vOffset = parseInt(text, "vOffset");
					int imageWidth = parseInt(text, "imageWidth");
					int imageHeight = parseInt(text, "imageHeight");
					int imageUOffset = parseInt(text, "imageUOffset");
					int imageVOffset = parseInt(text, "imageVOffset");
					Color baseCol = parseColor(text, "baseColor");
    
					BufferedImage imageObject = loadImage(imagePath);
    
					AnodizedTextMaterial atm = new AnodizedTextMaterial(
						word, textColor, gradientColor, gradientType, bgColor, fontFamily,
						fontStyle, fontSize, uOffset, vOffset, imageObject, imageWidth,
						imageHeight, imageUOffset, imageVOffset, baseCol
					);
					atm.setImagePath(imagePath);
					return atm;
				} case "AuroraCeramicMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color aurora = parseColor(text, "auroraColor");
                    double intensity = parseDouble(text, "auroraIntensity");
                    return new AuroraCeramicMaterial(base, aurora, intensity);
                } case "BlackHoleMaterial": {
                    if (text.contains("singularity")) {
                        Point3 singularity = parsePoint3(text, "singularity");
                        return new BlackHoleMaterial(singularity, Matrix4.identity());
                    } else {
                        return new BlackHoleMaterial(Matrix4.identity());
                    }
                } case "BrightnessMaterial": {
                    Color baseCol = parseColor(text, "baseColor");
                    double brightness = parseDouble(text, "brightness");
                    boolean useLight = parseBoolean(text, "useLightColor");
                    return new BrightnessMaterial(baseCol, brightness, useLight);
                } case "BrunostCheeseMaterial": {
                    Color cheese = parseColor(text, "cheeseColor");
                    Color caramel = parseColor(text, "caramelColor");
                    double amount = parseDouble(text, "caramelAmount");
                    return new BrunostCheeseMaterial(cheese, caramel, amount);
                } case "CalligraphyRuneMaterial": {
                    Color parchment = parseColor(text, "parchmentColor");
                    Color ink = parseColor(text, "inkColor");
                    Color gold = parseColor(text, "goldLeafColor");
                    double writing = parseDouble(text, "writingIntensity");
                    return new CalligraphyRuneMaterial(parchment, ink, gold, writing);
                } case "CarpetTextureMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color pattern = parseColor(text, "patternColor");
                    return new CarpetTextureMaterial(base, pattern);
                } case "CheckerboardMaterial": {
					Color c1 = parseColor(text, "color1");
					Color c2 = parseColor(text, "color2");
					double size = parseDouble(text, "size");
					double ambientCoeff = parseDouble(text, "ambientCoeff");
					double diffuseCoeff = parseDouble(text, "diffuseCoeff");
					double specularCoeff = parseDouble(text, "specularCoeff");
					double shininess = parseDouble(text, "shininess");
					Color specularColor = parseColor(text, "specularColor");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new CheckerboardMaterial(c1, c2, size, ambientCoeff, diffuseCoeff, specularCoeff, shininess, specularColor, reflectivity, ior, transparency, Matrix4.identity());
				} case "CiniMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color pattern = parseColor(text, "patternColor");
                    double glossiness = parseDouble(text, "glossiness");
                    return new CircleTextureMaterial(base, pattern, glossiness, Matrix4.identity());
                } case "CircleTextureMaterial": {
                    Color solid = parseColor(text, "solidColor");
                    Color pattern = parseColor(text, "patternColor");
                    double patternSize = parseDouble(text, "patternSize");
                    return new CircleTextureMaterial(solid, pattern, patternSize, Matrix4.identity());
                } case "ContrastMaterial": {
                    Color baseC = parseColor(text, "baseColor");
                    double contrast = parseDouble(text, "contrast");
                    boolean useL = parseBoolean(text, "useLightColor");
                    return new ContrastMaterial(baseC, contrast, useL);
                } 
                case "CopperMaterial": return new CopperMaterial();
                case "MoonSurfaceMaterial": return new MoonSurfaceMaterial();
                case "WoodGrainMaterial": return new WoodGrainMaterial();
                case "StarryNightMaterial": return new StarryNightMaterial();
                case "CrystalClearMaterial": {
                    Color tint = parseColor(text, "glassTint");
                    double clarity = parseDouble(text, "clarity");
                    double ior = parseDouble(text, "ior");
                    double dispersion = parseDouble(text, "dispersion");
                    return new CrystalClearMaterial(tint, clarity, ior, dispersion, Matrix4.identity());
                } case "CrystalMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color crystal = parseColor(text, "crystalColor");
                    return new CrystalMaterial(base, crystal);
                } case "DamaskCeramicMaterial": {
                    Color primary = parseColor(text, "primary");
                    Color secondary = parseColor(text, "secondary");
                    double shininess = parseDouble(text, "shininess");
                    double ambient = parseDouble(text, "ambient");
                    double specular = parseDouble(text, "specular");
                    return new DamaskCeramicMaterial(primary, secondary, shininess, ambient, specular, Matrix4.identity());
                } case "DewDropMaterial": {
					Color base = parseColor(text, "baseColor");
					Color drop = parseColor(text, "dropColor");
					double density = parseDouble(text, "dropDensity");
					double dropSize = parseDouble(text, "dropSize");
					double ambient = parseDouble(text, "ambient");
					double diffuse = parseDouble(text, "diffuse");
					double specular = parseDouble(text, "specular");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new DewDropMaterial(base, drop, density, dropSize, ambient, diffuse, specular, shininess, reflectivity, ior, transparency, Matrix4.identity());
				} case "DiagonalCheckerMaterial": {
					Color color1 = parseColor(text, "color1");
					Color color2 = parseColor(text, "color2");
					double scale = parseDouble(text, "scale");
					double ambient = parseDouble(text, "ambient");
					double diffuse = parseDouble(text, "diffuse");
					double specular = parseDouble(text, "specular");
					double shininess = parseDouble(text, "shininess");
					Color specularColor = parseColor(text, "specularColor");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new DiagonalCheckerMaterial(color1, color2, scale, ambient, diffuse, specular, shininess, specularColor, reflectivity, ior, transparency, Matrix4.identity());
				} case "DielectricMaterial": {
                    Color diffuse = parseColor(text, "diffuseColor");
                    double ior = parseDouble(text, "ior");
                    double transparency = parseDouble(text, "transparency");
                    double reflectivity = parseDouble(text, "reflectivity");
                    return new DielectricMaterial(diffuse, ior, transparency, reflectivity);
                } case "DiffuseMaterial": {
                    Color color = parseColor(text, "color");
                    return new DiffuseMaterial(color);
                } 
                //case "ElenaTextureMaterial": {
               //     String path = parseString(text, "imagePath");
                //    return new ElenaTextureMaterial(path, Matrix4.identity());
                //} 
                case "EmissiveMaterial": {
                    Color emissive = parseColor(text, "emissiveColor");
                    double strength = parseDouble(text, "emissiveStrength");
                    return new EmissiveMaterial(emissive, strength);
                } case "FractalBarkMaterial": {
                    double roughness = parseDouble(text, "roughness");
                    return new FractalBarkMaterial(Matrix4.identity(), roughness);
                } case "FractalFireMaterial": {
                    int iterations = parseInt(text, "iterations");
                    double chaos = parseDouble(text, "chaos");
                    double scale = parseDouble(text, "scale");
                    double speed = parseDouble(text, "speed");
                    return new FractalFireMaterial(iterations, chaos, scale, speed);
                } case "FjordCrystalMaterial": {
                    Color water = parseColor(text, "waterColor");
                    Color crystal = parseColor(text, "crystalColor");
                    double clarity = parseDouble(text, "clarity");
                    return new FjordCrystalMaterial(water, crystal, clarity);
                } case "GhostTextMaterial": {
					String imagePath = parseString(text, "imagePath");
					String w = parseString(text, "word");
					Color tc = parseColor(text, "textColor");
					Color gradientColor = parseColor(text, "gradientColor");
					String gradientType = parseString(text, "gradientType");
					String fontFamily = parseString(text, "fontFamily");
					int fontStyle = parseInt(text, "fontStyle");
					int fontSize = parseInt(text, "fontSize");
					int uOffset = parseInt(text, "uOffset");
					int vOffset = parseInt(text, "vOffset");
					int imageWidth = parseInt(text, "imageWidth");
					int imageHeight = parseInt(text, "imageHeight");
					int imageUOffset = parseInt(text, "imageUOffset");
					int imageVOffset = parseInt(text, "imageVOffset");
					double trans = parseDouble(text, "transparency");
					double refl = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
    
					BufferedImage imageObject = loadImage(imagePath);
    
					GhostTextMaterial gtm = new GhostTextMaterial(
						w, tc, gradientColor, gradientType, fontFamily, fontStyle, fontSize,
						uOffset, vOffset, imageObject, imageWidth, imageHeight,
						imageUOffset, imageVOffset, trans, refl, ior
					);
					gtm.setImagePath(imagePath);
					return gtm;
				}
                case "GoldMaterial": return new GoldMaterial();
                case "GradientChessMaterial": {
					Color bc1 = parseColor(text, "baseColor1");
					Color bc2 = parseColor(text, "baseColor2");
					double squareSize = parseDouble(text, "squareSize");
					double ambient = parseDouble(text, "ambient");
					double diffuse = parseDouble(text, "diffuse");
					double specular = parseDouble(text, "specular");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new GradientChessMaterial(bc1, bc2, squareSize, ambient, diffuse, specular, shininess, reflectivity, ior, transparency, Matrix4.identity());
				} case "GradientImageTextMaterial": {
					Color bgStart = parseColor(text, "bgStart");
					Color bgEnd = parseColor(text, "bgEnd");
					Color textStart = parseColor(text, "textStart");
					Color textEnd = parseColor(text, "textEnd");
					String imgP = parseString(text, "imagePath");
					float bgAlpha = parseFloat(text, "bgAlpha");
					float textAlpha = parseFloat(text, "textAlpha");
					String txt = parseString(text, "text");
					Font font = parseFont(text, "font");
					StripeDirection direction = parseDirection(text, "direction");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					int xOffset = parseInt(text, "xOffset");
					int yOffset = parseInt(text, "yOffset");
					int imgOffsetX = parseInt(text, "imgOffsetX");
					int imgOffsetY = parseInt(text, "imgOffsetY");
					boolean isWrap = parseBoolean(text, "isWrap");
					BufferedImage img = loadImage(imgP);
					GradientImageTextMaterial gitm = new GradientImageTextMaterial(bgStart, bgEnd, textStart, textEnd, img, bgAlpha, textAlpha, txt, font, direction, reflectivity, ior, transparency, Matrix4.identity(), xOffset, yOffset, imgOffsetX, imgOffsetY, isWrap);
			        gitm.setImagePath(imgP);
			        return gitm;	
				} case "GradientTextMaterial": {
					Color bgStart = parseColor(text, "bgStart");
					Color bgEnd = parseColor(text, "bgEnd");
					Color textStart = parseColor(text, "textStart");
					Color textEnd = parseColor(text, "textEnd");
					String t = parseString(text, "text");
					String fname = parseString(text, "fontName");
					int stl = parseInt(text, "fontStyle");
					int fsz = parseInt(text, "fontSize");
					Font font = new Font(fname, stl, fsz);
					StripeDirection direction = parseDirection(text, "direction");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					int xOffset = parseInt(text, "xOffset");
					int yOffset = parseInt(text, "yOffset");
					return new GradientTextMaterial(bgStart, bgEnd, textStart, textEnd, t, font, direction, reflectivity, ior, transparency, Matrix4.identity(), xOffset, yOffset);
				} case "HexagonalHoneycombMaterial": {
					Color prim = parseColor(text, "primary");
					Color sec = parseColor(text, "secondary");
					Color borderColor = parseColor(text, "borderColor");
					double cellSize = parseDouble(text, "cellSize");
					double borderWidth = parseDouble(text, "borderWidth");
					double ambientStrength = parseDouble(text, "ambientStrength");
					double specularStrength = parseDouble(text, "specularStrength");
					double shininess = parseDouble(text, "shininess");
					return new HexagonalHoneycombMaterial(prim, sec, borderColor, cellSize, borderWidth, ambientStrength, specularStrength, shininess);
				} case "HamamSaunaMaterial": {
                    Color marble = parseColor(text, "marbleColor");
                    Color wood = parseColor(text, "woodColor");
                    Color steam = parseColor(text, "steamColor");
                    double steamInt = parseDouble(text, "steamIntensity");
                    return new HamamSaunaMaterial(marble, wood, steam, steamInt);
                } case "HokusaiMaterial": {
					Color waterColor = parseColor(text, "waterColor");
					Color foamColor = parseColor(text, "foamColor");
					double scale = parseDouble(text, "scale");
					int mode = parseInt(text, "mode");
					double radius = parseDouble(text, "radius");
					return new HokusaiMaterial(waterColor, foamColor, scale, mode, radius, Matrix4.identity());
				} case "HologramDataMaterial": {
                    double density = parseDouble(text, "dataDensity");
                    int res = parseInt(text, "resolution");
                    return new HologramDataMaterial(density, res);
                } case "HolographicDiffractionMaterial": {
                    double refl = parseDouble(text, "reflectivity");
                    return new HolographicDiffractionMaterial(Matrix4.identity(), refl);
                } case "HotCopperMaterial": {
                    Color copper = parseColor(text, "copperColor");
                    Color patina = parseColor(text, "patinaColor");
                    double patinaAmt = parseDouble(text, "patinaAmount");
                    return new HotCopperMaterial(copper, patina, patinaAmt);
                } case "HybridTextMaterial": {
					String imagePath = parseString(text, "imagePath");
					String word = parseString(text, "word");
					Color textColor = parseColor(text, "textColor");
					Color gradientColor = parseColor(text, "gradientColor");
					String gradientType = parseString(text, "gradientType");
					Color bgColor = parseColor(text, "bgColor");
					String fontFamily = parseString(text, "fontFamily");
					int fontStyle = parseInt(text, "fontStyle");
					int fontSize = parseInt(text, "fontSize");
					int uOffset = parseInt(text, "uOffset");
					int vOffset = parseInt(text, "vOffset");
					int imageWidth = parseInt(text, "imageWidth");
					int imageHeight = parseInt(text, "imageHeight");
					int imageUOffset = parseInt(text, "imageUOffset");
					int imageVOffset = parseInt(text, "imageVOffset");
					Color diffuse = parseColor(text, "diffuseColor");
					double ior = parseDouble(text, "indexOfRefraction");
					double trans = parseDouble(text, "transparency");
					double reflect = parseDouble(text, "reflectivity");
					Color filterColorInside = parseColor(text, "filterColorInside");
					Color filterColorOutside = parseColor(text, "filterColorOutside");
					Color specularColor = parseColor(text, "specularColor");
					double shininess = parseDouble(text, "shininess");
					double ambientCoefficient = parseDouble(text, "ambientCoefficient");
					double diffuseCoefficient = parseDouble(text, "diffuseCoefficient");
					double specularCoefficient = parseDouble(text, "specularCoefficient");
    
					BufferedImage imageObject = loadImage(imagePath);
    
					HybridTextMaterial material = new HybridTextMaterial(
						word, textColor, gradientColor, gradientType, bgColor, fontFamily, 
						fontStyle, fontSize, uOffset, vOffset, imageObject, imageWidth, 
						imageHeight, imageUOffset, imageVOffset, diffuse, ior, trans, 
						reflect, filterColorInside, filterColorOutside, specularColor, 
						shininess, ambientCoefficient, diffuseCoefficient, specularCoefficient
					);
					material.setImagePath(imagePath);
					return material;
				} case "ImageTextureMaterial": {
					String p = parseString(text, "imagePath");
					BufferedImage im = loadImage(p);
					double uScale = parseDouble(text, "uScale");
					double vScale = parseDouble(text, "vScale");
					double uOffset = parseDouble(text, "uOffset");
					double vOffset = parseDouble(text, "vOffset");
					double ambientCoefficient = parseDouble(text, "ambientCoefficient");
					double diffuseCoefficient = parseDouble(text, "diffuseCoefficient");
					double specularCoefficient = parseDouble(text, "specularCoefficient");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					ImageTextureMaterial itm = new ImageTextureMaterial(im, uScale, vScale, uOffset, vOffset, ambientCoefficient, diffuseCoefficient, specularCoefficient, shininess, reflectivity, ior, Matrix4.identity());
				    itm.setImagePath(p);
				    return itm;
				}
                case "InvertLightColorMaterial": return new InvertLightColorMaterial();
                case "KilimRosemalingMaterial": {
                    Color kilim = parseColor(text, "kilimColor");
                    Color rosemaling = parseColor(text, "rosemalingColor");
                    Color accent = parseColor(text, "accentColor");
                    double intensity = parseDouble(text, "patternIntensity");
                    return new KilimRosemalingMaterial(kilim, rosemaling, accent, intensity);
                } case "LambertMaterial": {
                    Color col = parseColor(text, "color");
                    double ambient = parseDouble(text, "ambient");
                    double diffuse = parseDouble(text, "diffuse");
                    return new LambertMaterial(col, ambient, diffuse);
                } case "LavaFlowMaterial": {
                    Color hot = parseColor(text, "hotColor");
                    Color cool = parseColor(text, "coolColor");
                    double speed = parseDouble(text, "flowSpeed");
                    return new LavaFlowMaterial(hot, cool, speed, Matrix4.identity());
                } case "LightningMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double intensity = parseDouble(text, "intensity");
                    return new LightningMaterial(base, intensity);
                } case "MarbleMaterial": {
					Color baseM = parseColor(text, "baseColor");
					Color vein = parseColor(text, "veinColor");
					double scale = parseDouble(text, "scale");
					double density = parseDouble(text, "veinDensity");
					double turbulence = parseDouble(text, "turbulence");
					double ambientCoefficient = parseDouble(text, "ambient");
					double diffuseCoefficient = parseDouble(text, "diffuse");
					double specularCoefficient = parseDouble(text, "specular");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new MarbleMaterial(baseM, vein, scale, density, turbulence, ambientCoefficient, diffuseCoefficient, specularCoefficient, shininess, reflectivity, ior, transparency, Matrix4.identity());
				} case "MetallicMaterial": {
                    Color metal = parseColor(text, "metallicColor");
                    Color spec = parseColor(text, "specularColor");
                    double refl = parseDouble(text, "reflectivity");
                    double shin = parseDouble(text, "shininess");
                    double amb = parseDouble(text, "ambient");
                    double diff = parseDouble(text, "diffuse");
                    double specCoeff = parseDouble(text, "specular");
                    return new MetallicMaterial(metal, spec, refl, shin, amb, diff, specCoeff, new Matrix4());
                } case "MosaicMaterial": {
                    Color baseMos = parseColor(text, "baseColor");
                    Color tile = parseColor(text, "tileColor");
                    double tileSize = parseDouble(text, "tileSize");
                    double groutWidth = parseDouble(text, "groutWidth");
                    double randomness = parseDouble(text, "randomness");
                    return new MosaicMaterial(baseMos, tile, tileSize, groutWidth, randomness);
                } case "NeutralMaterial": {
                    Color baseN = parseColor(text, "baseColor");
                    double reflect = parseDouble(text, "reflectivity");
                    double trans = parseDouble(text, "transparency");
                    double iorN = parseDouble(text, "indexOfRefraction");
                    return new NeutralMaterial(baseN, reflect, trans, iorN);
                } case "NordicWoodMaterial": {
                    Color wood = parseColor(text, "woodColor");
                    Color grain = parseColor(text, "grainColor");
                    double intensity = parseDouble(text, "grainIntensity");
                    return new NordicWoodMaterial(wood, grain, intensity);
                } case "NordicWeaveMaterial": {
                    Color primary = parseColor(text, "primaryColor");
                    Color secondary = parseColor(text, "secondaryColor");
                    Color accent = parseColor(text, "accentColor");
                    double scale = parseDouble(text, "patternScale");
                    return new NordicWeaveMaterial(primary, secondary, accent, scale);
                } case "NorthernLightMaterial": {
                    Color primaryAurora = parseColor(text, "primaryAurora");
                    Color secondaryAurora = parseColor(text, "secondaryAurora");
                    double intensity = parseDouble(text, "intensity");
                    return new NorthernLightMaterial(primaryAurora, secondaryAurora, intensity);
                } case "OpticalIllusionMaterial": {
                    Color c1 = parseColor(text, "color1");
                    Color c2 = parseColor(text, "color2");
                    double freq = parseDouble(text, "frequency");
                    double smooth = parseDouble(text, "smoothness");
                    return new OpticalIllusionMaterial(c1, c2, freq, smooth, Matrix4.identity());
                } case "CeramicTilePBRMaterial": {
					Color tile = parseColor(text, "tileColor");
					Color grout = parseColor(text, "groutColor");
					double tileSize = parseDouble(text, "tileSize");
					double groutWidth = parseDouble(text, "groutWidth");
					double tileRough = parseDouble(text, "tileRoughness");
					double groutRough = parseDouble(text, "groutRoughness");
					double tileSpecular = parseDouble(text, "tileSpecular");
					double groutSpecular = parseDouble(text, "groutSpecular");
					double fresnelIntensity = parseDouble(text, "fresnelIntensity");
					double normalMicroFacet = parseDouble(text, "normalMicroFacet");
					double reflectionSharpness = parseDouble(text, "reflectionSharpness");
					double energyConservation = parseDouble(text, "energyConservation");
					return new CeramicTilePBRMaterial(tile, grout, tileSize, groutWidth, tileRough, groutRough, tileSpecular, groutSpecular, fresnelIntensity, normalMicroFacet, reflectionSharpness, energyConservation);
				} case "ChromePBRMaterial": {
                    Color baseRef = parseColor(text, "baseReflectance");
                    double rough = parseDouble(text, "roughness");
                    double aniso = parseDouble(text, "anisotropy");
                    double coat = parseDouble(text, "clearCoat");
                    Color edge = parseColor(text, "edgeTint");
                    return new ChromePBRMaterial(baseRef, rough, aniso, coat, edge);
                } case "CoffeeFjordMaterial": {
                    Color coffee = parseColor(text, "coffeeColor");
                    Color fjord = parseColor(text, "fjordColor");
                    double blend = parseDouble(text, "blendIntensity");
                    return new CoffeeFjordMaterial(coffee, fjord, blend);
                } case "CopperPBRMaterial": {
                    if (text.contains("baseColor")) {
                        Color base = parseColor(text, "baseColor");
                        double rough = parseDouble(text, "roughness");
                        double ox = parseDouble(text, "oxidation");
                        return new CopperPBRMaterial(base, rough, ox);
                    } else {
                        double rough = parseDouble(text, "roughness");
                        double ox = parseDouble(text, "oxidation");
                        return new CopperPBRMaterial(rough, ox);
                    }
                } case "DiamondMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double ior = parseDouble(text, "ior");
                    double reflect = parseDouble(text, "reflectivity");
                    double trans = parseDouble(text, "transparency");
                    double dispersion = parseDouble(text, "dispersionStrength");
                    double fire = parseDouble(text, "fireEffect");
                    return new DiamondMaterial(base, ior, reflect, trans, dispersion, fire);
                } case "EdgeLightColorMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color edge = parseColor(text, "edgeColor");
                    float threshold = (float) parseDouble(text, "edgeThreshold");
                    EdgeLightColorMaterial mat = new EdgeLightColorMaterial();
                    mat.setBaseColor(base);
                    mat.setEdgeColor(edge);
                    mat.setEdgeThreshold(threshold);
                    return mat;
                } case "EmeraldMaterial": {
                    Color baseE = parseColor(text, "baseColor");
                    double density = parseDouble(text, "density");
                    double reflect = parseDouble(text, "reflectivity");
                    return new EmeraldMaterial(baseE, density, reflect);
                } case "GlassicTilePBRMaterial": {
                    Color tile = parseColor(text, "tileColor");
                    Color grout = parseColor(text, "groutColor");
                    double tileSize = parseDouble(text, "tileSize");
                    double groutWidth = parseDouble(text, "groutWidth");
                    double tileRough = parseDouble(text, "tileRoughness");
                    double groutRough = parseDouble(text, "groutRoughness");
                    return new GlassicTilePBRMaterial(tile, grout, tileSize, groutWidth, tileRough, groutRough);
                } case "GlassMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double ior = parseDouble(text, "ior");
                    double reflect = parseDouble(text, "reflectivity");
                    double trans = parseDouble(text, "transparency");
                    return new GlassMaterial(base, ior, reflect, trans);
                } case "GoldPBRMaterial": {
                    if (text.contains("albedo")) {
                        Color albedo = parseColor(text, "albedo");
                        double rough = parseDouble(text, "roughness");
                        double metal = parseDouble(text, "metalness");
                        return new GoldPBRMaterial(albedo, rough, metal);
                    } else {
                        double rough = parseDouble(text, "roughness");
                        return new GoldPBRMaterial(rough);
                    }
                } case "HolographicPBRMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double rainbow = parseDouble(text, "rainbowSpeed");
                    double scan = parseDouble(text, "scanLineDensity");
                    double glitch = parseDouble(text, "glitchIntensity");
                    double time = parseDouble(text, "timeOffset");
                    double distort = parseDouble(text, "distortionFactor");
                    double data = parseDouble(text, "dataDensity");
                    return new HolographicPBRMaterial(base, rainbow, scan, glitch, time, distort, data);
                } case "LinearGradientMaterial": {
                    Color top = parseColor(text, "topColor");
                    Color bottom = parseColor(text, "bottomColor");
                    return new LinearGradientMaterial(top, bottom);
                } case "MarblePBRMaterial": {
                    Color baseM = parseColor(text, "baseColor");
                    Color vein = parseColor(text, "veinColor");
                    double scale = parseDouble(text, "veinScale");
                    double contrast = parseDouble(text, "veinContrast");
                    double rough = parseDouble(text, "roughness");
                    double refl = parseDouble(text, "reflectivity");
                    double intensity = parseDouble(text, "veinIntensity");
                    return new MarblePBRMaterial(baseM, vein, scale, contrast, rough, refl, intensity);
                } case "NonScaledTransparentPNGMaterial": {
                    String imgPath = parseString(text, "imagePath");
                    BufferedImage img = loadImage(imgPath);
                    double width = parseDouble(text, "billboardWidth");
                    double height = parseDouble(text, "billboardHeight");
                    NonScaledTransparentPNGMaterial mat = new NonScaledTransparentPNGMaterial(img, width, height);
                    if (text.contains("shadowAlphaThreshold")) {
                        double threshold = parseDouble(text, "shadowAlphaThreshold");
                        mat.setShadowAlphaThreshold(threshold);
                    }
                    if (text.contains("gammaCorrection")) {
                        float gamma = (float) parseDouble(text, "gammaCorrection");
                        mat.setGammaCorrection(gamma);
                    }
                    
                    mat.setImagePath(imgPath);
                    return mat;
                } case "NorwegianRoseMaterial": {
                    Color wood = parseColor(text, "woodColor");
                    Color rose = parseColor(text, "roseColor");
                    return new NorwegianRoseMaterial(wood, rose);
                } case "OrbitalMaterial": {
                    Color center = parseColor(text, "centerColor");
                    Color orbit = parseColor(text, "orbitColor");
                    double ringWidth = parseDouble(text, "ringWidth");
                    int ringCount = parseInt(text, "ringCount");
                    return new OrbitalMaterial(center, orbit, ringWidth, ringCount, new Matrix4());
                } case "PhongElenaMaterial": {
                    Color diffuse = parseColor(text, "diffuseColor");
                    double reflect = parseDouble(text, "reflectivity");
                    double shin = parseDouble(text, "shininess");
                    double amb = parseDouble(text, "ambientCoefficient");
                    return new PhongElenaMaterial(diffuse, reflect, shin, amb);
                } case "PhongMaterial": {
                    Color diffuse = parseColor(text, "diffuseColor");
                    Color specular = parseColor(text, "specularColor");
                    double shin = parseDouble(text, "shininess");
                    double amb = parseDouble(text, "ambientCoefficient");
                    double diffCoeff = parseDouble(text, "diffuseCoefficient");
                    double specCoeff = parseDouble(text, "specularCoefficient");
                    double reflect = parseDouble(text, "reflectivity");
                    double ior = parseDouble(text, "ior");
                    double trans = parseDouble(text, "transparency");
                    return new PhongMaterial(diffuse, specular, shin, amb, diffCoeff, specCoeff, reflect, ior, trans);
                } case "PhongTextMaterial": {
                    String word = parseString(text, "word");
                    Color textColor = parseColor(text, "textColor");
                    Color diffuse = parseColor(text, "diffuseColor");
                    Color specular = parseColor(text, "specularColor");
                    double shin = parseDouble(text, "shininess");
                    double amb = parseDouble(text, "ambientCoefficient");
                    double diffCoeff = parseDouble(text, "diffuseCoefficient");
                    double specCoeff = parseDouble(text, "specularCoefficient");
                    double reflect = parseDouble(text, "reflectivity");
                    double ior = parseDouble(text, "ior");
                    double trans = parseDouble(text, "transparency");
                    return new PhongTextMaterial(word, textColor, null, "horizontal", new Color(0,0,0,0), "Arial", Font.BOLD, 100, 0, 0, null, 0, 0, 0, 0, diffuse, specular, shin, amb, diffCoeff, specCoeff, reflect, ior, trans);
                } case "PixelArtMaterial": {
                    String paletteStr = parseString(text, "palette");
                    String[] colors = paletteStr.replaceAll("[\\[\\]#]", "").split(",");
                    Color[] palette = new Color[colors.length];
                    for (int i = 0; i < colors.length; i++) {
                        String hex = colors[i].trim();
                        if (hex.length() == 3) {
                            hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
                        }
                        int rgb = Integer.parseInt(hex, 16);
                        palette[i] = new Color(rgb);
                    }
                    double pixelSize = parseDouble(text, "pixelSize");
                    return new PixelArtMaterial(palette, pixelSize, Matrix4.identity());
                } case "PlasticPBRMaterial": {
                    Color albedo = parseColor(text, "albedo");
                    if (text.contains("roughness")) {
                        double rough = parseDouble(text, "roughness");
                        double reflect = parseDouble(text, "reflectivity");
                        double ior = parseDouble(text, "ior");
                        double trans = parseDouble(text, "transparency");
                        return new PlasticPBRMaterial(albedo, rough, reflect, ior, trans);
                    } else {
                        return new PlasticPBRMaterial(albedo);
                    }
                } case "PlatinumMaterial": {
                    if (text.contains("specularBalance")) {
                        double balance = parseDouble(text, "specularBalance");
                        return new PlatinumMaterial(Matrix4.identity(), balance);
                    } else {
                        return new PlatinumMaterial(Matrix4.identity());
                    }
                } case "PolkaDotMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color dot = parseColor(text, "dotColor");
                    if (text.contains("dotSize")) {
                        double dotSize = parseDouble(text, "dotSize");
                        double dotSpacing = parseDouble(text, "dotSpacing");
                        return new PolkaDotMaterial(base, dot, dotSize, dotSpacing);
                    } else {
                        return new PolkaDotMaterial(base, dot);
                    }
                } case "ProceduralCloudMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color highlight = parseColor(text, "highlightColor");
                    return new ProceduralCloudMaterial(base, highlight);
                } case "ProceduralFlowerMaterial": {
                    double petalCount = parseDouble(text, "petalCount");
                    Color petal = parseColor(text, "petalColor");
                    Color center = parseColor(text, "centerColor");
                    if (text.contains("ambientStrength")) {
                        double amb = parseDouble(text, "ambientStrength");
                        return new ProceduralFlowerMaterial(petalCount, petal, center, amb);
                    } else {
                        return new ProceduralFlowerMaterial(petalCount, petal, center);
                    }
                } case "PureWaterMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double speed = parseDouble(text, "flowSpeed");
                    return new PureWaterMaterial(base, speed);
                } case "QuantumFieldMaterial": {
                    Color primary = parseColor(text, "primary");
                    Color secondary = parseColor(text, "secondary");
                    double energy = parseDouble(text, "energy");
                    return new QuantumFieldMaterial(primary, secondary, energy, Matrix4.identity());
                }
                case "RadialGradientMaterial": {
					Color centerColor = parseColor(text, "centerColor");
					Color edgeColor = parseColor(text, "edgeColor");
					return new RadialGradientMaterial(centerColor, edgeColor);
				}
                case "RandomMaterial": return new RandomMaterial(Matrix4.identity());
                //case "RectangleCheckerMaterial": {
                //    Color c1 = parseColor(text, "color1");
                //    Color c2 = parseColor(text, "color2");
                //    double w = parseDouble(text, "rectWidth");
                //    double h = parseDouble(text, "rectHeight");
                //    return new RectangleCheckerMaterial(c1, c2, w, h, Matrix4.identity());
                //} 
                case "ReflectiveMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double reflect = parseDouble(text, "reflectivity");
                    double rough = parseDouble(text, "roughness");
                    return new ReflectiveMaterial(base, reflect, rough);
                } case "RosemalingMaterial": {
                    Color bg = parseColor(text, "backgroundColor");
                    Color flower = parseColor(text, "flowerColor");
                    Color accent = parseColor(text, "accentColor");
                    double density = parseDouble(text, "patternDensity");
                    return new RosemalingMaterial(bg, flower, accent, density);
                } case "RoughMaterial": {
                    Color color = parseColor(text, "color");
                    double rough = parseDouble(text, "roughness");
                    if (text.contains("diffuseCoefficient")) {
                        double diff = parseDouble(text, "diffuseCoefficient");
                        double reflect = parseDouble(text, "reflectivity");
                        return new RoughMaterial(color, rough, diff, reflect);
                    } else {
                        return new RoughMaterial(color, rough);
                    }
                } case "RubyMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double density = parseDouble(text, "density");
                    double reflect = parseDouble(text, "reflectivity");
                    return new RubyMaterial(base, density, reflect);
                } case "RuneStoneMaterial": {
                    Color stone = parseColor(text, "stoneColor");
                    Color rune = parseColor(text, "runeColor");
                    double density = parseDouble(text, "runeDensity");
                    return new RuneStoneMaterial(stone, rune, density);
                } case "SandMaterial": {
					Color baseColor = parseColor(text, "baseSandColor");
					Color darkColor = parseColor(text, "darkSandColor");
					double grainSize = parseDouble(text, "grainSize");
					double roughness = parseDouble(text, "roughness");
					return new SandMaterial(baseColor, darkColor, grainSize, roughness, Matrix4.identity());
				}
                case "SilverMaterial": return new SilverMaterial();
                case "SilverPBRMaterial": {
                    Color albedo = parseColor(text, "albedo");
                    double roughness = parseDouble(text, "roughness");
                    double metalness = parseDouble(text, "metalness");
                    return new SilverPBRMaterial(albedo, roughness, metalness);
			    } case "GraniteMaterial": {
					Color baseColor = parseColor(text, "baseColor");
					double roughness = parseDouble(text, "roughness");
					double specular = parseDouble(text, "specular");
					double reflectivity = parseDouble(text, "reflectivity");
					return new GraniteMaterial(baseColor, roughness, specular, reflectivity, new Matrix4());
				} case "NazarMaterial": {
					Color blueColor = parseColor(text, "blueColor");
					Color whiteColor = parseColor(text, "whiteColor");
					double glowIntensity = parseDouble(text, "glowIntensity");
					double transparency = parseDouble(text, "transparency");
					double reflectivity = parseDouble(text, "reflectivity");
					return new NazarMaterial(blueColor, whiteColor, glowIntensity, transparency, reflectivity, new Matrix4());
				} case "TurkishDelightMaterial": {
					Color primaryColor = parseColor(text, "primaryColor");
					Color powderColor = parseColor(text, "powderColor");
					double softness = parseDouble(text, "softness");
					double sweetness = parseDouble(text, "sweetness");
					double transparency = parseDouble(text, "transparency");
					return new TurkishDelightMaterial(primaryColor, powderColor, softness, sweetness, transparency, new Matrix4());
				} case "SalmonMaterial": {
					Color fleshColor = parseColor(text, "fleshColor");
					Color skinColor = parseColor(text, "skinColor");
					Color fatColor = parseColor(text, "fatColor");
					double freshness = parseDouble(text, "freshness");
					double oiliness = parseDouble(text, "oiliness");
					return new SalmonMaterial(fleshColor, skinColor, fatColor, freshness, oiliness, new Matrix4());
				} case "MirrorMaterial": {
					Color tintColor = parseColor(text, "tintColor");
					double reflectivity = parseDouble(text, "reflectivity");
					double sharpness = parseDouble(text, "sharpness");
					return new MirrorMaterial(tintColor, reflectivity, sharpness, new Matrix4());
				} case "BaklavaMaterial": {
					Color pastryColor = parseColor(text, "pastryColor");
					Color syrupColor = parseColor(text, "syrupColor");
					Color nutColor = parseColor(text, "nutColor");
					double layers = parseDouble(text, "layers");
					double syrupiness = parseDouble(text, "syrupiness");
					return new BaklavaMaterial(pastryColor, syrupColor, nutColor, layers, syrupiness, new Matrix4());
				} case "SimitMaterial": {
					Color crustColor = parseColor(text, "crustColor");
					Color sesameColor = parseColor(text, "sesameColor");
					Color softColor = parseColor(text, "softColor");
					double crispiness = parseDouble(text, "crispiness");
					double sesameDensity = parseDouble(text, "sesameDensity");
					return new SimitMaterial(crustColor, sesameColor, softColor, crispiness, sesameDensity, new Matrix4());
				} case "SmartGlassMaterial": {
                    Color color = parseColor(text, "color");
                    double clarity = parseDouble(text, "clarity");
                    return new SmartGlassMaterial(color, clarity);
                } case "SolidCheckerboardMaterial": {
                    Color c1 = parseColor(text, "color1");
                    Color c2 = parseColor(text, "color2");
                    double size = parseDouble(text, "size");
                    double amb = parseDouble(text, "ambient");
                    double diff = parseDouble(text, "diffuse");
                    return new SolidCheckerboardMaterial(c1, c2, size, amb, diff, Matrix4.identity());
                } case "SolidColorMaterial": {
                    Color color = parseColor(text, "color");
                    return new SolidColorMaterial(color);
                } case "SphereWordTextureMaterial": {
					String imagePath = parseString(text, "imagePath");
					String word = parseString(text, "word");
					Color textColor = parseColor(text, "textColor");
					Color gradientColor = parseColor(text, "gradientColor");
					String gradientType = parseString(text, "gradientType");
					Color bgColor = parseColor(text, "bgColor");
					String fontFamily = parseString(text, "fontFamily");
					int fontStyle = parseInt(text, "fontStyle");
					int fontSize = parseInt(text, "fontSize");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					int uOffset = parseInt(text, "uOffset");
					int vOffset = parseInt(text, "vOffset");
					int imageWidth = parseInt(text, "imageWidth");
					int imageHeight = parseInt(text, "imageHeight");
					int imageUOffset = parseInt(text, "imageUOffset");
					int imageVOffset = parseInt(text, "imageVOffset");
    
					BufferedImage imageObject = loadImage(imagePath);
    
					SphereWordTextureMaterial swtm = new SphereWordTextureMaterial(word, textColor, gradientColor, gradientType, bgColor, fontFamily, fontStyle, fontSize, reflectivity, ior, transparency, uOffset, vOffset, imageObject, imageWidth, imageHeight, imageUOffset, imageVOffset);
					swtm.setImagePath(imagePath);
					return swtm;
				} case "SquaredMaterial": {
					Color c1 = parseColor(text, "color1");
					Color c2 = parseColor(text, "color2");
					double scale = parseDouble(text, "scale");
					double ambient = parseDouble(text, "ambient");
					double diffuse = parseDouble(text, "diffuse");
					double specular = parseDouble(text, "specular");
					double shininess = parseDouble(text, "shininess");
					Color specularColor = parseColor(text, "specularColor");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new SquaredMaterial(c1, c2, scale, ambient, diffuse, specular, shininess, specularColor, reflectivity, ior, transparency, Matrix4.identity());
				} case "StainedGlassMaterial": {
                    Color tint = parseColor(text, "tint");
                    double rough = parseDouble(text, "roughness");
                    return new StainedGlassMaterial(tint, rough, Matrix4.identity());
                } case "StarfieldMaterial": {
                    Color nebula = parseColor(text, "nebulaColor");
                    double size = parseDouble(text, "starSize");
                    double density = parseDouble(text, "starDensity");
                    double speed = parseDouble(text, "twinkleSpeed");
                    return new StarfieldMaterial(Matrix4.identity(), nebula, size, density, speed);
                } case "StripedMaterial": {
					Color c1 = parseColor(text, "color1");
					Color c2 = parseColor(text, "color2");
					double size = parseDouble(text, "stripeSize");
    
					// Parse all the new parameters with default values
					double ambient = parseDouble(text, "ambientCoefficient");
					double diffuse = parseDouble(text, "diffuseCoefficient");
					double specular = parseDouble(text, "specularCoefficient");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
    
					StripeDirection dir = StripeDirection.HORIZONTAL;
					if (text.contains("direction")) {
						String dirStr = parseString(text, "direction");
						try {
							dir = StripeDirection.valueOf(dirStr.toUpperCase());
						} catch (Exception e) {
							dir = StripeDirection.HORIZONTAL;
						}
					}
    
					return new StripedMaterial(c1, c2, size, dir, ambient, diffuse, specular, 
                              shininess, reflectivity, ior, transparency, Matrix4.identity());
				} case "SultanKingMaterial": {
                    Color gold = parseColor(text, "goldColor");
                    Color ruby = parseColor(text, "rubyColor");
                    Color sapphire = parseColor(text, "sapphireColor");
                    double royalty = parseDouble(text, "royaltyIntensity");
                    return new SultanKingMaterial(gold, ruby, sapphire, royalty);
                } case "TelemarkPatternMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color pattern = parseColor(text, "patternColor");
                    Color accent = parseColor(text, "accentColor");
                    double scale = parseDouble(text, "patternScale");
                    return new TelemarkPatternMaterial(base, pattern, accent, scale);
                } case "IsotropicMetalTextMaterial": {
					String imagePathT = parseString(text, "imagePath");
					Color metalColorT = parseColor(text, "metalColor");
					double roughnessT = parseDouble(text, "roughness");
					double reflectivityT = parseDouble(text, "reflectivity");
					double iorT = parseDouble(text, "ior");
					double transT = parseDouble(text, "transparency");
					String wordT = parseString(text, "text");
					Color textColorT = parseColor(text, "textColor");
					Color gradientColorT = parseColor(text, "gradientColor");
					String gradientTypeT = parseString(text, "gradientType");
					Color bgColorT = parseColor(text, "bgColor");
					String fontFamilyT = parseString(text, "fontFamily");
					int fontStyleT = parseInt(text, "fontStyle");
					int fontSizeT = parseInt(text, "fontSize");
					int uOffsetT = parseInt(text, "uOffset");
					int vOffsetT = parseInt(text, "vOffset");
					int imageWidthT = parseInt(text, "imageWidth");
					int imageHeightT = parseInt(text, "imageHeight");
					int imageUOffsetT = parseInt(text, "imageUOffset");
					int imageVOffsetT = parseInt(text, "imageVOffset");

					BufferedImage imageObjectT = null;
					if (imagePathT != null && !imagePathT.isEmpty()) {
						imageObjectT = loadImage(imagePathT);
					}
					IsotropicMetalTextMaterial imtm = new IsotropicMetalTextMaterial(metalColorT, roughnessT, reflectivityT, iorT, transT, new Matrix4(),
						wordT, textColorT, gradientColorT, gradientTypeT, bgColorT, fontFamilyT, fontStyleT, fontSizeT, 
						uOffsetT, vOffsetT, imageObjectT, imageWidthT, imageHeightT, imageUOffsetT, imageVOffsetT);
						imtm.setImagePath(imagePathT);
						return imtm;
				} case "TextDielectricMaterial": {
					String wordT = parseString(text, "word");
					Color textColorT = parseColor(text, "textColor");
					Color gradientColorT = parseColor(text, "gradientColor");
					String gradientTypeT = parseString(text, "gradientType");
					Color bgColorT = parseColor(text, "bgColor");
					String fontFamilyT = parseString(text, "fontFamily");
					int fontStyleT = parseInt(text, "fontStyle");
					int fontSizeT = parseInt(text, "fontSize");
					int uOffsetT = parseInt(text, "uOffset");
					int vOffsetT = parseInt(text, "vOffset");
					String imagePathT = parseString(text, "imagePath");
					int imageWidthT = parseInt(text, "imageWidth");
					int imageHeightT = parseInt(text, "imageHeight");
					int imageUOffsetT = parseInt(text, "imageUOffset");
					int imageVOffsetT = parseInt(text, "imageVOffset");
					Color diffuseColorT = parseColor(text, "diffuseColor");
					double iorT = parseDouble(text, "ior");
					double transT = parseDouble(text, "transparency");
					double reflectT = parseDouble(text, "reflectivity");
					Color filterColorInsideT = parseColor(text, "filterColorInside");
					Color filterColorOutsideT = parseColor(text, "filterColorOutside");
    
					BufferedImage imageObjectT = null;
					if (imagePathT != null && !imagePathT.isEmpty()) {
						imageObjectT = loadImage(imagePathT);
					}
					TextDielectricMaterial tdm = new TextDielectricMaterial(wordT, textColorT, gradientColorT, gradientTypeT, bgColorT, 
						fontFamilyT, fontStyleT, fontSizeT, uOffsetT, vOffsetT, imageObjectT, imageWidthT, 
						imageHeightT, imageUOffsetT, imageVOffsetT, diffuseColorT, iorT, transT, reflectT, 
						filterColorInsideT, filterColorOutsideT);
					tdm.setImagePath(imagePathT);
					return tdm;
				} case "TexturedCheckerboardMaterial": {
					Color c1 = parseColor(text, "color1");
					Color c2 = parseColor(text, "color2");
					double size = parseDouble(text, "size");
					String txt = parseString(text, "text");
					Color textColor = parseColor(text, "textColor");
					Color gradientColor = parseColor(text, "gradientColor");
					String gradientType = parseString(text, "gradientType");
					Color bgColor = parseColor(text, "bgColor");
					String fontFamily = parseString(text, "fontFamily");
					int fontStyle = parseInt(text, "fontStyle");
					int fontSize = parseInt(text, "fontSize");
					int textUOffset = parseInt(text, "textUOffset");
					int textVOffset = parseInt(text, "textVOffset");
					String imagePath = parseString(text, "imagePath");
					int imageWidth = parseInt(text, "imageWidth");
					int imageHeight = parseInt(text, "imageHeight");
					int imageUOffset = parseInt(text, "imageUOffset");
					int imageVOffset = parseInt(text, "imageVOffset");
					double ambientCoeff = parseDouble(text, "ambientCoeff");
					double diffuseCoeff = parseDouble(text, "diffuseCoeff");
					double specularCoeff = parseDouble(text, "specularCoeff");
					double shininess = parseDouble(text, "shininess");
					Color specularColor = parseColor(text, "specularColor");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
    
					BufferedImage imageObject = null;
					if (imagePath != null && !imagePath.isEmpty()) {
						imageObject = loadImage(imagePath);
					}
					TexturedCheckerboardMaterial tcm = new TexturedCheckerboardMaterial(c1, c2, size, txt, textColor, gradientColor, gradientType, bgColor, 
						fontFamily, fontStyle, fontSize, textUOffset, textVOffset, imageObject, imageWidth, imageHeight, 
						imageUOffset, imageVOffset, ambientCoeff, diffuseCoeff, specularCoeff, shininess, specularColor, 
						reflectivity, ior, transparency, Matrix4.identity());
				    tcm.setImagePath(imagePath);
				    return tcm;
				} case "TexturedPhongMaterial": {
					Color base = parseColor(text, "baseDiffuseColor");
					String imgPath = parseString(text, "imagePath");
					BufferedImage img = loadImage(imgPath);
					double trans = parseDouble(text, "transparency");
					Color spec = parseColor(text, "specularColor");
					double shin = parseDouble(text, "shininess");
					double amb = parseDouble(text, "ambientCoefficient");
					double diff = parseDouble(text, "diffuseCoefficient");
					double specCoeff = parseDouble(text, "specularCoefficient");
					double reflect = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double uOffset = parseDouble(text, "uOffset");
					double vOffset = parseDouble(text, "vOffset");
					double uScale = parseDouble(text, "uScale");
					double vScale = parseDouble(text, "vScale");
    
					TexturedPhongMaterial tmt = new TexturedPhongMaterial(base, spec, shin, amb, diff, specCoeff, reflect, ior, trans, img, uOffset, vOffset, uScale, vScale, new Matrix4());
					tmt.setImagePath(imgPath);
					return tmt;
				} case "TextureMaterial": {
                    String p = parseString(text, "imagePath");
                    BufferedImage im = loadImage(p);
                    boolean onTile = parseBoolean(text, "isTile");
                    TextureMaterial tm = new TextureMaterial(im, onTile);
                    tm.setImagePath(p);
                    return tm;
                } case "ThresholdMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double threshold = parseDouble(text, "threshold");
                    Color above = parseColor(text, "aboveColor");
                    Color below = parseColor(text, "belowColor");
                    boolean useLight = parseBoolean(text, "useLightColor");
                    boolean invert = parseBoolean(text, "invertThreshold");
                    return new ThresholdMaterial(base, threshold, above, below, useLight, invert);
                } 
                //case "TransparentColorMaterial": {
                //    double trans = parseDouble(text, "transparency");
                //    double reflect = parseDouble(text, "reflectivity");
                //    double ior = parseDouble(text, "ior");
                //    return new TransparentColorMaterial(trans, reflect, ior);
                //} 
                case "TransparentEmojiMaterial": {
                    String imgP = parseString(text, "imagePath");
                    BufferedImage img = loadImage(imgP);
                    double bwidth = parseDouble(text, "billboardWidth");
                    double bheight = parseDouble(text, "billboardHeight");
                    Color c1 = parseColor(text, "checkerColor1");
                    Color c2 = parseColor(text, "checkerColor2");
                    double size = parseDouble(text, "checkerSize");
                    double uOff = parseDouble(text, "uOffset");
                    double vOff = parseDouble(text, "vOffset");
                    double uScale = parseDouble(text, "uScale");
                    double vScale = parseDouble(text, "vScale");
                    boolean repeat = parseBoolean(text, "isRepeatTexture");
                    boolean messy = parseBoolean(text, "isMessy");
                    TransparentEmojiMaterial tem = new TransparentEmojiMaterial(img, c1, c2, size, uOff, vOff, uScale, vScale, repeat, messy);
                    tem.setImagePath(imgP);
                    tem.setBillboardHeight(bheight);
                    tem.setBillboardWidth(bwidth);
                    return tem;
                } case "TransparentPNGMaterial": {
                    String path = parseString(text, "imagePath");
                    BufferedImage image = loadImage(path);
                    double bwidth = parseDouble(text, "billboardWidth");
                    double bheight = parseDouble(text, "billboardHeight");
                    double uOff = parseDouble(text, "uOffset");
                    double vOff = parseDouble(text, "vOffset");
                    double uScale = parseDouble(text, "uScale");
                    double vScale = parseDouble(text, "vScale");
                    boolean repeat = parseBoolean(text, "isRepeatTexture");
                    TransparentPNGMaterial pm = new TransparentPNGMaterial(image, uOff, vOff, uScale, vScale, repeat);
                    pm.setImagePath(path);
                    pm.setBillboardHeight(bheight);
                    pm.setBillboardWidth(bwidth);
                    return pm;
                } case "TransparentEmissivePNGMaterial": {
                    String pathE = parseString(text, "imagePath");
                    BufferedImage imageE = loadImage(pathE);
                    double bwidth = parseDouble(text, "billboardWidth");
                    double bheight = parseDouble(text, "billboardHeight");
                    Color emissive = parseColor(text, "emissiveColor");
                    double strength = parseDouble(text, "emissiveStrength");
                    double uOff = parseDouble(text, "uOffset");
                    double vOff = parseDouble(text, "vOffset");
                    double uScale = parseDouble(text, "uScale");
                    double vScale = parseDouble(text, "vScale");
                    boolean repeat = parseBoolean(text, "isRepeatTexture");
                    TransparentEmissivePNGMaterial tepn = new TransparentEmissivePNGMaterial(imageE, uOff, vOff, uScale, vScale, repeat, emissive, strength);
                    tepn.setImagePath(pathE);
                    tepn.setBillboardHeight(bheight);
                    tepn.setBillboardWidth(bwidth);
                    return tepn;
                } case "TriangleMaterial": {
					Color c1 = parseColor(text, "color1");
					Color c2 = parseColor(text, "color2");
					double size = parseDouble(text, "triangleSize");
					double ambientCoefficient = parseDouble(text, "ambientCoefficient");
					double diffuseCoefficient = parseDouble(text, "diffuseCoefficient");
					double specularCoefficient = parseDouble(text, "specularCoefficient");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new TriangleMaterial(c1, c2, size, ambientCoefficient, diffuseCoefficient, specularCoefficient, shininess, reflectivity, ior, transparency, Matrix4.identity());
				} case "TulipFjordMaterial": {
                    Color tulip = parseColor(text, "tulipColor");
                    Color fjord = parseColor(text, "fjordColor");
                    Color stem = parseColor(text, "stemColor");
                    double bloom = parseDouble(text, "bloomIntensity");
                    return new TulipFjordMaterial(tulip, fjord, stem, bloom);
                } case "TurkishTileMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color pattern = parseColor(text, "patternColor");
                    double tileSize = parseDouble(text, "tileSize");
                    return new TurkishTileMaterial(base, pattern, tileSize);
                } case "VikingMetalMaterial": {
                    Color base = parseColor(text, "baseColor");
                    Color rust = parseColor(text, "rustColor");
                    double density = parseDouble(text, "rustDensity");
                    return new VikingMetalMaterial(base, rust, density);
                } case "VikingRuneMaterial": {
                    Color stone = parseColor(text, "stoneColor");
                    Color rune = parseColor(text, "runeColor");
                    double depth = parseDouble(text, "runeDepth");
                    return new VikingRuneMaterial(stone, rune, depth);
                } case "WaterfallMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double speed = parseDouble(text, "flowSpeed");
                    return new WaterfallMaterial(base, speed);
                } case "WaterPBRMaterial": {
                    Color water = parseColor(text, "waterColor");
                    double rough = parseDouble(text, "roughness");
                    double wave = parseDouble(text, "waveIntensity");
                    double murk = parseDouble(text, "murkiness");
                    double foam = parseDouble(text, "foamThreshold");
                    return new WaterPBRMaterial(water, rough, wave, murk, foam);
                } 
                //case "WaterRippleMaterial": {
                //    Color water = parseColor(text, "waterColor");
                //    double speed = parseDouble(text, "waveSpeed");
                //    double reflect = parseDouble(text, "reflectivity");
                //    return new WaterRippleMaterial(water, speed, reflect, Matrix4.identity());
                //} 
                case "WoodMaterial": {
					Color base = parseColor(text, "baseColor");
					Color grain = parseColor(text, "grainColor");
					double freq = parseDouble(text, "grainFrequency");
					double ring = parseDouble(text, "ringVariation");
					double ambientCoeff = parseDouble(text, "ambientCoeff");
					double diffuseCoeff = parseDouble(text, "diffuseCoeff");
					double specularCoeff = parseDouble(text, "specularCoeff");
					double shininess = parseDouble(text, "shininess");
					double reflectivity = parseDouble(text, "reflectivity");
					double ior = parseDouble(text, "ior");
					double transparency = parseDouble(text, "transparency");
					return new WoodMaterial(base, grain, freq, ring, ambientCoeff, diffuseCoeff, specularCoeff, shininess, reflectivity, ior, transparency, new Matrix4());
				} case "WoodPBRMaterial": {
                    Color c1 = parseColor(text, "color1");
                    Color c2 = parseColor(text, "color2");
                    double tileSize = parseDouble(text, "tileSize");
                    double rough = parseDouble(text, "roughness");
                    double spec = parseDouble(text, "specularScale");
                    return new WoodPBRMaterial(c1, c2, tileSize, rough, spec);
                } case "WordMaterial": {
					String imagePath = parseString(text, "imagePath");
					String textStr = parseString(text, "text");
					Color fg = parseColor(text, "foregroundColor");
					Color bg = parseColor(text, "backgroundColor");
					String fontName = parseString(text, "fontName");
					int fontSize = parseInt(text, "fontSize");
					int fontStyle = parseInt(text, "fontStyle");
					boolean useGradient = parseBoolean(text, "useGradient");
					Color gradientColor = parseColor(text, "gradientColor");
					int width = parseInt(text, "width");
					int height = parseInt(text, "height");
    
					Font font = new Font(fontName, fontStyle, fontSize);
					BufferedImage wordImage = loadImage(imagePath);
    
					// Tüm parametreleri constructor'a geç
					WordMaterial mw = new WordMaterial(textStr, fg, bg, font, useGradient, gradientColor, wordImage, width, height);
					mw.setImagePath(imagePath);
					return mw;           
				} case "XRayMaterial": {
                    Color base = parseColor(text, "baseColor");
                    double trans = parseDouble(text, "transparency");
                    double reflect = parseDouble(text, "reflectivity");
                    return new XRayMaterial(base, trans, reflect);
                } case "CustomMaterial": {
					String path = parseString(text, "className");
					return (Utilities.loadCustomMaterial(path));
				}
                default:
                    return new DiffuseMaterial(Color.GRAY);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(pane, "Material parse error: " + e.getMessage());
            return new DiffuseMaterial(Color.GRAY);
        }
    }

    public static Light createLightFromText(String type, String text) {
        try {
            switch (type) {
                case "BioluminescentLight": {
					List<Point3> positions = parsePoints3(text, "positions");
					Color col = parseColor(text, "color");
					double ps = parseDouble(text, "pulseSpeed");
					double bi = parseDouble(text, "baseIntensity");
					double af = parseDouble(text, "attenuationFactor");
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					Light lg = new BioluminescentLight(positions, col, ps, bi, af);
					lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				} case "BlackHoleLight": {
                    Point3 singularity = parsePoint3(text, "singularity");
                    double radius = parseDouble(text, "radius");
                    Color color = parseColor(text, "color");
                    double intensity = parseDouble(text, "intensity");
                    double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
                    Light lg = new BlackHoleLight(singularity, radius, color, intensity);
                    lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
                } case "ElenaDirectionalLight": {
                    Vector3 dir = parseVector3(text, "direction");
                    Color color = parseColor(text, "color");
                    double intensity = parseDouble(text, "intensity");
                    double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
                    Light lg = new ElenaDirectionalLight(dir, color, intensity);
                    lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
                } case "ElenaMuratAmbientLight": {
                    Color color = parseColor(text, "color");
                    double intensity = parseDouble(text, "intensity");
                    double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
                    Light lg = new ElenaMuratAmbientLight(color, intensity);
                    lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
                } case "FractalLight": {
                    Point3 pos = parsePoint3(text, "position");
                    Color color = parseColor(text, "color");
                    double intensity = parseDouble(text, "intensity");
                    if (text.contains("octaves")) {
                        int octaves = parseInt(text, "octaves");
                        double persistence = parseDouble(text, "persistence");
                        double frequency = parseDouble(text, "frequency");
                        double dx = parseDouble(text, "firstAnimationIntensity");
					    double dy = parseDouble(text, "secondAnimationIntensity");
                        Light lg = new FractalLight(pos, color, intensity, octaves, persistence, frequency);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    } else {
						double dx = 0.0;
						double dy = 0.0;
                        Light lg = new FractalLight(pos, color, intensity);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    }
                } case "MuratPointLight": {
					Point3 position = parsePoint3(text, "position");
					Color color = parseColor(text, "color");
					double intensity = parseDouble(text, "intensity");
					double constantAttenuation = parseDouble(text, "constantAttenuation");
					double linearAttenuation = parseDouble(text, "linearAttenuation");
					double quadraticAttenuation = parseDouble(text, "quadraticAttenuation");
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					Light lg = new MuratPointLight(position, color, intensity, constantAttenuation, linearAttenuation, quadraticAttenuation);
				    lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				} case "PulsatingPointLight": {
                    Point3 initialPos = parsePoint3(text, "initialPosition");
                    Color baseColor = parseColor(text, "baseColor");
                    double baseIntensity = parseDouble(text, "baseIntensity");
                    double pulsationSpeed = parseDouble(text, "pulsationSpeed");
                    double movementSpeed = parseDouble(text, "movementSpeed");
                    double movementAmplitude = parseDouble(text, "movementAmplitude");
                    if (text.contains("constantAttenuation")) {
                        double c = parseDouble(text, "constantAttenuation");
                        double l = parseDouble(text, "linearAttenuation");
                        double q = parseDouble(text, "quadraticAttenuation");
                        double dx = parseDouble(text, "firstAnimationIntensity");
					    double dy = parseDouble(text, "secondAnimationIntensity");
                        Light lg = new PulsatingPointLight(initialPos, baseColor, baseIntensity, pulsationSpeed, movementSpeed, movementAmplitude, c, l, q);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    } else {
						double dx = 0.0;
						double dy = 0.0;
                        Light lg = new PulsatingPointLight(initialPos, baseColor, baseIntensity, pulsationSpeed, movementSpeed, movementAmplitude);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    }
                } case "SpotLight": {
                    Point3 position = parsePoint3(text, "position");
                    Vector3 direction = parseVector3(text, "direction");
                    Color color = parseColor(text, "color");
                    double intensity = parseDouble(text, "intensity");
                    double inner = parseDouble(text, "innerConeAngle");
                    double outer = parseDouble(text, "outerConeAngle");
                    if (text.contains("constantAttenuation")) {
                        double c = parseDouble(text, "constantAttenuation");
                        double l = parseDouble(text, "linearAttenuation");
                        double q = parseDouble(text, "quadraticAttenuation");
                        double dx = parseDouble(text, "firstAnimationIntensity");
					    double dy = parseDouble(text, "secondAnimationIntensity");
                        Light lg = new SpotLight(position, direction, color, intensity, inner, outer, c, l, q);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    } else {
						double dx = 0.0;
						double dy = 0.0;
                        Light lg = new SpotLight(position, direction, color, intensity, inner, outer);
                        lg.setIncDecIntensity(new double[]{dx, dy});
					    return lg;
                    }
                } case "SphereLight": {
					Point3 position = parsePoint3(text, "position");
					Color color = parseColor(text, "color");
					double intensity = parseDouble(text, "intensity");
					double radius = parseDouble(text, "radius");
					double sampleCount = parseDouble(text, "sampleCount");
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					Light lg = new SphereLight(position, radius, color, intensity, (int)sampleCount);
					lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				} case "TubeLight": {
					Point3 position = parsePoint3(text, "position");
					Color color = parseColor(text, "color");
					double intensity = parseDouble(text, "intensity");
					Point3 startPoint = parsePoint3(text, "startPoint");
					Point3 endPoint = parsePoint3(text, "endPoint");
					double radius = parseDouble(text, "radius");
					double length = parseDouble(text, "length");
					double sampleCount = parseDouble(text, "sampleCount");
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					Light lg = new TubeLight(startPoint, endPoint, radius, color, intensity, (int)sampleCount);
					lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				} case "AreaLight": {
					Point3 position = parsePoint3(text, "position");
					Color color = parseColor(text, "color");
					double intensity = parseDouble(text, "intensity");
					Vector3 normal = parseVector3(text, "normal");
					double width = parseDouble(text, "width");
					double height = parseDouble(text, "height");
					double samplesU = parseDouble(text, "samplesU");
					double samplesV = parseDouble(text, "samplesV");
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					Light lg = new AreaLight(position, normal, width, height, color, intensity, (int)samplesU, (int)samplesV);
					lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				} case "CustomLight": {
					double dx = parseDouble(text, "firstAnimationIntensity");
					double dy = parseDouble(text, "secondAnimationIntensity");
					String path = parseString(text, "className");
					Light lg = (Utilities.loadCustomLight(path));
					lg.setIncDecIntensity(new double[]{dx, dy});
					return lg;
				}
                default:
                    return new MuratPointLight(new Point3(5,5,5), Color.WHITE, 1.0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(pane, "Light parse error: " + e.getMessage());
            return new MuratPointLight(new Point3(5,5,5), Color.WHITE, 1.0);
        }
    }

    // ——————— UTILS ———————
private static Matrix4 parseTransformFromBlock(String block) {
    String transformStr = extractValue(block, "transform");
    if (transformStr == null) return Matrix4.identity();
    
    try {
        Matrix4 transform = Matrix4.identity();
        transformStr = transformStr.replace(";", "").trim();
        
        // Translate
        if (transformStr.contains("translate")) {
            int start = transformStr.indexOf("translate(") + 10;
            int end = transformStr.indexOf(")", start);
            String translatePart = transformStr.substring(start, end);
            
            String[] translateValues = translatePart.split(",");
            double tx = Double.parseDouble(translateValues[0].trim());
            double ty = Double.parseDouble(translateValues[1].trim());
            double tz = Double.parseDouble(translateValues[2].trim());
            transform = transform.multiply(Matrix4.translate(tx, ty, tz));
        }
        
        // Rotate
        if (transformStr.contains("rotate")) {
            int start = transformStr.indexOf("rotate(") + 7;
            int end = transformStr.indexOf(")", start);
            String rotatePart = transformStr.substring(start, end);
            
            String[] rotateValues = rotatePart.split(",");
            double rx = Double.parseDouble(rotateValues[0].trim());
            double ry = Double.parseDouble(rotateValues[1].trim());
            double rz = Double.parseDouble(rotateValues[2].trim());
            
            transform = transform.multiply(Matrix4.rotateZ(rz));
            transform = transform.multiply(Matrix4.rotateY(ry)); 
            transform = transform.multiply(Matrix4.rotateX(rx));
        }
        
        // Scale
        if (transformStr.contains("scale")) {
            int start = transformStr.indexOf("scale(") + 6;
            int end = transformStr.indexOf(")", start);
            String scalePart = transformStr.substring(start, end);
            
            String[] scaleValues = scalePart.split(",");
            double sx = Double.parseDouble(scaleValues[0].trim());
            double sy = Double.parseDouble(scaleValues[1].trim());
            double sz = Double.parseDouble(scaleValues[2].trim());
            transform = transform.multiply(Matrix4.scale(sx, sy, sz));
        }
        
        return transform;
    } catch (Exception e) {
        System.err.println("Transform parse error: " + e.getMessage());
        return Matrix4.identity();
    }
}

public static Matrix4 parseCSGTransformFromBlock(String block) {
    int index = block.lastIndexOf("transform = ");
    block = block.substring(index);
    
    String transformStr = extractValue(block, "transform");
    if (transformStr == null) return Matrix4.identity();
    
    try {
        Matrix4 transform = Matrix4.identity();
        transformStr = transformStr.replace(";", "").trim();
        
        // Translate
        if (transformStr.contains("translate")) {
            int start = transformStr.indexOf("translate(") + 10;
            int end = transformStr.indexOf(")", start);
            String translatePart = transformStr.substring(start, end);
            
            String[] translateValues = translatePart.split(",");
            double tx = Double.parseDouble(translateValues[0].trim());
            double ty = Double.parseDouble(translateValues[1].trim());
            double tz = Double.parseDouble(translateValues[2].trim());
            transform = transform.multiply(Matrix4.translate(tx, ty, tz));
        }
        
        // Rotate
        if (transformStr.contains("rotate")) {
            int start = transformStr.indexOf("rotate(") + 7;
            int end = transformStr.indexOf(")", start);
            String rotatePart = transformStr.substring(start, end);
            
            String[] rotateValues = rotatePart.split(",");
            double rx = Double.parseDouble(rotateValues[0].trim());
            double ry = Double.parseDouble(rotateValues[1].trim());
            double rz = Double.parseDouble(rotateValues[2].trim());
            
            transform = transform.multiply(Matrix4.rotateZ(rz));
            transform = transform.multiply(Matrix4.rotateY(ry)); 
            transform = transform.multiply(Matrix4.rotateX(rx));
        }
        
        // Scale
        if (transformStr.contains("scale")) {
            int start = transformStr.indexOf("scale(") + 6;
            int end = transformStr.indexOf(")", start);
            String scalePart = transformStr.substring(start, end);
            
            String[] scaleValues = scalePart.split(",");
            double sx = Double.parseDouble(scaleValues[0].trim());
            double sy = Double.parseDouble(scaleValues[1].trim());
            double sz = Double.parseDouble(scaleValues[2].trim());
            transform = transform.multiply(Matrix4.scale(sx, sy, sz));
        }
        
        return transform;
    } catch (Exception e) {
        System.err.println("Transform parse error: " + e.getMessage());
        return Matrix4.identity();
    }
}

public static double parseDouble(String text) {
    try { 
        return Double.parseDouble(text.trim()); 
    } catch (Exception e) { 
        return 0.0; 
    }
}

public static double parseDouble(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            return Double.parseDouble(val);
        }
    }
    return 0.0;
}

private static StripeDirection parseDirection(String text, String key) {
    String value = parseString(text, key);
    if (value == null) return StripeDirection.HORIZONTAL;
    
    try {
        return StripeDirection.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
        return StripeDirection.HORIZONTAL;
    }
}

public static int parseInt(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            return Integer.parseInt(val);
        }
    }
    return 0;
}

public static float parseFloat(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            return Float.parseFloat(val);
        }
    }
    return 0.0f;
}

public static Font parseFont(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            
            // "Arial Black - 1 - 72"
            String[] parts = val.split("-");
            if (parts.length >= 3) {
                String fontName = parts[0].trim();
                int style = Integer.parseInt(parts[1].trim());
                int size = Integer.parseInt(parts[2].trim());
                return new Font(fontName, style, size);
            }
            
            // Standart format
            return Font.decode(val);
        }
    }
    return new Font("Arial", Font.PLAIN, 12);
}

public static boolean parseBoolean(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            return Boolean.parseBoolean(val);
        }
    }
    return false;
}

public static Matrix4 parseAnimationTransform(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            val = val.replace("\"", "").replace("'", "");
            return Matrix4.createMatrixFromString(val);
        }
    }
    return new Matrix4();
}

public static String parseString(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            // Tırnak işaretlerini temizle
            return val.replace("\"", "").replace("'", "");
        }
    }
    return "";
}

public static Point3 parsePoint3(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            // P(1,2,3) formatını işle
            if (val.startsWith("P(") && val.endsWith(")")) {
                val = val.substring(2, val.length() - 1);
            }
            return parsePoint3(val);
        }
    }
    return new Point3(0,0,0);
}

private static java.util.List<Point3> parsePoints3(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            java.util.List<Point3> points = new ArrayList<>();
            
            // [P(0,1,0)-(1, 0, 0)] formatını işle
            if (val.startsWith("[") && val.endsWith("]")) {
                val = val.substring(1, val.length() - 1);
            }
            
            // Tire ile ayrılmış point'leri işle
            String[] pointStrs = val.split("-");
            for (String pointStr : pointStrs) {
                pointStr = pointStr.trim();
                // P(1,2,3) formatını işle
                if (pointStr.startsWith("P(") && pointStr.endsWith(")")) {
                    pointStr = pointStr.substring(2, pointStr.length() - 1);
                }
                points.add(parsePoint3(pointStr));
            }
            return points;
        }
    }
    return java.util.Arrays.asList(new Point3(0,0,0));
}

public static Vector3 parseVector3(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            // V(1,2,3) formatını işle
            if (val.startsWith("V(") && val.endsWith(")")) {
                val = val.substring(2, val.length() - 1);
            }
            return parseVector3(val);
        }
    }
    return new Vector3(0,0,0);
}

public static Point3 parsePoint3(String s) {
    try {
        String[] p = s.split(",");
        if (p.length == 3) {
            return new Point3(
                Double.parseDouble(p[0].trim()),
                Double.parseDouble(p[1].trim()),
                Double.parseDouble(p[2].trim())
            );
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return new Point3(0,0,0);
}

    public static String extractValue(String block, String key) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.contains(key + " =")) {
                String value = line.split("=", 2)[1].trim();
                // Yorumları temizle
                if (value.contains("//")) {
                    value = value.split("//")[0].trim();
                }
                return value;
            }
        }
        return null;
    }
    
protected static String createDateString() {
    return java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
}
  
public static Vector3 parseVector3(String s) {
    try {
        String[] v = s.split(",");
        if (v.length == 3) {
            return new Vector3(
                Double.parseDouble(v[0].trim()),
                Double.parseDouble(v[1].trim()),
                Double.parseDouble(v[2].trim())
            );
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return new Vector3(0,0,0);
}

public static Color parseColor(String block, String key) {
    String[] lines = block.split("\n");
    for (String line : lines) {
        if (line.contains(key + " =")) {
            String val = line.split("=")[1].split(";")[0].split("//")[0].trim();
            return parseFlexibleColor(val);
        }
    }
    return Color.WHITE;
}

private static Color parseFlexibleColor(String colorStr) {
    if (colorStr == null || colorStr.isEmpty()) {
        return Color.BLACK;
    }
    
    colorStr = colorStr.trim();
    
    try {
        // HEX formatları (#RRGGBB, #AARRGGBB, #RGB, #ARGB)
        if (colorStr.startsWith("#")) {
            return parseHexColor(colorStr);
        }
        
        // Integer format (255,0,0 veya 255,0,0,255)
        if (colorStr.contains(",") && !colorStr.contains("f")) {
            return parseIntegerColor(colorStr);
        }
        
        // Float format (1.0f,0f,0f veya 1.0f,0f,0f,1f)
        if (colorStr.contains("f")) {
            return parseFloatColor(colorStr);
        }
        
        // Named colors (red, blue, green, etc.)
        return parseNamedColor(colorStr);
        
    } catch (Exception e) {
        System.err.println("Error parsing color: " + colorStr + " - " + e.getMessage());
        return Color.BLACK;
    }
}

public static Color parseHexColor(String hex) {
    hex = hex.replace("#", "").trim();
    
    if (hex.length() == 8) {
        // #AARRGGBB formatı
        int alpha = Integer.parseInt(hex.substring(0, 2), 16);
        int red = Integer.parseInt(hex.substring(2, 4), 16);
        int green = Integer.parseInt(hex.substring(4, 6), 16);
        int blue = Integer.parseInt(hex.substring(6, 8), 16);
        return new Color(red, green, blue, alpha);
    } else if (hex.length() == 6) {
        // #RRGGBB formatı
        return new Color(
            Integer.parseInt(hex.substring(0, 2), 16),
            Integer.parseInt(hex.substring(2, 4), 16),
            Integer.parseInt(hex.substring(4, 6), 16)
        );
    } else if (hex.length() == 3) {
        // #RGB formatını #RRGGBB'ye çevir
        String r = hex.substring(0, 1) + hex.substring(0, 1);
        String g = hex.substring(1, 2) + hex.substring(1, 2);
        String b = hex.substring(2, 3) + hex.substring(2, 3);
        return new Color(
            Integer.parseInt(r, 16),
            Integer.parseInt(g, 16),
            Integer.parseInt(b, 16)
        );
    } else if (hex.length() == 4) {
        // #ARGB formatını #AARRGGBB'ye çevir
        String a = hex.substring(0, 1) + hex.substring(0, 1);
        String r = hex.substring(1, 2) + hex.substring(1, 2);
        String g = hex.substring(2, 3) + hex.substring(2, 3);
        String b = hex.substring(3, 4) + hex.substring(3, 4);
        return new Color(
            Integer.parseInt(r, 16),
            Integer.parseInt(g, 16),
            Integer.parseInt(b, 16),
            Integer.parseInt(a, 16)
        );
    }
    
    throw new IllegalArgumentException("Invalid hex color format: " + hex);
}

private static Color parseIntegerColor(String intStr) {
    String[] parts = intStr.split(",");
    for (int i = 0; i < parts.length; i++) {
        parts[i] = parts[i].trim();
    }
    
    if (parts.length == 3) {
        // RGB format: 255,0,0
        int r = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        int b = Integer.parseInt(parts[2]);
        return new Color(r, g, b);
    } else if (parts.length == 4) {
        // RGBA format: 255,0,0,255
        int r = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        int b = Integer.parseInt(parts[2]);
        int a = Integer.parseInt(parts[3]);
        return new Color(r, g, b, a);
    }
    
    throw new IllegalArgumentException("Invalid integer color format: " + intStr);
}

private static Color parseFloatColor(String floatStr) {
    String[] parts = floatStr.split(",");
    for (int i = 0; i < parts.length; i++) {
        parts[i] = parts[i].trim().replace("f", "");
    }
    
    if (parts.length == 3) {
        // RGB format: 1.0f,0f,0f
        float r = Float.parseFloat(parts[0]);
        float g = Float.parseFloat(parts[1]);
        float b = Float.parseFloat(parts[2]);
        return new Color(r, g, b);
    } else if (parts.length == 4) {
        // RGBA format: 1.0f,0f,0f,1f
        float r = Float.parseFloat(parts[0]);
        float g = Float.parseFloat(parts[1]);
        float b = Float.parseFloat(parts[2]);
        float a = Float.parseFloat(parts[3]);
        return new Color(r, g, b, a);
    }
    
    throw new IllegalArgumentException("Invalid float color format: " + floatStr);
}

private static Color parseNamedColor(String name) {
    switch (name.toLowerCase()) {
        case "black": return Color.BLACK;
        case "white": return Color.WHITE;
        case "red": return Color.RED;
        case "green": return Color.GREEN;
        case "blue": return Color.BLUE;
        case "yellow": return Color.YELLOW;
        case "cyan": return Color.CYAN;
        case "magenta": return Color.MAGENTA;
        case "orange": return Color.ORANGE;
        case "pink": return Color.PINK;
        case "gray": return Color.GRAY;
        case "darkgray": return Color.DARK_GRAY;
        case "lightgray": return Color.LIGHT_GRAY;
        default: 
            throw new IllegalArgumentException("Unknown color name: " + name);
    }
}

    private static BufferedImage loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        try {
            return javax.imageio.ImageIO.read(new java.io.File(path));
        } catch (Exception e) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }
  
  public static EMShape parseCSGShape(String text) {
    try {
        System.out.println("=== STARTING CSG PARSING ===");
        
        // CSG tipini belirle
        String csgType = text.split("\\s+")[0];
        System.out.println("CSG Type: " + csgType);
        
        // Ana blokları ayır
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        
        if (firstBrace == -1 || lastBrace == -1) {
            System.err.println("CSG parse error: No braces found");
            return null;
        }
        
        String content = text.substring(firstBrace + 1, lastBrace).trim();
        System.out.println("CSG Content length: " + content.length());
        
        // Left ve right bloklarını bul
        CSGBlocks blocks = extractCSGBlocks(content);
        if (blocks == null) {
            System.err.println("CSG parse error: Could not extract left/right blocks");
            return null;
        }
        
        System.out.println("Left block found: " + blocks.leftBlock.substring(0, Math.min(50, blocks.leftBlock.length())));
        System.out.println("Right block found: " + blocks.rightBlock.substring(0, Math.min(50, blocks.rightBlock.length())));
        
        // Left şeklini parse et
        EMShape leftShape = parseCSGSubShape(blocks.leftBlock);
        if (leftShape == null) {
            System.err.println("CSG parse error: Failed to parse left shape");
            return null;
        }
        
        // Right şeklini parse et
        EMShape rightShape = parseCSGSubShape(blocks.rightBlock);
        if (rightShape == null) {
            System.err.println("CSG parse error: Failed to parse right shape");
            return null;
        }
        
        // Transformları parse et
        Matrix4 transform = parseCSGTransform(blocks.transformBlock);
        Matrix4 a1Transform = parseCSGTransform(blocks.a1TransformBlock);
        Matrix4 a2Transform = parseCSGTransform(blocks.a2TransformBlock);
        
        // Materialı parse et
        Material material = parseCSGMaterial(blocks.materialBlock);
        
        // CSG şeklini oluştur
        EMShape csgShape = createCSGShape(csgType, leftShape, rightShape);
        if (csgShape == null) {
            return null;
        }
        
        csgShape.setTransform(transform);
        csgShape.setAnimationTransforms(new Matrix4[]{a1Transform, a2Transform});
        if (material != null) {
            //material.setObjectTransform(transform.inverse());
            csgShape.setMaterial(material);
        }
        
        System.out.println("=== CSG PARSING SUCCESSFUL ===");
        return csgShape;
        
    } catch (Exception e) {
        System.err.println("CSG parse error: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

private static class CSGBlocks {
    String leftBlock;
    String rightBlock;
    String transformBlock;
    String a1TransformBlock;
    String a2TransformBlock;
    String materialBlock;
}

private static CSGBlocks extractCSGBlocks(String content) {
    CSGBlocks blocks = new CSGBlocks();
    
    try {
        // Left bloğunu bul
        int leftStart = content.indexOf("left = ");
        if (leftStart == -1) return null;
        
        String afterLeft = content.substring(leftStart + 7);
        blocks.leftBlock = extractCompleteBlock(afterLeft);
        if (blocks.leftBlock == null) return null;
        
        // Right bloğunu bul
        String afterLeftBlock = afterLeft.substring(blocks.leftBlock.length());
        int rightStart = afterLeftBlock.indexOf("right = ");
        if (rightStart == -1) return null;
        
        String afterRight = afterLeftBlock.substring(rightStart + 8);
        blocks.rightBlock = extractCompleteBlock(afterRight);
        if (blocks.rightBlock == null) return null;
        
        // Kalan kısımları işle (transform ve material)
        String remaining = afterRight.substring(blocks.rightBlock.length());
        
        // Transform bloklarını bul
        blocks.transformBlock = extractLineAfter(remaining, "transform = ");
        blocks.a1TransformBlock = extractLineAfter(remaining, "firstAnim_transform = ");
        blocks.a2TransformBlock = extractLineAfter(remaining, "secondAnim_transform = ");
        
        // Material bloğunu bul
        int materialStart = remaining.indexOf("material = ");
        if (materialStart != -1) {
            String materialContent = remaining.substring(materialStart + 11);
            blocks.materialBlock = extractCompleteBlock(materialContent);
        }
        
        return blocks;
        
    } catch (Exception e) {
        System.err.println("Block extraction error: " + e.getMessage());
        return null;
    }
}

private static String extractCompleteBlock(String content) {
    content = content.trim();
    if (!content.contains("{")) {
        // Basit şekil için (Sphere, Box vb.)
        int semicolon = content.indexOf(';');
        if (semicolon != -1) {
            return content.substring(0, semicolon).trim();
        }
        return null;
    }
    
    // CSG veya karmaşık şekil için
    int braceCount = 0;
    int startIndex = content.indexOf('{');
    int endIndex = -1;
    
    for (int i = startIndex; i < content.length(); i++) {
        char c = content.charAt(i);
        if (c == '{') braceCount++;
        if (c == '}') braceCount--;
        
        if (braceCount == 0) {
            endIndex = i;
            break;
        }
    }
    
    if (endIndex == -1) return null;
    return content.substring(0, endIndex + 1).trim();
}

private static String extractLineAfter(String content, String keyword) {
    int start = content.indexOf(keyword);
    if (start == -1) return "";
    
    String afterKeyword = content.substring(start + keyword.length());
    int end = afterKeyword.indexOf(';');
    if (end == -1) return "";
    
    return afterKeyword.substring(0, end).trim();
}

private static EMShape parseCSGSubShape(String block) {
    block = block.trim();
    System.out.println("Parsing sub-shape: " + block.substring(0, Math.min(30, block.length())));
    
    // Eğer bu bir CSG şekliyse recursive olarak parse et
    if (block.contains("CSG") && block.contains("{")) {
        String[] lines = block.split("\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            String shapeType = firstLine.split("\\s+")[0];
            
            // CSG şeklini recursive olarak parse et
            if (shapeType.contains("CSG")) {
                return parseCSGShape(block);
            }
        }
    }
    
    // Normal şekil parse et
    String[] lines = block.split("\n");
    if (lines.length == 0) return null;
    
    String firstLine = lines[0].trim();
    String[] parts = firstLine.split("\\s+");
    if (parts.length < 2) return null;
    
    String shapeType = parts[0];
    
    EMShape shape = createShapeFromText(shapeType, block);
    if (shape != null) {
        // Transformları ayarla
        Matrix4 transform = parseTransformFromShapeBlock(block);
        Matrix4 a1Transform = parseAnimationTransform(block, "firstAnim_transform");
        Matrix4 a2Transform = parseAnimationTransform(block, "secondAnim_transform");
        
        shape.setTransform(transform);
        shape.setAnimationTransforms(new Matrix4[]{a1Transform, a2Transform});
    }
    
    return shape;
}

private static Matrix4 parseCSGTransform(String transformBlock) {
    try {
        if (transformBlock == null || transformBlock.isEmpty()) {
            return Matrix4.identity();
        }
        return Matrix4.createMatrixFromString(transformBlock);
    } catch (Exception e) {
        System.err.println("CSG transform parse error: " + e.getMessage());
        return Matrix4.identity();
    }
}

private static Material parseCSGMaterial(String materialBlock) {
    try {
        if (materialBlock == null || materialBlock.isEmpty()) {
            return new DiffuseMaterial(Color.RED);
        }
        
        String[] lines = materialBlock.split("\n");
        if (lines.length == 0) return new DiffuseMaterial(Color.RED);
        
        String firstLine = lines[0].trim();
        String materialType = firstLine.split("\\s+")[0];
        if (materialType.startsWith("MultiMixMaterial")) {
			return parseMultiMixMaterialForCSG (materialType, materialBlock);
		} else {
           return createMaterialFromText(materialType, materialBlock);
	    }
    } catch (Exception e) {
        System.err.println("CSG material parse error: " + e.getMessage());
        return new DiffuseMaterial(Color.RED);
    }
}

private static EMShape createCSGShape(String csgType, EMShape left, EMShape right) {
    switch (csgType) {
        case "UnionCSG":
            return new UnionCSG(left, right);
        case "IntersectionCSG":
            return new IntersectionCSG(left, right);
        case "DifferenceCSG":
            return new DifferenceCSG(left, right);
        default:
            System.err.println("Unknown CSG type: " + csgType);
            return null;
    }
}

private static Matrix4 parseTransformFromShapeBlock(String block) {
    try {
        String transformStr = extractValue(block, "transform");
        if (transformStr == null) return Matrix4.identity();
        return Matrix4.createMatrixFromString(transformStr);
    } catch (Exception e) {
        System.err.println("Inner transform parse error: " + e.getMessage());
        return Matrix4.identity();
    }
}

public static EMShape loadCustomShape(String classPath) {
    try {
        System.out.println("Loading custom shape: " + classPath);
        
        // Class path ve class adını ayır
        File classFile = new File(classPath);
        String className = classFile.getName();
        File classDir = classFile.getParentFile();
        
        // .class uzantısını temizle
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - 6);
        }
        
        // Class loader oluştur
        URLClassLoader classLoader = new URLClassLoader(new URL[]{classDir.toURI().toURL()});
        
        // Class'ı yükle
        Class<?> shapeClass = classLoader.loadClass(className);
        
        // EMShape interface'ini implemente ettiğinden emin ol
        if (!EMShape.class.isAssignableFrom(shapeClass)) {
            throw new IllegalArgumentException("Class does not implement EMShape interface: " + className);
        }
        
        // Instance oluştur
        EMShape shape = (EMShape) shapeClass.getDeclaredConstructor().newInstance();
        
        System.out.println("Custom shape loaded successfully: " + classPath);
        return shape;
        
    } catch (Exception e) {
        System.err.println("Failed to load custom shape: " + classPath + " - " + e.getMessage());
        return null;
    }
}

public static Material loadCustomMaterial(String classPath) {
    try {
        System.out.println("Loading custom material: " + classPath);
        
        // Class path ve class adını ayır
        File classFile = new File(classPath);
        String className = classFile.getName();
        File classDir = classFile.getParentFile();
        
        // .class uzantısını temizle
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - 6);
        }
        
        // Class loader oluştur
        URLClassLoader classLoader = new URLClassLoader(new URL[]{classDir.toURI().toURL()});
        
        // Class'ı yükle
        Class<?> materialClass = classLoader.loadClass(className);
        
        // Material interface'ini implemente ettiğinden emin ol
        if (!Material.class.isAssignableFrom(materialClass)) {
            throw new IllegalArgumentException("Class does not implement Material interface: " + className);
        }
        
        // Instance oluştur
        Material material = (Material) materialClass.getDeclaredConstructor().newInstance();
        
        System.out.println("Custom material loaded successfully: " + classPath);
        return material;
        
    } catch (Exception e) {
        System.err.println("Failed to load custom material: " + classPath + " - " + e.getMessage());
        return null;
    }
}

public static Light loadCustomLight(String classPath) {
    try {
        System.out.println("Loading custom light: " + classPath);
        
        // Class path ve class adını ayır
        File classFile = new File(classPath);
        String className = classFile.getName();
        File classDir = classFile.getParentFile();
        
        // .class uzantısını temizle
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - 6);
        }
        
        // Class loader oluştur
        URLClassLoader classLoader = new URLClassLoader(new URL[]{classDir.toURI().toURL()});
        
        // Class'ı yükle
        Class<?> lightClass = classLoader.loadClass(className);
        
        // Light interface'ini implemente ettiğinden emin ol
        if (!Light.class.isAssignableFrom(lightClass)) {
            throw new IllegalArgumentException("Class does not implement Light interface: " + className);
        }
        
        // Instance oluştur
        Light light = (Light) lightClass.getDeclaredConstructor().newInstance();
        
        System.out.println("Custom light loaded successfully: " + classPath);
        return light;
        
    } catch (Exception e) {
        System.err.println("Failed to load custom light: " + classPath + " - " + e.getMessage());
        return null;
    }
}
    
    private static String getType(String def) {
		int index = def.indexOf(" ");
		String retdef = def.substring(0, index);
		retdef = retdef.trim();
		return retdef;
	}
	
	public static Material parseMultiMixMaterialWithoutDialog(String materialContent) {
    try {
        String[] lines = materialContent.split("\n");
        String materialType = lines[0].trim().split(" ")[0];
        
        // MultiMixMaterial değilse normal parse et
        if (!"MultiMixMaterial".equals(materialType)) {
            return Utilities.createMaterialFromText(materialType, materialContent);
        }
        
        // MultiMixMaterial için MANUEL parsing - DİALOG YOK!
        String fullContent = String.join("\n", lines);
        
        // Materyalleri bul [material1, material2, ...]
        int materialsStart = fullContent.indexOf("[");
        int materialsEnd = fullContent.indexOf("]", materialsStart);
        String materialsBlock = fullContent.substring(materialsStart + 1, materialsEnd);
        
        // Oranları bul [0.3, 0.7]
        int ratiosStart = fullContent.indexOf("[", materialsEnd + 1);
        int ratiosEnd = fullContent.indexOf("]", ratiosStart);
        String ratiosBlock = fullContent.substring(ratiosStart + 1, ratiosEnd);
        
        // Materyalleri parse et
        java.util.List<Material> materials = new ArrayList<>();
        String[] materialParts = materialsBlock.split("},");
        for (String materialPart : materialParts) {
            materialPart = materialPart.trim() + "}"; // } ekle
            String type = materialPart.split("\\s+")[0];
            Material mat = Utilities.createMaterialFromText(type, materialPart);
            if (mat != null) materials.add(mat);
        }
        
        // Oranları parse et
        java.util.List<Double> ratios = new ArrayList<>();
        String[] ratioParts = ratiosBlock.split(",");
        for (String ratioPart : ratioParts) {
            ratios.add(Double.parseDouble(ratioPart.trim()));
        }
        
        if (materials.size() == ratios.size() && materials.size() >= 2) {
            return new MultiMixMaterial(
                materials.toArray(new Material[0]),
                ratios.stream().mapToDouble(Double::doubleValue).toArray()
            );
        }
        
    } catch (Exception e) {
        System.err.println("MultiMix parse error: " + e.getMessage());
    }
    
    return new DiffuseMaterial(Color.GRAY);
   }
  
private static String extractReferencedName(String text, String side) {
    String[] lines = text.split("\n");
    for (String line : lines) {
        line = line.trim();
        if (line.startsWith(side + " =")) {
            String name = line.split("=", 2)[1].trim();
            if (name.endsWith(";")) {
                name = name.substring(0, name.length() - 1).trim();
            }
            return name;
        }
    }
    return null;
}

    public static void main(String[] args) {
    }
    
}
