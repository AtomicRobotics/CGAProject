package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class TronCamera( var parent: Transformable? = null, var FOV:Float = Math.toRadians(90f), var aspectRatio:Float = 16f/9f, var nearPlane:Float = 0.1f, var farPlane:Float = 100f): Transformable(parent), ICamera {
    override fun getCalculateViewMatrix(): Matrix4f {
        val eye = getWorldPosition()
        val center = getWorldPosition().sub(getWorldZAxis())
        val up = getWorldYAxis()
        return  Matrix4f().lookAt(eye, center, up)
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(FOV,aspectRatio,nearPlane,farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("projection_matrix", this.getCalculateProjectionMatrix(), false)
        shader.setUniform("view_matrix", this.getCalculateViewMatrix(), false)
    }
}