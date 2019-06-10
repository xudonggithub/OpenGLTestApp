//#version 120
#version 300 es

uniform mat4 projectMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;


layout(location = 0) in vec4 aPos;
layout(location = 1) in vec2 aText;
layout(location = 2) in vec4 aColor;
out vec2 v_Text;
out vec4 v_Color;

void main() {
    gl_Position =  projectMatrix * viewMatrix *modelMatrix*aPos;//
    v_Text = aText;
    v_Color = aColor;
}
