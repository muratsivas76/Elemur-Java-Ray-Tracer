package net.elena.murat.material;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import net.elena.murat.light.Light;
import net.elena.murat.light.LightProperties;
import net.elena.murat.math.*;
import net.elena.murat.util.ColorUtil;

/**
 * IsotropicMetalTextMaterial - Combines isotropic metal properties with text/image rendering
 * Creates metallic surfaces with embedded text or images
 */
public class IsotropicMetalTextMaterial implements Material {
  
  // Metal properties
  private final Color metalColor;
  private final double roughness;
  private final double reflectivity;
  private final double ior;
  private final double transparency;
  private Matrix4 objectInverseTransform;
  
  // Text/Image properties
  private final String text;
  private final Color textColor;
  private final Color gradientColor;
  private final String gradientType;
  private final Color bgColor;
  private final String fontFamily;
  private final int fontStyle;
  private final int fontSize;
  private final int uOffset;
  private final int vOffset;
  private final BufferedImage imageObject;
  private final int imageWidth;
  private final int imageHeight;
  private final int imageUOffset;
  private final int imageVOffset;
  private BufferedImage texture;
  
  // Phong constants
  private final double ambientCoefficient = 0.15;
  private final double diffuseCoefficient = 0.4;
  private final double specularCoefficient = 0.8;
  private final double shininess = 100.0;
  
  // Default constructor
  public IsotropicMetalTextMaterial() {
    this(new Color(179, 179, 191),   // Silver-gray metal
      0.2,                        // Roughness (isotropic - same in all directions)
      0.7,                        // Reflectivity
      1.0,                        // IOR
      0.0,                        // Transparency
      new Matrix4(),              // Identity transform
      "Hello",                    // Default text
      Color.WHITE,                // Text color
      null,                       // No gradient
      "horizontal",               // Gradient type
      new Color(0, 0, 0, 0),      // Transparent background
      "Arial",                    // Font family
      Font.BOLD,                  // Font style
      48,                         // Font size
      0, 0,                       // UV offsets
    null, 0, 0, 0, 0);         // No image
  }
  
  // Text-only constructor
  public IsotropicMetalTextMaterial(Color metalColor, double roughness, double reflectivity,
    String text, Color textColor, String fontFamily,
    int fontStyle, int fontSize) {
    this(metalColor, roughness, reflectivity, 1.0, 0.0, new Matrix4(),
      text, textColor, null, "horizontal", new Color(0, 0, 0, 0),
    fontFamily, fontStyle, fontSize, 0, 0, null, 0, 0, 0, 0);
  }
  
  // Full constructor
  public IsotropicMetalTextMaterial(Color metalColor, double roughness, double reflectivity,
    double ior, double transparency, Matrix4 objectInverseTransform,
    String text, Color textColor, Color gradientColor,
    String gradientType, Color bgColor, String fontFamily,
    int fontStyle, int fontSize, int uOffset, int vOffset,
    BufferedImage imageObject, int imageWidth, int imageHeight,
    int imageUOffset, int imageVOffset) {
    
    // Metal properties
    this.metalColor = metalColor;
    this.roughness = Math.max(0.01, Math.min(1.0, roughness));
    this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
    this.ior = ior;
    this.transparency = Math.max(0.0, Math.min(1.0, transparency));
    this.objectInverseTransform = objectInverseTransform;
    
    // Text/Image properties
    this.text = convertToNorwegianText(text != null ? text.replaceAll("_", " ") : "");
    this.textColor = textColor;
    this.gradientColor = gradientColor;
    this.gradientType = gradientType != null ? gradientType : "horizontal";
    this.bgColor = bgColor;
    this.fontFamily = fontFamily != null ? fontFamily.replaceAll("_", " ") : "Arial";
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    this.imageObject = imageObject;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.imageUOffset = imageUOffset;
    this.imageVOffset = imageVOffset;
    
    this.texture = createTexture();
  }
  
  /**
   * Creates the texture image with text and/or image
   */
  private BufferedImage createTexture() {
    final int size = 1024;
    BufferedImage texture = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = texture.createGraphics();
    
    g2d.setComposite(AlphaComposite.Clear);
    g2d.fillRect(0, 0, size, size);
    
    // Setup rendering quality
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    
    // Clear with background color
    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, size, size);
    
    // Draw background image if provided
    if (imageObject != null) {
      int imgX = ((size - imageWidth) / 2) + imageUOffset;
      int imgY = ((size - imageHeight) / 2) + imageVOffset;
      g2d.drawImage(imageObject, imgX, imgY, imageWidth, imageHeight, null);
    }
    
    // Draw text if provided
    if (text != null && !text.isEmpty()) {
      Font font;
      try {
        font = new Font(fontFamily, fontStyle, fontSize);
        } catch (Exception e) {
        font = new Font("Arial", fontStyle, fontSize); // Fallback
      }
      g2d.setFont(font);
      
      FontMetrics fm = g2d.getFontMetrics();
      int textWidth = fm.stringWidth(text);
      int textHeight = fm.getHeight();
      int ascent = fm.getAscent();
      
      int x = ((size - textWidth) / 2) + uOffset;
      int y = ((size - textHeight) / 2) + ascent + vOffset;
      
      // Apply gradient or solid color
      if (gradientColor != null) {
        GradientPaint gradient = createGradient(x, y - ascent, textWidth, textHeight);
        g2d.setPaint(gradient);
        } else {
        g2d.setColor(textColor);
      }
      
      g2d.drawString(text, x, y);
    }
    
