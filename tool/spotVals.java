SpotLight spotlight {
  position = Point3(0.0, 4.0, 0.0);
  direction = Vector3(0.0, -1.0, 0.0);
  color = rgb(255, 255, 255);
  intensity = 3.0;
  innerConeAngle = 0.877;      // cos(45°/2) radyan
  outerConeAngle = 0.819;      // cos(55°/2) radyan
  constantAttenuation = 1.0;
  linearAttenuation = 0.09;
  quadraticAttenuation = 0.032;
}