#version 400 core

out vec4 out_Color;

uniform vec2 size;
uniform vec2 centerCoordinates;
uniform float maxDistance;
uniform sampler2D texture;

float pow(float x){
	return x*x;
}

void main(){

	vec2 position = gl_FragCoord.xy / size.xy;
	vec4 color = texture2D(texture, position);
	if(color.r == 1.0){
		float deltaX = pow(gl_FragCoord.x - centerCoordinates.x);
		float deltaY = pow(gl_FragCoord.y - centerCoordinates.y);
		out_Color = vec4(1.0-sqrt(deltaX+deltaY)/maxDistance);
	} else {
		out_Color = color;
	}
}
