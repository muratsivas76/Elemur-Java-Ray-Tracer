package net.elena.murat.material;

import java.awt.Color;

import net.elena.murat.math.Matrix4;

public class SilverMaterial extends MetallicMaterial {
    public SilverMaterial() {
        super(new Color(192, 192, 192), // Silver base color
              Color.WHITE,              // White specular highlight for silver
              0.95,                     // Very high reflectivity
              200.0,                    // Very high shininess
              new Matrix4()             // Identity matrix as default transform
        );
    }
    
    // Alternatif constructor - Matrix4 parametresi ile
    public SilverMaterial(Matrix4 objectInverseTransform) {
        super(new Color(192, 192, 192),
              Color.WHITE,
              0.95,
              200.0,
              objectInverseTransform
        );
    }
    
    // Detaylı constructor - tüm parametrelerle
    public SilverMaterial(Matrix4 objectInverseTransform, 
                         double ambientCoefficient, 
                         double diffuseCoefficient, 
                         double specularCoefficient) {
        super(new Color(192, 192, 192),
              Color.WHITE,
              0.95,
              200.0,
              ambientCoefficient,
              diffuseCoefficient,
              specularCoefficient,
              objectInverseTransform
        );
    }
    
    @Override
    public String toString() {
        return "SilverMaterial silvermaterial = {\n    }";  
    }
    
}
