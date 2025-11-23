// SceneParser.java
// Murat Inan
package net.elena.murat.gui;

// JAVA
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

// CUSTOM
import net.elena.murat.light.*;
import net.elena.murat.material.*;
import net.elena.murat.material.pbr.*;
import net.elena.murat.shape.*;
import net.elena.murat.shape.letters.*;
import net.elena.murat.math.*;
import net.elena.murat.lovert.*;
import net.elena.murat.util.*;

public class SceneParser {
    private final Map<String, EMShape> shapes = new HashMap<>();
    private final Map<String, Light> lights = new HashMap<>();
    private final java.util.List<EMShape> sceneShapes = new ArrayList<>();
    private final java.util.List<Light> sceneLights = new ArrayList<>();

    private Camera camera = new Camera();
    private Color bgColor = Color.BLUE;
    private Color shadowColor = Color.BLACK;
    private int width = 800;
    private int height = 600;
    
    private int shapeCounter = 0;
    
    public SceneParser() {
        camera.setCameraPosition(new Point3(0, 0, 5));
        camera.setLookAt(new Point3(0, 0, 0));
        camera.setUpVector(new Vector3(0, 1, 0));
        camera.setFov(60.0);
        camera.setOrthographic(false);
        camera.setReflective(true);
        camera.setRefractive(true);
        camera.setShadowsEnabled(true);
        camera.setMaxRecursionDepth(3);
        camera.setAnimationPoints(new Point3[]{new Point3(0, 0, 0), new Point3(0, 0, 0)});
    }
    
    public BufferedImage renderScene(String filename) throws IOException {
        parseSceneFile(filename);
        return render();
    }
    
    public BufferedImage renderScene(File file) throws IOException {
        parseSceneFile(file);
        return render();
    }
    
    private void parseSceneFile(String filename) throws IOException {
        parseSceneFile(new File(filename));
    }
    
    private void parseSceneFile(File file) throws IOException {
        shapes.clear();
        lights.clear();
        sceneShapes.clear();
        sceneLights.clear();
        shapeCounter = 0;
        
        java.util.List<String> lines = readFileLines(file);
        parseSections(lines);
        buildScene();
    }
    
