package net.elena.murat.material;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import net.elena.murat.math.*;

/**
 * Material that applies a spherical texture with a word drawn on it.
 * The word is rendered on a texture mapped onto a sphere.
 * Supports transparency, reflectivity, gradient text, and background image parameters.
 */
public class SphereWordTextureMaterial implements Material {
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
  
  private final double reflectivity;
  private final double ior;
  private double transparency;
  
  private Matrix4 objectInverseTransform;
  private BufferedImage texture;
  
  /**
   * Constructor with default background color (transparent black),
   * reflectivity=0.3, ior=1.0, transparency=0.0 (opaque), no offsets.
   * No gradient (uses solid textColor) and no background image.
   */
  public SphereWordTextureMaterial(String word, Color textColor,
    String fontFamily, int fontStyle, int fontSize) {
    this(word, textColor, null, "horizontal", new Color(0x00000000), fontFamily, fontStyle, fontSize,
    0.3, 1.0, 0.0, 0, 0, null, 0, 0, 0, 0);
  }
  
  /**
   * Full constructor with all parameters.
   *
   * @param word           The word to render on the sphere texture.
   * @param textColor      Primary text color (also used for gradient start/end depending on type).
   * @param gradientColor  Secondary color for gradient effect. If null, solid textColor is used.
   * @param gradientType   Type of gradient: "horizontal", "vertical", "diagonal".
   * @param bgColor        Background color of the texture.
   * @param fontFamily     Font family name.
   * @param fontStyle      Font style (Font.PLAIN, Font.BOLD, etc).
   * @param fontSize       Font size in points.
   * @param reflectivity   Reflectivity coefficient [0..1].
   * @param ior            Index of refraction (>=1.0).
   * @param uOffset        Horizontal pixel offset for text positioning.
   * @param vOffset        Vertical pixel offset for text positioning.
   * @param imageObject    BufferedImage to draw on the background, can be null.
   * @param imageWidth     Width to draw the image.
   * @param imageHeight    Height to draw the image.
   * @param imageUOffset   Horizontal pixel offset for image positioning.
   * @param imageVOffset   Vertical pixel offset for image positioning.
   */
  public SphereWordTextureMaterial(String word, Color textColor, Color gradientColor,
    String gradientType, Color bgColor,
    String fontFamily, int fontStyle, int fontSize,
    double reflectivity, double ior, double transparency,
    int uOffset, int vOffset,
    BufferedImage imageObject, int imageWidth, int imageHeight,
    int imageUOffset, int imageVOffset) {
    // Convert special English sequences to Norwegian characters and replace underscores with spaces
    this.word = (convertToNorwegianText(word)).replaceAll("_", " ");
    this.textColor = textColor;
    this.gradientColor = gradientColor;
    this.gradientType = (gradientType != null) ? gradientType : "horizontal";
    this.bgColor = bgColor;
    this.fontFamily = fontFamily.replaceAll("_", " ");
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    this.reflectivity = Math.min(1.0, Math.max(0.0, reflectivity));
    this.ior = Math.max(1.0, ior);
    this.transparency=transparency;
    
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    this.imageObject = imageObject;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.imageUOffset = imageUOffset;
    this.imageVOffset = imageVOffset;
    
    this.objectInverseTransform = new Matrix4();
    
    this.texture = createTexture();
  }
  
