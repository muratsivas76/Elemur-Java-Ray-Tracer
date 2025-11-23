// Murat Inan
package net.elena.murat.gui;

// JAVA
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.imageio.ImageIO;

// CUSTOM
import net.elena.murat.light.*;
import net.elena.murat.material.*;
import net.elena.murat.material.pbr.*;
import net.elena.murat.shape.*;
import net.elena.murat.shape.letters.*;
import net.elena.murat.math.*;
import net.elena.murat.lovert.*;
import net.elena.murat.util.*;

public class ElenaRayTracerGUI extends JFrame {
  //private List<EMShape> TEMPSHAPES = new ArrayList<>();
  //private List<Light> TEMPLIGHTS = new ArrayList<>();
  
  private Camera camera = new Camera();
  private Color bgColor = Color.BLUE;
  private Color shadowColor = Color.BLACK;
  private int width = 800;
  private int height = 600;
  
  private int CURSCN = -1;
  
  private ElenaMuratRayTracer currentTracer = new ElenaMuratRayTracer(new Scene(), width, height, bgColor);
  
  private final JFileChooser textChooser = new JFileChooser();
  private final JFileChooser imageChooser = new JFileChooser();
  
  private final JButton fromFileBtn = getJButton("From File");
  private final JButton saveBtn = getJButton("Save");
  private final JButton renderBtn = getJButton("Render Scene");
  private final JButton startBtn = getJButton("Start");
  private final JButton generateSceneImagesButton = getJButton("Generate From Dir");
  
  private BufferedImage renderImage;
  //private JLabel renderLabel;
  private final RenderPanel renderPanel = new RenderPanel();
  
  private final JTextField countField = getJTextField("5");
  
  private int FRAME_COUNT = 5;
  
  private boolean isCancelledAnimation = false;
  
  //private Point3 TEMPPOS = new Point3(0, 0, 0);
  //private Point3 ORIGCAMPOS = new Point3(0, 0, 0);
  
  // Transform fields
  private final JTextField txField = getJTextField("0.0");
  private final JTextField tyField = getJTextField("0.0");
  private final JTextField tzField = getJTextField("0.0");
  private final JTextField rxField = getJTextField("0.0");
  private final JTextField ryField = getJTextField("0.0");
  private final JTextField rzField = getJTextField("0.0");
  private final JTextField sxField = getJTextField("1.0");
  private final JTextField syField = getJTextField("1.0");
  private final JTextField szField = getJTextField("1.0");
  
  // Camera & render
  private final JTextField camPosField = getJTextField("0,0,5");
  private final JTextField camLookField = getJTextField("0,0,0");
  private final JTextField camUpField = getJTextField("0,1,0");
  private final JTextField fovField = getJTextField("60");
  private final JTextField depthField = getJTextField("3");
  private final JTextField widthField = getJTextField("800");
  private final JTextField heightField = getJTextField("600");
  private final JTextField bgColorField = getJTextField("#FF000000");
  private final JTextField shadowColorField = getJTextField("#FF000000");
  private final JTextField camSumSubField = getJTextField("0,0,0:0,0,0");
  
  // Checkboxes
  private final JCheckBox reflcbox = getJCheckBox("Reflective:");
  private final JCheckBox refrcbox = getJCheckBox("Refractive:");
  private final JCheckBox shadowcbox = getJCheckBox("Shadow:");
  //private final JCheckBox ortocbox = getJCheckBox("Ortographic:");
  
  // Combos
  private JComboBox<String> shapeCombo;
  private JComboBox<String> materialCombo;
  private JComboBox<String> lightCombo;
  
  private boolean processing = false;
  
  // Text areas
  private final JTextArea shapeParamArea = getJTextArea(12, 55);
  private final JTextArea materialParamArea = getJTextArea(12, 55);
  private final JTextArea lightParamArea = getJTextArea(12, 55);
  private final JTextArea shapesListArea = getJTextArea(12,55);
  private final JTextArea lightsListArea = getJTextArea(12,55);
  
  public ElenaRayTracerGUI() {
    setTitle("Elena Ray Tracer GUI – ADDED by Murat Inan");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1300, 800);
    setLocationRelativeTo(null);
    
    textChooser.setFileFilter(new FilterText());
    imageChooser.setFileFilter(new FilterImage());
    
    currentTracer.setShadowColor(new Color(0, 0, 0));
    
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setForeground(Color.green.darker());
    tabbedPane.setFont(new Font("Serif", 1, 22));
    
