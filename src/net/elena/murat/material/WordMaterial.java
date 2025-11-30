package net.elena.murat.material;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import net.elena.murat.light.Light;
import net.elena.murat.math.Matrix4;
import net.elena.murat.math.Point3;
import net.elena.murat.math.Vector3;

/**
 * Material class that generates textures with rendered text and optional image on the fly.
 * Supports custom text, fonts, colors, gradients, transparent backgrounds, and image integration.
 * Uses planar UV mapping on XY plane (Z ignored) similar to TransparentPNGMaterial.
 */
public class WordMaterial implements Material {
  private BufferedImage texture;
  private Matrix4 objectInverseTransform = new Matrix4();
  private double transparency = 1.0;
  
  // UV parameters
  private double uOffset = 0.0;
  private double vOffset = 0.0;
  private double uScale = 1.0;
  private double vScale = 1.0;
  private boolean isRepeatTexture = false;
  
  private boolean isTriangleEtc = false;
  
  // Billboard dimensions
  private double billboardWidth = 1.0;
  private double billboardHeight = 1.0;

  // Text rendering parameters
  private String text;
  private Color foregroundColor;
  private Color backgroundColor;
  private Font font;
  private boolean gradientEnabled;
  private Color gradientColor;
  private BufferedImage wordImage;
  private int width;
  private int height;
  
  /**
   * Constructor with default styling (white text on transparent background, Arial Bold 48)
   * @param text The text to render on the material
   */
  public WordMaterial(String text) {
    this(text, Color.WHITE, new Color(0x00000000, true), new Font("Arial", Font.BOLD, 48),
    false, null, null, 256, 256);
  }
  
  /**
   * Constructor with custom colors and font
   * @param text The text to render
   * @param foregroundColor Text color (RGB or ARGB)
   * @param backgroundColor Background color (use 0x00000000 for transparent)
   * @param font The font to use for rendering
   */
  public WordMaterial(String text, Color foregroundColor, Color backgroundColor, Font font) {
    this(text, foregroundColor, backgroundColor, font, false, null, null, 256, 256);
  }
  
  /**
   * Constructor with gradient support
   * @param text The text to render
   * @param foregroundColor Starting gradient color
   * @param backgroundColor Background color
   * @param font The font to use
   * @param gradientColor Ending gradient color (if null, no gradient is applied)
   */
  public WordMaterial(String text, Color foregroundColor, Color backgroundColor,
    Font font, Color gradientColor) {
    this(text, foregroundColor, backgroundColor, font, true, gradientColor, null, 256, 256);
  }
  
  /**
   * Constructor with image support
   * @param text The text to render
   * @param foregroundColor Text color
   * @param backgroundColor Background color
   * @param font The font to use
   * @param wordImage Optional image to display above text (null for text only)
   */
  public WordMaterial(String text, Color foregroundColor, Color backgroundColor,
    Font font, BufferedImage wordImage) {
    this(text, foregroundColor, backgroundColor, font, false, null, wordImage,
    wordImage != null ? 384 : 256, wordImage != null ? 384 : 256);
  }
  
  /**
   * Constructor with custom size
   * @param text The text to render
   * @param foregroundColor Text color
   * @param backgroundColor Background color
   * @param font The font to use
   * @param width Texture width
   * @param height Texture height
   */
  public WordMaterial(String text, Color foregroundColor, Color backgroundColor,
    Font font, int width, int height) {
    this(text, foregroundColor, backgroundColor, font, false, null, null, width, height);
  }
  
  /**
   * Constructor for sentences with automatic sizing and UV offset support
   * @param sentence The sentence text to render
   * @param textColor Text color
   * @param sentenceFont Font for the sentence
   * @param maxWidth Maximum texture width
   * @param uOffset Horizontal UV offset
   * @param vOffset Vertical UV offset
   */
  public WordMaterial(String sentence, Color textColor, Font sentenceFont, int maxWidth, 
                     double uOffset, double vOffset) {
    this.text = sentence;
    this.foregroundColor = textColor;
    this.backgroundColor = new Color(0x00000000, true); // Transparent background
    this.font = sentenceFont;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    
    // Calculate texture size based on sentence
    BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = temp.createGraphics();
    g.setFont(sentenceFont);
    FontMetrics fm = g.getFontMetrics();
    
    int textWidth = fm.stringWidth(sentence);
    int textHeight = fm.getHeight();
    
    this.width = Math.min(textWidth + 40, maxWidth);
    this.height = textHeight + 20;
    
    g.dispose();
    
    this.texture = createTextImage(sentence, textColor, backgroundColor, 
                                  sentenceFont, false, null, null, width, height);
  }
  
