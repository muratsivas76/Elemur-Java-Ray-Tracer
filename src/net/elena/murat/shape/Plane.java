package net.elena.murat.shape;

import java.awt.Color;
import java.util.List;

import net.elena.murat.math.*;
import net.elena.murat.material.Material;

/**
 * Plane class represents an infinite plane in 3D space.
 * Implements EMShape interface, handling transformations internally.
 */
public class Plane implements EMShape {
  private Material material;
  
  private final Point3 pointOnPlane;
  private final Vector3 normal;
  
  private Matrix4 transform;
  private Matrix4 inverseTransform; // Inverse transform'u burada tutacağız
  
  private Color shadowColor = Color.BLACK;
  private double shadowBias = 0.001;
  private Matrix4[] animTransforms = new Matrix4[] {new Matrix4(), new Matrix4()};

  private String otherAnimationInfo = "veofr,0e0fr,veofr";
  private boolean isVisibleSpecial = true; 
  private boolean isVisible = true; 
  private boolean isShadowEnable = false;
  private boolean isShadowOnly = false;
  private boolean isReflective = true;
  private boolean isRefractive = true;
  
  private String name = "plane";
  
  public Plane(Point3 pointOnPlane, Vector3 normal) {
    this.pointOnPlane = pointOnPlane;
    this.normal = normal.normalize();
    
    this.transform = new Matrix4();
    this.inverseTransform = new Matrix4(); // Başlangıçta identity olarak ayarla
  }
  
  @Override
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  @Override
  public Material getMaterial() {
    return this.material;
  }
  
  @Override
  public void setTransform(Matrix4 transform) {
    this.transform = transform;
    this.inverseTransform = transform.inverse(); // setTransform çağrıldığında tersini hesapla
    if (this.inverseTransform == null) {
      System.err.println("Warning: Plane's transform is non-invertible. Inverse transform set to null.");
    }
  }
  
  @Override
  public Matrix4 getTransform() {
    return this.transform;
  }
  
  @Override // EMShape'te tanımlı olduğu için @Override
  public Matrix4 getInverseTransform() {
    return this.inverseTransform; // Saklanan ters dönüşümü döndür
  }
  
  @Override
  public double intersect(Ray ray) {
    // Artık yerel olarak hesaplamıyoruz, saklanan inverseTransform'u kullanıyoruz
    if (this.inverseTransform == null) {
      System.err.println("Error: Plane's inverse transform is null during intersect. Returning no intersection.");
      return -1;
    }
    
    Point3 localOrigin = this.inverseTransform.transformPoint(ray.getOrigin());
    Vector3 localDirection = this.inverseTransform.transformVector(ray.getDirection()).normalize();
    Ray localRay = new Ray(localOrigin, localDirection);
    
    double denom = localRay.getDirection().dot(this.normal);
    
    if (Math.abs(denom) < Ray.EPSILON) {
      double num = this.pointOnPlane.subtract(localRay.getOrigin()).dot(this.normal);
      if (Math.abs(num) < Ray.EPSILON) {
        return -1;
      }
      return -1;
    }
    
    double t = this.pointOnPlane.subtract(localRay.getOrigin()).dot(this.normal) / denom;
    
    if (t > Ray.EPSILON) {
      return t;
    }
    return -1;
  }
  
  /**
   * Calculates all intersection intervals between a ray and this infinite plane.
   * Since a plane is infinitely thin, the entry and exit points are considered the same.
   * @param ray The ray to test, in world coordinates.
   * @return A list containing a single degenerate interval if intersected, otherwise empty list.
   */
  @Override
  public List<IntersectionInterval> intersectAll(Ray ray) {
    // 1. Transform the ray into the plane's local space
    if (this.inverseTransform == null) {
      return java.util.Collections.emptyList();
    }
    
    Point3 localOrigin = this.inverseTransform.transformPoint(ray.getOrigin());
    Vector3 localDirection = this.inverseTransform.transformVector(ray.getDirection()).normalize();
    Ray localRay = new Ray(localOrigin, localDirection);
    
    double denom = localDirection.dot(this.normal);
    
    // Parallel to the plane
    if (Math.abs(denom) < Ray.EPSILON) {
      return java.util.Collections.emptyList();
    }
    
    double t = this.pointOnPlane.subtract(localOrigin).dot(this.normal) / denom;
    
    if (t <= Ray.EPSILON) {
      return java.util.Collections.emptyList();
    }
    
    // Create a single degenerate interval (in and out at the same point)
    Point3 hitPoint = ray.pointAtParameter(t);
    Vector3 hitNormal = getNormalAt(hitPoint);
    Intersection hit = new Intersection(hitPoint, hitNormal, t, this);
    
    IntersectionInterval interval = IntersectionInterval.point(t, hit);
    return java.util.Arrays.asList(interval);
  }
  
  @Override
  public Vector3 getNormalAt(Point3 worldPoint) {
    // Artık yerel olarak hesaplamıyoruz, saklanan inverseTransform'u kullanıyoruz
    if (this.inverseTransform == null) {
      System.err.println("Error: Plane's inverse transform is null during getNormalAt. Returning default normal.");
      return new Vector3(0, 1, 0);
    }
    Matrix4 normalTransformMatrix = this.inverseTransform.inverseTransposeForNormal();
    if (normalTransformMatrix == null) {
      System.err.println("Error: Plane's normal transform matrix is null. Returning default normal.");
      return new Vector3(0, 1, 0);
    }
    return normalTransformMatrix.transformVector(this.normal).normalize();
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
	  return ("Plane: " + getTransform().toString());
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
    sb.append("Plane " + name + " {\n");
    sb.append("    name = " + name + ";\n");
    sb.append("    pointOnPlane = " + pointOnPlane + ";\n");
    sb.append("    normal = " + normal + ";\n");
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
