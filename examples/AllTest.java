import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

// Custom classes
import net.elena.murat.shape.*;
import net.elena.murat.lovert.*;
import net.elena.murat.material.*;
import net.elena.murat.material.pbr.*;
import net.elena.murat.math.*;
import net.elena.murat.light.*;
import net.elena.murat.util.*;

final public class AllTest extends Object {
  
  private AllTest() {
    super();
  }
  
  @Override
  public String toString() {
    return "AllTest";
  }
  
  /**
   * Generates and renders a scene containing a glass sphere on a checkered floor
   * with optimized lighting and camera settings. Renders using ray tracing with
   * configurable recursion depth and saves the output to a PNG file.
   * @param args Command-line arguments (optional recursion depth [1-5])
   * @throws IOException If image saving fails
   */
  private static void generateSaveRenderedImage(String[] args) throws Exception {
    // ===== 1. INITIALIZATION AND USAGE INFO =====
    System.out.println("ElenaMurat Ray Tracer - All Test\n");
    System.out.println("Usage: java -cp bin/guielena.jar:. AllTest 2");
    System.out.println("Default recursion depth: 3\n");
    
    // ===== 2. SCENE SETUP =====
    Scene scene = new Scene();
    System.out.println("[Scene] Scene instance created");
    
    // ===== 3. RAY TRACER CONFIGURATION =====
    final int imageWidth = 800;
    final int imageHeight = 600;
    final Color backgroundColor = new Color(250, 250, 250);
    
    ElenaMuratRayTracer rayTracer = new ElenaMuratRayTracer(
      scene,
      imageWidth,
      imageHeight,
      backgroundColor
    );
    rayTracer.setShadowColor(Color.BLACK);
    
    System.out.printf("[Renderer] Initialized %dx%d ray tracer\n", imageWidth, imageHeight);
    
    // ===== 4. CAMERA CONFIGURATION =====
    Camera camera = new Camera();
    // Optimal camera positioning for glass objects
    camera.setCameraPosition(new Point3(2.5, 2.0, 4.0));
    camera.setLookAt(new Point3(0, 1.8, 0));
    camera.setUpVector(new Vector3(0, 1, 0).normalize());
    camera.setFov(28.0);
    camera.setOrthographic(false);
    
    // Ray tracing features
    camera.setReflective(false);
    camera.setRefractive(true);
    camera.setShadowsEnabled(true);
    
    // Set recursion depth (3-5 recommended for glass)
    int recursionDepth = 3;
    if (args.length > 0) {
      try {
        recursionDepth = Math.min(5, Math.max(1, Integer.parseInt(args[0])));
        System.out.printf("[Config] Using recursion depth: %d\n", recursionDepth);
        } catch (NumberFormatException e) {
        System.err.println("[Warning] Invalid depth argument. Using default: 5");
      }
    }
    camera.setMaxRecursionDepth(recursionDepth);
    rayTracer.setCamera(camera);
    
    // ===== 5. OPTIMIZED LIGHTING SETUP =====
    // Ambient Light - reduced intensity
    ElenaMuratAmbientLight ambientLight = new ElenaMuratAmbientLight(
      new Color(180, 195, 210),
      0.7
    );
    scene.addLight(ambientLight);
    
    // For shiny metals:
    scene.addLight(new MuratPointLight(
        new Point3(2, 5, 3),
        new Color(255, 240, 220),
        1.5
    ));
    
    // For matte metals:
    scene.addLight(new ElenaDirectionalLight(
        new Vector3(-1, -1, -0.5).normalize(),
        new Color(255, 230, 210), // Soft light
        0.7
    ));
    
            scene.addLight(new BioluminescentLight(
            java.util.Arrays.asList(new Point3(2, 2, 2), new Point3(-2, 2, 2)), 
            Color.CYAN, 0.5, 1.0, 0.1
        ));
        
        scene.addLight(new BlackHoleLight(
            new Point3(0, 0, 0), 1.0, Color.MAGENTA, 2.0
        ));
        
        scene.addLight(new ElenaDirectionalLight(
            new Vector3(0, -1, 0), Color.WHITE, 1.0
        ));
        
        scene.addLight(new ElenaMuratAmbientLight(
            Color.WHITE, 0.3
        ));
        
        scene.addLight(new FractalLight(
            new Point3(3, 3, 3), Color.ORANGE, 1.5, 3, 0.5, 2.0
        ));
        
        scene.addLight(new MuratPointLight(
            new Point3(5, 5, 5), Color.WHITE, 1.0, 1.0, 0.1, 0.01
        ));
        
        scene.addLight(new PulsatingPointLight(
            new Point3(-3, 3, 0), Color.YELLOW, 1.0, 0.5, 0.2, 1.0
        ));
        
        scene.addLight(new SpotLight(
            new Point3(0, 5, 0), new Vector3(0, -1, 0), Color.WHITE, 2.0, 0.5, 1.0
        ));
        
    System.out.println("[Lighting] 3-point lighting setup complete");
    
    // ===== 6. OPTIMIZED FLOOR =====
    EMShape ground = new Plane(new Point3(0, 0, 0), new Vector3(0, 1, 0));
    Matrix4 m=new Matrix4 ();
    m=m.translate(new Vector3(0, -1.7, 0));
    //m=m.multiply (m.rotateZ (45));
    ground.setTransform(m); // Position below objects
    
    Material floorMaterial = new CheckerboardMaterial(
      // Colors (optimized for golden reflections)
      new Color(30, 30, 30),    // Dark gray (for reflection contrast)
      new Color(80, 80, 80),    // Medium gray (complements gold color)
      
      // Scale and lighting settings
      0.4,                      // Larger squares (matches gold size)
      1.25,                     // Ambient (for dark areas)
      0.5,                      // Diffuse (color saturation)
      
      // Reflection optimizations
      0.4,                      // Specular coefficient (soft reflection)
      15.0,                     // Shininess (slightly diffuse reflection)
      new Color(255, 230, 180), // Warm specular color (matches gold)
      
      // PBR properties
      0.15,                     // Low reflectivity (avoid unnecessary reflections)
      1.0,                      // IOR (opaque floor)
      0.0,                      // Transparency (opaque)
      
      // Transform
      ground.getInverseTransform()
    );
    
    ground.setMaterial(floorMaterial);
    scene.addShape(ground);
    System.out.println("[Geometry] Checkered floor added");
    
    // ===== 7. CHROME SPHERE =====
        
    Sphere sphere = new Sphere(1.0);
    Matrix4 m2=new Matrix4 ();
    m2=m2.translate(new Vector3(0, 1, 0));
    sphere.setTransform(m2);
    
    ////// TEST BLOG STARTS //////////
    final BufferedImage TBIM = new BufferedImage(100, 100, 1);
		Material mat = new ChromePBRMaterial();
		sphere.setMaterial(mat);
        mat = new AfricanKenteMaterial(); sphere.setMaterial(mat);
        mat = new AmberMaterial(); sphere.setMaterial(mat);
        mat = new AnodizedMetalMaterial(Color.RED); sphere.setMaterial(mat);
        mat = new AnodizedTextMaterial("Test", Color.RED, Color.BLUE, "horizontal", new Color(0,0,0,0), "Arial", Font.PLAIN, 24, 0, 0, null, 0, 0, 0, 0, Color.WHITE); sphere.setMaterial(mat);
        mat = new AuroraCeramicMaterial(Color.WHITE, Color.CYAN, 1.0); sphere.setMaterial(mat);
        mat = new BaklavaMaterial(Color.YELLOW, new Color(139,69,19), new Color(245,222,179), 10.0, 0.5, new Matrix4()); sphere.setMaterial(mat);
        mat = new BlackHoleMaterial(Matrix4.identity()); sphere.setMaterial(mat);
        mat = new BrightnessMaterial(Color.BLUE, 1.0, true); sphere.setMaterial(mat);
        mat = new BrunostCheeseMaterial(Color.ORANGE, new Color(139,69,19), 0.5); sphere.setMaterial(mat);
        mat = new CalligraphyRuneMaterial(Color.WHITE, Color.BLACK, Color.YELLOW, 1.0); sphere.setMaterial(mat);
        mat = new CarpetTextureMaterial(Color.RED, Color.YELLOW); sphere.setMaterial(mat);
        mat = new CeramicTilePBRMaterial(Color.WHITE, Color.GRAY, 0.5, 0.05, 0.1, 0.3, 0.1, 0.2, 0.5, 0.1, 0.8, 1.0); sphere.setMaterial(mat);
        mat = new CheckerboardMaterial(Color.BLACK, Color.WHITE, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new ChromePBRMaterial(Color.LIGHT_GRAY, 0.1, 0.0, 1.0, Color.WHITE); sphere.setMaterial(mat);
        mat = new CircleTextureMaterial(Color.BLUE, Color.YELLOW, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new CoffeeFjordMaterial(new Color(139,69,19), Color.BLUE, 0.5); sphere.setMaterial(mat);
        mat = new ContrastMaterial(Color.GREEN, 1.0, true); sphere.setMaterial(mat);
        mat = new CopperMaterial(); sphere.setMaterial(mat);
        mat = new CopperPBRMaterial(0.2, 0.3); sphere.setMaterial(mat);
        mat = new CrystalClearMaterial(Color.CYAN, 0.9, 1.5, 0.1, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new CrystalMaterial(Color.WHITE, Color.CYAN); sphere.setMaterial(mat);
        mat = new DamaskCeramicMaterial(Color.WHITE, Color.YELLOW, 32.0, 0.1, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new DewDropMaterial(Color.GREEN, Color.WHITE, 0.5, 0.1, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new DiagonalCheckerMaterial(Color.BLACK, Color.WHITE, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new DiamondMaterial(Color.WHITE, 2.4, 0.1, 0.9, 0.5, 0.5); sphere.setMaterial(mat);
        mat = new DielectricMaterial(Color.CYAN, 1.5, 0.8, 0.1); sphere.setMaterial(mat);
        mat = new DiffuseMaterial(Color.RED); sphere.setMaterial(mat);
        mat = new EdgeLightColorMaterial(); sphere.setMaterial(mat);
        mat = new EmeraldMaterial(Color.GREEN, 0.5, 0.1); sphere.setMaterial(mat);
        mat = new EmissiveMaterial(Color.YELLOW, 2.0); sphere.setMaterial(mat);
        mat = new FjordCrystalMaterial(Color.BLUE, Color.CYAN, 0.9); sphere.setMaterial(mat);
        mat = new FractalBarkMaterial(Matrix4.identity(), 0.5); sphere.setMaterial(mat);
        mat = new FractalFireMaterial(10, 0.5, 1.0, 0.1); sphere.setMaterial(mat);
        mat = new GhostTextMaterial("Test", Color.RED, Color.BLUE, "horizontal", "Arial", Font.PLAIN, 24, 0, 0, null, 0, 0, 0, 0, 0.5, 0.1, 1.5); sphere.setMaterial(mat);
        mat = new GlassicTilePBRMaterial(Color.WHITE, Color.GRAY, 0.5, 0.05, 0.1, 0.3); sphere.setMaterial(mat);
        mat = new GlassMaterial(Color.CYAN, 1.5, 0.1, 0.8); sphere.setMaterial(mat);
        mat = new GoldMaterial(); sphere.setMaterial(mat);
        mat = new GoldPBRMaterial(0.1); sphere.setMaterial(mat);
        mat = new GradientChessMaterial(Color.BLACK, Color.WHITE, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new GradientImageTextMaterial(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, null, 0.5f, 0.8f, "Test", new Font("Arial", Font.PLAIN, 24), StripeDirection.HORIZONTAL, 0.1, 1.0, 0.0, Matrix4.identity(), 0, 0, 0, 0, false); sphere.setMaterial(mat);
        mat = new GradientTextMaterial(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, "Test", new Font("Arial", Font.PLAIN, 24), StripeDirection.HORIZONTAL, 0.1, 1.0, 0.0, Matrix4.identity(), 0, 0); sphere.setMaterial(mat);
        mat = new GraniteMaterial(Color.GRAY, 0.5, 0.1, 0.1, new Matrix4()); sphere.setMaterial(mat);
        mat = new HamamSaunaMaterial(Color.WHITE, new Color(139,69,19), Color.LIGHT_GRAY, 0.5); sphere.setMaterial(mat);
        mat = new HexagonalHoneycombMaterial(Color.YELLOW, Color.BLACK, 0.5, 0.1); sphere.setMaterial(mat);
        mat = new HokusaiMaterial(Color.BLUE, Color.WHITE, 1.0, 1, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new HologramDataMaterial(0.5, 10); sphere.setMaterial(mat);
        mat = new HolographicDiffractionMaterial(Matrix4.identity(), 0.5); sphere.setMaterial(mat);
        mat = new HolographicPBRMaterial(Color.CYAN, 0.5, 0.1, 0.2, 0.0, 0.1, 0.3); sphere.setMaterial(mat);
        mat = new HotCopperMaterial(Color.ORANGE, Color.GREEN, 0.3); sphere.setMaterial(mat);
        mat = new HybridTextMaterial("Test", Color.RED, "Arial", Font.PLAIN, 24); sphere.setMaterial(mat);
        mat = new ImageTextureMaterial(null, 1.0, 1.0, 0.0, 0.0, 0.1, 0.7, 0.2, 32.0, 0.1, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new InvertLightColorMaterial(); sphere.setMaterial(mat);
        mat = new KilimRosemalingMaterial(Color.RED, Color.BLUE, Color.YELLOW, 0.5); sphere.setMaterial(mat);
        mat = new LambertMaterial(Color.GREEN, 0.1, 0.7); sphere.setMaterial(mat);
        mat = new LavaFlowMaterial(Color.RED, Color.BLACK, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new LightningMaterial(Color.CYAN, 1.0); sphere.setMaterial(mat);
        mat = new LinearGradientMaterial(Color.RED, Color.BLUE); sphere.setMaterial(mat);
        mat = new MarbleMaterial(Color.WHITE, Color.GRAY, 1.0, 0.5, 0.3, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new MarblePBRMaterial(Color.WHITE, Color.GRAY, 1.0, 0.5, 0.1, 0.1, 0.5); sphere.setMaterial(mat);
        mat = new MetallicMaterial(Color.LIGHT_GRAY, Color.WHITE, 0.5, 32.0, 0.1, 0.7, 0.2, new Matrix4()); sphere.setMaterial(mat);
        mat = new MirrorMaterial(Color.WHITE, 0.9, 1.0, new Matrix4()); sphere.setMaterial(mat);
        mat = new MoonSurfaceMaterial(); sphere.setMaterial(mat);
        mat = new MosaicMaterial(Color.WHITE, Color.BLUE, 0.5, 0.05, 0.1); sphere.setMaterial(mat);
        mat = new NazarMaterial(Color.BLUE, Color.WHITE, 1.0, 0.5, 0.1, new Matrix4()); sphere.setMaterial(mat);
        mat = new NeutralMaterial(Color.GRAY, 0.1, 0.0, 1.0); sphere.setMaterial(mat);
        mat = new NonScaledTransparentPNGMaterial(TBIM, 1.0, 1.0); sphere.setMaterial(mat);
        mat = new NordicWeaveMaterial(Color.RED, Color.BLUE, Color.YELLOW, 1.0); sphere.setMaterial(mat);
        mat = new NordicWoodMaterial(new Color(139,69,19), Color.DARK_GRAY, 0.5); sphere.setMaterial(mat);
        mat = new NorthernLightMaterial(Color.GREEN, Color.PINK, 1.0); sphere.setMaterial(mat);
        mat = new NorwegianRoseMaterial(new Color(139,69,19), Color.RED); sphere.setMaterial(mat);
        mat = new OpticalIllusionMaterial(Color.BLACK, Color.WHITE, 0.5, 0.1, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new OrbitalMaterial(Color.YELLOW, Color.BLUE, 0.1, 3, new Matrix4()); sphere.setMaterial(mat);
        mat = new PhongElenaMaterial(Color.GREEN, 0.1, 32.0, 0.1); sphere.setMaterial(mat);
        mat = new PhongMaterial(Color.RED, Color.WHITE, 32.0, 0.1, 0.7, 0.2, 0.1, 1.0, 0.0); sphere.setMaterial(mat);
        mat = new PhongTextMaterial("Test", Color.RED, null, "horizontal", new Color(0,0,0,0), "Arial", Font.BOLD, 100, 0, 0, null, 0, 0, 0, 0, Color.RED, Color.WHITE, 32.0, 0.1, 0.7, 0.2, 0.1, 1.0, 0.0); sphere.setMaterial(mat);
        mat = new PixelArtMaterial(new Color[]{Color.RED, Color.GREEN, Color.BLUE}, 0.1, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new PlasticPBRMaterial(Color.RED); sphere.setMaterial(mat);
        mat = new PlatinumMaterial(Matrix4.identity()); sphere.setMaterial(mat);
        mat = new PolkaDotMaterial(Color.WHITE, Color.RED); sphere.setMaterial(mat);
        mat = new ProceduralCloudMaterial(Color.WHITE, Color.LIGHT_GRAY); sphere.setMaterial(mat);
        mat = new ProceduralFlowerMaterial(5.0, Color.RED, Color.YELLOW); sphere.setMaterial(mat);
        mat = new PureWaterMaterial(Color.BLUE, 0.5); sphere.setMaterial(mat);
        mat = new QuantumFieldMaterial(new Color(128,0,128), Color.CYAN, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new RadialGradientMaterial(Color.RED, Color.BLUE); sphere.setMaterial(mat);
        mat = new RandomMaterial(Matrix4.identity()); sphere.setMaterial(mat);
        mat = new ReflectiveMaterial(Color.LIGHT_GRAY, 0.8, 0.1); sphere.setMaterial(mat);
        mat = new RosemalingMaterial(Color.WHITE, Color.RED, Color.GREEN, 0.5); sphere.setMaterial(mat);
        mat = new RoughMaterial(Color.GRAY, 0.5); sphere.setMaterial(mat);
        mat = new RubyMaterial(Color.RED, 0.5, 0.1); sphere.setMaterial(mat);
        mat = new RuneStoneMaterial(Color.GRAY, Color.WHITE, 0.5); sphere.setMaterial(mat);
        mat = new SalmonMaterial(Color.ORANGE, Color.PINK, Color.WHITE, 0.8, 0.3, new Matrix4()); sphere.setMaterial(mat);
        mat = new SandMaterial(Color.YELLOW, Color.ORANGE, 0.1, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new SilverMaterial(); sphere.setMaterial(mat);
        mat = new SilverPBRMaterial(Color.LIGHT_GRAY, 0.1, 0.9); sphere.setMaterial(mat);
        mat = new SimitMaterial(new Color(139,69,19), Color.YELLOW, Color.WHITE, 0.7, 0.8, new Matrix4()); sphere.setMaterial(mat);
        mat = new SmartGlassMaterial(Color.CYAN, 0.9); sphere.setMaterial(mat);
        mat = new SolidCheckerboardMaterial(Color.BLACK, Color.WHITE, 1.0, 0.1, 0.7, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new SolidColorMaterial(Color.MAGENTA); sphere.setMaterial(mat);
        mat = new SphereWordTextureMaterial("Test", Color.RED, "Arial", Font.PLAIN, 24); sphere.setMaterial(mat);
        mat = new SquaredMaterial(Color.BLACK, Color.WHITE, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new StainedGlassMaterial(Color.RED, 0.1, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new StarfieldMaterial(Matrix4.identity(), Color.BLUE, 0.01, 0.1, 0.5); sphere.setMaterial(mat);
        mat = new StarryNightMaterial(); sphere.setMaterial(mat);
        mat = new StripedMaterial(Color.RED, Color.WHITE, 0.5, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new SultanKingMaterial(Color.YELLOW, Color.RED, Color.BLUE, 1.0); sphere.setMaterial(mat);
        mat = new TelemarkPatternMaterial(Color.WHITE, Color.BLUE, Color.RED, 1.0); sphere.setMaterial(mat);
        mat = new TextDielectricMaterial("Test", Color.BLUE, "Arial", Font.BOLD, 18); sphere.setMaterial(mat);        
        mat = new TexturedCheckerboardMaterial(Color.BLACK, Color.WHITE, 1.0, "Test", Color.RED, Color.BLUE, "horizontal", new Color(0,0,0,0), "Arial", Font.PLAIN, 24, 0, 0, null, 0, 0, 0, 0, 0.1, 0.7, 0.2, 32.0, Color.WHITE, 0.1, 1.0, 0.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new TexturedPhongMaterial(Color.RED, Color.WHITE, 32.0, 0.1, 0.7, 0.2, 0.1, 1.0, 0.0, null, 0.0, 0.0, 1.0, 1.0, new Matrix4()); sphere.setMaterial(mat);
        mat = new TextureMaterial(TBIM, false); sphere.setMaterial(mat);
        mat = new ThresholdMaterial(Color.GRAY, 0.5, Color.WHITE, Color.BLACK, true, false); sphere.setMaterial(mat);
        mat = new TransparentEmissivePNGMaterial(null, 0.0, 0.0, 1.0, 1.0, false, Color.YELLOW, 1.0); sphere.setMaterial(mat);
        mat = new TransparentEmojiMaterial(null, Color.BLACK, Color.WHITE, 1.0, 0.0, 0.0, 1.0, 1.0, false, false); sphere.setMaterial(mat);
        mat = new TransparentPNGMaterial(null, 0.0, 0.0, 1.0, 1.0, false); sphere.setMaterial(mat);
        mat = new TriangleMaterial(Color.BLACK, Color.WHITE, 1.0, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new TulipFjordMaterial(Color.RED, Color.BLUE, Color.GREEN, 0.5); sphere.setMaterial(mat);
        mat = new TurkishDelightMaterial(Color.PINK, Color.WHITE, 0.5, 0.5, 0.3, new Matrix4()); sphere.setMaterial(mat);
        mat = new TurkishTileMaterial(Color.RED, Color.BLUE, 0.5); sphere.setMaterial(mat);
        mat = new VikingMetalMaterial(Color.LIGHT_GRAY, new Color(139,69,19), 0.3); sphere.setMaterial(mat);
        mat = new VikingRuneMaterial(Color.GRAY, Color.WHITE, 0.5); sphere.setMaterial(mat);
        mat = new WaterfallMaterial(Color.BLUE, 0.5); sphere.setMaterial(mat);
        mat = new WaterPBRMaterial(Color.BLUE, 0.1, 0.5, 0.1, 0.5); sphere.setMaterial(mat);
        mat = new WoodGrainMaterial(); sphere.setMaterial(mat);
        mat = new WoodMaterial(new Color(139,69,19), Color.DARK_GRAY, 0.5, 0.3, Matrix4.identity()); sphere.setMaterial(mat);
        mat = new WoodPBRMaterial(new Color(139,69,19), Color.DARK_GRAY, 0.5, 0.2, 0.1); sphere.setMaterial(mat);
        mat = new WordMaterial("Test", Color.RED, Color.WHITE, new Font("Arial", Font.PLAIN, 24), false, Color.BLUE, null, 100, 100); sphere.setMaterial(mat);
        mat = new XRayMaterial(Color.CYAN, 0.5, 0.1); sphere.setMaterial(mat);
    ///// TEST BLOG ENDS /////////////
    
    scene.addShape(sphere);
    System.out.println("[Geometry] Sphere added");
    
    // ===== 8. RENDERING =====
    System.out.println("\n=== RENDERING STARTED ===");
    System.out.printf("Resolution: %dx%d\n", imageWidth, imageHeight);
    System.out.println("Max Recursion: " + camera.getMaxRecursionDepth());
    System.out.println("Active Lights: " + scene.getLights().size());
    System.out.println("Scene Objects: " + scene.getShapes().size());
    
    long startTime = System.nanoTime();
    BufferedImage renderedImage = rayTracer.render();
    long durationMs = (System.nanoTime() - startTime) / 1_000_000;
    
    System.out.printf("Render completed in %.2f seconds\n", durationMs/1000.0);
    
    // ===== 9. OUTPUT =====
    String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    String filename = String.format(
      "images%schrome_render_%s_depth%d.png",
      File.separator, timestamp, recursionDepth
    );
    
    File outputFile = new File(filename);
    if (!outputFile.getParentFile().exists()) {
      outputFile.getParentFile().mkdirs();
    }
    
    try {
      ImageIO.write(renderedImage, "png", outputFile);
      System.out.println("Image saved to: " + outputFile.getAbsolutePath());
      } catch (IOException e) {
      System.err.println("Save failed: " + e.getMessage());
      throw e;
    }
    
    System.out.println("=== RENDER COMPLETE ===\n");
  }
  
  final public static void main(final String[] args) {
    try {
      generateSaveRenderedImage(args);
      } catch (Exception ioe) {
      ioe.printStackTrace();
      System.exit(-1);
    }
  }
  
}
//cd ..
//Compile: javac -cp bin/guielena.jar examples/AllTest -d examples > compilation_success.txt 2> compilation_errors.txt
//Run: java -cp bin/guielena.jar:examples AllTest 2
