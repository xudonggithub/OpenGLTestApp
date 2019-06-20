#version 320 es
precision highp float;

//uniform vec2 test;
uniform sampler2D uTextue;
uniform sampler2D uDepTextue;
uniform vec4 uLightPos;
uniform mat4 uLightViewMatrix;
uniform vec2 uDepTextueSize;
uniform vec4 uEyePos;

in vec4 v_Pos;
in vec2 v_Text;
in vec4 v_Color;
in vec3 v_Normal;

out vec4 fragColor;

void calPointLight(in vec4 pos, in vec3 normal,in vec4 lightPos, in vec4 eyePos, in float shininess,
            inout vec4 ambient, inout vec4 diffuse, inout vec4 specular) {
    vec3 N = normalize(normal.xyz);
    vec3 L = normalize(lightPos.xyz - pos.xyz);
    vec3 E = normalize(eyePos.xyz - pos.xyz);
    vec3 H = normalize(L+E);

    diffuse = max(0.0, dot(L, N)) * diffuse;
    specular = pow(max(0.0, dot(N, H)),shininess) * specular;
}
void main() {
    vec4 depPos = uLightViewMatrix * v_Pos;
    depPos = depPos / depPos.w;
    vec2 st;
    st.s = (depPos.s + 1.0) / 2.0;
    st.t = (1.0 - depPos.t) / 2.0;
    if (st.s < 0.0 || st.s > 1.0 || st.t < 0.0 || st.t >1.0){
        fragColor = v_Color;
    }
    else {
        vec4 depth = texture(uDepTextue, st);
        float depthDist = depth.r * 256.0 * 256.0 + depth.g * 256.0 + depth.b + depth.a/32.0;
        float srcDist = distance(uLightPos.xyz, v_Pos.xyz);
        if(srcDist-0.4>depthDist)
            fragColor =mix(v_Color,vec4(0.57,0.57,0.57,1.0), 0.5);//v_Color;//vec4(1.0,0,0,1.0);//texture(uTextue, v_Text);//
        else {
            vec4 ambient =  v_Color;//vec4(0.0, 0.0, 1.0, 0.0);//
            vec4 diffuse = vec4(0.9, 0.9, 0.0, 1.0);
            vec4 specular = vec4(0.9, 0.9,0.0, 1.0);
            calPointLight(v_Pos, v_Normal, uLightPos, uEyePos, 32.0, ambient,diffuse,specular);

            float r = clamp(ambient.r+diffuse.r+specular.r, 0.0, 1.0);
            float g = clamp(ambient.g+diffuse.g+specular.g, 0.0, 1.0);
            float b = clamp(ambient.b+diffuse.b+specular.b, 0.0, 1.0);
            float a = clamp(ambient.a+diffuse.a+specular.a, 0.0, 1.0);
            fragColor = vec4(r, g, b, a);

        }
//        float zsbfDep = floor(floor(depthDist) / 256.0);
//        float xsbfDep = mod(floor(depthDist), 256.0);
//        float zsbfSrc = floor(floor(srcDist) / 256.0);
//        float xsbfSrc = mod(floor(srcDist), 256.0);
//        fragColor = vec4(zsbfDep/256.0, xsbfDep/256.0, zsbfSrc/256.0, xsbfSrc/256.0);
    }


}
