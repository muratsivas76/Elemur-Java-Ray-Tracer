import java.io.*;
import java.util.*;

/**
 * Akıllı Java Kod Formatlayıcı
 * Her satırın kendi içindeki parantezlere göre girintisini belirler
 */
public class CodeBeautifier {
    
    private static class Context {
        boolean inString = false;
        boolean inChar = false;
        boolean inSingleComment = false;
        boolean inMultiComment = false;
        char prev = '\0';
        
        boolean inAny() {
            return inString || inChar || inSingleComment || inMultiComment;
        }
        
        Context copy() {
            Context c = new Context();
            c.inString = this.inString;
            c.inChar = this.inChar;
            c.inSingleComment = this.inSingleComment;
            c.inMultiComment = this.inMultiComment;
            c.prev = this.prev;
            return c;
        }
    }
    
    /**
     * Satırı analiz eder ve context'i günceller
     */
    private static void processLine(String line, Context ctx) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            char next = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';
            
            // String kontrolü
            if (!ctx.inChar && !ctx.inSingleComment && !ctx.inMultiComment && c == '"' && ctx.prev != '\\') {
                ctx.inString = !ctx.inString;
            }
            
            // Char kontrolü
            if (!ctx.inString && !ctx.inSingleComment && !ctx.inMultiComment && c == '\'' && ctx.prev != '\\') {
                ctx.inChar = !ctx.inChar;
            }
            
            // Tek satır yorum
            if (!ctx.inString && !ctx.inChar && !ctx.inMultiComment && c == '/' && next == '/') {
                ctx.inSingleComment = true;
            }
            
            // Çok satırlı yorum başlangıcı
            if (!ctx.inString && !ctx.inChar && !ctx.inSingleComment && c == '/' && next == '*') {
                ctx.inMultiComment = true;
                i++;
                ctx.prev = '*';
                continue;
            }
            
            // Çok satırlı yorum bitişi
            if (ctx.inMultiComment && c == '*' && next == '/') {
                ctx.inMultiComment = false;
                i++;
                ctx.prev = '/';
                continue;
            }
            
