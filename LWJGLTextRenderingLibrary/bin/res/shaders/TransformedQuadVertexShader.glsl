#version 400 core

in vec2 position;

uniform mat4 transformationMatrix;

void main(){
	gl_Position = transformationMatrix * vec4(position.xy, 0.0, 1.0);
}
