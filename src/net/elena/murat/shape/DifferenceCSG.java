package net.elena.murat.shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.elena.murat.math.*;

/**
 * Represents a Constructive Solid Geometry (CSG) Difference operation.
 * The resulting shape is the difference of two shapes: A - B.
 * A point is inside the difference if it is inside shape A and outside shape B.
 */
public class DifferenceCSG extends CSGShape {
  
  private Color shadowColor = Color.BLACK;
  private double shadowBias = 0.001;
  private Matrix4[] animTransforms = new Matrix4[] {new Matrix4(), new Matrix4()};

  private String otherAnimationInfo = "veofr,0e0fr,veofr";
  private boolean isVisibleSpecial = true; 
  private boolean isVisible = true; 
  private boolean isShadowEnable = true;
  private boolean isShadowOnly = false;
  private boolean isReflective = true;
  private boolean isRefractive = true;
  
  private String name = "differencecsg";
  
  /**
   * Constructs a DifferenceCSG from two shapes.
   * @param left The first shape (left operand, the one being subtracted from).
   * @param right The second shape (right operand, the one being subtracted).
   */
  public DifferenceCSG(EMShape left, EMShape right) {
    super(left, right);
  }
  
  /**
   * Combines the intersection intervals of two shapes using the Difference operation.
   * The difference is formed by finding intervals where the ray is inside 'left' and outside 'right'.
   * @param a List of intervals from the left shape (A).
   * @param b List of intervals from the right shape (B).
   * @return The resulting list of intervals for the difference (A - B).
   */
  @Override
  protected List<IntersectionInterval> combine(
    List<IntersectionInterval> a,
    List<IntersectionInterval> b) {
    
    // 1. If A has no intersections, result is empty
    if (a.isEmpty()) {
      return Collections.emptyList();
    }
    
    // 2. If B has no intersections, result is just A
    if (b.isEmpty()) {
      return new ArrayList<>(a);
    }
    
    // 3. Sort intervals by tIn
    List<IntersectionInterval> sortedA = new ArrayList<>(a);
    List<IntersectionInterval> sortedB = new ArrayList<>(b);
    Collections.sort(sortedA, (ia, ib) -> Double.compare(ia.tIn, ib.tIn));
    Collections.sort(sortedB, (ia, ib) -> Double.compare(ia.tIn, ib.tIn));
    
    List<IntersectionInterval> result = new ArrayList<>();
    
    for (IntersectionInterval intervalA : sortedA) {
      double currentTIn = intervalA.tIn;
      double currentTOut = intervalA.tOut;
      
      // For each interval in A, subtract all overlaps with B
      for (IntersectionInterval intervalB : sortedB) {
        // If B interval starts after A ends, no overlap
        if (intervalB.tIn >= currentTOut - Ray.EPSILON) {
          break;
        }
        
        // If B interval ends before A starts, no overlap
        if (intervalB.tOut <= currentTIn + Ray.EPSILON) {
          continue;
        }
        
        // There is an overlap
        double overlapTIn = Math.max(currentTIn, intervalB.tIn);
        double overlapTOut = Math.min(currentTOut, intervalB.tOut);
        
        // Add part before overlap (if exists)
        if (currentTIn < overlapTIn - Ray.EPSILON) {
          result.add(createInterval(intervalA, currentTIn, overlapTIn));
        }
        
        // Update current interval start
        currentTIn = overlapTOut;
      }
      
      // Add remaining part after all B intervals
      if (currentTIn < currentTOut - Ray.EPSILON) {
        result.add(createInterval(intervalA, currentTIn, currentTOut));
      }
    }
    
    return result;
  }
  
  /**
   * Helper method to create a new IntersectionInterval with correct hit data.
   * Uses linear interpolation to estimate point and normal at new t values.
   * @param original The original interval to copy data from.
   * @param tIn New tIn value.
   * @param tOut New tOut value.
   * @return A new IntersectionInterval.
   */
  private IntersectionInterval createInterval(IntersectionInterval original, double tIn, double tOut) {
    // Interpolate points
    Point3 pointIn = original.in.getPoint().add(
      original.out.getPoint().subtract(original.in.getPoint())
      .scale((tIn - original.tIn) / (original.tOut - original.tIn + Ray.EPSILON))
    );
    Point3 pointOut = original.in.getPoint().add(
      original.out.getPoint().subtract(original.in.getPoint())
      .scale((tOut - original.tIn) / (original.tOut - original.tIn + Ray.EPSILON))
    );
    
    // Use original normals (approximation)
    Vector3 normalIn = original.in.getNormal();
    Vector3 normalOut = original.out.getNormal();
    
    Intersection in = new Intersection(pointIn, normalIn, tIn, this);
    Intersection out = new Intersection(pointOut, normalOut, tOut, this);
    
    return new IntersectionInterval(tIn, tOut, in, out);
  }
  
