package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

class Renderable(var Meshes:MutableList<Mesh>, var name:String = ""): Transformable(), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        for(m:Mesh in Meshes){
            shaderProgram.setUniform( "model_matrix", this.getWorldModelMatrix(), false)
            m.render(shaderProgram)
        }
    }

    fun copy():Renderable{
        return Renderable(Meshes)
    }
}