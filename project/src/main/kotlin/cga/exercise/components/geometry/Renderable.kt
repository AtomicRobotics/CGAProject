package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

class Renderable(var Meshes:MutableList<Mesh>): Transformable(), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        for(m:Mesh in Meshes){
            shaderProgram.setUniform( "model_matrix", this.getWorldModelMatrix(), false)
            m.render(shaderProgram)
        }
    }
}