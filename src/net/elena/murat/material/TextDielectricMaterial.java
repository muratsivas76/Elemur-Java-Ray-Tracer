package net.elena.murat.material;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import net.elena.murat.math.*;
import net.elena.murat.light.Light;
import net.elena.murat.util.ColorUtil;

/**
 * TextDielectricMaterial - Combines text rendering with dielectric material properties
 * Creates transparent glass-like text on sphere with refraction and reflection effects
 */
public class TextDielectricMaterial implements Material {
  
  // Text properties
  private final String word;
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
  
  // Dielectric properties
  private Color diffuseColor;
  private double indexOfRefraction;
  private double transparency;
  private double reflectivity;
  private Color filterColorInside;
  private Color filterColorOutside;
  private Matrix4 objectTransform;
  private final Random random;
  private double currentReflectivity;
  private double currentTransparency;
  
  public TextDielectricMaterial(String word, Color textColor, Color gradientColor,
    String gradientType, Color bgColor,
    String fontFamily, int fontStyle, int fontSize,
    int uOffset, int vOffset,
    BufferedImage imageObject, int imageWidth, int imageHeight,
    int imageUOffset, int imageVOffset,
    Color diffuseColor, double ior, double transparency, double reflectivity,
    Color filterColorInside, Color filterColorOutside) {
    
    // Text properties
    this.word = convertToNorwegianText(word).replaceAll("_", " ");
    this.textColor = textColor;
    this.gradientColor = gradientColor;
    this.gradientType = gradientType != null ? gradientType : "horizontal";
    this.bgColor = bgColor;
    this.fontFamily = fontFamily.replaceAll("_", " ");
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    this.imageObject = imageObject;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.imageUOffset = imageUOffset;
    this.imageVOffset = imageVOffset;
    
    // Dielectric properties
    this.diffuseColor = diffuseColor;
    this.indexOfRefraction = ior;
    this.transparency = transparency;
    this.reflectivity = reflectivity;
    this.filterColorInside = filterColorInside;
    this.filterColorOutside = filterColorOutside;
    
    this.random = new Random();
    this.currentReflectivity = reflectivity;
    this.currentTransparency = transparency;
    this.objectTransform = new Matrix4().identity();
    
    this.texture = createTexture();
  }
  
  public TextDielectricMaterial(String word, Color textColor,
    String fontFamily, int fontStyle, int fontSize) {
    this(word, textColor, null, "horizontal", new Color(0x00000000),
      fontFamily, fontStyle, fontSize, 0, 0,
      null, 0, 0, 0, 0,
      new Color(0.9f, 0.9f, 0.9f), 1.5, 0.8, 0.1,
    new Color(1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f));
  }
  
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
    
    if (imageObject != null) {
      int imgX = ((size - imageWidth) / 2) + imageUOffset;
      int imgY = ((size - imageHeight) / 2) + imageVOffset;
      g2d.drawImage(imageObject, imgX, imgY, imageWidth, imageHeight, null);
    }
    
    Font font;
    try {
      font = new Font(fontFamily, fontStyle, fontSize);
      } catch (Exception e) {
      font = new Font("Arial", fontStyle, fontSize);
    }
    g2d.setFont(font);
    
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(word);
    int textHeight = fm.getHeight();
    int ascent = fm.getAscent();
    
    int x = ((size - textWidth) / 2) + uOffset;
    int y = ((size - textHeight) / 2) + (ascent * 2) + (textHeight / 3) + vOffset;
    
    if (gradientColor != null) {
      GradientPaint gradient = createGradient(x, y - ascent, textWidth, textHeight);
      g2d.setPaint(gradient);
      } else {
      g2d.setColor(textColor);
    }
    
    g2d.drawString(word, x, y);
    g2d.dispose();
    
