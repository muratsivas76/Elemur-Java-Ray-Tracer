import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AddToString {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Kullanım: java AddToString <kaynak_dizin> <hedef_dizin>");
      return;
    }
    
    Path sourceDir = Paths.get(args[0]);
    Path targetDir = Paths.get(args[1]);
    
    Files.walk(sourceDir)
    .filter(path -> path.toString().endsWith(".java"))
    .forEach(path -> processFile(path, sourceDir, targetDir));
    
    System.out.println("İşlem tamamlandı!");
  }
  
  private static void processFile(Path javaFile, Path sourceDir, Path targetDir) {
    try {
      System.out.println("\n========================================");
      System.out.println("DEBUG - Processing file: " + javaFile.getFileName());
      
      String content = Files.readString(javaFile);
      
      // Sınıf tanımını bul
      String classDefinition = findClassDefinition(content);
      if (classDefinition == null) {
        System.out.println("DEBUG - No class definition found, copying: " + javaFile.getFileName());
        copyFile(javaFile, sourceDir, targetDir);
        return;
      }
      
      // Enum, interface veya @interface ise atla
      if (classDefinition.contains(" enum ") ||
        classDefinition.startsWith("public interface") ||
        classDefinition.startsWith("interface") ||
        classDefinition.contains("@interface")) {
        System.out.println("DEBUG - Skipping (enum/interface/@interface): " + javaFile.getFileName());
        copyFile(javaFile, sourceDir, targetDir);
        return;
      }
      
      // Sınıf adını bul
      String className = findClassName(classDefinition);
      if (className == null) {
        System.out.println("DEBUG - Class name NOT FOUND in: " + javaFile.getFileName());
        copyFile(javaFile, sourceDir, targetDir);
        return;
      }
      System.out.println("DEBUG - Found class name: " + className);
      
      // En uzun kurucu metot içindeki field'ları bul (İSİM ve TİP ile)
      List<FieldInfo> fields = findConstructorFields(content, className);
      System.out.println("DEBUG - Processing " + className + " with " + fields.size() + " fields from constructor");
      
      if (fields.isEmpty()) {
        System.out.println("DEBUG - NO CONSTRUCTOR FIELDS FOUND, copying without modification");
        copyFile(javaFile, sourceDir, targetDir);
        return;
      }
      
      // toString metodu var mı kontrol et
      boolean hasToString = content.contains("public String toString()");
      System.out.println("DEBUG - Has existing toString: " + hasToString);
      
      String newContent;
      if (hasToString) {
        newContent = addToString2(content, className, fields);
        } else {
        newContent = addToString(content, className, fields);
      }
      
      // Hedef dosyaya yaz
      Path relativePath = sourceDir.relativize(javaFile);
      Path targetFile = targetDir.resolve(relativePath);
      Files.createDirectories(targetFile.getParent());
      Files.writeString(targetFile, newContent);
      
      System.out.println("DEBUG - ✓ toString successfully added to " + className);
      
      } catch (Exception e) {
      System.err.println("ERROR processing " + javaFile + ": " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private static String findClassDefinition(String content) {
    // Sınıf tanımını bul (public class veya class ile başlayan satır)
    String[] lines = content.split("\n");
    for (String line : lines) {
      String trimmed = line.trim();
      if ((trimmed.startsWith("public class ") ||
          trimmed.startsWith("class ") ||
          trimmed.startsWith("public interface ") ||
          trimmed.startsWith("interface ") ||
        trimmed.contains("@interface")) &&
        !trimmed.startsWith("//") && !trimmed.startsWith("*")) {
        return trimmed;
      }
    }
    return null;
  }
  
  private static String findClassName(String classDefinition) {
    // class definition satırından sınıf adını çıkar
    String[] parts = classDefinition.split("\\s+");
    for (int i = 0; i < parts.length; i++) {
      if (parts[i].equals("class") && i + 1 < parts.length) {
        return parts[i + 1].split("[<{\\s]")[0];
      }
    }
    return null;
  }
  
  private static List<FieldInfo> findConstructorFields(String content, String className) {
    List<List<FieldInfo>> constructors = new ArrayList<>();
    
    System.out.println("DEBUG - Searching for constructors of: " + className);
    
    // Tüm kurucu başlıklarını bul (çok satırlı olabilir)
    int index = 0;
    while ((index = content.indexOf(className + "(", index)) != -1) {
      System.out.println("DEBUG - Found potential constructor at index: " + index);
      int start = content.indexOf('(', index);
      int end = findMatchingParen(content, start);
      if (end != -1) {
        String params = content.substring(start + 1, end);
        List<FieldInfo> fields = parseParameters(params);
        constructors.add(fields);
        System.out.println("DEBUG - Found constructor with " + fields.size() + " params");
        index = end + 1;
        } else {
        System.out.println("DEBUG - Could not find matching paren");
        index += className.length() + 1;
      }
    }
    
    // "public " ile başlayan kurucuları da kontrol et
    index = 0;
    while ((index = content.indexOf("public " + className + "(", index)) != -1) {
      System.out.println("DEBUG - Found public constructor at index: " + index);
      int start = content.indexOf('(', index);
      int end = findMatchingParen(content, start);
      if (end != -1) {
        String params = content.substring(start + 1, end);
        List<FieldInfo> fields = parseParameters(params);
        constructors.add(fields);
        System.out.println("DEBUG - Found public constructor with " + fields.size() + " params");
        index = end + 1;
        } else {
        System.out.println("DEBUG - Could not find matching paren for public constructor");
        index += ("public " + className).length() + 1;
      }
    }
    
    System.out.println("DEBUG - Total constructors found: " + constructors.size());
    
    // En uzun kurucuyu bul (en fazla parametreye sahip olan)
    List<FieldInfo> longest = new ArrayList<>();
    for (List<FieldInfo> cons : constructors) {
      if (cons.size() > longest.size()) {
        longest = cons;
      }
    }
    
    return longest;
  }
  
  private static int findMatchingParen(String content, int openIndex) {
    int count = 0;
    for (int i = openIndex; i < content.length(); i++) {
      char c = content.charAt(i);
      if (c == '(') {
        count++;
        } else if (c == ')') {
        count--;
        if (count == 0) {
          return i;
        }
      }
    }
    return -1; // Eşleşmeyen parantez
  }
  
  private static List<FieldInfo> parseParameters(String params) {
    List<FieldInfo> fields = new ArrayList<>();
    if (params.trim().isEmpty()) {
      System.out.println("DEBUG - Empty parameter list");
      return fields;
    }
    
    System.out.println("DEBUG - Parsing parameters: " + params.replace("\n", " ").trim());
    
    // Parametreleri virgülle ayır, ama çok satırlı olabilir
    String[] paramArray = params.split(",");
    for (String param : paramArray) {
      param = param.trim();
      if (param.isEmpty()) continue;
      
      // Son boşluktan böl (tip ve isim)
      int lastSpace = param.lastIndexOf(' ');
      if (lastSpace != -1) {
        String type = param.substring(0, lastSpace).trim();
        String name = param.substring(lastSpace + 1).trim();
        // Geçerli field adı kontrolü
        if (name.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
          fields.add(new FieldInfo(type, name));
          System.out.println("DEBUG - ✓ Parsed param: type='" + type + "', name='" + name + "'");
          } else {
          System.out.println("DEBUG - ✗ Invalid field name: '" + name + "'");
        }
        } else {
        System.out.println("DEBUG - ✗ Could not parse parameter: '" + param + "'");
      }
    }
    return fields;
  }
  
  private static String addToString(String content, String className, List<FieldInfo> fields) {
    int lastBrace = content.lastIndexOf('}');
    if (lastBrace == -1) return content;
    
    StringBuilder toString = new StringBuilder("  @Override\n  public String toString() {\n");
    toString.append("    StringBuffer sb = new StringBuffer();\n");
    toString.append("    sb.append(\"").append(className).append(" ").append(className.toLowerCase(java.util.Locale.ENGLISH)).append(" {\\n\");\n");
    
    // Normal field'ları ekle
    for (int i = 0; i < fields.size(); i++) {
      FieldInfo field = fields.get(i);
      toString.append("    sb.append(\"        ").append(field.name).append(" = \" + ");
      
      // Type Control
      if (field.type.equals("BufferedImage")) {
        toString.append("getImagePath()");
        } else if (field.type.equals("Color") || field.type.equals("java.awt.Color")) {
        toString.append("net.elena.murat.util.ColorUtil.toColorString(").append(field.name).append(")");
        } else if (field.type.equals("FloatColor")) {
        toString.append("net.elena.murat.util.ColorUtil.toFloatColorString(").append(field.name).append(")");
        } else if (field.type.equals("Font") || field.type.equals("java.awt.Font")) {
        toString.append("net.elena.murat.util.ColorUtil.toFontString(").append(field.name).append(")");
        } else {
        toString.append(field.name);
      }
      
      toString.append(" + \";\\n\");\n");
    }
    
    toString.append("    sb.append(\"    }\");\n");
    toString.append("    return sb.toString();\n  }\n");
    
    return content.substring(0, lastBrace) + toString + "\n}";
  }
  
  private static String addToString2(String content, String className, List<FieldInfo> fields) {
    int lastBrace = content.lastIndexOf('}');
    if (lastBrace == -1) return content;
    
    StringBuilder toString2 = new StringBuilder("  public String toString2() {\n");
    toString2.append("    StringBuffer sb = new StringBuffer();\n");
    toString2.append("    sb.append(\"").append(className).append(" ").append(className.toLowerCase(java.util.Locale.ENGLISH)).append(" {\\n\");\n");
    
    // Normal field'ları ekle
    for (int i = 0; i < fields.size(); i++) {
      FieldInfo field = fields.get(i);
      toString2.append("    sb.append(\"        ").append(field.name).append(" = \" + ");
      
      // ÖZEL TİP FORMATLAMA
      if (field.type.equals("BufferedImage")) {
        toString2.append("getImagePath()");
        } else if (field.type.equals("Color") || field.type.equals("java.awt.Color")) {
        toString2.append("net.elena.murat.util.ColorUtil.toColorString(").append(field.name).append(")");
        } else if (field.type.equals("FloatColor")) {
        toString2.append("net.elena.murat.util.ColorUtil.toFloatColorString(").append(field.name).append(")");
        } else if (field.type.equals("Font") || field.type.equals("java.awt.Font")) {
        toString2.append("net.elena.murat.util.ColorUtil.toFontString(").append(field.name).append(")");
        } else {
        toString2.append(field.name);
      }
      
      toString2.append(" + \";\\n\");\n");
    }
    
    toString2.append("    sb.append(\"    }\");\n");
    toString2.append("    return sb.toString();\n  }\n");
    
    return content.substring(0, lastBrace) + toString2 + "\n}";
  }
  
  private static void copyFile(Path sourceFile, Path sourceDir, Path targetDir) throws IOException {
    Path relativePath = sourceDir.relativize(sourceFile);
    Path targetFile = targetDir.resolve(relativePath);
    Files.createDirectories(targetFile.getParent());
    Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
  }
  
  static class FieldInfo {
    String type;
    String name;
    
    FieldInfo(String type, String name) {
      this.type = type;
      this.name = name;
    }
  }
  
}
