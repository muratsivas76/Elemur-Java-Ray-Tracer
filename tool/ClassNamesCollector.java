import java.io.*;
import java.util.*;

public class ClassNamesCollector {
  private static final Vector<String> vector = new Vector<String>();
  
  public static void main(String[] args) {
    String srcDir = "/home/muratsivas76/istasyon/java/extended_elemurrt/src";
    String outFile = "/home/muratsivas76/istasyon/java/extended_elemurrt/tool/classNames.txt";
    
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
      processJavaFiles(new File(srcDir), writer, srcDir);
      writeToFile(writer);
      System.out.println("All Java class names collected successfully to tool/classNames.txt!");
      } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  private static void processJavaFiles(File dir, BufferedWriter writer, String basePath) throws IOException {
    File[] files = dir.listFiles();
    if (files == null) return;
    
    for (File file : files) {
      if (file.isDirectory()) {
        processJavaFiles(file, writer, basePath);
        } else if (file.getName().endsWith(".java")) {
        writeFileContent(file, writer, basePath);
        //System.out.println ("Added: "+(file.getAbsolutePath ())+"");
      }
    }
  }
  
  private static void writeFileContent(File javaFile, BufferedWriter writer, String basePath) throws IOException {
    StringBuffer sb = new StringBuffer();
    
    String s = javaFile.getName().replace(".java", "");
    sb.append("        " + s + " " + s.toLowerCase(java.util.Locale.ENGLISH) + "_variable = null;");
    vector.add(sb.toString());
    //writer.write(sb.toString());
  }
  
  private static final void writeToFile(BufferedWriter writer) throws IOException {
    final int size = vector.size();
    String[] array = new String[size];
    
    for (int i = 0; i < size; i++) {
      array[i] = vector.get(i);
    }
    
    java.util.Arrays.sort(array);
    
    for (int i = 0; i < size; i++) {
      writer.write(array[i]);
      writer.write("\n");
      System.out.println(array[i]);
    }
    
    writer.close();
  }
}
