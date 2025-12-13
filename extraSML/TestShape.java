import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

// Custom imports
import net.elena.murat.math.*;
import net.elena.murat.material.Material;
import net.elena.murat.shape.EMShape;

/**
 * A simple test shape implementation for demonstrating custom shape loading.
 * Represents a basic axis-aligned box geometry.
 */
public class TestShape implements EMShape {
    
    private Material material;
    private Matrix4 transform = Matrix4.identity();
    private Matrix4[] animationTransforms = new Matrix4[]{Matrix4.identity(), Matrix4.identity()};
    
    // Box dimensions
    private double width = 2.0;
    private double height = 1.0; 
    private double depth = 1.5;
    
    // EMShape interface required fields
    private Color shadowColor = Color.BLACK;
    private double shadowBias = 0.001;
    private String otherAnimationInfo = "veofr,0e0fr,veofr";
    private boolean isVisibleSpecial = true;
    private boolean isVisible = true;
    private boolean isShadowEnable = true;
    private boolean isShadowOnly = false;
    private boolean isReflective = true;
    private boolean isRefractive = true;
    
    private String name = "testshape";
    
    /**
     * No-args constructor required for dynamic loading
     */
    public TestShape() {
        System.out.println("TestShape created successfully");
    }
    
    @Override
    public List<IntersectionInterval> intersectAll(Ray ray) {
        List<IntersectionInterval> intersections = new ArrayList<>();
        
        // Transform ray to object space
        Ray localRay = ray.transform(getInverseTransform());
        
        // Simple axis-aligned box intersection
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;
        
        // Check intersection with each pair of parallel planes
        for (int i = 0; i < 3; i++) {
            double invD = 1.0 / localRay.getDirection().get(i);
            double t0 = (-getDimension(i)/2 - localRay.getOrigin().get(i)) * invD;
            double t1 = (getDimension(i)/2 - localRay.getOrigin().get(i)) * invD;
            
            if (invD < 0.0) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            
            tMin = Math.max(t0, tMin);
            tMax = Math.min(t1, tMax);
            
            if (tMax <= tMin) {
                return intersections; // No intersection
            }
        }
        
        if (tMin > 0 && tMin < tMax) {
            // Create intersection data
            Point3 hitPoint = localRay.pointAtParameter(tMin);
            Vector3 normal = getNormalAt(hitPoint);
            Intersection hit = new Intersection(hitPoint, normal, tMin, this);
            intersections.add(IntersectionInterval.point(tMin, hit));
        }
        
        return intersections;
    }
    
    @Override
    public double intersect(Ray ray) {
        List<IntersectionInterval> intervals = intersectAll(ray);
        return intervals.isEmpty() ? -1.0 : intervals.get(0).getEntry().getT();
    }
    
    @Override
    public Vector3 getNormalAt(Point3 point) {
        // Transform point to object space
        Point3 localPoint = getInverseTransform().transformPoint(point);
        
        // Find which face the point is closest to
        double absX = Math.abs(localPoint.x);
        double absY = Math.abs(localPoint.y); 
        double absZ = Math.abs(localPoint.z);
        
        if (absX >= absY && absX >= absZ) {
            return new Vector3(Math.signum(localPoint.x), 0, 0);
        } else if (absY >= absZ) {
            return new Vector3(0, Math.signum(localPoint.y), 0);
        } else {
            return new Vector3(0, 0, Math.signum(localPoint.z));
        }
    }
    
    /**
     * Helper method to get dimension based on axis index
     * @param axis 0 for width (x), 1 for height (y), 2 for depth (z)
     * @return dimension value
     */
    private double getDimension(int axis) {
        switch (axis) {
            case 0: return width;
            case 1: return height;
            case 2: return depth;
            default: throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }
    
    // EMShape Interface Implementation - Getter and Setter methods
    
    @Override
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    @Override
    public Material getMaterial() {
        return material;
    }
    
    @Override
    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }
    
    @Override
    public Matrix4 getTransform() {
        return transform;
    }
    
    @Override
    public Matrix4 getInverseTransform() {
        return transform.inverse();
    }
    
    @Override
    public void setAnimationTransforms(Matrix4[] transform) {
        this.animationTransforms = transform;
    }
    
    @Override
    public Matrix4[] getAnimationTransforms() {
        return animationTransforms;
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
    
    @Override
    public String getNameInfo() {
        return "TestShape: " + getTransform().toString();
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
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CustomShape testshape {\n");
        sb.append("    className = extraSML/TestShape.class;\n");
        sb.append("\n");
        sb.append("    " + getTransform().toString() + "\n");
        sb.append("\n");
        sb.append("    firstAnim_" + getAnimationTransforms()[0].toString() + "\n");
        sb.append("    secondAnim_" + getAnimationTransforms()[1].toString() + "\n");
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
        sb.append("    material = " + (getMaterial().toString()) + ";\n");
        sb.append("}");
        return sb.toString();
    }
}
//cd extraSML
//javac -cp ../bin/guielena.jar TestShape.java
//OR: javac -cp ../bin/elenagui.jar TestShape.java
//OR: javac -cp ../obj TestShape.java
