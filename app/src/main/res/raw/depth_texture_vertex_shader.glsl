//#version 120
#version 300 es

uniform mat4 projectMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;


layout(location = 0) in vec4 aPos;

out vec4 v_Pos;
void main() {
    gl_Position =  projectMatrix * viewMatrix * modelMatrix * aPos;//
    v_Pos =  modelMatrix * aPos;
}
