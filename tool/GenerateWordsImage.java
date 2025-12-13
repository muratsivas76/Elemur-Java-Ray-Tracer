import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateWordsImage {
  
  private static String replaceNorwegianChars(String text) {
    return text.replace("aaaeee", "æ")
    .replace("ooo", "ø")
    .replace("aaa", "å")
    .replace("AAAEEE", "Æ")
    .replace("OOO", "Ø")
    .replace("AAA", "Å")
    .replace("_", " "); // Alt çizgiyi boşluğa çevir
  }
  
  private static Color parseColor(String colorStr) {
    if (colorStr.length() == 8) { // AARRGGBB
      int a = Integer.parseInt(colorStr.substring(0, 2), 16);
      int r = Integer.parseInt(colorStr.substring(2, 4), 16);
      int g = Integer.parseInt(colorStr.substring(4, 6), 16);
      int b = Integer.parseInt(colorStr.substring(6, 8), 16);
      return new Color(r, g, b, a);
      } else if (colorStr.length() == 6) { // RRGGBB
      int r = Integer.parseInt(colorStr.substring(0, 2), 16);
      int g = Integer.parseInt(colorStr.substring(2, 4), 16);
      int b = Integer.parseInt(colorStr.substring(4, 6), 16);
      return new Color(r, g, b);
      } else {
      throw new IllegalArgumentException("Geçersiz renk formatı: " + colorStr);
    }
  }
  
  private static Font parseFont(String fontSpec) {
    String[] parts = fontSpec.split("-");
    if (parts.length < 3) {
      throw new IllegalArgumentException("Geçersiz font formatı: " + fontSpec);
    }
    
    String fontName = parts[0].replace("_", " ");
    int style = Integer.parseInt(parts[1]);
    int size = Integer.parseInt(parts[2]);
    
    return new Font(fontName, style, size);
  }
  
  public static void main(String[] args) {
    if (args.length != 6) {
      System.out.println("Kullanım: java GenerateWordsImage word bgColor fgColor1 fgColor2 font dst.png");
      System.out.println("Örnek: java GenerateWordsImage \"merhaba\" FF000000 FFFFFF 000000 \"Arial_Black-1-70\" output.png");
      System.out.println("Norveççe karakterler: aaaeee=æ, ooo=ø, aaa=å, AAAEEE=Æ, OOO=Ø, AAA=Å");
      System.out.println("Boşluk için: _ (alt çizgi)");
      return;
    }
    
    try {
      String word = replaceNorwegianChars(args[0]);
      String bgColor = args[1];
      String fgColor1 = args[2];
      String fgColor2 = args[3];
      String fontSpec = args[4];
      String outputPath = args[5];
      
      final int WX = 2048;
      final int HX = 768;
      
      // WX*HX transparent image oluştur
      BufferedImage image = new BufferedImage(WX, HX, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = image.createGraphics();
      
      // Anti-aliasing
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      
      // Background'u temizle (transparent)
      g2d.setComposite(AlphaComposite.Clear);
      g2d.fillRect(0, 0, WX, HX);
      g2d.setComposite(AlphaComposite.SrcOver);
      
      // Background rengi (eğer tam transparent değilse)
      Color bg = parseColor(bgColor);
      if (bg.getAlpha() > 0) {
        g2d.setColor(bg);
        g2d.fillRect(0, 0, WX, HX);
      }
      
      // Font'u ayarla
      Font font = parseFont(fontSpec);
      g2d.setFont(font);
      
      // Gradient paint oluştur
      Color color1 = parseColor(fgColor1);
      Color color2 = parseColor(fgColor2);
      GradientPaint gradient = new GradientPaint(0, 0, color1, WX, HX, color2);
      g2d.setPaint(gradient);
      
      // Text bounds hesapla
      FontMetrics fm = g2d.getFontMetrics();
      Rectangle2D bounds = fm.getStringBounds(word, g2d);
      
      // Text'i tam ortala (TEK KERE)
      int x = (WX - (int)bounds.getWidth()) / 2;
      int y = (HX - (int)bounds.getHeight()) / 2 + fm.getAscent();
      
      g2d.drawString(word, x, y);
      
      g2d.dispose();
      
      // Dosyaya kaydet
      ImageIO.write(image, "PNG", new File(outputPath));
      System.out.println("Image oluşturuldu: " + outputPath);
      System.out.println("Kelime: " + word);
      
      } catch (Exception e) {
      System.err.println("Hata: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
}
