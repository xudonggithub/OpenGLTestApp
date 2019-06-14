//#version 120
#version 300 es
precision highp float;

//uniform vec2 test;
uniform sampler2D uTextue;
in vec2 v_Text;
in vec4 v_Color;

out vec4 fragColor;
void main() {
    fragColor =texture(uTextue, v_Text);// v_Color;//vec4(1.0,0,0,1.0);//
//    gl_PointSize = 10.0;
}
