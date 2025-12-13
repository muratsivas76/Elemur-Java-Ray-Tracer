public class ColorHexConverter {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Kullanım: java ColorHexConverter a r g b");
            System.exit(1);
        }
        
        float a = Float.parseFloat(args[0].replaceAll("[fF]$", ""));
        float r = Float.parseFloat(args[1].replaceAll("[fF]$", ""));
        float g = Float.parseFloat(args[2].replaceAll("[fF]$", ""));
        float b = Float.parseFloat(args[3].replaceAll("[fF]$", ""));
        
        System.out.printf("#%02x%02x%02x%02x%n", 
            Math.round(Math.max(0, Math.min(1, a)) * 255),
            Math.round(Math.max(0, Math.min(1, r)) * 255),
            Math.round(Math.max(0, Math.min(1, g)) * 255),
            Math.round(Math.max(0, Math.min(1, b)) * 255)
        );
    }
}