  /**
   * Constructor for multi-line sentences with automatic sizing and UV offset support
   * @param lines Array of text lines
   * @param textColor Text color
   * @param sentenceFont Font for the sentences
   * @param maxWidth Maximum texture width
   * @param uOffset Horizontal UV offset
   * @param vOffset Vertical UV offset
   */
  public WordMaterial(String[] lines, Color textColor, Font sentenceFont, int maxWidth,
                     double uOffset, double vOffset) {
    this.text = String.join(" ", lines);
    this.foregroundColor = textColor;
    this.backgroundColor = new Color(0x00000000, true);
    this.font = sentenceFont;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    
    // Calculate multi-line texture size
    BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = temp.createGraphics();
    g.setFont(sentenceFont);
    FontMetrics fm = g.getFontMetrics();
    
    int maxLineWidth = 0;
    for (String line : lines) {
      maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(line));
    }
    
    this.width = Math.min(maxLineWidth + 40, maxWidth);
    this.height = (fm.getHeight() * lines.length) + 20;
    
    g.dispose();
    
    this.texture = createMultiLineTextImage(lines, textColor, backgroundColor, 
                                           sentenceFont, width, height);
  }
  
  /**
   * Full constructor with all parameters including UV offset support
   * @param text The text to render
   * @param foregroundColor Text color
   * @param backgroundColor Background color
   * @param font The font to use
   * @param useGradient Whether to apply gradient effect
   * @param gradientColor Gradient end color (required if useGradient is true)
   * @param wordImage Optional image to display above text (null for text only)
   * @param width Texture width
   * @param height Texture height
   * @param uOffset Horizontal UV offset
   * @param vOffset Vertical UV offset
  */
  public WordMaterial(String text, Color foregroundColor, Color backgroundColor,
    Font font, boolean useGradient, Color gradientColor, BufferedImage wordImage,
    int width, int height, double uOffset, double vOffset) {
      this.text = text;
      this.foregroundColor = foregroundColor;
      this.backgroundColor = backgroundColor;
      this.font = font;
      this.gradientEnabled = useGradient;
      this.gradientColor = gradientColor;
      this.wordImage = wordImage;
      this.width = width;
      this.height = height;
      this.uOffset = uOffset;
      this.vOffset = vOffset;
  
      this.texture = createTextImage(text, foregroundColor, backgroundColor, font,
      useGradient, gradientColor, wordImage, width, height);
   }

	/**
	* Full constructor with all parameters (without UV offset)
	* @param text The text to render
	* @param foregroundColor Text color
	* @param backgroundColor Background color
	* @param font The font to use
	* @param useGradient Whether to apply gradient effect
	* @param gradientColor Gradient end color (required if useGradient is true)
	* @param wordImage Optional image to display above text (null for text only)
	* @param width Texture width
	* @param height Texture height
	*/
	public WordMaterial(String text, Color foregroundColor, Color backgroundColor,
	Font font, boolean useGradient, Color gradientColor, BufferedImage wordImage,
	int width, int height) {
	this(text, foregroundColor, backgroundColor, font, useGradient, gradientColor, 
       wordImage, width, height, 0.0, 0.0); // UV offset default 0
	}
  
  /**
   * Creates a BufferedImage with the rendered text and optional image
   * @param text Text to render
   * @param fgColor Text color
   * @param bgColor Background color
   * @param font Font to use
   * @param useGradient Whether to use gradient
   * @param gradientColor Gradient end color
   * @param wordImage Optional image to display above text
   * @param width Image width
   * @param height Image height
   * @return BufferedImage with rendered content
   */
  private static BufferedImage createTextImage(String text, Color fgColor, Color bgColor,
    Font font, boolean useGradient, Color gradientColor,
    BufferedImage wordImage, int width, int height) {
    
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
        
    // Background ARGB
    g2d.setColor(bgColor);
    g2d.setComposite(AlphaComposite.Src); 
    g2d.fillRect(0, 0, width, height);
    g2d.setComposite(AlphaComposite.SrcOver);
    
    // Anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    
    // Word image
    if (wordImage != null) {
        int imageSize = Math.min(width, height) - 64;
        int imageX = (width - imageSize) / 2;
        int imageY = 32;
        
        BufferedImage opaqueImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imgG2d = opaqueImage.createGraphics();
        imgG2d.drawImage(wordImage, 0, 0, imageSize, imageSize, null);
        imgG2d.dispose();
        
        // Force opaque
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int rgb = opaqueImage.getRGB(i, j);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha > 10) {
                    opaqueImage.setRGB(i, j, (255 << 24) | (rgb & 0x00FFFFFF));
                }
            }
        }
        
        g2d.drawImage(opaqueImage, imageX, imageY, null);
    }
    
    // Text with gradient or solid color
    if (useGradient && gradientColor != null) {
        int textY = (wordImage != null) ? (height * 2 / 3) : (height / 2);
        GradientPaint gradient = new GradientPaint(0, textY, makeOpaque(fgColor), 
                                                 width, textY + 50, makeOpaque(gradientColor));
        g2d.setPaint(gradient);
    } else {
        g2d.setColor(makeOpaque(fgColor));
    }
    
    g2d.setFont(font);
    FontMetrics metrics = g2d.getFontMetrics();
    int textWidth = metrics.stringWidth(text);
    int textX = (width - textWidth) / 2;
    int textY = (wordImage != null) ? height * 3 / 4 : (height - metrics.getHeight()) / 2 + metrics.getAscent();
    
    g2d.drawString(text, textX, textY);
    g2d.dispose();
    
    return image;
  }
  
  /**
   * Makes a color fully opaque (alpha=255)
   * Used for text and images to ensure they render with full opacity
   */
   private static Color makeOpaque(Color color) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
   }
  
  /**
   * Creates a BufferedImage with multiple lines of text
   * @param lines Array of text lines
   * @param fgColor Text color
   * @param bgColor Background color
   * @param font Font to use
   * @param width Image width
   * @param height Image height
   * @return BufferedImage with rendered multi-line content
   */
  private static BufferedImage createMultiLineTextImage(String[] lines, Color fgColor, Color bgColor,
                                                       Font font, int width, int height) {
    
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    
    // ARGB support:
    g2d.setColor(bgColor);
    g2d.setComposite(AlphaComposite.Src);
    g2d.fillRect(0, 0, width, height);
    g2d.setComposite(AlphaComposite.SrcOver);
    
    // Enable anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    // Set text color and font
    g2d.setColor(fgColor);
    g2d.setFont(font);
    
    FontMetrics metrics = g2d.getFontMetrics();
    int lineHeight = metrics.getHeight();
    
    // Center each line vertically and horizontally
    int startY = (height - (lineHeight * lines.length)) / 2 + metrics.getAscent();
    
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      int textWidth = metrics.stringWidth(line);
      int textX = (width - textWidth) / 2;
      int textY = startY + (i * lineHeight);
      
      g2d.drawString(line, textX, textY);
    }
    
    g2d.dispose();
    return image;
  }
  
  /**
   * Sets the inverse transform matrix of the object
   * @param inverseTransform Matrix4 inverse transform
   */
  @Override
  public void setObjectTransform(Matrix4 inverseTransform) {
    if (inverseTransform != null) {
      this.objectInverseTransform = inverseTransform;
    } else {
      this.objectInverseTransform = new Matrix4();
    }
  }
  
  public void setTriangleEtc(boolean nbool) {
    this.isTriangleEtc = nbool;
  }
  
	// Getters Setters:
	public double getBillboardWidth() {
		return billboardWidth;
	}

	public void setBillboardWidth(double billboardWidth) {
		this.billboardWidth = billboardWidth;
	}

	public double getBillboardHeight() {
		return billboardHeight;
	}

	public void setBillboardHeight(double billboardHeight) {
		this.billboardHeight = billboardHeight;
	}

  /**
   * Returns the color at the given world point on the surface
   * Uses planar UV mapping on XY plane similar to TransparentPNGMaterial
   * @param point World space point on surface
   * @param normal Surface normal
   * @param light Light source
   * @param viewerPos Viewer position
   * @return Color with alpha channel
   */
  @Override
  public Color getColorAt(Point3 point, Vector3 normal, Light light, Point3 viewerPos) {
    if (texture == null) {
      return new Color(0, 0, 0, 0); // Fully transparent
    }
    
    Point3 local = objectInverseTransform.transformPoint(point);
    
    double u = 0.0;
    double v = 0.0;
    
    if (isTriangleEtc) {
      u = local.x + 0.5;
      v = local.z + 0.5;
    } else {
        // BILLBOARD BOUNDS ACCORDING
        u = (local.x / billboardWidth) + 0.5;   // [-width/2, width/2] -> [0, 1]
        v = (local.y / billboardHeight) + 0.5;  // [-height/2, height/2] -> [0, 1]
        v = 1.0 - v; // Flip V for image coordinates
    }
    
    // Apply UV scale and offset
    double scaledU = u / uScale + uOffset;
    double scaledV = v / vScale + vOffset;
    
    double finalU, finalV;
    
    if (isRepeatTexture) {
      finalU = scaledU - Math.floor(scaledU);
      finalV = scaledV - Math.floor(scaledV);
    } else {
      if (scaledU < 0.0 || scaledU > 1.0 || scaledV < 0.0 || scaledV > 1.0) {
        return new Color(0, 0, 0, 0); // Fully transparent for out-of-bounds
      }
      finalU = scaledU;
      finalV = scaledV;
    }
    
    int px = (int) (finalU * (texture.getWidth() - 1));
    int py = (int) (finalV * (texture.getHeight() - 1));
    
    // Clamp coordinates to texture bounds
    px = Math.max(0, Math.min(px, texture.getWidth() - 1));
    py = Math.max(0, Math.min(py, texture.getHeight() - 1));
    
    int argb = texture.getRGB(px, py);
    
    int alpha = (argb >> 24) & 0xFF;
    int red = (argb >> 16) & 0xFF;
    int green = (argb >> 8) & 0xFF;
    int blue = argb & 0xFF;
    
    // Set transparency based on actual pixel alpha
    this.transparency = 1.0 - (alpha / 255.0);
    
    // Return color with exact alpha from texture
    return new Color(red, green, blue, alpha);
  }
  
  /**
   * Returns reflectivity of the material
   * @return 0.0 (non-reflective)
   */
  @Override
  public double getReflectivity() {
    return 0.0;
  }
  
  /**
   * Returns index of refraction
   * @return 1.0 (no refraction)
   */
  @Override
  public double getIndexOfRefraction() {
    return 1.0;
  }
  
  /**
   * Returns transparency of the material
   * @return transparency value (0.0 for opaque, 1.0 for fully transparent)
   */
  @Override
  public double getTransparency() {
    return transparency;
  }
  
  /**
   * Gets the horizontal texture offset
   * @return U offset value
   */
  public double getUOffset() {
    return uOffset;
  }
  
  /**
   * Sets the horizontal texture offset
   * @param uOffset U offset value
   */
  public void setUOffset(double uOffset) {
    this.uOffset = uOffset;
  }
  
  /**
   * Gets the vertical texture offset
   * @return V offset value
   */
  public double getVOffset() {
    return vOffset;
  }
  
  /**
   * Sets the vertical texture offset
   * @param vOffset V offset value
   */
  public void setVOffset(double vOffset) {
    this.vOffset = vOffset;
  }
  
  /**
   * Gets the horizontal texture scale factor
   * @return U scale factor
   */
  public double getUScale() {
    return uScale;
  }
  
  /**
   * Sets the horizontal texture scale factor
   * @param uScale U scale factor
   */
  public void setUScale(double uScale) {
    this.uScale = (uScale > 0.0) ? uScale : 1.0;
  }
  
  /**
   * Gets the vertical texture scale factor
   * @return V scale factor
   */
  public double getVScale() {
    return vScale;
  }
  
  /**
   * Sets the vertical texture scale factor
   * @param vScale V scale factor
   */
  public void setVScale(double vScale) {
    this.vScale = (vScale > 0.0) ? vScale : 1.0;
  }
  
  /**
   * Checks if texture repeating is enabled
   * @return true if texture repeating is enabled, false otherwise
   */
  public boolean isRepeatTexture() {
    return isRepeatTexture;
  }
  
  /**
   * Sets whether texture repeating is enabled
   * @param repeat true to enable repeating, false to disable
   */
  public void setRepeatTexture(boolean repeat) {
    this.isRepeatTexture = repeat;
  }
  
  /**
   * Gets the rendered text
   * @return The text displayed on this material
   */
  public String getText() {
    return text;
  }
  
  /**
   * Gets the foreground color
   * @return Text color
   */
  public Color getForegroundColor() {
    return foregroundColor;
  }
  
  /**
   * Gets the background color
   * @return Background color
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }
  
  /**
   * Gets the font used for rendering
   * @return The font
   */
  public Font getFont() {
    return font;
  }
  
  /**
   * Checks if gradient is enabled
   * @return true if gradient is enabled
   */
  public boolean isGradientEnabled() {
    return gradientEnabled;
  }
  
  /**
   * Gets the gradient end color
   * @return Gradient color
   */
  public Color getGradientColor() {
    return gradientColor;
  }
  
  /**
   * Gets the optional word image
   * @return The word image or null if not set
   */
  public BufferedImage getWordImage() {
    return wordImage;
  }
  
  /**
   * Gets the texture width
   * @return Texture width in pixels
   */
  public int getWidth() {
    return width;
  }
  
  /**
   * Sets the texture width and regenerates the texture
   * @param width New texture width
   */
  public void setWidth(int width) {
    this.width = width;
    regenerateTexture();
  }
  
  /**
   * Gets the texture height
   * @return Texture height in pixels
   */
  public int getHeight() {
    return height;
  }
  
  /**
   * Sets the texture height and regenerates the texture
   * @param height New texture height
   */
  public void setHeight(int height) {
    this.height = height;
    regenerateTexture();
  }
  
  /**
   * Sets both texture dimensions and regenerates the texture
   * @param width New texture width
   * @param height New texture height
   */
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with new text
   * @param newText New text to render
   */
  public void setText(String newText) {
    this.text = newText;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with new colors
   * @param newForeground New text color
   * @param newBackground New background color
   */
  public void setColors(Color newForeground, Color newBackground) {
    this.foregroundColor = newForeground;
    this.backgroundColor = newBackground;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with new font
   * @param newFont New font to use
   */
  public void setFont(Font newFont) {
    this.font = newFont;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with gradient settings
   * @param useGradient Whether to use gradient
   * @param newGradientColor Gradient end color
   */
  public void setGradient(boolean useGradient, Color newGradientColor) {
    this.gradientEnabled = useGradient;
    this.gradientColor = newGradientColor;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with new word image
   * @param newWordImage New word image to display (null for text only)
   */
  public void setWordImage(BufferedImage newWordImage) {
    this.wordImage = newWordImage;
    regenerateTexture();
  }
  
  /**
   * Regenerates the texture with all current settings
   * Useful when multiple properties change and you want to update once
   */
  public void regenerateTexture() {
    this.texture = createTextImage(text, foregroundColor, backgroundColor, font,
    gradientEnabled, gradientColor, wordImage, width, height);
  }
  
  private String imagePath = "null";
  public void setImagePath(String npath) {
    this.imagePath = npath;
  }
  
  public String getImagePath() {
    return this.imagePath;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("WordMaterial wordmaterial {\n");
    sb.append("        text = " + text + ";\n");
    sb.append("        foregroundColor = " + net.elena.murat.util.ColorUtil.toColorString(foregroundColor) + ";\n");
    sb.append("        backgroundColor = " + net.elena.murat.util.ColorUtil.toColorString(backgroundColor) + ";\n");
    sb.append("        fontName = " + font.getFamily() + ";\n");
    sb.append("        fontStyle = " + font.getStyle() + ";\n");
    sb.append("        fontSize = " + font.getSize() + ";\n");
    sb.append("        useGradient = " + gradientEnabled + ";\n");
    sb.append("        gradientColor = " + net.elena.murat.util.ColorUtil.toColorString(gradientColor) + ";\n");
    sb.append("        width = " + width + ";\n");
    sb.append("        height = " + height + ";\n");
    sb.append("        uOffset = " + uOffset + ";\n");
    sb.append("        vOffset = " + vOffset + ";\n");
    sb.append("        billboardWidth = " + billboardWidth + ";\n");
    sb.append("        billboardHeight = " + billboardHeight + ";\n");
    sb.append("    }");
    return sb.toString();
  }
  
}