    JButton eBtn = getJButton("EXIT");
    eBtn.setMnemonic(KeyEvent.VK_Q);
    eBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          System.exit(0);
        }
    });
    
    tabbedPane.addTab("Render View", createRenderTab());
    tabbedPane.addTab("Anim Settings", createAnimTab());
    tabbedPane.addTab("Camera & Renderer", createCameraTab());
    tabbedPane.addTab("Lights", createLightsTab());
    tabbedPane.addTab("Shapes & Materials", createShapesTab());
    tabbedPane.addTab("Exit", eBtn);
    
    add(tabbedPane);
    
    camera.setCameraPosition(new Point3(0, 0, 5));
    camera.setLookAt(new Point3(0, 0, 0));
    camera.setUpVector(new Vector3(0, 1, 0));
    camera.setFov(60.0);
    
    camera.setOrthographic(false);
    camera.setReflective(true);
    camera.setRefractive(true);
    camera.setShadowsEnabled(true);
    
    reflcbox.setSelected(true);
    refrcbox.setSelected(true);
    shadowcbox.setSelected(true);
    //ortocbox.setSelected(false);
    
    Utilities.lights.add(new ElenaMuratAmbientLight(Color.white, 1.0));
    Utilities.lights.add(new MuratPointLight(new Point3(5, 5, 5), Color.white, 1.0));
    
    textChooser.setCurrentDirectory(new File("."));
    imageChooser.setCurrentDirectory(new File("."));
    
    Utilities.setPane(renderPanel);
  }
  
  private JPanel createRenderTab() {
    JPanel panel = new JPanel(new BorderLayout());
    
    renderBtn.setMnemonic(KeyEvent.VK_R);
    renderBtn.setToolTipText("ALT+R");
    renderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (processing) return;
          
          processing = true;
          renderBtn.setEnabled(false);
          fromFileBtn.setEnabled(false);
          saveBtn.setEnabled(false);
          startBtn.setEnabled(false);
          generateSceneImagesButton.setEnabled(false);
          
          new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  renderScene();
                  } finally {
                  SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        processing = false;
                        renderBtn.setEnabled(true);
                        fromFileBtn.setEnabled(true);
                        saveBtn.setEnabled(true);
                        startBtn.setEnabled(true);
                        generateSceneImagesButton.setEnabled(true);
                      }
                  });
                }
              }
          }).start();
        }
    });
    
    fromFileBtn.setMnemonic(KeyEvent.VK_J);
    fromFileBtn.setToolTipText("ALT+J");
    fromFileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (processing) return;
          
          processing = true;
          renderBtn.setEnabled(false);
          fromFileBtn.setEnabled(false);
          saveBtn.setEnabled(false);
          startBtn.setEnabled(false);
          generateSceneImagesButton.setEnabled(false);
          
          new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  //renderScene();
                  int rep = textChooser.showOpenDialog(renderPanel);
                  if (rep != JFileChooser.APPROVE_OPTION) return;
                  File file = textChooser.getSelectedFile();
                  if (file.getName().toLowerCase().endsWith(".txt") == false) return;
                  
                  SceneParser parser = new SceneParser();
                  BufferedImage fimg = parser.renderScene(file);
                  renderPanel.setBufferedImage(fimg);
                  renderImage = fimg;
                  
                  Camera sceneCamera = parser.getCamera();
                  
                  Point3 pc = sceneCamera.getCameraPosition();
                  camPosField.setText("" + pc.x + ", " + pc.y + ", " + pc.z + "");
                  
                  pc = sceneCamera.getLookAt();
                  camLookField.setText("" + pc.x + ", " + pc.y + ", " + pc.z + "");
                  
                  Vector3 up = sceneCamera.getUpVector();
                  camUpField.setText("" + up.x + ", " + up.y + ", " + up.z + "");
                  
                  fovField.setText("" + sceneCamera.getFov());
                  depthField.setText("" + sceneCamera.getMaxRecursionDepth());
                  widthField.setText("" + parser.getWidth());
                  heightField.setText("" + parser.getHeight());
                  
                  StringBuffer fb = new StringBuffer();
                  Color c = parser.getBackgroundColor();
                  int alpha = c.getAlpha();
                  int red = c.getRed();
                  int green = c.getGreen();
                  int blue = c.getBlue();
                  fb.append("#");
                  fb.append(String.format("%02X", alpha));
                  fb.append(String.format("%02X", red));
                  fb.append(String.format("%02X", green));
                  fb.append(String.format("%02X", blue));
                  bgColorField.setText(fb.toString());
                  
                  fb = new StringBuffer();
                  c = parser.getShadowColor();
                  alpha = c.getAlpha();
                  red = c.getRed();
                  green = c.getGreen();
                  blue = c.getBlue();
                  fb.append("#");
                  fb.append(String.format("%02X", alpha));
                  fb.append(String.format("%02X", red));
                  fb.append(String.format("%02X", green));
                  fb.append(String.format("%02X", blue));
                  shadowColorField.setText(fb.toString());
                  
                  boolean cxrefl = sceneCamera.isReflective();
                  boolean cxrefr = sceneCamera.isRefractive();
                  boolean cxshdw = sceneCamera.isShadowsEnabled();
                  //boolean cxorto = sceneCamera.isOrthographic();
                  
                  reflcbox.setSelected(cxrefl);
                  refrcbox.setSelected(cxrefr);
                  shadowcbox.setSelected(cxshdw);
                  //ortocbox.setSelected(cxorto);
                  
                  Utilities.shapes.clear();
                  Utilities.shapes = parser.getSceneShapes();
                  
                  Utilities.lights = parser.getSceneLights();
                  
                  renderPanel.repaint();
                  } catch (Exception ioe) {
                  ioe.printStackTrace();
                  } finally {
                  SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        processing = false;
                        renderBtn.setEnabled(true);
                        fromFileBtn.setEnabled(true);
                        saveBtn.setEnabled(true);
                        startBtn.setEnabled(true);
                        generateSceneImagesButton.setEnabled(true);
                      }
                  });
                }
              }
          }).start();
        }
    });
    
    saveBtn.setMnemonic(KeyEvent.VK_S);
    saveBtn.setToolTipText("ALT+S");
    saveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveScene();
        }
    });
    
    JButton exit2Btn = getJButton("Exit");
    exit2Btn.setMnemonic(KeyEvent.VK_W);
    exit2Btn.setToolTipText("ALT+W");
    exit2Btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
    });
    
    JPanel xpanel = new JPanel(new GridLayout(0, 4, 0, 5));
    xpanel.add(renderBtn);
    xpanel.add(fromFileBtn);
    xpanel.add(saveBtn);
    xpanel.add(exit2Btn);
    
    panel.add(xpanel, BorderLayout.SOUTH);
    panel.add(renderPanel, BorderLayout.CENTER);
    return panel;
  }
  
  private JPanel createAnimTab() {
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    panel.add(getJLabel("Image/Frame Count:"));
    panel.add(countField);
    panel.add(getJLabel("SumSub Camera Anim Pos:"));
    panel.add(camSumSubField);
    
    startBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (processing) return;
          
          processing = true;
          isCancelledAnimation = false;
          renderBtn.setEnabled(false);
          fromFileBtn.setEnabled(false);
          saveBtn.setEnabled(false);
          startBtn.setEnabled(false);
          generateSceneImagesButton.setEnabled(false);
          
          new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  renderAnimScene();
                  //isCancelledAnimation = true;
                  } catch (Exception e) {
                  e.printStackTrace();
                  } finally {
                  SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        processing = false;
                        renderBtn.setEnabled(true);
                        fromFileBtn.setEnabled(true);
                        saveBtn.setEnabled(true);
                        startBtn.setEnabled(true);
                        generateSceneImagesButton.setEnabled(true);
                        isCancelledAnimation = true;
                      }
                  });
                }
              }
          }).start();
        }
    });
    panel.add(startBtn);
    
    JButton cancelBtn = getJButton("Cancel");
    cancelBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            isCancelledAnimation = true;
            } finally {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  isCancelledAnimation = true;
                }
            });
          }
        }
    });
    panel.add(cancelBtn);
    
    generateSceneImagesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          if (processing) return;
          
          processing = true;
          renderBtn.setEnabled(false);
          fromFileBtn.setEnabled(false);
          saveBtn.setEnabled(false);
          startBtn.setEnabled(false);
          generateSceneImagesButton.setEnabled(false);
          
          new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  //renderScene();
                  int rep = textChooser.showOpenDialog(renderPanel);
                  if (rep != JFileChooser.APPROVE_OPTION) return;
                  File file = textChooser.getSelectedFile();
                  if (file.getName().toLowerCase().endsWith(".txt") == false) return;
                  
                  File dir = new File(file.getParent());
                  if (dir == null) {
                    System.out.println("Null dir...");
                    return;
                  }
                  
                  if (dir.isDirectory() == false) {
                    System.out.println("Is not a directory...");
                    return;
                  }
                  
                  File[] files = dir.listFiles();
                  java.util.Arrays.sort(files);
                  final int len = files.length;
                  File sfile = null;
                  String name = "";
                  File mfile = null;
                  File imfi = new File("images");
                  if (imfi.exists() == false) imfi.mkdir();
                  
                  for (int i = 0; i < len; i++) {
                    sfile = files[i];
                    name = sfile.getName().toLowerCase();
                    if(name.endsWith(".txt") == false) continue;
                    
                    mfile = new File("images/" + (sfile.getName().replace(".txt", ".png")));
                    
                    SceneParser parser = new SceneParser();
                    BufferedImage fimg = parser.renderScene(sfile);
                    renderPanel.setBufferedImage(fimg);
                    renderImage = fimg;
                    
                    Utilities.shapes.clear();
                    Utilities.shapes = parser.getSceneShapes();
                    
                    Utilities.lights = parser.getSceneLights();
                    
                    renderPanel.repaint();
                    
                    ImageIO.write(renderImage, "PNG", mfile);
                    
                    System.out.println("Generated: " + (i+1) + "/" + len + "; images/" + mfile.getName());
                  }
                  } catch (Exception ioe) {
                  ioe.printStackTrace();
                  } finally {
                  SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        processing = false;
                        renderBtn.setEnabled(true);
                        fromFileBtn.setEnabled(true);
                        saveBtn.setEnabled(true);
                        startBtn.setEnabled(true);
                        generateSceneImagesButton.setEnabled(true);
                      }
                  });
                }
              }
          }).start();
        }
    });
    panel.add(generateSceneImagesButton);
    panel.add(getJLabel(""));
    
    return panel;
  }
  
  private JPanel createCameraTab() {
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    panel.add(getJLabel("Image Width:"));
    panel.add(widthField);
    panel.add(getJLabel("Image Height:"));
    panel.add(heightField);
    panel.add(getJLabel("BG Color (hex_argb):"));
    panel.add(bgColorField);
    panel.add(getJLabel("Shadow Color (hex_argb):"));
    panel.add(shadowColorField);
    panel.add(getJLabel("FOV (deg):"));
    panel.add(fovField);
    panel.add(getJLabel("Depth:"));
    panel.add(depthField);
    panel.add(getJLabel("Camera Pos (x,y,z):"));
    panel.add(camPosField);
    panel.add(getJLabel("Look At (x,y,z):"));
    panel.add(camLookField);
    panel.add(getJLabel("Up Vector (x,y,z):"));
    panel.add(camUpField);
    panel.add(reflcbox);
    panel.add(refrcbox);
    panel.add(shadowcbox);
    //panel.add(ortocbox);
    
    JButton applyBtn = getJButton("Apply Settings");
    applyBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          applyCameraSettings();
        }
    });
    panel.add(getJLabel(""));
    panel.add(getJLabel(""));
    panel.add(applyBtn);
    
    return panel;
  }
  
  private void applyCameraSettings() {
    try {
      width = Integer.parseInt(widthField.getText());
      height = Integer.parseInt(heightField.getText());
      bgColor = Utilities.parseHexColor(bgColorField.getText());
      shadowColor = Utilities.parseHexColor(shadowColorField.getText());
      double fov = Utilities.parseDouble(fovField.getText());
      int depth = Integer.parseInt(depthField.getText());
      Point3 pos = Utilities.parsePoint3(camPosField.getText());
      Point3 look = Utilities.parsePoint3(camLookField.getText());
      Vector3 up = Utilities.parseVector3(camUpField.getText());
      
      camera = new Camera();
      camera.setCameraPosition(pos);
      camera.setLookAt(look);
      camera.setUpVector(up);
      camera.setFov(fov);
      camera.setMaxRecursionDepth(depth);
      //camera.setOrthographic(ortocbox.isSelected());
      camera.setReflective(reflcbox.isSelected());
      camera.setRefractive(refrcbox.isSelected());
      camera.setShadowsEnabled(shadowcbox.isSelected());
      } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Invalid camera/render setting: " + ex.getMessage());
    }
  }
  
  private JPanel createLightsTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    lightCombo = new JComboBox<String>(Utilities.LIGHT_TYPES);
    lightCombo.setForeground(Color.GREEN.darker());
    lightCombo.setFont(new Font("Serif", 1, 20));
    JButton addLightBtn = getJButton("Add Light");
    
    addLightBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String type = (String) lightCombo.getSelectedItem();
          String template = Utilities.getLightTemplate(type);
          lightParamArea.setText(template);
          int result = JOptionPane.showConfirmDialog(ElenaRayTracerGUI.this,
            new JScrollPane(lightParamArea),
            "Edit " + type + " Parameters",
            JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE);
          if (result == JOptionPane.OK_OPTION) {
            Light light = Utilities.createLightFromText(type, lightParamArea.getText());
            if (light != null) {
              Utilities.lights.add(light);
            }
          }
        }
    });
    
    JPanel top = new JPanel();
    top.add(getJLabel("Light Type:"));
    top.add(lightCombo);
    top.add(addLightBtn);
    panel.add(top, BorderLayout.NORTH);
    
    JPanel fpanel = new JPanel(new GridLayout(0, 4, 0, 5));
    
    JButton changeAnimSettingsButton = getJButton("Change Anim Settings");
    changeAnimSettingsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          final int usize = Utilities.lights.size();
          if (usize < 1) return;
          String[] names = new String[usize];
          for (int i = 0; i < usize; i++) {
            names[i] = i + ": " + Utilities.getShortName(Utilities.lights.get(i).toString());
          }
          JList<String> list = new JList<String>(names);
          list.setForeground(Color.ORANGE.darker());
          list.setFont(new Font("Serif", 1, 20));
          list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          JScrollPane scroll = new JScrollPane(list);
          int resultx = JOptionPane.showConfirmDialog(renderPanel, scroll,
            "Select light for change its animation values:",
          JOptionPane.OK_CANCEL_OPTION);
          if (resultx != JOptionPane.OK_OPTION) return;
          int selected = list.getSelectedIndex();
          if (selected < 0) {
            JOptionPane.showMessageDialog(renderPanel, "Select a light please!");
            return;
          }
          
          Light light = Utilities.lights.get(selected);
          
          String input = JOptionPane.showInputDialog(renderPanel,
          "<html><body><font color=\"red\" size=\"5\">Enter inc dec values like this -><p>0.5:-0.5</font></body></html>");
          if (input == null) return;
          if (input.length() < 3) return;
          if (input.indexOf(":") < 0) return;
          
          String[] split = input.split(":");
          if (split == null) return;
          if (split.length < 2) return;
          
          double a = 0.0;
          double b = 0.0;
          
          try {
            a = Double.parseDouble(split[0]);
            b = Double.parseDouble(split[1]);
            } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            a = 0.0;
            b = 0.0;
          }
          
          double[] dbls = new double[] {a, b};
          light.setIncDecIntensity(dbls);
          Utilities.lights.set(selected, light);
          System.out.println("LIGHT WITH NEW ANIM VALS: " + Utilities.lights.get(selected));
          return;
        }
    });
    fpanel.add(changeAnimSettingsButton);
    
    JButton clearSelectedLightsBtn = getJButton("Clear Selected Lights");
    clearSelectedLightsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (Utilities.lights.isEmpty()) return;
          
          StringBuffer sb = new StringBuffer();
          
          final int ssize = Utilities.lights.size();
          
          for (int i = 0; i < ssize; i++) {
            sb.append("" + i + ": " + Utilities.getShortName(Utilities.lights.get(i).toString()) + "\n");
          }
          sb.append("\nPlease enter light numbers separated with comma\nfor remove after ###:\n");
          lightsListArea.setText (sb.toString());
          
          int result = JOptionPane.showConfirmDialog(renderPanel,
            new JScrollPane(lightsListArea),
            "Lights List:",
          JOptionPane.OK_CANCEL_OPTION);
          if (result != JOptionPane.OK_OPTION) return;
          
          String res = lightsListArea.getText();
          int index = res.lastIndexOf("###:");
          if (index < 0) return;
          
          res = (res.substring(index+4)).replaceAll("\n", "").trim();
          if (res.length() < 1) return;
          
          //if (res.indexOf(",") < 0) res = (res + ", ");
          String[] strs = res.split(",");
          int rslen = strs.length;
          
          int[] rs = new int[rslen];
          for (int i = 0; i < rslen; i++) {
            try {
              rs[i] = Integer.parseInt(strs[i]);
              } catch (NumberFormatException nfe) {
              System.out.println("ERROR: " + nfe.getMessage());
              continue;
            }
          }
          
          java.util.Arrays.sort(rs);
          for (int i = rslen-1; i >= 0; i--) {
            System.out.println("Removing: " + rs [i]);
            Utilities.lights.remove(rs[i]);
          }
          
          return;
        }
    });
    fpanel.add(clearSelectedLightsBtn);
    
    JButton clearLastLightBtn = getJButton("Clear Last Light");
    clearLastLightBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (Utilities.lights.isEmpty()) return;
          Utilities.lights.remove(Utilities.lights.size() - 1);
        }
    });
    fpanel.add(clearLastLightBtn);
    
    JButton clearAllLightsBtn = getJButton("Clear All Lights");
    clearAllLightsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Utilities.lights.clear();
        }
    });
    fpanel.add(clearAllLightsBtn);
    
    panel.add(fpanel, BorderLayout.SOUTH);
    
    return panel;
  }
  
  private JPanel createShapesTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    shapeCombo = new JComboBox<String>(Utilities.SHAPE_TYPES);
    shapeCombo.setFont(new Font("Serif", 1, 20));
    shapeCombo.setForeground(Color.GREEN.darker());
    materialCombo = new JComboBox<String>(Utilities.MATERIAL_TYPES);
    materialCombo.setFont(new Font("Serif", 1, 20));
    materialCombo.setForeground(Color.GREEN.darker());
    
    JButton addShapeBtn = getJButton("Add Shape");
    addShapeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addShape();
        }
    });
    
    JPanel top = new JPanel();
    top.add(getJLabel("Shape:"));
    top.add(shapeCombo);
    top.add(getJLabel("Material:"));
    top.add(materialCombo);
    top.add(addShapeBtn);
    panel.add(top, BorderLayout.NORTH);
    
    // Transform panel
    JPanel transPanel = new JPanel(new GridLayout(3, 4, 5, 5));
    transPanel.add(getJLabel("Tx: (+Right)")); transPanel.add(txField);
    transPanel.add(getJLabel("Ty: (+Up)")); transPanel.add(tyField);
    transPanel.add(getJLabel("Tz: (+Back)")); transPanel.add(tzField);
    transPanel.add(getJLabel("Rx:")); transPanel.add(rxField);
    transPanel.add(getJLabel("Ry:")); transPanel.add(ryField);
    transPanel.add(getJLabel("Rz:")); transPanel.add(rzField);
    transPanel.add(getJLabel("Sx:")); transPanel.add(sxField);
    transPanel.add(getJLabel("Sy:")); transPanel.add(syField);
    transPanel.add(getJLabel("Sz:")); transPanel.add(szField);
    panel.add(transPanel, BorderLayout.CENTER);
    
    JPanel zpanel = new JPanel(new GridLayout(0, 5, 0, 5));
    
    JButton changeTransformsButton = getJButton("Change Transforms");
    changeTransformsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          final int usize = Utilities.shapes.size();
          if (usize < 1) return;
          String[] names = new String[usize];
          for (int i = 0; i < usize; i++) {
            names[i] = i + ": " + Utilities.getShortName(Utilities.shapes.get(i).toString());
          }
          JList<String> list = new JList<String>(names);
          list.setForeground(Color.ORANGE.darker());
          list.setFont(new Font("Serif", 1, 20));
          list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          JScrollPane scroll = new JScrollPane(list);
          int resultx = JOptionPane.showConfirmDialog(renderPanel, scroll,
            "Select shape for change its transform values:",
          JOptionPane.OK_CANCEL_OPTION);
          if (resultx != JOptionPane.OK_OPTION) return;
          int selected = list.getSelectedIndex();
          if (selected < 0) {
            JOptionPane.showMessageDialog(renderPanel, "Select a shape please!");
            return;
          }
          
          EMShape shape = Utilities.shapes.get(selected);
          
          Object oinput = JOptionPane.showInputDialog(
            renderPanel,
            Utilities.VAL_SHAPE_STR_TR,
            Utilities.USER_INPUT,
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            Utilities.SHAPE_CHANGE_TEXT_TR
          );
          
          String input = "";
          if (oinput != null) {
            input = oinput.toString();
            } else {
            input = null;
          }
          
          if (input == null) return;
          if (input.length() < 12) return;
          if (input.indexOf("[") < 0) return;
          
          String val = Utilities.parseSingleXYZTransform(input);
          Matrix4 xtrans = Matrix4.createMatrixFromString(val);
          
          shape.setTransform(xtrans);
          Utilities.shapes.set(selected, shape);
          System.out.println("SHAPE WITH NEW TRANSFORM VALS: " + Utilities.shapes.get(selected));
          return;
        }
    });
    zpanel.add(changeTransformsButton);
    
    JButton changeAnimSettingsButton = getJButton("Change Anim Settings");
    changeAnimSettingsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          final int usize = Utilities.shapes.size();
          if (usize < 1) return;
          String[] names = new String[usize];
          for (int i = 0; i < usize; i++) {
            names[i] = i + ": " + Utilities.getShortName(Utilities.shapes.get(i).toString());
          }
          JList<String> list = new JList<String>(names);
          list.setForeground(Color.ORANGE.darker());
          list.setFont(new Font("Serif", 1, 20));
          list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          JScrollPane scroll = new JScrollPane(list);
          int resultx = JOptionPane.showConfirmDialog(renderPanel, scroll,
            "Select shape for change its animation values:",
          JOptionPane.OK_CANCEL_OPTION);
          if (resultx != JOptionPane.OK_OPTION) return;
          int selected = list.getSelectedIndex();
          if (selected < 0) {
            JOptionPane.showMessageDialog(renderPanel, "Select a shape please!");
            return;
          }
          
          EMShape shape = Utilities.shapes.get(selected);
          
          Object oinput = JOptionPane.showInputDialog(
            renderPanel,
            Utilities.VAL_SHAPE_STR,
            Utilities.USER_INPUT,
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            Utilities.SHAPE_CHANGE_TEXT
          );
          
          String input = "";
          if (oinput != null) {
            input = oinput.toString();
            } else {
            input = null;
          }
          
          if (input == null) return;
          if (input.length() < 25) return;
          if (input.indexOf(":") < 0) return;
          
          String[] vals = Utilities.parseXYZTransform(input);
          if (vals == null) return;
          if (vals.length < 2) return;
          
          Matrix4 a1m = Matrix4.createMatrixFromString(vals[0]);
          Matrix4 a2m = Matrix4.createMatrixFromString(vals[1]);
          
          shape.setAnimationTransforms(new Matrix4[]{a1m, a2m});
          Utilities.shapes.set(selected, shape);
          System.out.println("SHAPE WITH NEW ANIM VALS: " + Utilities.shapes.get(selected));
          return;
        }
    });
    zpanel.add(changeAnimSettingsButton);
    
    JButton clearSelectedShapesBtn = getJButton("Clear Selected Shapes");
    clearSelectedShapesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (Utilities.shapes.isEmpty()) return;
          
          StringBuffer sb = new StringBuffer();
          
          final int ssize = Utilities.shapes.size();
          
          for (int i = 0; i < ssize; i++) {
            sb.append("" + i + ": " + Utilities.getShortName(Utilities.shapes.get(i).toString()) + "\n");
          }
          sb.append("\nPlease enter shape numbers separated with comma\nfor remove after ###:\n");
          shapesListArea.setText (sb.toString());
          
          int result = JOptionPane.showConfirmDialog(renderPanel,
            new JScrollPane(shapesListArea),
            "Shapes List:",
          JOptionPane.OK_CANCEL_OPTION);
          if (result != JOptionPane.OK_OPTION) return;
          
          String res = shapesListArea.getText();
          int index = res.lastIndexOf("###:");
          if (index < 0) return;
          
          res = (res.substring(index+4)).replaceAll("\n", "").trim();
          if (res.length() < 1) return;
          
          //if (res.indexOf(",") < 0) res = (res + ", ");
          String[] strs = res.split(",");
          int rslen = strs.length;
          
          int[] rs = new int[rslen];
          for (int i = 0; i < rslen; i++) {
            try {
              rs[i] = Integer.parseInt(strs[i]);
              } catch (NumberFormatException nfe) {
              System.out.println("ERROR: " + nfe.getMessage());
              continue;
            }
          }
          
          java.util.Arrays.sort(rs);
          for (int i = rslen-1; i >= 0; i--) {
            System.out.println("Removing: " + rs [i]);
            Utilities.shapes.remove(rs[i]);
          }
          
          return;
        }
    });
    zpanel.add(clearSelectedShapesBtn);
    
    JButton clearLastShapeBtn = getJButton("Clear Last Shape");
    clearLastShapeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (Utilities.shapes.isEmpty()) return;
          Utilities.shapes.remove(Utilities.shapes.size() - 1);
        }
    });
    zpanel.add(clearLastShapeBtn);
    
    JButton clearAllShapesBtn = getJButton("Clear All Shapes");
    clearAllShapesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (Utilities.shapes.isEmpty()) return;
          Utilities.shapes.clear();
        }
    });
    zpanel.add(clearAllShapesBtn);
    
    panel.add(zpanel, BorderLayout.SOUTH);
    
    return panel;
  }
  
  private void addShape() {
    String shapeType = (String) shapeCombo.getSelectedItem();
    
    // CSG işlemleri
    if (shapeType.equals("UnionCSG") ||
      shapeType.equals("IntersectionCSG") ||
      shapeType.equals("DifferenceCSG")) {
      if (Utilities.shapes.size() < 2) {
        JOptionPane.showMessageDialog(this, "At least 2 shapes required for CSG.");
        return;
      }
      
      String shapeTemplate = Utilities.getShapeTemplate(shapeType);
      shapeParamArea.setText(shapeTemplate);
      int result = JOptionPane.showConfirmDialog(this,
        new JScrollPane(shapeParamArea),
        "Edit " + shapeType + " Parameters",
      JOptionPane.OK_CANCEL_OPTION);
      if (result != JOptionPane.OK_OPTION) return;
      String text = shapeParamArea.getText();
      Matrix4 animTransform1 = Utilities.parseAnimationTransform(text, "firstAnim_transform");
      Matrix4 animTransform2= Utilities.parseAnimationTransform(text, "secondAnim_transform");
      
      final int usize = Utilities.shapes.size();
      String[] names = new String[usize];
      for (int i = 0; i < usize; i++) {
        names[i] = i + ": " + Utilities.getShortName(Utilities.shapes.get(i).toString());
      }
      JList<String> list = new JList<String>(names);
      list.setForeground(Color.ORANGE.darker());
      list.setFont(new Font("Serif", 1, 20));
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      JScrollPane scroll = new JScrollPane(list);
      int resultx = JOptionPane.showConfirmDialog(this, scroll,
        "Select shapes for " + shapeType + " (min 2):",
      JOptionPane.OK_CANCEL_OPTION);
      if (resultx != JOptionPane.OK_OPTION) return;
      int[] selected = list.getSelectedIndices();
      if (selected.length < 2) {
        JOptionPane.showMessageDialog(this, "Select at least 2 shapes.");
        return;
      }
      if (shapeType.equals("DifferenceCSG") && selected.length != 2) {
        JOptionPane.showMessageDialog(this, "DifferenceCSG requires exactly 2 shapes.");
        return;
      }
      
      EMShape[] selectedShapes = new EMShape[selected.length];
      for (int i = 0; i < selected.length; i++) {
        selectedShapes[i] = Utilities.shapes.get(selected[i]);
      }
      
      EMShape csg = null;
      if (shapeType.equals("UnionCSG")) {
        csg = new UnionCSG(selectedShapes[0], selectedShapes[1]);
        for (int i = 2; i < selected.length; i++) {
          csg = new UnionCSG(csg, selectedShapes[i]);
        }
        } else if (shapeType.equals("IntersectionCSG")) {
        csg = new IntersectionCSG(selectedShapes[0], selectedShapes[1]);
        for (int i = 2; i < selected.length; i++) {
          csg = new IntersectionCSG(csg, selectedShapes[i]);
        }
        } else if (shapeType.equals("DifferenceCSG")) {
        csg = new DifferenceCSG(selectedShapes[0], selectedShapes[1]);
      }
      
      // Transform uygula
      Matrix4 T = buildTransform();
      csg.setTransform(T);
      csg.setAnimationTransforms(new Matrix4[]{animTransform1, animTransform2});
      
      // Materyal seç
      String matType = (String) materialCombo.getSelectedItem();
      String matTemplate = Utilities.getMaterialTemplate(matType);
      materialParamArea.setText(matTemplate);
      result = JOptionPane.showConfirmDialog(this,
        new JScrollPane(materialParamArea),
        "Edit Material for CSG",
      JOptionPane.OK_CANCEL_OPTION);
      Material mat = new DiffuseMaterial(Color.red);
      if (result == JOptionPane.OK_OPTION) {
        mat = Utilities.createMaterialFromText(matType, materialParamArea.getText());
        if (mat != null) {
          mat.setObjectTransform(T.inverse());
          csg.setMaterial(mat);
          } else {
          System.out.println("Null Material, using DiffuseMaterial...");
          mat = new DiffuseMaterial(Color.red);
          mat.setObjectTransform(T.inverse());
          csg.setMaterial(mat);
        }
        } else {
        System.out.println("Null Material, using DiffuseMaterial...");
        mat.setObjectTransform(T.inverse());
        csg.setMaterial(mat);
      }
      
      String leftDesc = "";
      String rightDesc = "";
      
      // Eski şekilleri sil, CSG'yi ekle
      for (int i = (selected.length - 1); i >= 0; i--) {
        Utilities.shapes.remove(selected[i]);
      }
      Utilities.shapes.add(csg);
      
      return;
    }
    
    // Normal şekiller
    String matType = (String) materialCombo.getSelectedItem();
    String shapeTemplate = Utilities.getShapeTemplate(shapeType);
    shapeParamArea.setText(shapeTemplate);
    int result = JOptionPane.showConfirmDialog(this,
      new JScrollPane(shapeParamArea),
      "Edit " + shapeType + " Parameters",
    JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;
    
    EMShape shape = Utilities.createShapeFromText(shapeType, shapeParamArea.getText());
    if (shape == null) return;
    
    Matrix4 T = buildTransform();
    shape.setTransform(T);
    
    String matTemplate = Utilities.getMaterialTemplate(matType);
    materialParamArea.setText(matTemplate);
    result = JOptionPane.showConfirmDialog(this,
      new JScrollPane(materialParamArea),
      "Edit " + matType + " Parameters",
    JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      Material mat = Utilities.createMaterialFromText(matType, materialParamArea.getText());
      if (mat != null) {
        mat.setObjectTransform(T.inverse());
        shape.setMaterial(mat);
        } else {
        System.out.println("Null material, using diffuse instead...");
        mat = new DiffuseMaterial(Color.red);
        mat.setObjectTransform(T.inverse());
        shape.setMaterial(mat);
      }
      } else {
      System.out.println("Cancelled material, using diffuse instead...");
      Material mat = new DiffuseMaterial(Color.red);
      mat.setObjectTransform(T.inverse());
      shape.setMaterial(mat);
    }
    
    Utilities.shapes.add(shape);
    
    return;
  }
  
  private void renderAnimScene() throws Exception {
    final int totalFrames = Integer.parseInt(countField.getText());
    if (Utilities.shapes.isEmpty()) return;
    
    String campos = camSumSubField.getText();
    String[] splitz = campos.split(":");
    String[] delta1 = splitz[0].split(",");
    String[] delta2 = splitz[1].split(",");
    
    Point3 camDelta1 = new Point3(Double.parseDouble(delta1[0]), Double.parseDouble(delta1[1]), Double.parseDouble(delta1[2]));
    Point3 camDelta2 = new Point3(Double.parseDouble(delta2[0]), Double.parseDouble(delta2[1]), Double.parseDouble(delta2[2]));
    Point3 originalCamPos = camera.getCameraPosition();
    
    // Save original values for final reset
    List<Matrix4> originalTransforms = new ArrayList<>();
    List<Matrix4[]> originalAnimTransforms = new ArrayList<>();
    List<Double> originalIntensities = new ArrayList<>();
    
    for (EMShape shape : Utilities.shapes) {
      originalTransforms.add(shape.getTransform());
      originalAnimTransforms.add(shape.getAnimationTransforms());
    }
    for (Light light : Utilities.lights) {
      originalIntensities.add(light.getIntensity());
    }
    
    // Store end values of first half for second half
    List<Matrix4> firstHalfEndTransforms = new ArrayList<>();
    List<Double> firstHalfEndIntensities = new ArrayList<>();
    
    // Animation directory
    File animDir = new File("animImages/scene_" + Utilities.createDateString() + "_" + (String.format("%03d", ++CURSCN)));
    while(animDir.exists()) animDir = new File("animImages/scene_" + Utilities.createDateString() + "_" + (String.format("%03d", ++CURSCN)));
    animDir.mkdir();
    new File("guiScenes/" + animDir.getName()).mkdir();
    
    // ALL FRAMES - One loop
    for (int frame = 0; frame < totalFrames * 2; frame++) {
      if (isCancelledAnimation) break;
      
      boolean isFirstHalf = frame < totalFrames;
      int animIndex = isFirstHalf ? 0 : 1;
      // Fixed: relativeFrame starts from 1 in both halves
      int relativeFrame = isFirstHalf ? frame + 1 : frame - totalFrames + 1;
      
      // CAMERA MOVEMENT - Fixed
      Point3 currentCamPos;
      if (isFirstHalf) {
        currentCamPos = originalCamPos.add(camDelta1.multiply(relativeFrame));
        } else {
        currentCamPos = originalCamPos.add(camDelta1.multiply(totalFrames)).add(camDelta2.multiply(relativeFrame));
      }
      camera.setCameraPosition(currentCamPos);
      
      // UPDATE SHAPE TRANSFORMS
      for (int i = 0; i < Utilities.shapes.size(); i++) {
        EMShape shape = Utilities.shapes.get(i);
        Matrix4 animT = originalAnimTransforms.get(i)[animIndex];
        
        Matrix4 scaledAnimT = new Matrix4();
        scaledAnimT.setTx(animT.getTx() * relativeFrame);
        scaledAnimT.setTy(animT.getTy() * relativeFrame);
        scaledAnimT.setTz(animT.getTz() * relativeFrame);
        scaledAnimT.setRx(animT.getRx() * relativeFrame);
        scaledAnimT.setRy(animT.getRy() * relativeFrame);
        scaledAnimT.setRz(animT.getRz() * relativeFrame);
        scaledAnimT.setSx(((animT.getSx()-1) * relativeFrame) + 1);
        scaledAnimT.setSy(((animT.getSy()-1) * relativeFrame) + 1);
        scaledAnimT.setSz(((animT.getSz()-1) * relativeFrame) + 1);
        
        // Use different base values for second half
        Matrix4 baseTransform;
        if (isFirstHalf) {
          baseTransform = originalTransforms.get(i);
          } else {
          baseTransform = firstHalfEndTransforms.get(i);
        }
        
        Matrix4 newTransform = Matrix4.add(baseTransform, scaledAnimT);
        shape.setTransform(newTransform);
      }
      
      for (int i = 0; i < Utilities.lights.size(); i++) {
        Light light = Utilities.lights.get(i);
        double[] incDec = light.getIncDecIntensity();
        double baseIntensity = originalIntensities.get(i);
        
        if (isFirstHalf) {
          // First half: base + (inc × relativeFrame) - now using relativeFrame for consistency
          double newIntensity = baseIntensity + (incDec[0] * relativeFrame);
          light.setIntensity(newIntensity);
          } else {
          // Second half: (first half max) + (dec × relativeFrame)
          // Fixed: firstHalfMax uses totalFrames instead of /2 to reach correct peak
          double firstHalfMax = baseIntensity + (incDec[0] * totalFrames);
          double newIntensity = firstHalfMax + (incDec[1] * relativeFrame);
          light.setIntensity(newIntensity);
        }
      }
      
      // Store end values of first half when we reach the last frame of first half
      if (isFirstHalf && frame == totalFrames - 1) {
        firstHalfEndTransforms.clear();
        firstHalfEndIntensities.clear();
        for (EMShape shape : Utilities.shapes) {
          firstHalfEndTransforms.add(shape.getTransform());
        }
        for (Light light : Utilities.lights) {
          firstHalfEndIntensities.add(light.getIntensity());
        }
      }
      
      // RENDER
      Scene scene = new Scene();
      for (EMShape s : Utilities.shapes) scene.addShape(s);
      for (Light l : Utilities.lights) scene.addLight(l);
      
      ElenaMuratRayTracer tracer = new ElenaMuratRayTracer(scene, width, height, bgColor);
      tracer.setCamera(camera);
      if (shadowColor != null) tracer.setShadowColor(shadowColor);
      
      renderImage = tracer.render();
      renderPanel.setBufferedImage(renderImage);
      renderPanel.repaint();
      
      // SAVE
      File file = new File(animDir.getPath() + "/animScene_" + String.format("%03d", frame) + ".png");
      ImageIO.write(renderImage, "PNG", file);
      saveSceneFile(file, Utilities.shapes, Utilities.lights, animDir.getName());
      
      System.out.println("Frame " + (frame + 1) + "/" + (totalFrames * 2) + " saved");
      try { Thread.sleep(100); } catch (InterruptedException ex) { }
      }
    
    // RESTORE ORIGINAL VALUES
    camera.setCameraPosition(originalCamPos);
    for (int i = 0; i < Utilities.shapes.size(); i++) {
      Utilities.shapes.get(i).setTransform(originalTransforms.get(i));
    }
    for (int i = 0; i < Utilities.lights.size(); i++) {
      Utilities.lights.get(i).setIntensity(originalIntensities.get(i));
    }
    
    JOptionPane.showMessageDialog(renderPanel,
    "<html><body><font color='red' size='5'>Animation completed! " + (totalFrames * 2) + " frames</font></body></html>");
  } // Entire method is this
  
  private void renderScene() {
    try {
      Scene scene = new Scene();
      for (EMShape s : Utilities.shapes) {
        scene.addShape(s);
        System.out.println(s);
        System.out.println("MATERIAL/TEXTURE:" + Utilities.getShortName(s.getMaterial().toString()));
      }
      for (Light l : Utilities.lights) {
        scene.addLight(l);
        System.out.println(Utilities.getShortName(l.toString()));
      }
      
      ElenaMuratRayTracer tracer = new ElenaMuratRayTracer(scene, width, height, bgColor);
      if (camera != null) tracer.setCamera(camera);
      //System.out.println("" + camera.toString());
      if (shadowColor != null) tracer.setShadowColor(shadowColor);
      
      currentTracer = tracer;
      renderImage = tracer.render();
      
      renderPanel.setBufferedImage(renderImage);
      renderPanel.repaint();
      } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Render failed: " + ex.getMessage());
    }
  }
  
  private void saveScene() {
    if (renderImage == null) return;
    
    int rep=imageChooser.showSaveDialog (renderPanel);
    if (rep != JFileChooser.APPROVE_OPTION) return;
    
    File f=imageChooser.getSelectedFile ();
    
    String format = getFormat(f);
    
    boolean errored = false;
    
    try {
      ImageIO.write(renderImage, format, f);
      System.out.println("Saved: " + f.getName());
      JOptionPane.showMessageDialog(renderPanel, "<html><body>Saved: <font size=\"5\">"+f.getName()+"</font></body></html>");
      } catch (IOException ioe) {
      ioe.printStackTrace();
      errored = true;
    }
    
    if (errored) return;
    
    try {
      saveSceneFileX(f);
      } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    
    return;
  }
  
  private final String getFormat(File f) {
    String name = (f.getName()).toLowerCase(java.util.Locale.ENGLISH);
    
    String FMT = "PNG";
    if (name.endsWith(".png")) {
      FMT = "PNG";
      } else if (name.endsWith(".jpg")) {
      FMT = "JPG";
      } else if (name.endsWith(".jpeg")) {
      FMT = "JPEG";
      } else {
      FMT = "PNG";
    }
    
    return FMT;
  }
  
  private final void saveSceneFile(File fx) throws IOException {
    String name = fx.getName();
    int index = name.lastIndexOf(".");
    if (index < 0) return;
    name = name.substring(0, index);
    
    name = "guiScenes/" + name + ".txt";
    
    File f = new File(name);
    
    OutputStream fos = new FileOutputStream(f);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");
    
    ps.println("# ===================================================");
    ps.println("# " + f.getName());
    ps.println("# ===================================================\n");
    
    ps.println("# Camera Settings");
    ps.println(camera.toString());
    
    ps.println("# Renderer Settings");
    ps.println(currentTracer.toString());
    
    ps.println("# Lighting");
    
    final int lsize = Utilities.lights.size();
    for (int i = 0; i < lsize; i++) {
      ps.println(Utilities.lights.get(i).toString());
      ps.println("");
    }
    
    ps.println("# Geometries/Shapes");
    
    final int ssize = Utilities.shapes.size();
    for (int i = 0; i < ssize; i++) {
      ps.println(Utilities.shapes.get(i).toString());
      if (i != ssize-1) ps.println("");
    }
    
    ps.println("\n-END-");
    
    ps.flush();
    ps.close();
    fos.flush();
    fos.close();
    
    System.out.println("Saved: " + name);
    
    return;
  }
  
  private final void saveSceneFileX(File fx) throws IOException {
    String name = fx.getName();
    int index = name.lastIndexOf(".");
    if (index < 0) return;
    name = name.substring(0, index);
    
    name = "scenes/" + name + ".txt";
    
    File f = new File(name);
    
    OutputStream fos = new FileOutputStream(f);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");
    
    ps.println("# ===================================================");
    ps.println("# " + f.getName());
    ps.println("# ===================================================\n");
    
    ps.println("# Camera Settings");
    ps.println(camera.toString());
    
    ps.println("# Renderer Settings");
    ps.println(currentTracer.toString());
    
    ps.println("# Lighting");
    
    final int lsize = Utilities.lights.size();
    for (int i = 0; i < lsize; i++) {
      ps.println(Utilities.lights.get(i).toString());
      ps.println("");
    }
    
    ps.println("# Geometries/Shapes");
    
    final int ssize = Utilities.shapes.size();
    for (int i = 0; i < ssize; i++) {
      ps.println(Utilities.shapes.get(i).toString());
      if (i != ssize-1) ps.println("");
    }
    
    ps.println("\n-END-");
    
    ps.flush();
    ps.close();
    fos.flush();
    fos.close();
    
    System.out.println("Saved: " + name);
    
    return;
  }
  
  private final void saveSceneFile(File fx,
    java.util.List<EMShape> scShapes,
    java.util.List<Light> scLights,
    String sceneDirName) throws IOException {
    String name = fx.getName();
    int index = name.lastIndexOf(".");
    if (index < 0) return;
    name = name.substring(0, index);
    
    name = "guiScenes/" + sceneDirName + "/" + name + ".txt";
    
    File f = new File(name);
    
    OutputStream fos = new FileOutputStream(f);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");
    
    ps.println("# ===================================================");
    ps.println("# " + f.getName());
    ps.println("# ===================================================\n");
    
    ps.println("# Camera Settings");
    ps.println(camera.toString());
    
    ps.println("# Renderer Settings");
    ps.println(currentTracer.toString());
    
    ps.println("# Lighting");
    
    final int lsize = scLights.size();
    for (int i = 0; i < lsize; i++) {
      ps.println(scLights.get(i).toString());
      ps.println("");
    }
    
    ps.println("# Geometries/Shapes");
    
    final int ssize = scShapes.size();
    for (int i = 0; i < ssize; i++) {
      ps.println(scShapes.get(i).toString());
      if (i != ssize-1) ps.println("");
    }
    
    ps.println("\n-END-");
    
    ps.flush();
    ps.close();
    fos.flush();
    fos.close();
    
    System.out.println("Saved: " + f.getName());
    return;
  }
  
  private final String toTransformString() {
    StringBuffer sb = new StringBuffer();
    sb.append("translate(");
    sb.append(txField.getText());
    sb.append(", ");
    sb.append(tyField.getText());
    sb.append(", ");
    sb.append(tzField.getText());
    sb.append(") * rotate(");
    sb.append(rxField.getText());
    sb.append(", ");
    sb.append(ryField.getText());
    sb.append(", ");
    sb.append(rzField.getText());
    sb.append(") * scale(");
    sb.append(sxField.getText());
    sb.append(", ");
    sb.append(syField.getText());
    sb.append(", ");
    sb.append(szField.getText());
    sb.append(");");
    
    return sb.toString();
  }
  
  ///////////////
  private final JTextField getJTextField(String s) {
    JTextField cmp = new JTextField(s);
    cmp.setForeground(Color.blue);
    cmp.setFont(new Font("Arial", 1, 20));
    return cmp;
  }
  
  private final JTextArea getJTextArea(int rww, int clm) {
    JTextArea cmp = new JTextArea(rww, clm);
    cmp.setForeground(Color.blue);
    cmp.setFont(new Font("Arial", 1, 20));
    return cmp;
  }
  
  private final JButton getJButton(String s) {
    JButton cmp = new JButton(s);
    cmp.setForeground(Color.red);
    cmp.setFont(new Font("Arial", 1, 20));
    return cmp;
  }
  
  private final JLabel getJLabel(String s) {
    JLabel cmp = new JLabel(s, JLabel.CENTER);
    cmp.setForeground(Color.red);
    cmp.setFont(new Font("Arial", 1, 20));
    return cmp;
  }
  
  private final JCheckBox getJCheckBox(String s) {
    JCheckBox cmp = new JCheckBox(s);
    cmp.setForeground(Color.orange.darker());
    cmp.setFont(new Font("Serif", 1, 16));
    return cmp;
  }
  ///////////////
  private final class RenderPanel extends JPanel {
    private BufferedImage bmg = null;
    
    private RenderPanel() {
      super(true);
    }
    
    private BufferedImage getBufferedImage() {
      return bmg;
    }
    
    private void setBufferedImage(BufferedImage b) {
      this.bmg = b;
    }
    
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      if (this.bmg != null) {
        g.drawImage(this.bmg, 0, 0, this);
      }
      
      return;
    }
  }
  
  // ——————— UTILS ———————
  private Matrix4 buildTransform() {
    double tx = Utilities.parseDouble(txField.getText());
    double ty = Utilities.parseDouble(tyField.getText());
    double tz = Utilities.parseDouble(tzField.getText());
    double rx = Utilities.parseDouble(rxField.getText());
    double ry = Utilities.parseDouble(ryField.getText());
    double rz = Utilities.parseDouble(rzField.getText());
    double sx = Utilities.parseDouble(sxField.getText());
    double sy = Utilities.parseDouble(syField.getText());
    double sz = Utilities.parseDouble(szField.getText());
    
    Matrix4 T = Matrix4.identity();
    T = T.multiply(Matrix4.translate(tx, ty, tz));
    T = T.multiply(Matrix4.rotateZ(rz));
    T = T.multiply(Matrix4.rotateY(ry));
    T = T.multiply(Matrix4.rotateX(rx));
    T = T.multiply(Matrix4.scale(sx, sy, sz));
    return T;
  }
  
  ///////////
  private static final void forCompileNames() {
    AmberMaterial ambermaterial_variable = null;
    AnodizedMetalMaterial anodizedmetalmaterial_variable = null;
    AnodizedTextMaterial anodizedtextmaterial_variable = null;
    AreaLight arealight_variable = null;
    AuroraCeramicMaterial auroraceramicmaterial_variable = null;
    BaklavaMaterial baklavamaterial_variable = null;
    BioluminescentLight bioluminescentlight_variable = null;
    BlackHoleLight blackholelight_variable = null;
    BlackHoleMaterial blackholematerial_variable = null;
    net.elena.murat.shape.Box box_variable = null;
    BrightnessMaterial brightnessmaterial_variable = null;
    BrunostCheeseMaterial brunostcheesematerial_variable = null;
    BumpMaterial bumpmaterial_variable = null;
    CSGShape csgshape_variable = null;
    CalligraphyRuneMaterial calligraphyrunematerial_variable = null;
    Camera camera_variable = null;
    CarpetTextureMaterial carpettexturematerial_variable = null;
    CeramicTilePBRMaterial ceramictilepbrmaterial_variable = null;
    CheckerboardMaterial checkerboardmaterial_variable = null;
    ChromePBRMaterial chromepbrmaterial_variable = null;
    CiniMaterial cinimaterial_variable = null;
    CircleTextureMaterial circletexturematerial_variable = null;
    CoffeeFjordMaterial coffeefjordmaterial_variable = null;
    ColorUtil colorutil_variable = null;
    Cone cone_variable = null;
    ContrastMaterial contrastmaterial_variable = null;
    CopperMaterial coppermaterial_variable = null;
    CopperPBRMaterial copperpbrmaterial_variable = null;
    Crescent crescent_variable = null;
    CrystalClearMaterial crystalclearmaterial_variable = null;
    CrystalMaterial crystalmaterial_variable = null;
    Cube cube_variable = null;
    Cylinder cylinder_variable = null;
    DamaskCeramicMaterial damaskceramicmaterial_variable = null;
    DewDropMaterial dewdropmaterial_variable = null;
    DiagonalCheckerMaterial diagonalcheckermaterial_variable = null;
    DiamondMaterial diamondmaterial_variable = null;
    DielectricMaterial dielectricmaterial_variable = null;
    DifferenceCSG differencecsg_variable = null;
    DiffuseMaterial diffusematerial_variable = null;
    DynamicGlassMaterial dynamicglassmaterial_variable = null;
    //EMShape emshape_variable = null;
    EdgeLightColorMaterial edgelightcolormaterial_variable = null;
    ElenaDirectionalLight elenadirectionallight_variable = null;
    ElenaMuratAmbientLight elenamuratambientlight_variable = null;
    ElenaMuratRayTracer elenamuratraytracer_variable = null;
    //ElenaRayTracerGUI elenaraytracergui_variable = null;
    ElenaTextureMaterial elenatexturematerial_variable = null;
    Ellipsoid ellipsoid_variable = null;
    EmeraldMaterial emeraldmaterial_variable = null;
    EmissiveMaterial emissivematerial_variable = null;
    EmojiBillboard emojibillboard_variable = null;
    FjordCrystalMaterial fjordcrystalmaterial_variable = null;
    FloatColor floatcolor_variable = null;
    FractalBarkMaterial fractalbarkmaterial_variable = null;
    FractalFireMaterial fractalfirematerial_variable = null;
    FractalLight fractallight_variable = null;
    GhostTextMaterial ghosttextmaterial_variable = null;
    GlassMaterial glassmaterial_variable = null;
    GlassicTilePBRMaterial glassictilepbrmaterial_variable = null;
    GoldMaterial goldmaterial_variable = null;
    GoldPBRMaterial goldpbrmaterial_variable = null;
    GradientChessMaterial gradientchessmaterial_variable = null;
    GradientImageTextMaterial gradientimagetextmaterial_variable = null;
    GradientTextMaterial gradienttextmaterial_variable = null;
    GraniteMaterial granitematerial_variable = null;
    HamamSaunaMaterial hamamsaunamaterial_variable = null;
    HexagonalHoneycombMaterial hexagonalhoneycombmaterial_variable = null;
    HologramDataMaterial hologramdatamaterial_variable = null;
    HolographicDiffractionMaterial holographicdiffractionmaterial_variable = null;
    HolographicPBRMaterial holographicpbrmaterial_variable = null;
    HotCopperMaterial hotcoppermaterial_variable = null;
    HybridTextMaterial hybridtextmaterial_variable = null;
    Hyperboloid hyperboloid_variable = null;
    Image3D image3d_variable = null;
    ImageTexture imagetexture_variable = null;
    ImageTextureMaterial imagetexturematerial_variable = null;
    ImageUtils3D imageutils3d_variable = null;
    Intersection intersection_variable = null;
    IntersectionCSG intersectioncsg_variable = null;
    IntersectionInterval intersectioninterval_variable = null;
    InvertLightColorMaterial invertlightcolormaterial_variable = null;
    IsotropicMetalTextMaterial isotropicmetaltextmaterial_variable = null;
    KilimRosemalingMaterial kilimrosemalingmaterial_variable = null;
    LambertMaterial lambertmaterial_variable = null;
    LavaFlowMaterial lavaflowmaterial_variable = null;
    Letter3D letter3d_variable = null;
    LetterUtils3D letterutils3d_variable = null;
    Light light_variable = null;
    LightProperties lightproperties_variable = null;
    LightningMaterial lightningmaterial_variable = null;
    LinearGradientMaterial lineargradientmaterial_variable = null;
    MarbleMaterial marblematerial_variable = null;
    MarblePBRMaterial marblepbrmaterial_variable = null;
    //Material material_variable = null;
    MaterialType materialtype_variable = null;
    MaterialUtils materialutils_variable = null;
    MathUtil mathutil_variable = null;
    Matrix3 matrix3_variable = null;
    Matrix4 matrix4_variable = null;
    MetallicMaterial metallicmaterial_variable = null;
    MirrorMaterial mirrormaterial_variable = null;
    MoonSurfaceMaterial moonsurfacematerial_variable = null;
    MosaicMaterial mosaicmaterial_variable = null;
    MultiMixMaterial multimixmaterial_variable = null;
    MuratPointLight muratpointlight_variable = null;
    NazarMaterial nazarmaterial_variable = null;
    NeutralMaterial neutralmaterial_variable = null;
    NoiseUtil noiseutil_variable = null;
    NonScaledTransparentPNGMaterial nonscaledtransparentpngmaterial_variable = null;
    NordicWeaveMaterial nordicweavematerial_variable = null;
    NordicWoodMaterial nordicwoodmaterial_variable = null;
    NorthernLightMaterial northernlightmaterial_variable = null;
    NorwegianRoseMaterial norwegianrosematerial_variable = null;
    ObsidianMaterial obsidianmaterial_variable = null;
    OpticalIllusionMaterial opticalillusionmaterial_variable = null;
    OrbitalMaterial orbitalmaterial_variable = null;
    //PBRCapableMaterial pbrcapablematerial_variable = null;
    PhongElenaMaterial phongelenamaterial_variable = null;
    PhongMaterial phongmaterial_variable = null;
    PhongTextMaterial phongtextmaterial_variable = null;
    PixelArtMaterial pixelartmaterial_variable = null;
    Plane plane_variable = null;
    PlasticPBRMaterial plasticpbrmaterial_variable = null;
    PlatinumMaterial platinummaterial_variable = null;
    Point3 point3_variable = null;
    PolkaDotMaterial polkadotmaterial_variable = null;
    PolynomialSolver polynomialsolver_variable = null;
    ProceduralCloudMaterial proceduralcloudmaterial_variable = null;
    ProceduralFlowerMaterial proceduralflowermaterial_variable = null;
    PulsatingPointLight pulsatingpointlight_variable = null;
    PureWaterMaterial purewatermaterial_variable = null;
    QuantumFieldMaterial quantumfieldmaterial_variable = null;
    RadialGradientMaterial radialgradientmaterial_variable = null;
    RandomMaterial randommaterial_variable = null;
    Ray ray_variable = null;
    Rectangle3D rectangle3d_variable = null;
    RectangleCheckerMaterial rectanglecheckermaterial_variable = null;
    RectangularPrism rectangularprism_variable = null;
    ReflectiveMaterial reflectivematerial_variable = null;
    ResizeImage resizeimage_variable = null;
    RosemalingMaterial rosemalingmaterial_variable = null;
    RoughMaterial roughmaterial_variable = null;
    RubyMaterial rubymaterial_variable = null;
    RuneStoneMaterial runestonematerial_variable = null;
    SalmonMaterial salmonmaterial_variable = null;
    Scene scene_variable = null;
    SceneParser sceneparser_variable = null;
    SilverMaterial silvermaterial_variable = null;
    SilverPBRMaterial silverpbrmaterial_variable = null;
    SimitMaterial simitmaterial_variable = null;
    SmartGlassMaterial smartglassmaterial_variable = null;
    SolidCheckerboardMaterial solidcheckerboardmaterial_variable = null;
    SolidColorMaterial solidcolormaterial_variable = null;
    Sphere sphere_variable = null;
    SphereLight spherelight_variable = null;
    SphereWordTextureMaterial spherewordtexturematerial_variable = null;
    SpotLight spotlight_variable = null;
    SquaredMaterial squaredmaterial_variable = null;
    StainedGlassMaterial stainedglassmaterial_variable = null;
    StarfieldMaterial starfieldmaterial_variable = null;
    StarryNightMaterial starrynightmaterial_variable = null;
    StripeDirection stripedirection_variable = null;
    StripedMaterial stripedmaterial_variable = null;
    SultanKingMaterial sultankingmaterial_variable = null;
    SuperBrightDebugMaterial superbrightdebugmaterial_variable = null;
    TelemarkPatternMaterial telemarkpatternmaterial_variable = null;
    TextDielectricMaterial textdielectricmaterial_variable = null;
    TextureMaterial texturematerial_variable = null;
    TexturedCheckerboardMaterial texturedcheckerboardmaterial_variable = null;
    TexturedPhongMaterial texturedphongmaterial_variable = null;
    ThresholdMaterial thresholdmaterial_variable = null;
    Torus torus_variable = null;
    TorusKnot torusknot_variable = null;
    TransparentColorMaterial transparentcolormaterial_variable = null;
    TransparentEmissivePNGMaterial transparentemissivepngmaterial_variable = null;
    TransparentEmojiMaterial transparentemojimaterial_variable = null;
    TransparentPNGMaterial transparentpngmaterial_variable = null;
    TransparentPlane transparentplane_variable = null;
    Triangle triangle_variable = null;
    TriangleMaterial trianglematerial_variable = null;
    TubeLight tubelight_variable = null;
    TulipFjordMaterial tulipfjordmaterial_variable = null;
    TurkishDelightMaterial turkishdelightmaterial_variable = null;
    TurkishTileMaterial turkishtilematerial_variable = null;
    UnionCSG unioncsg_variable = null;
    Utilities utilities_variable = null;
    Vector2 vector2_variable = null;
    Vector3 vector3_variable = null;
    VikingMetalMaterial vikingmetalmaterial_variable = null;
    VikingRuneMaterial vikingrunematerial_variable = null;
    WaterPBRMaterial waterpbrmaterial_variable = null;
    WaterRippleMaterial waterripplematerial_variable = null;
    WaterfallMaterial waterfallmaterial_variable = null;
    WoodGrainMaterial woodgrainmaterial_variable = null;
    WoodMaterial woodmaterial_variable = null;
    WoodPBRMaterial woodpbrmaterial_variable = null;
    WordMaterial wordmaterial_variable = null;
    XRayMaterial xraymaterial_variable = null;
  }
  ///////////
  
  private static final void setManagerColorsFonts() {
    Font bigFont = new Font("Arial", Font.PLAIN, 20);
    Color textColor = Color.BLUE;
    
    UIManager.put("TextField.font", bigFont);
    UIManager.put("Label.font", bigFont);
    UIManager.put("Button.font", bigFont);
    UIManager.put("OptionPane.messageFont", bigFont);
    
    // Yazı renklerini ayarla
    UIManager.put("TextField.foreground", textColor);
    UIManager.put("Label.foreground", textColor);
    UIManager.put("OptionPane.messageForeground", textColor);
    UIManager.put("Button.foreground", textColor);
  }
  
  public static void main(String[] args) {
    setManagerColorsFonts();
    forCompileNames(); //For compile all classes
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          new ElenaRayTracerGUI().setVisible(true);
        }
    });
  }
  
}
