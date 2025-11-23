package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.Matrix4;

public class CopperMaterial extends MetallicMaterial {
    public CopperMaterial() {
        super(new Color(184, 115, 51),   // Copper color (reddish-brown)
              new Color(220, 150, 100),  // Slightly reddish specular color for copper
              0.85,                      // Reflectivity strength
              120.0,                     // Shininess (medium-high)
              new Matrix4()              // Identity matrix as default transform
        );
    }
    
    // Alternatif constructor - Matrix4 parametresi ile
    public CopperMaterial(Matrix4 objectInverseTransform) {
        super(new Color(184, 115, 51),
              new Color(220, 150, 100),
              0.85,
              120.0,
              objectInverseTransform
        );
    }
    
    // Detaylı constructor - tüm parametrelerle
    public CopperMaterial(Matrix4 objectInverseTransform, 
                         double ambientCoefficient, 
                         double diffuseCoefficient, 
                         double specularCoefficient) {
        super(new Color(184, 115, 51),
              new Color(220, 150, 100),
              0.85,
              120.0,
              ambientCoefficient,
              diffuseCoefficient,
              specularCoefficient,
              objectInverseTransform
        );
    }
    
    @Override 
    public String toString() {
        return "CopperMaterial coppermaterial {\n    }";
    }
    
}
