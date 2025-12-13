package net.elena.murat.shape;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

import net.elena.murat.math.*;
import net.elena.murat.material.Material;

public class Ellipsoid implements EMShape {
  private final Point3 center;
  private final double a, b, c;
  
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
   
  private Matrix4 transform;
  private Matrix4 inverseTransform;
  private Matrix4 inverseTransposeTransform;
  private Material material;
  
  private String name = "ellipsoid";
  
  public Ellipsoid(Point3 center, double a, double b, double c) {
    this.center = Objects.requireNonNull(center);
    this.a = a;
    this.b = b;
    this.c = c;
    this.transform = Matrix4.identity();
    this.inverseTransform = Matrix4.identity();
    this.inverseTransposeTransform = Matrix4.identity();
  }
  
  @Override
  public double intersect(Ray ray) {
    Ray localRay = transformRayToLocalSpace(ray);
    
    double a2 = a * a;
    double b2 = b * b;
    double c2 = c * c;
    
    Vector3 o = localRay.getOrigin().toVector3();
    Vector3 d = localRay.getDirection();
    
    double A = (d.x*d.x)/a2 + (d.y*d.y)/b2 + (d.z*d.z)/c2;
    double B = 2*((o.x*d.x)/a2 + (o.y*d.y)/b2 + (o.z*d.z)/c2);
    double C = (o.x*o.x)/a2 + (o.y*o.y)/b2 + (o.z*o.z)/c2 - 1;
    
    double discriminant = B*B - 4*A*C;
    if (discriminant < 0) return -1;
    
    double sqrtD = Math.sqrt(discriminant);
    double t1 = (-B - sqrtD)/(2*A);
    double t2 = (-B + sqrtD)/(2*A);
    
    return (t1 > Ray.EPSILON) ? t1 : (t2 > Ray.EPSILON) ? t2 : -1;
  }
  
  /**
   * Calculates all intersection intervals between a ray and this ellipsoid.
   * The ray is transformed into the ellipsoid's local space for calculation.
   * The method solves the quadratic equation for the ellipsoid and returns
   * an interval where the ray is inside the shape.
   * @param ray The ray to test, in world coordinates.
   * @return A list of IntersectionInterval objects. Empty if no intersection.
   */
  @Override
  public List<IntersectionInterval> intersectAll(Ray ray) {
    // 1. Transform the ray into local space
    Ray localRay = transformRayToLocalSpace(ray);
    
    double a2 = a * a;
    double b2 = b * b;
    double c2 = c * c;
    
    Vector3 o = localRay.getOrigin().toVector3();
    Vector3 d = localRay.getDirection();
    
    double A = (d.x*d.x)/a2 + (d.y*d.y)/b2 + (d.z*d.z)/c2;
    double B = 2*((o.x*d.x)/a2 + (o.y*d.y)/b2 + (o.z*d.z)/c2);
    double C = (o.x*o.x)/a2 + (o.y*o.y)/b2 + (o.z*o.z)/c2 - 1;
    
    double discriminant = B*B - 4*A*C;
    if (discriminant < 0) {
      return java.util.Collections.emptyList();
    }
    
    double sqrtD = Math.sqrt(discriminant);
    double t1 = (-B - sqrtD)/(2*A);
    double t2 = (-B + sqrtD)/(2*A);
    
    // Ensure t1 is entry, t2 is exit
    if (t1 > t2) {
      double temp = t1;
      t1 = t2;
      t2 = temp;
    }
    
    // Only consider intersections in front of the ray
    boolean t1Valid = t1 > Ray.EPSILON;
    boolean t2Valid = t2 > Ray.EPSILON;
    
    if (!t1Valid && !t2Valid) {
      return java.util.Collections.emptyList();
    }
    
    // Create Intersection objects
    Point3 pointIn = ray.pointAtParameter(t1);
    Vector3 normalIn = getNormalAt(pointIn);
    Intersection in = new Intersection(pointIn, normalIn, t1, this);
    
    Point3 pointOut = ray.pointAtParameter(t2);
    Vector3 normalOut = getNormalAt(pointOut);
    Intersection out = new Intersection(pointOut, normalOut, t2, this);
    
    // Return a single interval
    return java.util.Arrays.asList(new IntersectionInterval(t1, t2, in, out));
  }
  
  @Override
  public Vector3 getNormalAt(Point3 worldPoint) {
    Point3 localPoint = inverseTransform.transformPoint(worldPoint);
    Vector3 localNormal = new Vector3(
      localPoint.x/(a*a),
      localPoint.y/(b*b),
      localPoint.z/(c*c)
    ).normalize();
    
    return inverseTransposeTransform.transformVector(localNormal).normalize();
  }
  
  @Override
  public Material getMaterial() {
    return material;
  }
  
  @Override
  public void setMaterial(Material material) {
    this.material = Objects.requireNonNull(material);
  }
  
  @Override
  public void setTransform(Matrix4 transform) {
    this.transform = Objects.requireNonNull(transform);
    this.inverseTransform = transform.inverse();
    this.inverseTransposeTransform = inverseTransform.transpose();
  }
  
  @Override
  public Matrix4 getTransform() {
    return transform;
  }
  
  @Override
  public Matrix4 getInverseTransform() {
    return inverseTransform;
  }
  
  private Ray transformRayToLocalSpace(Ray worldRay) {
    Point3 localOrigin = inverseTransform.transformPoint(worldRay.getOrigin());
    Vector3 localDirection = inverseTransform.transformVector(worldRay.getDirection()).normalize();
    return new Ray(localOrigin, localDirection);
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
	  return ("Ellipsoid: " + getTransform().toString());
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
    sb.append("Ellipsoid " + name + " {\n");
    sb.append("    name = " + name + ";\n");
    sb.append("    center = " + center + ";\n");
    sb.append("    a = " + a + ";\n");
    sb.append("    b = " + b + ";\n");
    sb.append("    c = " + c + ";\n");
    sb.append("\n");
    sb.append("    " + getTransform().toString() + "\n");
    sb.append("\n");
    sb.append("    firstAnim_" + animTransforms[0].toString() + "\n");
    sb.append("    secondAnim_" + animTransforms[1].toString() + "\n");
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
