package rendering.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform{

	private float currentValue;
	private boolean used = false;

	public UniformFloat(String name) {
		super(name);
	}

	public void loadFloat(float value){
		if(!used || value != currentValue){
			this.currentValue = value;
			used = true;
			GL20.glUniform1f(super.getLocation(), value);
		}
	}

}
