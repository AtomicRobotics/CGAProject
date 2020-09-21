package cga.exercise.components.animation

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.geometry.animatedModel
import org.joml.Quaternionf

class KeyFrame(mesh:animatedModel) {
    var frameData = HashMap<Transformable, Quaternionf>()
    var time = 0f
    init{
        for(m in mesh.getMeshes()){
            addFrameData(m, m.getRotation())
        }
    }

    override fun toString(): String {
        var a = "{time: $time"
        for(f in frameData){
            a = "$a, [${f.key}, ${f.value}]"
        }
        return "$a}"
    }

    fun addFrameData(joint: Transformable, rotation:Quaternionf){
        frameData.put(joint, rotation)
    }
}