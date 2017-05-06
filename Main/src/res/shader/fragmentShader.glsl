#version 400

in vec2 pass_texCoords;

out vec4 out_color;

uniform sampler2D texture_image;

void main(){
	vec4 textureColor = texture(texture_image, pass_texCoords);
	
	out_color = textureColor;
}