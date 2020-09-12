#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec3 normal;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

//uniform for Light 1
uniform vec3 firstLightPosition;
uniform vec3 firstLightColor;
uniform vec3 firstLightAttenuation;
//uniform for Light 2
uniform vec3 secondLightPosition;
uniform vec3 secondLightColor;
uniform vec3 secondLightAttenuation;

//uniform vec2 tcMult;
uniform vec2 tcMult;

out struct VertexData
{
    vec3 normal;
    vec2 textureCoord;
    vec3 position;
} vertexData;

out vec3 firstLightDirection;
out vec3 secondLightDirection;
out vec3 viewDirection;

//
void main(){
    //Calculate Normals
    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 1.0f);

    vec4 modelSpacePos = model_matrix * vec4(position, 1.0);
    vec4 viewSpacePos = view_matrix * modelSpacePos;
    vec4 vec = projection_matrix * viewSpacePos;
    gl_Position = vec;

    //Set VertexData

    vertexData.position = vec.xyz;
    vertexData.normal = norm.xyz;

    vertexData.textureCoord = texCoords * tcMult;

    //Calculate light direction

    vec4 lp = view_matrix * vec4(firstLightPosition, 1.0);
    firstLightDirection = (lp - viewSpacePos).xyz;

    vec4 lp2 = view_matrix * vec4(secondLightPosition, 1.0);
    secondLightDirection = (lp2 - viewSpacePos).xyz;

    //Calculate view direction

    viewDirection = -viewSpacePos.xyz;
}

