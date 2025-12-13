// Murat Inan
package net.elena.murat.gui;

public class AnimationInfo {
	
	private final boolean isVisible;
	private final boolean isShadowEnable;
	private final boolean isShadowOnly;
	private final boolean isReflective;
	private final boolean isRefractive;
	
    public AnimationInfo(boolean isVisible, boolean isShadowEnable, boolean isShadowOnly, 
                         boolean isReflective, boolean isRefractive) {
		this.isVisible = isVisible;
		this.isShadowEnable = isShadowEnable;
		this.isShadowOnly = isShadowOnly;
		this.isReflective = isReflective;
		this.isRefractive = isRefractive;
    }
    
    public boolean isVisible() {
		return this.isVisible;
	}
	
	public boolean isShadowEnable() {
		return this.isShadowEnable;
	}
	
	public boolean isShadowOnly() {
		return this.isShadowOnly;
	}
	
	public boolean isReflective() {
		return this.isReflective;
	}
	
	public boolean isRefractive() {
		return this.isRefractive;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AnimationInfo: \n");
		sb.append("    isVisible = " + isVisible + ";\n");
		sb.append("    isShadowEnable = " + isShadowEnable + ";\n");
		sb.append("    isShadowOnly = " + isShadowOnly + ";\n");
		sb.append("    isReflective = " + isReflective + ";\n");
		sb.append("    isRefractive = " + isRefractive + ";\n");
		return sb.toString();
	}
	
}