  /**
   * Creates the texture image with the word drawn centered, optionally with a gradient and background image.
   * The texture size is fixed at 1024x1024 pixels.
   * BACKGROUND: Uses user's bgColor with original alpha
   * TEXT & IMAGE: Always fully opaque (alpha=255)
   *
   * @return BufferedImage containing the rendered word texture.
   */
  private BufferedImage createTexture() {
    final int size = 1024;
    BufferedImage texture = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = texture.createGraphics();
    
    // Enable anti-aliasing for smooth text and images
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    // Set background color with USER'S ALPHA (transparency)
    g2d.setBackground(bgColor);
    g2d.clearRect(0, 0, size, size);
    
    // Draw background image if provided - ALWAYS OPAQUE but preserve original colors
    if (imageObject != null) {
      int x = (size - imageWidth) / 2 + imageUOffset;
      int y = (size - imageHeight) / 2 - fontSize / 2;
      y -= (imageHeight/3);
      y += imageVOffset;
      
      // Create a copy of the image with forced opaque alpha but preserved RGB colors
      BufferedImage opaqueImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics2D imgG2d = opaqueImage.createGraphics();
      imgG2d.drawImage(imageObject, 0, 0, imageWidth, imageHeight, null);
      imgG2d.dispose();
      
      // Force all non-transparent pixels to be fully opaque
      for (int i = 0; i < imageWidth; i++) {
        for (int j = 0; j < imageHeight; j++) {
          int rgb = opaqueImage.getRGB(i, j);
          int alpha = (rgb >> 24) & 0xFF;
          if (alpha > 10) { // If not mostly transparent
            int opaqueRGB = (255 << 24) | (rgb & 0x00FFFFFF); // Force alpha to 255
            opaqueImage.setRGB(i, j, opaqueRGB);
          }
        }
      }
      
      g2d.drawImage(opaqueImage, x, y, null);
    }
    
    // Set font
    Font font = new Font(fontFamily, fontStyle, fontSize);
    g2d.setFont(font);
    
    // Calculate text position to center it, applying uOffset and vOffset
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(word);
    int textHeight = fm.getHeight();
    int ascent = fm.getAscent();
    
    int x = (size - textWidth) / 2 + uOffset;
    int y = (size - textHeight) / 2 + (textHeight/3) + ascent + vOffset;
    
    // GradientPaint or solid color - ALWAYS OPAQUE
    if (gradientColor != null && gradientType != null) {
      java.awt.geom.Rectangle2D textBounds = fm.getStringBounds(word, g2d);
      
      float textX = x;
      float textY = y - fm.getAscent();
      
      float ftextWidth = (float) textBounds.getWidth();
      float ftextHeight = (float) textBounds.getHeight();
      
      GradientPaint gradient;
      
      switch (gradientType.toLowerCase()) {
        case "vertical":
        gradient = new GradientPaint(
          textX, textY, makeOpaque(textColor),
          textX, textY + ftextHeight/2, makeOpaque(gradientColor),
          true
        );
        break;
        
        case "diagonal":
        gradient = new GradientPaint(
          textX, textY, makeOpaque(textColor),
          textX + ftextWidth/3, textY + ftextHeight/5, makeOpaque(gradientColor),
          true
        );
        break;
        
        case "horizontal":
        default:
        gradient = new GradientPaint(
          textX, textY, makeOpaque(textColor),
          textX + ftextWidth/3, textY, makeOpaque(gradientColor),
          true
        );
        break;
      }
      
      g2d.setPaint(gradient);
      } else {
      g2d.setColor(makeOpaque(textColor)); // Ensure text is opaque
    }
    
    g2d.drawString(word, x, y);
    g2d.dispose();
    
    return texture;
  }
  
