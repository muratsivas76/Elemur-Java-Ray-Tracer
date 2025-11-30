// Murat Inan
package net.elena.murat.gui;

import java.util.*;
import java.io.*;

// Custom
import net.elena.murat.math.Point3;

public class DepthCalculator {
  
    // Display usage information
    private static void printUsage() {
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
    private static Point3 parseCoordinate(String coordStr) {
        try {
            String clean = coordStr.trim();
            String[] parts = clean.split(",");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Coordinate must have exactly 3 values: " + coordStr);
            }
            
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            
            return new Point3(x, y, z);
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
    private static double calculateDistance(Point3 p1, Point3 p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dz = p2.z - p1.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public static Map<String, Object> calculateMaterialProperties(
              Point3 objPos, Point3 camPos, Point3 lightPos, 
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
    private static void printToConsole(Point3 objPos, String baseColor, Point3 camPos, Point3 lightPos,
                                     double fadeFactor, double tempFactor, Map<String, Object> results) {
    }
    
    private static void writeToFile(String filename, Point3 objPos, String baseColor, Point3 camPos, Point3 lightPos,
                                  double fadeFactor, double tempFactor, Map<String, Object> results) throws IOException {
    }
    
    public static String generateOutputString(
                   String shapeName, 
                   Point3 objPos, 
                   String baseColor, 
                   Point3 camPos, 
                   Point3 lightPos,
                   double fadeFactor, 
                   double tempFactor, 
                   Map<String, Object> results) {
    StringBuilder html = new StringBuilder();
    
    html.append("<html><body style='font-family: Arial, sans-serif; margin: 10px;'>");
    
    // Header - border-radius olmadan
    html.append("<div style='background: #2c3e50; color: white; padding: 15px; margin-bottom: 15px;'>");
    html.append("<h1 style='margin: 0; font-size: 20px;'>DEPTH CALCULATOR ANALYSIS</h1>");
    html.append("<p style='margin: 5px 0 0 0; font-size: 12px;'>Generated by DepthCalculator • ").append(new Date()).append("</p>");
    html.append("</div>");
    
    // Shape Info - border-left ile basit border
    html.append("<div style='background: white; padding: 12px; border-left: 4px solid #3498db; margin-bottom: 15px;'>");
    html.append("<h2 style='margin: 0 0 8px 0; color: #333; font-size: 16px;'>SHAPE INFORMATION</h2>");
    html.append("<p style='margin: 0; color: #3498db; font-size: 14px; font-weight: bold;'>").append(shapeName).append("</p>");
    html.append("</div>");
    
    // Input Parameters
    html.append("<div style='background: white; padding: 12px; border-left: 4px solid #27ae60; margin-bottom: 15px;'>");
    html.append("<h2 style='margin: 0 0 10px 0; color: #333; font-size: 17px;'>INPUT PARAMETERS</h2>");
    html.append("<table style='width: 100%; border-collapse: collapse; font-size: 16px;'>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555; width: 140px;'>Object Position:</td><td style='padding: 4px;'>").append(objPos).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Base Color:</td><td style='padding: 4px;'><span style='color: ").append(baseColor).append("; font-weight: bold;'>").append(baseColor).append("</span></td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Camera Position:</td><td style='padding: 4px;'>").append(camPos).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Light Position:</td><td style='padding: 4px;'>").append(lightPos).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Fade Factor:</td><td style='padding: 4px;'>").append(fadeFactor).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Temp Factor:</td><td style='padding: 4px;'>").append(tempFactor).append("</td></tr>");
    html.append("</table>");
    html.append("</div>");
    
    // Calculated Values
    html.append("<div style='background: white; padding: 12px; border-left: 4px solid #e74c3c; margin-bottom: 15px;'>");
    html.append("<h2 style='margin: 0 0 10px 0; color: #333; font-size: 16px;'>CALCULATED VALUES</h2>");
    html.append("<table style='width: 100%; border-collapse: collapse; font-size: 16px;'>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Camera Distance:</td><td style='padding: 4px;'>").append(String.format("%.2f units", results.get("camera_distance"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Light Distance:</td><td style='padding: 4px;'>").append(String.format("%.2f units", results.get("light_distance"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>Color Fade:</td><td style='padding: 4px;'>").append(String.format("%.1f%%", results.get("fade_percentage"))).append("</td></tr>");
    html.append("</table>");
    html.append("</div>");
    
    // Material Properties
    html.append("<div style='background: white; padding: 12px; border-left: 4px solid #9b59b6; margin-bottom: 15px;'>");
    html.append("<h2 style='margin: 0 0 10px 0; color: #333; font-size: 16px;'>MATERIAL PROPERTIES</h2>");
    html.append("<table style='width: 100%; border-collapse: collapse; font-size: 16px;'>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>diffuseColor:</td><td style='padding: 4px;'><span style='color: ").append(results.get("diffuseColor")).append("; font-weight: bold;'>").append(results.get("diffuseColor")).append("</span></td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>ambientCoeff:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("ambientCoeff"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>diffuseCoeff:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("diffuseCoeff"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>specularCoeff:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("specularCoeff"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>specularColor:</td><td style='padding: 4px;'><span style='color: ").append(results.get("specularColor")).append("; font-weight: bold;'>").append(results.get("specularColor")).append("</span></td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>shininess:</td><td style='padding: 4px;'>").append(String.format("%.1f", results.get("shininess"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>reflectivity:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("reflectivity"))).append("</td></tr>");
    html.append("</table>");
    html.append("</div>");
    
    // Light Recommendations
    html.append("<div style='background: white; padding: 12px; border-left: 4px solid #f39c12;'>");
    html.append("<h2 style='margin: 0 0 10px 0; color: #333; font-size: 16px;'>LIGHT RECOMMENDATIONS</h2>");
    html.append("<table style='width: 100%; border-collapse: collapse; font-size: 16px;'>");
    Point3 lightPosResult = (Point3) results.get("light_position");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>position:</td><td style='padding: 4px;'>P(")
        .append(String.format("%.2f, %.2f, %.2f", lightPosResult.x, lightPosResult.y, lightPosResult.z)).append(")</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>linearAttenuation:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("linearAttenuation"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>quadraticAttenuation:</td><td style='padding: 4px;'>").append(String.format("%.3f", results.get("quadraticAttenuation"))).append("</td></tr>");
    html.append("<tr><td style='padding: 4px; font-weight: bold; color: #555;'>intensity:</td><td style='padding: 4px;'>").append(String.format("%.2f", results.get("light_intensity"))).append("</td></tr>");
    html.append("</table>");
    html.append("</div>");
    
    html.append("</body></html>");
    return html.toString();
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
            Point3 objPos = parseCoordinate(args[0]);
            String baseColor = parseColor(args[1]);
            Point3 camPos = parseCoordinate(args[2]);
            Point3 lightPos = parseCoordinate(args[3]);
            double fadeFactor = parseFactor(args[4]);
            double tempFactor = parseFactor(args[5]);
            
            // Calculate material properties
            Map<String, Object> results = calculateMaterialProperties(
                    objPos, camPos, lightPos, 
                    baseColor, fadeFactor, tempFactor);
            
            // Output results
            //printToConsole(objPos, baseColor, camPos, lightPos, fadeFactor, tempFactor, results);
            //writeToFile("objDepthInfo.txt", objPos, baseColor, camPos, lightPos, fadeFactor, tempFactor, results);
            
            //System.out.println(" Results saved to: objDepthInfo.txt");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("\nUse: java DepthCalculator -h for usage information");
            System.exit(1);
        }
    }
    
}
