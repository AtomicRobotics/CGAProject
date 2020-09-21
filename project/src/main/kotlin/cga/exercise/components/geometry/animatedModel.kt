package cga.exercise.components.geometry

import cga.exercise.components.animation.Animation
import cga.exercise.components.animation.KeyFrame
import cga.exercise.components.shader.ShaderProgram
import org.joml.Quaternionf

class animatedModel(_meshes:List<Renderable> = ArrayList<Renderable>(), _parent:Transformable? = null):Transformable(_parent){
    private val Meshes:List<Renderable> = _meshes

    var timer = 0f

    fun getMesh(index:Int):Renderable?{
        if(index < Meshes.size){
            return Meshes[index]
        }
        return null
    }

    fun animate(anim:Animation):Boolean{
        val prev = anim.getFramePrev(timer)
        val next = anim.getFrameNext(timer)
        for(m in Meshes){
            m.setRotation( Quaternionf(prev.frameData[m]!!).slerp(next.frameData[m]!!, (timer - prev.time) /(next.time - prev.time)))
        }
        return timer < anim.getAnimationLength()
    }

    fun getMeshes():ArrayList<Renderable>{
        return Meshes as ArrayList<Renderable>
    }

    fun render(shaderProgram: ShaderProgram){
        for(m in Meshes){
            m.render(shaderProgram)
        }
    }

    fun incrementTimer(t:Float){
        timer += t
    }

    fun resetTime(){
        timer = 0f
    }
}