  /**
   * Makes a color fully opaque (alpha=255)
   */
  private Color makeOpaque(Color color) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
  }
  
  /**
   * Retrieves the color from the texture at the given local 3D point on the sphere.
   * TEXT & IMAGE: Always fully opaque
   * BACKGROUND: Uses user's bgColor alpha for transparency
   */
  private Color getTextureColor(Point3 localPoint) {
    if (texture == null) {
      transparency = 1.0;
      return new Color(0, 0, 0, 0);
    }
    
    // Normalize the point to get direction vector on unit sphere
    Vector3 dir = new Vector3(localPoint.x, localPoint.y, localPoint.z).normalize();
    
    // Spherical coordinates
    double phi = Math.atan2(dir.z, dir.x);
    double theta = Math.asin(dir.y);
    
    // Convert spherical coordinates to UV texture coordinates [0..1]
    double u = 1.0 - (phi + Math.PI) / (2 * Math.PI);
    double v = (theta + Math.PI / 2) / Math.PI;
    v = 1.0 - v;
    
    u = (u + 0.25) % 1.0;
    
    int texX = (int) (u * texture.getWidth());
    texX = texX % texture.getWidth();
    if (texX < 0) texX += texture.getWidth();
    
    int texY = (int) (v * texture.getHeight());
    if (texY < 0 || texY >= texture.getHeight()) {
      // Outside bounds - use bgColor with user's alpha
      transparency = bgColor.getAlpha() / 255.0;
      return bgColor;
    }
    
    // Get pixel color from texture
    int argb = texture.getRGB(texX, texY);
    int alpha = (argb >> 24) & 0xFF;
    int red = (argb >> 16) & 0xFF;
    int green = (argb >> 8) & 0xFF;
    int blue = argb & 0xFF;
    
    // If pixel is from background (low alpha), use bgColor transparency
    // If pixel is from text/image (high alpha), force opaque
    int finalAlpha = (alpha < 128) ? bgColor.getAlpha() : 255;
    
    transparency = 1.0 - (finalAlpha / 255.0);
    return new Color(red, green, blue, finalAlpha);
  }
  
  /**
   * Converts English character sequences to Norwegian special characters.
   * For example, "AE" -> "Æ", "O/" -> "Ø", "A0" -> "Å", etc.
   *
   * @param input Input string possibly containing English sequences.
   * @return Converted string with Norwegian characters.
   */
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
  
  /**
   * Sets the inverse transform matrix of the object.
   * This matrix is used to convert world coordinates to local object coordinates.
   *
   * Note: The method keeps the original implementation as requested.
   *
   * @param tm The transformation matrix of the object.
   */
  @Override
  public void setObjectTransform(Matrix4 tm) {
    if (tm == null) tm = new Matrix4();
    this.objectInverseTransform = tm;
  }
  
  /**
   * Returns the color of the material at the given world point, considering lighting and viewer position.
   * Applies diffuse and specular lighting based on reflectivity and transparency.
   * Uses dynamic transparency based on user's bgColor alpha.
   *
   * @param worldPoint  Point in world coordinates.
   * @param worldNormal Surface normal at the point.
   * @param light       Light source affecting the point.
   * @param viewerPos   Position of the viewer/camera.
   * @return Color of the material at the point.
   */
  @Override
  public Color getColorAt(Point3 worldPoint, Vector3 worldNormal, net.elena.murat.light.Light light, Point3 viewerPos) {
    Point3 localPoint = objectInverseTransform.transformPoint(worldPoint);
    Color textureColor = getTextureColor(localPoint);
    
    // Use textureColor alpha directly (which comes from bgColor)
    int alpha = textureColor.getAlpha();
    
    // Dynamic transparency based on user's bgColor alpha
    transparency = 1.0 - (alpha / 255.0);
    
    // If mostly transparent, return transparent
    if (alpha < 5) {
      return new Color(0, 0, 0, 0);
    }
    
    // Ambient light returns texture color as-is (with user's alpha)
    if (light instanceof net.elena.murat.light.ElenaMuratAmbientLight) {
      return textureColor;
    }
    
    Vector3 lightDir = getLightDirection(light, worldPoint);
    if (lightDir != null) {
      // Calculate diffuse lighting
      double diffuseFactor = Math.max(0, worldNormal.dot(lightDir));
      
      // Calculate specular lighting if material is reflective
      double specularFactor = 0.0;
      if (reflectivity > 0.0) {
        Vector3 viewDir = viewerPos.subtract(worldPoint).normalize();
        Vector3 reflectDir = lightDir.negate().reflect(worldNormal);
        specularFactor = Math.pow(Math.max(0, viewDir.dot(reflectDir)), 32) * reflectivity;
      }
      
      // Combine lighting factors
      double totalFactor = Math.min(1.0, diffuseFactor + specularFactor);
      
      // Apply lighting but PRESERVE user's alpha from bgColor
      return new Color(
        (int)(textureColor.getRed() * totalFactor),
        (int)(textureColor.getGreen() * totalFactor),
        (int)(textureColor.getBlue() * totalFactor),
        alpha // Preserve user's alpha from bgColor
      );
    }
    
    // Fallback: return texture color without lighting (with user's alpha)
    return textureColor;
  }
  
  /**
   * Helper method to get the light direction vector for various light types.
   *
   * @param light      Light source.
   * @param worldPoint Point on the surface in world coordinates.
   * @return Normalized direction vector from point to light or light direction.
   */
  private Vector3 getLightDirection(net.elena.murat.light.Light light, Point3 worldPoint) {
    if (light instanceof net.elena.murat.light.ElenaDirectionalLight) {
      return ((net.elena.murat.light.ElenaDirectionalLight) light).getDirection().normalize();
      } else if (light instanceof net.elena.murat.light.MuratPointLight) {
      return ((net.elena.murat.light.MuratPointLight) light).getPosition().subtract(worldPoint).normalize();
      } else if (light instanceof net.elena.murat.light.PulsatingPointLight) {
      return ((net.elena.murat.light.PulsatingPointLight) light).getPosition().subtract(worldPoint).normalize();
      } else if (light instanceof net.elena.murat.light.BioluminescentLight) {
      return ((net.elena.murat.light.BioluminescentLight) light).getDirectionAt(worldPoint).normalize();
      } else if (light instanceof net.elena.murat.light.BlackHoleLight) {
      return ((net.elena.murat.light.BlackHoleLight) light).getDirectionAt(worldPoint).normalize();
      } else if (light instanceof net.elena.murat.light.FractalLight) {
      return ((net.elena.murat.light.FractalLight) light).getDirectionAt(worldPoint).normalize();
      } else if (light instanceof net.elena.murat.light.SpotLight) {
      return ((net.elena.murat.light.SpotLight) light).getDirectionAt(worldPoint).normalize();
    }
    return null;
  }
  
  /**
   * Returns the reflectivity coefficient of the material.
   *
   * @return Reflectivity [0..1].
   */
  @Override
  public double getReflectivity() {
    return reflectivity;
  }
  
  /**
   * Returns the index of refraction of the material.
   *
   * @return Index of refraction (>=1.0).
   */
  @Override
  public double getIndexOfRefraction() {
    return ior;
  }
  
  /**
   * Returns the transparency coefficient of the material.
   *
   * @return Transparency [0..1], 1.0 = fully transparent.
   */
  @Override
  public double getTransparency() {
    return transparency;
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
    sb.append("SphereWordTextureMaterial spherewordtexturematerial {\n");
    sb.append("        imagePath = " + getImagePath() + ";\n");
    sb.append("        word = " + word + ";\n");
    sb.append("        textColor = " + net.elena.murat.util.ColorUtil.toColorString(textColor) + ";\n");
    sb.append("        gradientColor = " + net.elena.murat.util.ColorUtil.toColorString(gradientColor) + ";\n");
    sb.append("        gradientType = " + gradientType + ";\n");
    sb.append("        bgColor = " + net.elena.murat.util.ColorUtil.toColorString(bgColor) + ";\n");
    sb.append("        fontFamily = " + fontFamily + ";\n");
    sb.append("        fontStyle = " + fontStyle + ";\n");
    sb.append("        fontSize = " + fontSize + ";\n");
    sb.append("        reflectivity = " + reflectivity + ";\n");
    sb.append("        ior = " + ior + ";\n");
    sb.append("        transparency = " + transparency + ";\n");
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