    g2d.dispose();
    return texture;
  }
  
  private GradientPaint createGradient(float x, float y, float width, float height) {
    switch (gradientType.toLowerCase()) {
      case "vertical":
        return new GradientPaint(x, y, textColor, x, y + height, gradientColor, true);
      case "diagonal":
        return new GradientPaint(x, y, textColor, x + width, y + height, gradientColor, true);
      case "horizontal":
      default:
        return new GradientPaint(x, y, textColor, x + width, y, gradientColor, true);
    }
  }
  
  public static String convertToNorwegianText(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    
    String result = input;
    result = result.replace("AE", "\u00C6");
    result = result.replace("O/", "\u00D8");
    result = result.replace("A0", "\u00C5");
    result = result.replace("ae", "\u00E6");
    result = result.replace("o/", "\u00F8");
    result = result.replace("a0", "\u00E5");
    
    return result;
  }
  
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, Light light, Point3 viewerPos) {
    LightProperties lightProps = LightProperties.getLightProperties(light, worldPoint);
    Vector3 lightDir = lightProps.direction;
    Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
    
    // Get texture color with alpha
    Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
    Color textureColor = getTextureColor(localPoint, worldNormal);
    double textureAlpha = textureColor.getAlpha() / 255.0;
    
    // If there's significant texture, just return it with simple lighting
    if (textureAlpha > 0.3) {
      // Just apply basic lighting to texture - keep it vibrant
      double brightness = Math.max(0.4, Math.min(1.0, 0.6 + 0.4 * Math.max(0, worldNormal.dot(lightDir))));
      int r = (int)(textureColor.getRed() * brightness);
      int g = (int)(textureColor.getGreen() * brightness);
      int b = (int)(textureColor.getBlue() * brightness);
      return new Color(
        Math.max(0, Math.min(255, r)),
        Math.max(0, Math.min(255, g)),
        Math.max(0, Math.min(255, b))
      );
    }
    
    // Otherwise do full metallic shading
    double NdotL = Math.max(0, worldNormal.dot(lightDir));
    
    // Diffuse component
    Color diffuse = ColorUtil.multiplyColors(
      metalColor,
      lightProps.color,
      diffuseCoefficient * NdotL * lightProps.intensity
    );
    
    // Specular - isotropic metal reflection
    Vector3 reflectDir = lightDir.reflect(worldNormal);
    double RdotV = Math.max(0, viewDir.dot(reflectDir));
    double specularIntensity = Math.pow(RdotV, shininess * (1.0 - roughness));
    
    Color specular = ColorUtil.multiplyColors(
      new Color(240, 240, 250),
      lightProps.color,
      specularCoefficient * specularIntensity * lightProps.intensity
    );
    
    // Ambient component
    Color ambient = ColorUtil.multiplyColors(
      metalColor,
      lightProps.color,
      ambientCoefficient * lightProps.intensity
    );
    
    // Combine all lighting components
    Color finalColor = ColorUtil.add(ColorUtil.add(ambient, diffuse), specular);
    return ColorUtil.clampColor(finalColor);
  }
  
  private Color getTextureColor(Point3 localPoint, Vector3 worldNormal) {
    if (texture == null) return textColor;
    
    // Spherical mapping
    Vector3 dir = worldNormal.normalize();
    double phi = Math.atan2(dir.z, dir.x);
    double theta = Math.asin(dir.y);
    
    double u = 1.0 - (phi + Math.PI) / (2 * Math.PI);
    double v = (theta + Math.PI / 2) / Math.PI;
    v = 1.0 - v;
    
    u = (u + 0.25) % 1.0; // Offset for better alignment
    
    int texX = (int) (u * texture.getWidth());
    texX = Math.max(0, Math.min(texture.getWidth() - 1, texX));
    
    int texY = (int) (v * texture.getHeight());
    texY = Math.max(0, Math.min(texture.getHeight() - 1, texY));
    
    return new Color(texture.getRGB(texX, texY), true);
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4();
    this.objectInverseTransform = tm;
  }
  
  @Override
  public double getReflectivity() {
    return reflectivity;
  }
  
  @Override
  public double getIndexOfRefraction() {
    return ior;
  }
  
  @Override
  public double getTransparency() {
    return transparency;
  }
  
  // Getters
  public Color getMetalColor() { return metalColor; }
  public double getRoughness() { return roughness; }
  public String getText() { return text; }
  public Color getTextColor() { return textColor; }
  
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
    sb.append("IsotropicMetalTextMaterial isotropicmetaltextmaterial {\n");
    sb.append("        imagePath = " + getImagePath() + ";\n");
    sb.append("        metalColor = " + net.elena.murat.util.ColorUtil.toColorString(metalColor) + ";\n");
    sb.append("        roughness = " + roughness + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("        ior = " + ior + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
    sb.append("        text = " + text + ";\n");
    sb.append("        textColor = " + net.elena.murat.util.ColorUtil.toColorString(textColor) + ";\n");
    sb.append("        gradientColor = " + net.elena.murat.util.ColorUtil.toColorString(gradientColor) + ";\n");
    sb.append("        gradientType = " + gradientType + ";\n");
    sb.append("        bgColor = " + net.elena.murat.util.ColorUtil.toColorString(bgColor) + ";\n");
    sb.append("        fontFamily = " + fontFamily + ";\n");
    sb.append("        fontStyle = " + fontStyle + ";\n");
    sb.append("        fontSize = " + fontSize + ";\n");
    sb.append("        uOffset = " + uOffset + ";\n");
    sb.append("        vOffset = " + vOffset + ";\n");
    sb.append("        imageWidth = " + imageWidth + ";\n");
    sb.append("        imageHeight = " + imageHeight + ";\n");
    sb.append("        imageUOffset = " + imageUOffset + ";\n");
    sb.append("        imageVOffset = " + imageVOffset + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
