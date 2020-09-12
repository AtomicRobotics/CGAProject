package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.*

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        // todo 3.2
        diff.bind(0)
        emit.bind(1)
        specular.bind(2)
        shaderProgram.setUniform("diff", 0)
        shaderProgram.setUniform("emit", 1)
        shaderProgram.setUniform("spec", 2)
        shaderProgram.setUniform("tcMult", tcMultiplier)
        shaderProgram.setUniform("shine", shininess)
    }
}