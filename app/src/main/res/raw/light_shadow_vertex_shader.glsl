#version 320 es

uniform mat4 projectMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;


layout(location = 0) in vec4 aPos;
layout(location = 1) in vec2 aText;
layout(location = 2) in vec4 aColor;
layout(location = 3) in vec3 aNormal;

out vec4 v_Pos;
out vec2 v_Text;
out vec4 v_Color;
out vec3 v_Normal;


out VS_OUT {
    vec3 normal;
} vs_out;

void main() {
    gl_Position =  projectMatrix * viewMatrix * modelMatrix * aPos;
    v_Text = aText;
    v_Color = aColor;
    v_Pos = modelMatrix * aPos;
    v_Normal = mat3(transpose(inverse(modelMatrix))) * aNormal;
    gl_PointSize = 20.0;


    mat3 normalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
    vs_out.normal = vec3(projectMatrix * vec4(normalMatrix * aNormal, 0.0));
}