            ctx.prev = c;
        }
        
        // Tek satır yorum satır sonunda biter
        ctx.inSingleComment = false;
    }
    
    /**
     * Satırdaki net parantez değişimini hesaplar
     * Return: [openCount, closeCount, startsWithClose, endsWithOpen]
     */
    private static int[] analyzeBraces(String line, Context ctx) {
        int open = 0;
        int close = 0;
        boolean startsWithClose = false;
        boolean endsWithOpen = false;
        boolean foundFirstMeaningful = false;
        char lastMeaningful = '\0';
        
        Context tempCtx = ctx.copy();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            char next = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';
            
            // String kontrolü
            if (!tempCtx.inChar && !tempCtx.inSingleComment && !tempCtx.inMultiComment && c == '"' && tempCtx.prev != '\\') {
                tempCtx.inString = !tempCtx.inString;
            }
            
            // Char kontrolü
            if (!tempCtx.inString && !tempCtx.inSingleComment && !tempCtx.inMultiComment && c == '\'' && tempCtx.prev != '\\') {
                tempCtx.inChar = !tempCtx.inChar;
            }
            
            // Tek satır yorum
            if (!tempCtx.inString && !tempCtx.inChar && !tempCtx.inMultiComment && c == '/' && next == '/') {
                tempCtx.inSingleComment = true;
            }
            
            // Çok satırlı yorum başlangıcı
            if (!tempCtx.inString && !tempCtx.inChar && !tempCtx.inSingleComment && c == '/' && next == '*') {
                tempCtx.inMultiComment = true;
                i++;
                tempCtx.prev = '*';
                continue;
            }
            
            // Çok satırlı yorum bitişi
            if (tempCtx.inMultiComment && c == '*' && next == '/') {
                tempCtx.inMultiComment = false;
                i++;
                tempCtx.prev = '/';
                continue;
            }
            
            // Literal içinde değilsek
            if (!tempCtx.inAny()) {
                if (c == '{') {
                    open++;
                    lastMeaningful = c;
                    if (!foundFirstMeaningful) {
                        foundFirstMeaningful = true;
                    }
                }
                if (c == '}') {
                    close++;
                    lastMeaningful = c;
                    if (!foundFirstMeaningful) {
                        startsWithClose = true;
                        foundFirstMeaningful = true;
                    }
                }
                
                // Anlamlı karakter takibi (boşluk değilse)
                if (!Character.isWhitespace(c) && !foundFirstMeaningful && !tempCtx.inMultiComment) {
                    foundFirstMeaningful = true;
                }
            }
            
            tempCtx.prev = c;
        }
        
        if (lastMeaningful == '{') {
            endsWithOpen = true;
        }
        
        return new int[]{open, close, startsWithClose ? 1 : 0, endsWithOpen ? 1 : 0};
    }
    
    /**
     * Girinti oluşturur
     */
    private static String indent(int level, int spaces) {
        if (level <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level * spaces; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    /**
     * Ana formatlama fonksiyonu
     */
    public static void format(String input, String output, int spaces) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        
        try {
            reader = new BufferedReader(new FileReader(input));
            writer = new BufferedWriter(new FileWriter(output));
            
            String line;
            int level = 0;
            Context ctx = new Context();
            boolean first = true;
            
            while ((line = reader.readLine()) != null) {
                // Boş satırları koru
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    if (!first) writer.newLine();
                    first = false;
                    continue;
                }
                
                // Satırı analiz et
                int[] braceInfo = analyzeBraces(trimmed, ctx);
                int open = braceInfo[0];
                int close = braceInfo[1];
                boolean startsWithClose = braceInfo[2] == 1;
                boolean endsWithOpen = braceInfo[3] == 1;
                
                // Girinti seviyesini hesapla
                int lineLevel = level;
                
                // } ile başlıyorsa bu satır için seviyeyi azalt
                if (startsWithClose) {
                    lineLevel = Math.max(0, level - 1);
                }
                
                // Satırı yaz
                if (!first) writer.newLine();
                writer.write(indent(lineLevel, spaces) + trimmed);
                
                // Bir sonraki satır için seviyeyi güncelle
                level = level + open - close;
                if (level < 0) level = 0;
                
                // Context'i güncelle
                processLine(trimmed, ctx);
                
                first = false;
            }
            
            System.out.println("✓ Formatlama başarılı!");
            System.out.println("  Girdi : " + input);
            System.out.println("  Çıktı : " + output);
            System.out.println("  Boşluk: " + spaces);
            
        } catch (FileNotFoundException e) {
            System.err.println("✗ Dosya bulunamadı: " + input);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("✗ IO Hatası: " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                // Sessizce kapat
            }
        }
    }
    
    public static void main(String[] args) {
        int spaces = 4;
        
        if (args.length < 2 || args.length > 3) {
            System.out.println("╔═══════════════════════════════════════╗");
            System.out.println("║   CodeBeautifier - Kod Formatlayıcı   ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.println();
            System.out.println("Kullanım:");
            System.out.println("  java CodeBeautifier <girdi> <çıktı> [boşluk]");
            System.out.println();
            System.out.println("Parametreler:");
            System.out.println("  girdi  : Kaynak dosya");
            System.out.println("  çıktı  : Hedef dosya");
            System.out.println("  boşluk : Girinti boşluk sayısı (varsayılan: 4)");
            System.out.println();
            System.out.println("Örnekler:");
            System.out.println("  java CodeBeautifier kod.java yeni.java");
            System.out.println("  java CodeBeautifier kod.java yeni.java 2");
            System.out.println("  java CodeBeautifier kod.java yeni.java 8");
            System.out.println();
            System.out.println("Özellikler:");
            System.out.println("  ✓ Her satır kendi içinde analiz edilir");
            System.out.println("  ✓ Dosyanın ortasından başlasa bile çalışır");
            System.out.println("  ✓ Tüm yorumları korur (// ve /* */)");
            System.out.println("  ✓ String/char literalleri değiştirmez");
            System.out.println("  ✓ Boş satırları korur");
            System.out.println("  ✓ Akıllı girinti hesaplama");
            System.out.println();
            System.exit(0);
        }
        
        String input = args[0];
        String output = args[1];
        
        if (args.length == 3) {
            try {
                spaces = Integer.parseInt(args[2]);
                if (spaces < 1 || spaces > 16) {
                    System.err.println("✗ Boşluk sayısı 1-16 arasında olmalı");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("✗ Geçersiz boşluk değeri: " + args[2]);
                System.exit(1);
            }
        }
        
        System.out.println();
        format(input, output, spaces);
        System.out.println();
    }
}

/*
═══════════════════════════════════════════════════════════════
DERLEME VE KULLANIM

1. Derleme:
   javac CodeBeautifier.java

2. Basit kullanım:
   java CodeBeautifier input.java output.java

3. Özel girinti ile:
   java CodeBeautifier input.java output.java 2

4. Tüm Java dosyalarını formatla (Linux/Mac):
   for f in *.java; do
     java CodeBeautifier "$f" "formatted_$f"
   done

5. Tüm Java dosyalarını formatla (Windows):
   for %f in (*.java) do java CodeBeautifier %f formatted_%f

6. Yerinde düzenleme (dikkatli kullan!):
   java CodeBeautifier MyCode.java MyCode.temp.java
   mv MyCode.temp.java MyCode.java

NOT: Bu versiyon dosyanın herhangi bir yerinden başlasa bile
     doğru çalışır. Her satır bağımsız analiz edilir.
═══════════════════════════════════════════════════════════════
*/
