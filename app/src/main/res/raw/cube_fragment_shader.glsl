//#version 120
#version 300 es
precision highp float;

//uniform vec2 test;
uniform sampler2D uTextue;
in vec3 v_Normal;
in vec4 v_Color;

out vec4 fragColor;
void main() {
    fragColor = vec4(1.0,1.0,1.0,1.0);//v_Color;//texture(uTextue, v_Text);//
//    gl_PointSize = 10.0;
}