    return texture;
  }
  
  private GradientPaint createGradient(float x, float y, float width, float height) {
    switch (gradientType.toLowerCase()) {
      case "vertical":
        return new GradientPaint(x, y, textColor, x, y + height/2, gradientColor, true);
      case "diagonal":
        return new GradientPaint(x, y, textColor, x + width/3, y + height/5, gradientColor, true);
      case "horizontal":
      default:
        return new GradientPaint(x, y, textColor, x + width/3, y, gradientColor, true);
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
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPoint) {
    // Get texture color
    Point3 localPoint = objectTransform.inverse().transformPoint(point);
    Color textureColor = getTextureColor(localPoint, normal);
    
    // Lighting calculations
    Vector3 lightDir = light.getDirectionTo(point).normalize();
    double diffuseFactor = Math.max(0.4, normal.dot(lightDir));
    
    // Fresnel effect for dynamic properties
    Vector3 viewDir = viewerPoint.subtract(point).normalize();
    double fresnel = Vector3.calculateFresnel(viewDir, normal, 1.0, indexOfRefraction);
    
    // Optimized reflection and transparency with better balance
    this.currentReflectivity = Math.min(0.85, reflectivity + (fresnel * 0.7));
    this.currentTransparency = Math.max(0.1, transparency * (1.0 - fresnel * 0.15));
    
    // Enhanced color processing
    Color boostedTexture = enhanceColorQuality(textureColor, 1.15f);
    Color diffuse = ColorUtil.multiplyColor(boostedTexture, diffuseFactor * light.getIntensity());
    
    // Improved specular with better glass appearance
    Vector3 reflectDir = lightDir.reflect(normal);
    double specularFactor = Math.pow(Math.max(0, viewDir.dot(reflectDir)), 140);
    
    Color glassSpecular = new Color(0.94f, 0.96f, 0.98f);
    Color tintedSpecular = ColorUtil.multiplyColors(light.getColor(), glassSpecular);
    Color specular = ColorUtil.multiplyColor(tintedSpecular, specularFactor * 1.6 * light.getIntensity());
    
    // Combine components
    Color result = ColorUtil.add(diffuse, specular);
    
    // Enhanced filter color application - fixed green edge issue
    if (fresnel > 0.12) {
      Color filterBlend = applyAdvancedColorFilter(filterColorInside, filterColorOutside, fresnel);
      result = blendColorsWithCompensation(result, filterBlend, fresnel * 0.6);
    }
    
    return ColorUtil.clampColor(result);
  }
  
  // Improved color enhancement without green edges
  private Color enhanceColorQuality(Color color, float saturationFactor) {
    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    // Careful saturation boost to avoid color shifts
    hsb[1] = Math.min(0.9f, hsb[1] * saturationFactor);
    return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
  }
  
  // Advanced filter color blending to prevent green edges
  private Color applyAdvancedColorFilter(Color inside, Color outside, double fresnel) {
    double blendFactor = Math.min(1.0, fresnel * 1.3);
    
    float r = (float) (inside.getRed() * (1 - blendFactor) + outside.getRed() * blendFactor) / 255f;
    float g = (float) (inside.getGreen() * (1 - blendFactor) + outside.getGreen() * blendFactor) / 255f;
    float b = (float) (inside.getBlue() * (1 - blendFactor) + outside.getBlue() * blendFactor) / 255f;
    
    return new Color(r, g, b);
  }
  
  // Safe color blending with compensation
  private Color blendColorsWithCompensation(Color base, Color filter, double strength) {
    float baseR = base.getRed() / 255f;
    float baseG = base.getGreen() / 255f;
    float baseB = base.getBlue() / 255f;
    
    float filterR = filter.getRed() / 255f;
    float filterG = filter.getGreen() / 255f;
    float filterB = filter.getBlue() / 255f;
    
    // Balanced blending to prevent color artifacts
    float blend = (float) Math.min(0.8, strength);
    float finalR = baseR * (1 - blend * 0.4f) + filterR * blend * 0.6f;
    float finalG = baseG * (1 - blend * 0.4f) + filterG * blend * 0.6f;
    float finalB = baseB * (1 - blend * 0.4f) + filterB * blend * 0.6f;
    
    return new Color(finalR, finalG, finalB);
  }
  
  private Color getTextureColor(Point3 localPoint, Vector3 worldNormal) {
    if (texture == null) return textColor;
    
    Vector3 dir = worldNormal.normalize();
    
    double phi = Math.atan2(dir.z, dir.x);
    double theta = Math.asin(dir.y);
    
    double u = 1.0 - (phi + Math.PI) / (2 * Math.PI);
    double v = (theta + Math.PI / 2) / Math.PI;
    v = 1.0 - v;
    
    u = (u + 0.25) % 1.0;
    
    int texX = (int) (u * texture.getWidth());
    texX = texX % texture.getWidth();
    if (texX < 0) texX += texture.getWidth();
    
    int texY = (int) (v * texture.getHeight());
    if (texY < 0 || texY >= texture.getHeight()) {
      return new Color(0, 0, 0, 0);
    }
    
    return new Color(texture.getRGB(texX, texY), true);
  }
  
  @Override
  public void setObjectTransform(Matrix4 tm) {
    this.objectTransform = (tm != null) ? tm : new Matrix4();
  }
  
  @Override
  public double getIndexOfRefraction() {
    return indexOfRefraction;
  }
  
  @Override
  public double getTransparency() {
    return currentTransparency;
  }
  
  @Override
  public double getReflectivity() {
    return currentReflectivity;
  }
  
  public Color getFilterColorInside() { return filterColorInside; }
  public Color getFilterColorOutside() { return filterColorOutside; }
  
  public void setFilterColorInside(Color filterInside) {
    this.filterColorInside = filterInside;
  }
  
  public void setFilterColorOutside(Color filterOutside) {
    this.filterColorOutside = filterOutside;
  }
  
  public Color getDiffuseColor() { return diffuseColor; }
  public void setDiffuseColor(Color color) { this.diffuseColor = color; }
  
  public void setIndexOfRefraction(double ior) { this.indexOfRefraction = ior; }
  public void setTransparency(double transparency) { this.transparency = transparency; }
  public void setReflectivity(double reflectivity) { this.reflectivity = reflectivity; }
  
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
    sb.append("TextDielectricMaterial textdielectricmaterial {\n");
    sb.append("        imagePath = " + getImagePath() + ";\n");
    sb.append("        word = " + word + ";\n");
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
    sb.append("        diffuseColor = " + net.elena.murat.util.ColorUtil.toColorString(diffuseColor) + ";\n");
    sb.append("        ior = " + indexOfRefraction + ";\n");
    sb.append("        transparency = " + currentTransparency + ";\n");
    sb.append("        reflectivity = " + currentReflectivity + ";\n");
    sb.append("        filterColorInside = " + net.elena.murat.util.ColorUtil.toColorString(filterColorInside) + ";\n");
    sb.append("        filterColorOutside = " + net.elena.murat.util.ColorUtil.toColorString(filterColorOutside) + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
