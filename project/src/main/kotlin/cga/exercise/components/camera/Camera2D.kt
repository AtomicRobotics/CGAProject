package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Camera2D(parent: Transformable? = null, var width:Float = 16f, var height:Float = 9f, var nearPlane:Float = 0.1f, var farPlane:Float = 100f): Transformable(parent), ICamera {
    override fun getCalculateViewMatrix(): Matrix4f {
        val eye = getWorldPosition()
        val center = getWorldPosition().sub(getWorldZAxis())
        val up = getWorldYAxis()
        return  Matrix4f().lookAt(eye, center, up)
    }


    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().ortho(-width/2, width/2, -height/2,height/2, nearPlane, farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("projection_matrix", this.getCalculateProjectionMatrix(), false)
        shader.setUniform("view_matrix", this.getCalculateViewMatrix(), false)
    }
}