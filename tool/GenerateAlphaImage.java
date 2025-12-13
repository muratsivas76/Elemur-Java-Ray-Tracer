import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GenerateAlphaImage {
  
  private int width = 512;
  private int height = 512;
  private Color bgColor = Color.WHITE;
  private boolean border = false;
  private Color borderColor = Color.RED;
  private String imagePath = null;
  private int imageX = 0, imageY = 0, imageW = 0, imageH = 0;
  private Color fg1 = Color.RED;
  private Color fg2 = Color.GREEN;
  private String fontName = "Arial";
  private int fontStyle = Font.PLAIN;
  private int fontSize = 70;
  private int fg1x = 0;
  private int fg1y = 0;
  private int fg2x = 40;
  private int fg2y = 10;
  private boolean shadow = false;
  private Color shadowColor = new Color(0, 0, 0, 128); // Default semi-transparent black
  private float shadowInc = 0.1f;
  private int shadowDepth = 20;
  private String text = "Word";
  private int textX = 200, textY = 200;
  private String outputFileName = "merhaba.png";
  private java.util.List<AlphaComposite> alphaComposites = new ArrayList<>();
  
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java GenerateAlphaImage <config-file>");
      System.out.println("Example: java GenerateAlphaImage infoImage.txt");
      return;
    }
    
    String configFile = args[0];
    GenerateAlphaImage generator = new GenerateAlphaImage();
    generator.loadConfig(configFile);
    generator.generateImage();
  }
  
  private void loadConfig(String filename) {
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      int compositeIndex = 0;
      
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;
        if (line.startsWith("END") || line.startsWith("End") || line.startsWith("end")) break;
        if (line.startsWith("Width:")) {
          width = Integer.parseInt(line.substring(6).trim());
          } else if (line.startsWith("Height:")) {
          height = Integer.parseInt(line.substring(7).trim());
          } else if (line.startsWith("Bg argb:")) {
          bgColor = parseColor(line.substring(8).trim());
          } else if (line.startsWith("Border:")) {
          border = line.substring(7).trim().equalsIgnoreCase("yes");
          } else if (line.startsWith("Border color argb:")) {
          borderColor = parseColor(line.substring(18).trim());
          } else if (line.startsWith("Alpha composite:")) {
          String[] parts = line.substring(16).trim().split(",");
          int rule = Integer.parseInt(parts[0].trim());
          float alpha = Float.parseFloat(parts[1].replace("f", "").trim());
          alphaComposites.add(AlphaComposite.getInstance(rule, alpha));
          } else if (line.startsWith("Image path,x,y,w,h:")) {
          parseImagePath(line.substring(19).trim());
          } else if (line.startsWith("Fg1 argb:")) {
          fg1 = parseColor(line.substring(9).trim());
          } else if (line.startsWith("Fg2 argb:")) {
          fg2 = parseColor(line.substring(9).trim());
          } else if (line.startsWith("Fg1 xy:")) {
          int[] fvals = parseVals(line.substring(7).trim());
          fg1x = fvals[0];
          fg1y = fvals[1];
          } else if (line.startsWith("Fg2 xy:")) {
          int[] fvals = parseVals(line.substring(7).trim());
          fg2x = fvals[0];
          fg2y = fvals[1];
          } else if (line.startsWith("Font:")) {
          parseFont(line.substring(5).trim());
          } else if (line.startsWith("Shadow:")) {
          shadow = line.substring(7).trim().equalsIgnoreCase("yes");
          } else if (line.startsWith("Shadow color argb:")) {
          shadowColor = parseColor(line.substring(18).trim());
          } else if (line.startsWith("Shadow Inc, depth:")) {
          parseShadow(line.substring(18).trim());
          } else if (line.startsWith("Text x, y:")) {
          parseText(line.substring(10).trim());
          } else if (line.startsWith("Output:")) {
          outputFileName = (line.substring(7)).trim();
          } else {
        }
      }
      } catch (IOException e) {
      System.err.println("Config file read error: " + e.getMessage());
    }
  }
  
  private Color parseColor(String colorStr) {
    if (colorStr.startsWith("#")) {
      long rgba = Long.parseLong(colorStr.substring(1), 16);
      if (colorStr.length() == 9) { // #aarrggbb format
        int a = (int) ((rgba >> 24) & 0xFF);
        int r = (int) ((rgba >> 16) & 0xFF);
        int g = (int) ((rgba >> 8) & 0xFF);
        int b = (int) (rgba & 0xFF);
        return new Color(r, g, b, a);
      }
    }
    return Color.WHITE;
  }
  
  private int[] parseVals(String line) {
    line = line.replaceAll(" ", "");
    String[] valstrs = line.split(",");
    int[] vals = new int[]{0, 0};
    try {
      vals[0] = Integer.parseInt(valstrs[0]);
      vals[1] = Integer.parseInt(valstrs[1]);
      } catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    }
    return vals;
  }
  
  private void parseImagePath(String line) {
    // Format: "../textures/flag.png, 0, 0, 128, 128"
    String[] parts = line.split(",");
    if (parts.length >= 5) {
      imagePath = parts[0].trim().replace("\"", "");
      imageX = Integer.parseInt(parts[1].trim());
      imageY = Integer.parseInt(parts[2].trim());
      imageW = Integer.parseInt(parts[3].trim());
      imageH = Integer.parseInt(parts[4].trim());
    }
  }
  
  private void parseFont(String line) {
    // Format: "Arial, 1, 70"
    String[] parts = line.split(",");
    if (parts.length >= 3) {
      fontName = parts[0].trim().replace("\"", "");
      fontStyle = Integer.parseInt(parts[1].trim());
      fontSize = Integer.parseInt(parts[2].trim());
    }
  }
  
  private void parseShadow(String line) {
    // Format: "0.1f, 20"
    String[] parts = line.split(",");
    if (parts.length >= 2) {
      shadowInc = Float.parseFloat(parts[0].replace("f", "").trim());
      shadowDepth = Integer.parseInt(parts[1].trim());
    }
  }
  
  private void parseText(String line) {
    // Format: "\"Word\", 200, 200"
    String[] parts = line.split(",");
    if (parts.length >= 3) {
      text = parts[0].trim().replace("\"", "");
      textX = Integer.parseInt(parts[1].trim());
      textY = Integer.parseInt(parts[2].trim());
    }
  }
  
  private void generateImage() {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    
    // Anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Clear background
    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, width, height);
    
    // Apply gradient paint
    GradientPaint gradient = new GradientPaint(
      fg1x, fg1y, fg1,
      fg2x, fg2y, fg2,
      true
    );
    
    // Apply alpha composite operations
    if (!alphaComposites.isEmpty()) {
      AlphaComposite composite = alphaComposites.get(0);
      g2d.setComposite(composite);
    }
    
    // Draw border
    if (border) {
      g2d.setColor(borderColor);
      g2d.setStroke(new BasicStroke(2));
      g2d.drawRect(1, 1, width - 3, height - 3);
    }
    
    // Add image
    if (imagePath != null && !imagePath.isEmpty()) {
      try {
        BufferedImage overlay = ImageIO.read(new File(imagePath));
        if (overlay != null) {
          g2d.drawImage(overlay, imageX, imageY, imageW, imageH, null);
        }
        } catch (IOException e) {
        System.err.println("Image load failed: " + imagePath);
      }
    }
    
    // Text and shadow effect
    Font font = new Font(fontName, fontStyle, fontSize);
    g2d.setFont(font);
    
    if (shadow) {
      // Shadow effect
      g2d.setColor(shadowColor);
      
      for (int i = 1; i <= shadowDepth; i++) {
        int offset = (int) (i * shadowInc);
        g2d.drawString(text, textX + offset, textY + offset);
      }
    }
    
    // Main text
    g2d.setPaint(gradient);
    // Apply alpha composite operations
    if (!alphaComposites.isEmpty()) {
      AlphaComposite composite = alphaComposites.get(1);
      g2d.setComposite(composite);
    }
    g2d.drawString(text, textX, textY);
    
    g2d.dispose();
    
    // Save image
    try {
      String outputName = outputFileName;
      ImageIO.write(image, "PNG", new File(outputName));
      System.out.println("Image successfully generated: " + outputName);
      } catch (IOException e) {
      System.err.println("Image save failed: " + e.getMessage());
    }
  }
}
