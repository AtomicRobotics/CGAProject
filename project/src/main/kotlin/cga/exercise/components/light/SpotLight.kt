package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Math
import org.joml.Vector4f

class SpotLight(parentNode: Transformable?, _positionSet: Vector3f, _Color: Vector3f, _innerAngle:Float, _outerAngle:Float, _Attenuation:Vector3f = Vector3f(0.5f, 0.05f, 0.01f)): PointLight(parentNode, _positionSet, _Color, _Attenuation), ISpotLight {
    private var innerAngle:Float = _innerAngle
    private var outerAngle:Float = _outerAngle

    init{
        this.translateGlobal(positionSet)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        shaderProgram.setUniform("${name}Position", this.getWorldPosition())
        shaderProgram.setUniform("${name}Color", Color)
        shaderProgram.setUniform("${name}Attenuation", Attenuation)
        shaderProgram.setUniform("${name}SpotInnerAngle", Math.cos(innerAngle))
        shaderProgram.setUniform("${name}SpotOuterAngle", Math.cos(outerAngle))
        val V = Vector4f(this.getWorldZAxis().negate(), 0f).mul(viewMatrix)
        shaderProgram.setUniform("${name}Orientation", Vector3f(V.x,V.y,V.z))
    }
}