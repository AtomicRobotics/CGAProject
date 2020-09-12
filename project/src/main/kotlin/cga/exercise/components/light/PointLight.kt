package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f
import org.joml.Vector4f

open class PointLight(ParentNode:Transformable?, var positionSet:Vector3f, var Color:Vector3f, var Attenuation:Vector3f = Vector3f(1f, 0.5f, 0.1f)): Transformable(ParentNode), IPointLight {

    init{
        this.translateGlobal(positionSet)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.setUniform("${name}Position", this.getWorldPosition())
        shaderProgram.setUniform("${name}Color", Color)
        shaderProgram.setUniform("${name}Attenuation", Attenuation)
    }
}