#version 330 core


//input from vertex shader
in struct VertexData
{
    vec3 normal;
    vec2 textureCoord;
    vec3 position;
} vertexData;


uniform vec3 firstLightColor;
uniform vec3 firstLightAttenuation;
in vec3 firstLightDirection;

uniform vec3 secondLightColor;
uniform vec3 secondLightAttenuation;
in vec3 secondLightDirection;


in vec3 viewDirection;

uniform vec3 ambientColor;
uniform vec3 emissionColor;
uniform vec3 secondLightOrientation;
uniform float secondLightSpotInnerAngle;
uniform float secondLightSpotOuterAngle;

//Material Attributes

in vec2 tc;
uniform float shine;
uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D spec;

//fragment shader output
out vec4 color;


void calculateLight(float intensity, vec3 direction,vec3 col, vec3 attenuation, vec3 N, vec3 V){
    vec3 ND = normalize(direction);
    float distance = length(direction);
    float attenuationCal = 1.0 / (attenuation.x + attenuation.y * distance + attenuation.z * (distance * distance));

    float cosa = max(0.0, dot(N, ND));
    vec3 DiffuseTerm = texture(diff, vertexData.textureCoord).rgb * col;
    color += vec4(DiffuseTerm * cosa, 0.0f) * intensity * attenuationCal;

    vec3 reflect = normalize(reflect(-ND,N));

    float cosb = max(0.0, dot(reflect,V));
    float cosbk = pow(cosb, shine);
    vec3 SpecularTerm = texture(spec, vertexData.textureCoord).rgb * col;
    color += vec4(SpecularTerm * cosbk, 0.0) * intensity * attenuationCal;
}

void main(){
    vec3 N = normalize(vertexData.normal);
    vec3 V = normalize(viewDirection);

    color = vec4(0,0,0,1.0);
    float theta = dot(normalize(secondLightDirection), normalize(-secondLightOrientation));
    float epsilon = secondLightSpotInnerAngle - secondLightSpotOuterAngle;
    float intensity = clamp((theta - secondLightSpotOuterAngle)/epsilon, 0.0, 1.0);

    if(theta > secondLightSpotInnerAngle){
        calculateLight(1.0, secondLightDirection, secondLightColor, secondLightAttenuation, N, V);
    }
    else{
        calculateLight(intensity, secondLightDirection, secondLightColor, secondLightAttenuation, N, V);
    }
    calculateLight(1.0, firstLightDirection, firstLightColor, firstLightAttenuation, N, V);

    vec3 emitColor = texture(emit, vertexData.textureCoord).rgb;
    vec3 AmbientEmissiveTerm = vec3(emitColor.x * emissionColor.x, emitColor.y * emissionColor.y, emitColor.z * emissionColor.z) + texture(diff, vertexData.textureCoord).rgb * ambientColor;
    color += vec4(AmbientEmissiveTerm, 0.0);
}

