package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

open class Transformable(var parentNode:Transformable? = null): ITransformable {
    private var modelMatrix:Matrix4f = Matrix4f(Vector4f(1f,0f,0f,0f),Vector4f(0f,1f,0f,0f),Vector4f(0f,0f,1f,0f),Vector4f(0f,0f,0f,1f))
    override fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.
                rotateX(pitch).
                rotateY(yaw).
                rotateZ(roll)
    }

    override fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        modelMatrix.
                translate(altMidpoint).
                rotate(pitch, Vector3f(1f,0f,0f)).
                rotate(yaw,Vector3f(0f,1f,0f)).
                rotate(roll, Vector3f(0f,0f,1f)).
                translate(altMidpoint.mul(-1f))
    }

    override fun translateLocal(deltaPos: Vector3f) {
        //var offsetVector3f = getXAxis().mul(deltaPos.x)
        //offsetVector3f = offsetVector3f.add(getYAxis().mul(deltaPos.y))
        //offsetVector3f = offsetVector3f.add(getZAxis().mul(deltaPos.z))
        modelMatrix.translate(deltaPos)
    }

    override fun translateGlobal(deltaPos: Vector3f) {
        var offsetVector3f = when(parentNode){
            null -> getXAxis().mul(deltaPos.x).add(getYAxis().mul(deltaPos.y).add(getZAxis().mul(deltaPos.z)))
            else -> parentNode!!.getXAxis().mul(deltaPos.x).add(parentNode!!.getYAxis().mul(deltaPos.y)).add(parentNode!!.getZAxis().mul(deltaPos.z))
        }
        modelMatrix.translate(offsetVector3f)
    }

    override fun scaleLocal(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    override fun getPosition(): Vector3f {
        return modelMatrix.normalize3x3().getColumn(3, Vector3f(0f))
    }

    override fun getWorldPosition(): Vector3f {
        return getWorldModelMatrix().normalize3x3().getColumn(3, Vector3f(0f))
    }

    override fun getXAxis(): Vector3f {
        return modelMatrix.getColumn(0, Vector3f(0f))
    }

    override fun getYAxis(): Vector3f {
        return modelMatrix.getColumn(1, Vector3f(0f))
    }

    override fun getZAxis(): Vector3f {
        return modelMatrix.getColumn(2, Vector3f(0f))
    }

    override fun getWorldXAxis(): Vector3f {
        return getWorldModelMatrix().normalize3x3().getColumn(0, Vector3f(0f))
    }

    override fun getWorldYAxis(): Vector3f {
        return getWorldModelMatrix().normalize3x3().getColumn(1, Vector3f(0f))
    }

    override fun getWorldZAxis(): Vector3f {
        return getWorldModelMatrix().normalize3x3().getColumn(2, Vector3f(0f))
    }

    override fun getWorldModelMatrix(): Matrix4f {
        val num = when(parentNode){
            null -> Matrix4f(modelMatrix)
            else -> parentNode!!.getWorldModelMatrix().mul(modelMatrix)
        }
        return num
    }

    override fun getLocalModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }
}