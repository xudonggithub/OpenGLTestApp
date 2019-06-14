//#version 120
#version 300 es
precision highp float;

//uniform vec2 test;
uniform sampler2D uTextue;
uniform vec4 lightPos;
in vec2 v_Text;
in vec4 v_Color;
in vec4 v_Pos;

out vec4 fragColor;
void main() {
    float dis = distance(lightPos.xyz, v_Pos.xyz);
    float zsbf = floor(dis);
    float xsbf = fract(dis);
    xsbf = floor(xsbf * 1024.0);
    float hzsbf = floor(zsbf / 256.0);
    float lzsbf = mod(zsbf,256.0);
    float hxsbf = floor(xsbf / 32.0);
    float lxsbf = mod(xsbf,32.0);
    float r = hzsbf / 256.0;
    float g = lzsbf / 256.0;
    float b = hxsbf / 32.0;
    float a = lxsbf / 32.0;
    fragColor = vec4(r,g,b,a);
}
