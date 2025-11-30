package net.elena.murat.material;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.elena.murat.light.Light;
import net.elena.murat.math.*;

public class TextureMaterial implements Material {
  private final BufferedImage texture;
  private final int width;
  private final int height;
  private final boolean isTile;
  
  private double transparency = 1.0;
  private Matrix4 objectTransform = Matrix4.identity();
  
  public TextureMaterial(BufferedImage texture) {
    this(texture, true);
  }
  
  public TextureMaterial(BufferedImage texture, boolean isTile) {
    if (texture == null) {
      throw new IllegalArgumentException("Texture cannot be null");
    }
    this.texture = texture;
    this.width = texture.getWidth();
    this.height = texture.getHeight();
    this.isTile = isTile;
  }
  
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
    if (texture == null) {
      transparency = 1.0;
      return new Color(0, 0, 0, 0);
    }
    
    double u, v;
    
    if (isTile) {
      // Tile mode
      u = clamp(point.x - Math.floor(point.x));
      v = 1.0 - clamp(point.y - Math.floor(point.y));
      } else {
      Point3 localPoint = objectTransform.transformPoint(point);// Original line
      u = clamp((localPoint.x + 1.0) / 2.0); // Original line
      v = 1.0 - clamp((localPoint.y + 1.0) / 2.0); // Original line
    }
    
    // Calculate pixel position
    int x = (int)(u * (width - 1));
    int y = (int)(v * (height - 1));
    
    // Get raw ARGB value
    int argb = texture.getRGB(x, y);
    int alpha = (argb >> 24) & 0xFF;
    
    if (alpha > 5) {
      int red = (argb >> 16) & 0xFF;
      int green = (argb >> 8) & 0xFF;
      int blue = argb & 0xFF;
      transparency = 0.0;
      return new Color(red, green, blue, 255);
    }
    
    transparency = 1.0;
    return new Color(0, 0, 0, 0);
  }
  
  private double clamp(double value) {
    return Math.max(0.0, Math.min(1.0, value));
  }
  
  @Override
  public double getReflectivity() {
    return 0.0;
  }
  
  @Override
  public double getIndexOfRefraction() {
    return 1.0;
  }
  
  @Override
  public double getTransparency() {
    return transparency;
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    this.objectTransform = tm;
  }
  
  public boolean isTile() {
    return isTile;
  }
  
  private String imagePath = "textures/turkeyFlag.png";
  public void setImagePath(String npath) {
    this.imagePath = npath;
  }
  
  public String getImagePath() {
    return this.imagePath;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TextureMaterial texturematerial {\n");
    sb.append("        imagePath = " + getImagePath() + ";\n");
    sb.append("        isTile = " + isTile + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