    private java.util.List<String> readFileLines(File file) throws IOException {
        java.util.List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("//") && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
    
    private boolean isSectionStart(String line) {
        if (line.startsWith("Camera")) return true;
        if (line.startsWith("Renderer Settings")) return true;
        if (line.contains("Light") && line.endsWith("{")) return true;
        if (Utilities.isShapeLine(line)) return true;
        return false;
    }
    
    private void parseSection(String content) {
        String firstLine = content.split("\n")[0].trim();
        
        if (firstLine.startsWith("Camera")) {
            parseCamera(content);
        } else if (firstLine.startsWith("Renderer Settings")) {
            parseRenderer(content);
        } else if (firstLine.contains("Light")) {
            parseLight(content);
        } else if (Utilities.isShapeLine(content)) {
            parseShape(content);
        }
    }
    
    private void parseCamera(String content) {
        try {
            Point3 position = Utilities.parsePoint3(content, "position");
            Point3 lookAt = Utilities.parsePoint3(content, "lookAt");
            Vector3 upVector = Utilities.parseVector3(content, "upVector");
            double fov = Utilities.parseDouble(content, "fov");
            boolean orthographic = Utilities.parseBoolean(content, "orthographic");
            int maxRecursionDepth = Utilities.parseInt(content, "maxRecursionDepth");
            Point3 a1p = Utilities.parsePoint3(content, "firstAnimationPoint");
            Point3 a2p = Utilities.parsePoint3(content, "secondAnimationPoint");
            boolean reflective = Utilities.parseBoolean(content, "reflective");
            boolean refractive = Utilities.parseBoolean(content, "refractive");
            boolean shadowsEnabled = Utilities.parseBoolean(content, "shadowsEnabled");
            
            camera.setCameraPosition(position);
            camera.setLookAt(lookAt);
            camera.setUpVector(upVector);
            camera.setFov(fov);
            camera.setOrthographic(orthographic);
            camera.setMaxRecursionDepth(maxRecursionDepth);
            camera.setAnimationPoints(new Point3[]{a1p, a2p});
            camera.setReflective(reflective);
            camera.setRefractive(refractive);
            camera.setShadowsEnabled(shadowsEnabled);
            
            System.out.println("Camera parsed: " + position);
        } catch (Exception e) {
            System.err.println("Camera parse error: " + e.getMessage());
        }
    }
    
    private void parseRenderer(String content) {
        try {
            width = Utilities.parseInt(content, "width");
            height = Utilities.parseInt(content, "height");
            bgColor = Utilities.parseColor(content, "backgroundColor");
            shadowColor = Utilities.parseColor(content, "shadowColor");
            
            System.out.println("Renderer parsed: " + width + "x" + height);
        } catch (Exception e) {
            System.err.println("Renderer parse error: " + e.getMessage());
        }
    }
    
    private void parseLight(String content) {
        try {
            String[] lines = content.split("\n");
            String firstLine = lines[0].trim();
            String lightType = firstLine.split(" ")[0];
            String lightName = firstLine.split(" ")[1];
            
            Light light = Utilities.createLightFromText(lightType, content);
            if (light != null) {
                lights.put(lightName, light);
                System.out.println("Light parsed: " + lightName);
            }
        } catch (Exception e) {
            System.err.println("Light parse error: " + e.getMessage());
        }
    }
    
    private void parseShape(String content) {
    try {
        EMShape shape = null;
        String shapeType = "";
        String originalName = "";
        
        String[] lines = content.split("\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            String[] parts = firstLine.split("\\s+");
            if (parts.length >= 2) {
                shapeType = parts[0];
                originalName = parts[1];
            }
        }
        
        boolean isCSG = shapeType.contains("CSG");
        
        if (isCSG) {
            shape = parseCSGShape(content);
            if (shape == null) return;
            
            String uniqueName = originalName + "_" + (shapeCounter++);
            shapes.put(uniqueName, shape);
            
        } else {
            shape = Utilities.createShapeFromText(shapeType, content);
            if (shape != null) {
                Matrix4 transform = parseTransformFromContent(content);
                shape.setTransform(transform);
                
                Material material = extractMaterialFromShape(content);
                if (material != null) {
                    material.setObjectTransform(transform.inverse());
                    shape.setMaterial(material);
                }
                
                String uniqueName = originalName + "_" + (shapeCounter++);
                shapes.put(uniqueName, shape);
            }
        }
        
    } catch (Exception e) {
        System.err.println("Shape parse error: " + e.getMessage());
    }
}
    
private void parseSections(java.util.List<String> lines) {
    StringBuilder currentSection = new StringBuilder();
    int braceCount = 0;
    boolean inSection = false;

    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.equals("-END-")) break;

        if (!inSection && isSectionStart(line)) {
            inSection = true;
            currentSection = new StringBuilder();
            braceCount = 0;
        }

        if (inSection) {
            currentSection.append(line).append("\n");
            for (char c : line.toCharArray()) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
            }

            if (braceCount == 0 && currentSection.length() > 0) {
                parseSection(currentSection.toString());
                inSection = false;
            }
        }
    }
}

private EMShape parseCSGShape(String content) {
  return Utilities.parseCSGShape(content);
}

