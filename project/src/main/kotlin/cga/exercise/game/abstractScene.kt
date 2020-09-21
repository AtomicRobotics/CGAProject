package cga.exercise.game

import cga.framework.GLError
import cga.framework.GameWindow
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

open class abstractScene(val window:GameWindow) {

    init{
        //initial opengl state
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glFrontFace(GL11.GL_CCW)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glEnable(GL11.GL_DEPTH_TEST); GLError.checkThrow()
        GL11.glDepthFunc(GL11.GL_LESS); GLError.checkThrow()
    }

    open fun render(dt: Float, t: Float) {}

    open fun update(dt: Float, t: Float) {}

    open fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    open fun onMouseMove(xpos: Double, ypos: Double) {}

    open fun cleanup() {}
}