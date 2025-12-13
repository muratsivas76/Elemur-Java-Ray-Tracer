package net.elena.murat.shape;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import net.elena.murat.math.*;
import net.elena.murat.material.Material;

import static net.elena.murat.math.Vector3.*;

public class Rectangle3D implements EMShape {
  private final Point3 p1, p2;
  private final float thickness;
  private Material material;
  private Matrix4 transform = Matrix4.identity();
  private Matrix4 inverseTransform = Matrix4.identity();
  
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
  
  private String name = "rectangle3d";
  
  public Rectangle3D(Point3 p1, Point3 p2, float thickness) {
    this.p1 = p1;
    this.p2 = p2;
    this.thickness = thickness;
  }
  
  @Override
  public double intersect(Ray ray) {
    List<IntersectionInterval> intervals = intersectAll(ray);
    if (intervals.isEmpty()) return -1;
    
    // Return the closest intersection
    double minT = Double.MAX_VALUE;
    for (IntersectionInterval interval : intervals) {
      if (interval.getEntry() != null && interval.getEntry().getT() < minT) {
        minT = interval.getEntry().getT();
      }
    }
    return minT < Double.MAX_VALUE ? minT : -1;
  }
  
  /**
   * Calculates all intersection intervals between a ray and this Rectangle3D.
   * For CSG operations, we need to treat the rectangle as a thin slab with thickness.
   */
  @Override
  public List<IntersectionInterval> intersectAll(Ray ray) {
    List<IntersectionInterval> intervals = new ArrayList<>();
    
    // 1. Transform the ray into local space
    Ray localRay = new Ray(
      inverseTransform.transformPoint(ray.getOrigin()),
      inverseTransform.transformVector(ray.getDirection()).normalize()
    );
    
    // 2. Check for intersection with both front and back faces (considering thickness)
    double halfThickness = thickness / 2.0;
    
    // Front face (z = -halfThickness)
    double tFront = (-halfThickness - localRay.getOrigin().z) / localRay.getDirection().z;
    
    // Back face (z = halfThickness)
    double tBack = (halfThickness - localRay.getOrigin().z) / localRay.getDirection().z;
    
    // Ensure tFront is the entry and tBack is the exit
    if (tFront > tBack) {
      double temp = tFront;
      tFront = tBack;
      tBack = temp;
    }
    
    // Check if both intersections are valid
    if (tBack < Ray.EPSILON) {
      return intervals; // Both intersections behind the ray
    }
    
    if (tFront < Ray.EPSILON) {
      tFront = Ray.EPSILON; // Ray starts inside the slab
    }
    
    // 3. Check if intersection points are within rectangle bounds
    Point3 localHitFront = localRay.pointAtParameter(tFront);
    Point3 localHitBack = localRay.pointAtParameter(tBack);
    
    double minX = Math.min(p1.x, p2.x);
    double maxX = Math.max(p1.x, p2.x);
    double minY = Math.min(p1.y, p2.y);
    double maxY = Math.max(p1.y, p2.y);
    
    boolean frontInside = isPointInRectangle(localHitFront, minX, maxX, minY, maxY);
    boolean backInside = isPointInRectangle(localHitBack, minX, maxX, minY, maxY);
    
    if (!frontInside && !backInside) {
      return intervals; // No valid intersections
    }
    
    // 4. Create intersection points with proper normals
    if (frontInside) {
      Point3 worldHitFront = ray.pointAtParameter(tFront);
      Vector3 worldNormalFront = getNormalAt(worldHitFront);
      Intersection entry = new Intersection(worldHitFront, worldNormalFront, tFront, this);
      
      if (backInside) {
        // Both intersections are inside the rectangle bounds
        Point3 worldHitBack = ray.pointAtParameter(tBack);
        Vector3 worldNormalBack = getNormalAt(worldHitBack).negate(); // Exit normal points outward
        Intersection exit = new Intersection(worldHitBack, worldNormalBack, tBack, this);
        
        intervals.add(new IntersectionInterval(entry, exit));
        } else {
        // Only entry is inside, exit is outside bounds
        intervals.add(IntersectionInterval.point(tFront, entry));
      }
      } else if (backInside) {
      // Only exit is inside bounds (ray starts inside the rectangle)
      Point3 worldHitBack = ray.pointAtParameter(tBack);
      Vector3 worldNormalBack = getNormalAt(worldHitBack).negate(); // Exit normal points outward
      Intersection exit = new Intersection(worldHitBack, worldNormalBack, tBack, this);
      
      intervals.add(IntersectionInterval.point(tBack, exit));
    }
    
    // TEST
    if (material != null) {
      material.setObjectTransform(this.inverseTransform);
    }
    
    return intervals;
  }
  
  /**
   * Checks if a point is within the rectangle bounds in local space.
   */
  private boolean isPointInRectangle(Point3 point, double minX, double maxX, double minY, double maxY) {
    return point.x >= minX && point.x <= maxX &&
    point.y >= minY && point.y <= maxY;
  }
  
  @Override
  public Vector3 getNormalAt(Point3 worldPoint) {
    Point3 localPoint = inverseTransform.transformPoint(worldPoint);
    Vector3 localNormal = new Vector3(0, 0, 1);
    return inverseTransform.transpose().transformVector(localNormal).normalize();
  }
  
  @Override
  public void setTransform(Matrix4 transform) {
    this.transform = transform;
    this.inverseTransform = transform.inverse();
  }
  
  @Override public Matrix4 getTransform() { return transform; }
  @Override public Matrix4 getInverseTransform() { return inverseTransform; }
  @Override public Material getMaterial() { return material; }
  @Override public void setMaterial(Material material) { this.material = material; }
  
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
	  return ("Rectangle3D: " + getTransform().toString());
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
    sb.append("Rectangle3D " + name + " {\n");
    sb.append("    name = " + name + ";\n");
    sb.append("    p1 = " + p1 + ";\n");
    sb.append("    p2 = " + p2 + ";\n");
    sb.append("    thickness = " + thickness + ";\n");
    sb.append("\n");
    sb.append("    " + getTransform().toString() + "\n");
    sb.append("\n");
    sb.append("firstAnim_" + animTransforms[0].toString() + "\n");
    sb.append("secondAnim_" + animTransforms[1].toString() + "\n");
    sb.append("    otherAnimationInfo = " + otherAnimationInfo + ";\n");
    sb.append("\n");
    sb.append("    shadowColor = " + net.elena.murat.util.ColorUtil.toColorString(shadowColor) + ";\n");
	sb.append("    shadowBias = " + shadowBias + ";\n");
	sb.append("\n");
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