private Matrix4 parseTransformFromContent(String content) {
    try {
        String transformStr = Utilities.extractValue(content, "transform");
        if (transformStr == null) return Matrix4.identity();
        
        // Matrix4.createMatrixFromString kullan, o zaten doğru sırayı kullanıyor
        return Matrix4.createMatrixFromString(transformStr);
        
    } catch (Exception e) {
        System.err.println("Transform parse error: " + e.getMessage());
        return Matrix4.identity();
    }
}
    
    private Material extractMaterialFromShape(String shapeContent) {
        try {
            int materialStart = shapeContent.indexOf("material = ");
            if (materialStart < 0) return null;
            
            String remaining = shapeContent.substring(materialStart + "material = ".length());
            int braceStart = remaining.indexOf("{");
            if (braceStart < 0) return null;
            
            int braceCount = 1;
            int currentPos = braceStart + 1;
            
            while (currentPos < remaining.length() && braceCount > 0) {
                char c = remaining.charAt(currentPos);
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                currentPos++;
            }
            
            if (braceCount == 0) {
                String materialBlock = "material = " + remaining.substring(0, currentPos);
                return parseMaterialContent(materialBlock);
            }
        } catch (Exception e) {
            System.err.println("Material extraction error: " + e.getMessage());
        }
        return null;
    }
    
    private Material parseMaterialContent(String content) {
        try {
            String cleanContent = content.replace("material = ", "").trim();
            String[] lines = cleanContent.split("\n");
            if (lines.length == 0) return new DiffuseMaterial(Color.RED);
            
            String firstLine = lines[0].trim();
            String materialType = firstLine.split(" ")[0];
            
            System.out.println("Parsing material: " + materialType);
            
            if ("MultiMixMaterial".equals(materialType)) {
                return Utilities.parseMultiMixMaterialWithoutDialog(cleanContent);
            }
            
            return Utilities.createMaterialFromText(materialType, cleanContent);
            
        } catch (Exception e) {
            System.err.println("Material parse error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new DiffuseMaterial(Color.RED);
    }
    
    private java.util.List<Material> parseMultiMixMaterials(String materialsBlock) {
        java.util.List<Material> materials = new ArrayList<>();
        
        try {
            String[] materialParts = splitMultiMixComponents(materialsBlock);
            
            for (String materialPart : materialParts) {
                materialPart = materialPart.trim();
                if (!materialPart.isEmpty()) {
                    Material material = parseMaterialContent(materialPart);
                    if (material != null) {
                        materials.add(material);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("MultiMix materials parse error: " + e.getMessage());
        }
        
        return materials;
    }
    
    private java.util.List<Double> parseMultiMixRatios(String ratiosBlock) {
        java.util.List<Double> ratios = new ArrayList<>();
        
        try {
            String[] ratioParts = ratiosBlock.split(",");
            for (String ratioPart : ratioParts) {
                ratioPart = ratioPart.trim();
                if (!ratioPart.isEmpty()) {
                    ratios.add(Double.parseDouble(ratioPart));
                }
            }
        } catch (Exception e) {
            System.err.println("MultiMix ratios parse error: " + e.getMessage());
        }
        
        return ratios;
    }
    
    private String[] splitMultiMixComponents(String block) {
        java.util.List<String> components = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        
        for (char c : block.toCharArray()) {
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            
            if (c == ',' && braceCount == 0) {
                components.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            components.add(current.toString().trim());
        }
        
        return components.toArray(new String[0]);
    }
    
    private int findMatchingBracket(String str, int start) {
        if (start < 0 || start >= str.length()) return -1;
        
        int count = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') count++;
            if (c == ']') count--;
            
            if (count == 0) return i;
        }
        return -1;
    }
    
    private void buildScene() {
        sceneLights.addAll(lights.values());
        sceneShapes.addAll(shapes.values());
        
        System.out.println("Scene built with " + sceneShapes.size() + " shapes and " + sceneLights.size() + " lights");
        
        for (EMShape shape : sceneShapes) {
            System.out.println("Scene shape: " + shape.getClass().getSimpleName());
        }
    }
    
    private BufferedImage render() {
        try {
            Scene scene = new Scene();
            
            System.out.println("Adding " + sceneShapes.size() + " shapes to scene");
            for (EMShape shape : sceneShapes) {
                scene.addShape(shape);
                System.out.println("Added to scene: " + shape.getClass().getSimpleName());
            }
            
            System.out.println("Adding " + sceneLights.size() + " lights to scene");
            for (Light light : sceneLights) {
                scene.addLight(light);
            }
            
            ElenaMuratRayTracer tracer = new ElenaMuratRayTracer(scene, width, height, bgColor);
            tracer.setCamera(camera);
            tracer.setShadowColor(shadowColor);
            
            System.out.println("Starting render...");
            BufferedImage result = tracer.render();
            System.out.println("Render completed");
            
            return result;
        } catch (Exception e) {
            System.err.println("Render error: " + e.getMessage());
            e.printStackTrace();
            
            BufferedImage errorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = errorImage.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.RED);
            g.drawString("Render Error: " + e.getMessage(), 10, 20);
            g.dispose();
            return errorImage;
        }
    }

    public Camera getCamera() { return camera; }
    public Color getBackgroundColor() { return bgColor; }
    public Color getShadowColor() { return shadowColor; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Map<String, EMShape> getShapes() { return shapes; }
    public Map<String, Light> getLights() { return lights; }
    public java.util.List<EMShape> getSceneShapes() { return sceneShapes; }
    public java.util.List<Light> getSceneLights() { return sceneLights; }
    
}
