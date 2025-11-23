package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.Matrix4;

public class GoldMaterial extends MetallicMaterial {
    public GoldMaterial() {
        super(new Color(255, 215, 0),    // Gold color
              new Color(255, 223, 186),  // Light yellowish specular color for gold
              0.9,                       // Reflectivity strength
              150.0,                     // Shininess
              new Matrix4()              // Identity matrix as default transform
        );
    }
    
    // Alternatif constructor - Matrix4 parametresi ile
    public GoldMaterial(Matrix4 objectInverseTransform) {
        super(new Color(255, 215, 0),
              new Color(255, 223, 186),
              0.9,
              150.0,
              objectInverseTransform
        );
    }
    
    // Detaylı constructor - tüm parametrelerle
    public GoldMaterial(Matrix4 objectInverseTransform, 
                       double ambientCoefficient, 
                       double diffuseCoefficient, 
                       double specularCoefficient) {
        super(new Color(255, 215, 0),
              new Color(255, 223, 186),
              0.9,
              150.0,
              ambientCoefficient,
              diffuseCoefficient,
              specularCoefficient,
              objectInverseTransform
        );
    }
    
    @Override
    public String toString() {
        return "GoldMaterial goldmaterial = {\n    }";
    }
    
}
