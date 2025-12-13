package net.elena.murat.shape;

import java.awt.Color;
import java.util.List;

//custom
import net.elena.murat.math.*;
import net.elena.murat.material.Material;

public interface EMShape {
  
  //For CSG
  List<IntersectionInterval> intersectAll(Ray ray);
  
  //Old Methods
  double intersect(Ray ray);
  
  void setMaterial(Material material);
  void setTransform(Matrix4 transform);
  void setAnimationTransforms(Matrix4[] transform);
  
  Vector3 getNormalAt(Point3 point);
  Material getMaterial();
  
  Matrix4 getTransform();
  Matrix4 getInverseTransform();
  Matrix4[] getAnimationTransforms();
  
  Color getShadowColor();
  void setShadowColor(Color color);
  
  double getShadowBias();
  void setShadowBias(double bias);
  
  String getNameInfo();
  String getName();
  
  void setName(String name);
  
  String getOtherAnimationInfo();
  void setOtherAnimationInfo(String str);
  
  boolean isVisibleSpecial();
  boolean isVisible(); 
  boolean isShadowEnable();
  boolean isShadowOnly();
  boolean isReflective();
  boolean isRefractive();
  
  void setVisibleSpecial(boolean visible);
  void setVisible(boolean visible);
  void setShadowEnable(boolean enable);
  void setShadowOnly(boolean only);
  void setReflective(boolean rfl);
  void setRefractive(boolean rfr);
}
