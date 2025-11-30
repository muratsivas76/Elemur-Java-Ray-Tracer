import java.util.*;
import java.io.*;

public class DepthCalculator {
    
    // 3D point representation
    static class Point {
        double x, y, z;
        Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f, %.2f)", x, y, z);
        }
    }
    
    // Display usage information
    private static void printUsage() {
        System.out.println("=".repeat(70));
        System.out.println("                    DEPTH CALCULATOR - USAGE GUIDE");
        System.out.println("=".repeat(70));
        
        System.out.println("\nUSAGE FORMAT:");
        System.out.println("  java DepthCalculator \"x,y,z\" baseColor \"camX,camY,camZ\" \"lightX,lightY,lightZ\" fadeFactor tempFactor");
        
        System.out.println("\nPARAMETERS:");
        System.out.println("  \"x,y,z\"         - Object position coordinates (in quotes)");
        System.out.println("  baseColor       - Base color in hex format (with or without #)");
        System.out.println("  \"camX,camY,camZ\" - Camera position coordinates (in quotes)");
        System.out.println("  \"lightX,lightY,lightZ\" - Light position coordinates (in quotes)");
        System.out.println("  fadeFactor      - Color fade factor (0.0 - 1.0)");
        System.out.println("  tempFactor      - Temperature factor (0.0 - 1.0)");
        
        System.out.println("\nUSAGE EXAMPLE:");
        System.out.println("  java DepthCalculator \"1.0,0.0,-1.0\" \"#0000FF\" \"0.0,0.0,5.0\" \"5.0,5.0,5.0\" 0.03 0.04");
        System.out.println("  java DepthCalculator \"0,0,-3\" FF0000 \"0,0,10\" \"2,8,4\" 0.02 0.05");
        
        System.out.println("\nOUTPUT:");
        System.out.println("  - Detailed analysis printed to console");
        System.out.println("  - Results saved to 'objDepthInfo.txt'");
        
        System.out.println("\n" + "=".repeat(70));
    }
    
    // Color utility methods
    private static String normalizeColor(String color) {
        String clean = color.startsWith("#") ? color.substring(1) : color;
        if (clean.length() != 6) {
            throw new IllegalArgumentException("Invalid color format: " + color);
        }
        return "#" + clean.toUpperCase();
    }
    
    private static String applyFade(String color, double fadeFactor) {
        String clean = color.startsWith("#") ? color.substring(1) : color;
        
        int r = Integer.parseInt(clean.substring(0, 2), 16);
        int g = Integer.parseInt(clean.substring(2, 4), 16);
        int b = Integer.parseInt(clean.substring(4, 6), 16);
        
        r = (int) (r * fadeFactor);
        g = (int) (g * fadeFactor);
        b = (int) (b * fadeFactor);
        
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return String.format("#%02X%02X%02X", r, g, b);
    }
    
    private static String calculateSpecularColor(String baseColor, double distance, double tempFactor) {
        String clean = baseColor.startsWith("#") ? baseColor.substring(1) : baseColor;
        
        int r = Integer.parseInt(clean.substring(0, 2), 16);
        int g = Integer.parseInt(clean.substring(2, 4), 16);
        int b = Integer.parseInt(clean.substring(4, 6), 16);
        
        // Add blue tint based on distance (atmospheric effect)
        double blueBoost = distance * tempFactor * 50;
        b = (int) Math.min(255, b + blueBoost);
        
        // Reduce intensity for specular
        r = (int) (r * 0.4 + 100);
        g = (int) (g * 0.4 + 100);
        b = (int) (b * 0.4 + 150);
        
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return String.format("#%02X%02X%02X", r, g, b);
    }
    
    // Parsing methods
    private static Point parseCoordinate(String coordStr) {
        try {
            String clean = coordStr.trim();
            String[] parts = clean.split(",");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Coordinate must have exactly 3 values: " + coordStr);
            }
            
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            
            return new Point(x, y, z);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid coordinate format: " + coordStr, e);
        }
    }
    
    private static String parseColor(String colorStr) {
        try {
            return normalizeColor(colorStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid color format: " + colorStr, e);
        }
    }
    
    private static double parseFactor(String factorStr) {
        try {
            double factor = Double.parseDouble(factorStr);
            if (factor < 0 || factor > 1) {
                throw new IllegalArgumentException("Factor must be between 0 and 1: " + factorStr);
            }
            return factor;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid factor format: " + factorStr, e);
        }
    }
    
    // Calculation methods
    private static double calculateDistance(Point p1, Point p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dz = p2.z - p1.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    private static Map<String, Object> calculateMaterialProperties(Point objPos, Point camPos, Point lightPos, 
                                                                  String baseColor, double fadeFactor, double tempFactor) {
        double camDistance = calculateDistance(objPos, camPos);
        double lightDistance = calculateDistance(objPos, lightPos);
        
        double colorFade = 1 - (camDistance * fadeFactor);
        colorFade = Math.max(0.1, Math.min(1.0, colorFade));
        
        String fadedColor = applyFade(baseColor, colorFade);
        String specularColor = calculateSpecularColor(baseColor, camDistance, tempFactor);
        
        Map<String, Object> results = new LinkedHashMap<>();
        
        // Basic metrics
        results.put("camera_distance", camDistance);
        results.put("light_distance", lightDistance);
        results.put("fade_percentage", colorFade * 100);
        
        // Material properties
        results.put("diffuseColor", fadedColor);
        results.put("ambientCoeff", 0.3 + (camDistance * 0.04));
        results.put("diffuseCoeff", 0.6);
        results.put("specularCoeff", 0.6 - (camDistance * 0.05));
        results.put("specularColor", specularColor);
        results.put("shininess", 64.0 - (camDistance * 5));
        results.put("reflectivity", 0.3 - (camDistance * 0.03));
        
        // Light recommendations
        results.put("light_position", lightPos);
        results.put("linearAttenuation", 0.1);
        results.put("quadraticAttenuation", 0.01);
        results.put("light_intensity", 1.0 / (1 + 0.1 * lightDistance));
        
        return results;
    }
    
    // Output methods
    private static void printToConsole(Point objPos, String baseColor, Point camPos, Point lightPos,
                                     double fadeFactor, double tempFactor, Map<String, Object> results) {
        System.out.println("=" .repeat(60));
        System.out.println("           DEPTH CALCULATOR - RAY TRACER OPTIMIZER");
        System.out.println("=" .repeat(60));
        
        System.out.println("\n INPUT PARAMETERS:");
        System.out.printf("   Object Position: %s\n", objPos);
        System.out.printf("   Base Color: %s\n", baseColor);
        System.out.printf("   Camera Position: %s\n", camPos);
        System.out.printf("   Light Position: %s\n", lightPos);
        System.out.printf("   Fade Factor: %.3f\n", fadeFactor);
        System.out.printf("   Temperature Factor: %.3f\n", tempFactor);
        
        System.out.println("\n CALCULATED METRICS:");
        System.out.printf("   Camera Distance: %.2f units\n", results.get("camera_distance"));
        System.out.printf("   Light Distance: %.2f units\n", results.get("light_distance"));
        System.out.printf("   Color Fade: %.1f%%\n", results.get("fade_percentage"));
        
        System.out.println("\n MATERIAL PROPERTIES:");
        System.out.printf("   diffuseColor = %s\n", results.get("diffuseColor"));
        System.out.printf("   ambientCoeff = %.2f\n", results.get("ambientCoeff"));
        System.out.printf("   diffuseCoeff = %.2f\n", results.get("diffuseCoeff"));
        System.out.printf("   specularCoeff = %.2f\n", results.get("specularCoeff"));
        System.out.printf("   specularColor = %s\n", results.get("specularColor"));
        System.out.printf("   shininess = %.1f\n", results.get("shininess"));
        System.out.printf("   reflectivity = %.2f\n", results.get("reflectivity"));
        
        System.out.println("\n LIGHT RECOMMENDATIONS:");
        System.out.printf("   position = P(%.2f, %.2f, %.2f)\n", 
                         ((Point)results.get("light_position")).x,
                         ((Point)results.get("light_position")).y,
                         ((Point)results.get("light_position")).z);
        System.out.printf("   linearAttenuation = %.2f\n", results.get("linearAttenuation"));
        System.out.printf("   quadraticAttenuation = %.3f\n", results.get("quadraticAttenuation"));
        System.out.printf("   intensity = %.2f\n", results.get("light_intensity"));
        
        System.out.println("\n" + "=" .repeat(60));
    }
    
    private static void writeToFile(String filename, Point objPos, String baseColor, Point camPos, Point lightPos,
                                  double fadeFactor, double tempFactor, Map<String, Object> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            writer.println("# OBJECT DEPTH ANALYSIS - Generated by DepthCalculator");
            writer.println("# Timestamp: " + new Date());
            writer.println("# Input Parameters:");
            writer.println("#   Object Position: " + objPos);
            writer.println("#   Base Color: " + baseColor);
            writer.println("#   Camera Position: " + camPos);
            writer.println("#   Light Position: " + lightPos);
            writer.println("#   Fade Factor: " + fadeFactor);
            writer.println("#   Temperature Factor: " + tempFactor);
            writer.println();
            
            writer.println("CALCULATED_VALUES:");
            writer.printf("camera_distance = %.2f\n", results.get("camera_distance"));
            writer.printf("light_distance = %.2f\n", results.get("light_distance"));
            writer.printf("color_fade = %.1f%%\n", results.get("fade_percentage"));
            writer.println();
            
            writer.println("MATERIAL_PROPERTIES:");
            writer.printf("diffuseColor = %s\n", results.get("diffuseColor"));
            writer.printf("ambientCoeff = %.2f\n", results.get("ambientCoeff"));
            writer.printf("diffuseCoeff = %.2f\n", results.get("diffuseCoeff"));
            writer.printf("specularCoeff = %.2f\n", results.get("specularCoeff"));
            writer.printf("specularColor = %s\n", results.get("specularColor"));
            writer.printf("shininess = %.1f\n", results.get("shininess"));
            writer.printf("reflectivity = %.2f\n", results.get("reflectivity"));
            writer.println();
            
            writer.println("LIGHT_RECOMMENDATIONS:");
            Point lightPosResult = (Point) results.get("light_position");
            writer.printf("position = P(%.2f, %.2f, %.2f)\n", lightPosResult.x, lightPosResult.y, lightPosResult.z);
            writer.printf("linearAttenuation = %.2f\n", results.get("linearAttenuation"));
            writer.printf("quadraticAttenuation = %.3f\n", results.get("quadraticAttenuation"));
            writer.printf("intensity = %.2f\n", results.get("light_intensity"));
        }
    }
    
    public static void main(String[] args) {
        // Show usage if no arguments provided
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        // Show usage if help is requested
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help") || args[0].equals("help"))) {
            printUsage();
            return;
        }
        
        try {
            if (args.length != 6) {
                throw new IllegalArgumentException("Error: Exactly 6 arguments required. Use -h for help.");
            }
            
            // Parse all arguments
            Point objPos = parseCoordinate(args[0]);
            String baseColor = parseColor(args[1]);
            Point camPos = parseCoordinate(args[2]);
            Point lightPos = parseCoordinate(args[3]);
            double fadeFactor = parseFactor(args[4]);
            double tempFactor = parseFactor(args[5]);
            
            // Calculate material properties
            Map<String, Object> results = calculateMaterialProperties(objPos, camPos, lightPos, baseColor, fadeFactor, tempFactor);
            
            // Output results
            printToConsole(objPos, baseColor, camPos, lightPos, fadeFactor, tempFactor, results);
            writeToFile("objDepthInfo.txt", objPos, baseColor, camPos, lightPos, fadeFactor, tempFactor, results);
            
            System.out.println("✓ Results saved to: objDepthInfo.txt");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("\nUse: java DepthCalculator -h for usage information");
            System.exit(1);
        }
    }
    
}
