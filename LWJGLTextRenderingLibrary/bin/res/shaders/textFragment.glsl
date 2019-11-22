#version 140

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec4 textColor;
uniform sampler2D sampler;

void main(){

	float val = texture2D(sampler, pass_textureCoords).r;
	out_Color = vec4(textColor.rgb, textColor.a*val);
}
