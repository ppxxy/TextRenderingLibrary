#version 400 core

out vec4 out_Color;

uniform vec2 size;
uniform int width;
uniform int height;
uniform sampler2D texture;

vec4 fetchColor(){
	float total = 0.0;
	vec4 color;
	vec2 position = vec2(0.0, 0.0);
	for(int y = 0; y < height; y++){
		position.x = 0.0;
		for(int x = 0; x < width; x++){
			color = texture2D(texture, (gl_FragCoord.xy+position)/size.xy);
			if(color.r == 0.0){
				return vec4(0.0);
			} else{
				total += color.r;
			}
			position.x += 1.0;
		}
		position.y += 1.0;
	}
	return vec4(total/(width*height));
}

void main(){

	out_Color = fetchColor();
	//out_Color = texture2D(texture, gl_FragCoord.xy / size.xy);
}