  @Override
  public void setAnimationTransforms(Matrix4[] atm) {
    this.animTransforms = atm;
  }
  
  @Override
  public Matrix4[] getAnimationTransforms() {
    return this.animTransforms;
  }
  
  @Override
  public Color getShadowColor() {
	  return this.shadowColor;
  }
  
  @Override
  public void setShadowColor(Color color) {
	  this.shadowColor = color;
  }
  
  @Override
  public double getShadowBias() {
	  return this.shadowBias;
  }
  
  @Override
  public void setShadowBias(double bias) {
	  this.shadowBias = bias;
  }
  
  ////////////////////////////
  @Override
  public String getNameInfo() {
	  return ("DifferenceCSG: " + getTransform().toString());
  }
  
  @Override
  public String getOtherAnimationInfo() {
      return this.otherAnimationInfo;
  }
  
  @Override
  public void setOtherAnimationInfo(String str) {
      this.otherAnimationInfo = str;
  }
  
  @Override
  public boolean isVisible() {
      return this.isVisible;
  }
  
  @Override  
  public boolean isShadowEnable() {
      return this.isShadowEnable;
  }
  
  @Override
  public boolean isShadowOnly() {
      return this.isShadowOnly;
  }

  @Override
  public boolean isReflective() {
      return this.isReflective;
  }

  @Override
  public boolean isRefractive() {
    return this.isRefractive;
  }

  @Override
  public void setVisible(boolean visible) {
      this.isVisible = visible;
  }

  @Override
  public void setShadowEnable(boolean enable) {
      this.isShadowEnable = enable;
  }

  @Override
  public void setShadowOnly(boolean only) {
      this.isShadowOnly = only;
  }
  
  @Override
  public void setReflective(boolean rfl) {
      this.isReflective = rfl;
  }
   
  @Override
  public void setRefractive(boolean rfr) {
      this.isRefractive = rfr;
  }
  
  @Override
  public boolean isVisibleSpecial() {
	  return this.isVisibleSpecial;
  }
  
  @Override
  public void setVisibleSpecial(boolean visible) {
	  this.isVisibleSpecial = visible;
  }
  
  @Override
  public String getName() {
	  return this.name;
  }
  
  @Override
  public void setName(String name) {
	  this.name = name;
  }
  ///////////////////////
 
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    String leftToString = left.toString();
    String rightToString = right.toString();
    int index1 = leftToString.indexOf("material =");
    int index2 = rightToString.indexOf("material =");
    
    leftToString = leftToString.substring(0, index1) + "};///";
    rightToString = rightToString.substring(0, index2) + "};///";
    
    sb.append("DifferenceCSG " + name + " {\n");
    sb.append("    name = " + name + ";\n");
    sb.append("    left = " + ((leftToString.replaceAll("\n", "\n        ")).replace("    }\n        ", "}")) + "\n");
    sb.append("    right = " + ((rightToString.replaceAll("\n", "\n        ")).replace("    }\n        ", "}")) + "\n");
    sb.append("\n");
    sb.append("    " + getTransform().toString() + "///\n");
    sb.append("\n");
    sb.append("    firstAnim_" + animTransforms[0].toString() + "///\n");
    sb.append("    secondAnim_" + animTransforms[1].toString() + "///\n");
    sb.append("\n");
    sb.append("    shadowColor = " + net.elena.murat.util.ColorUtil.toColorString(shadowColor) + ";\n");
	sb.append("    shadowBias = " + shadowBias + ";\n");
	sb.append("    otherAnimationInfo = " + otherAnimationInfo + ";\n");
	sb.append("    isVisibleSpecial = " + isVisibleSpecial + ";\n");
	sb.append("    isVisible = " + isVisible + ";\n");
	sb.append("    isShadowEnable = " + isShadowEnable + ";\n");
	sb.append("    isShadowOnly = " + isShadowOnly + ";\n");
	sb.append("    isReflective = " + isReflective + ";\n");
	sb.append("    isRefractive = " + isRefractive + ";\n");
	sb.append("\n");
    sb.append("    material = " + getMaterial().toString() + ";\n}");
    return sb.toString();
  }
  
}
