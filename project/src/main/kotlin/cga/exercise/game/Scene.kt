package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Math
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import java.io.File
import kotlin.math.PI


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val tronShader:ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    private val tronShaderMeme:ShaderProgram = ShaderProgram("assets/shaders/tron_vert2.glsl", "assets/shaders/tron_frag.glsl")
    private val planeObj: OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    private var player = Transformable()
    private var Dolly = Transformable(player)
    private val lightCycle:Renderable = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90f), Math.toRadians(90f), 0f)!!
    private val planeObjMesh = planeObj.objects[0].meshes[0]
    private var planeMesh: Mesh
    private var plane: Renderable
    private var pointLight: PointLight
    private var spotLight: SpotLight
    private var camera: TronCamera
    private var speed = 0f
    private var rot = 0f
    private var oldx:Double = 0.0
    private var firstMouse = true;


    //scene setup
    init {

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE)
        glFrontFace(GL_CCW)
        glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        val stride: Int = 8 * 4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0)      //position
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4)   //textureCoordinate
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4)   //normalval
        val vertexAttributes = arrayOf(attrPos, attrTC, attrNorm)


        //Create Plane Material
        val diffTex = Texture2D("assets/textures/ground_diff.png", true)
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val emitTex = Texture2D("assets/textures/ground_emit.png", true)
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val specTex = Texture2D("assets/textures/ground_spec.png", true)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val material = Material(diffTex, emitTex, specTex, 60f, Vector2f(64f))

        //Create Plane Mesh
        planeMesh = Mesh(planeObjMesh.vertexData, planeObjMesh.indexData, vertexAttributes, material)

        //Set Player as the Parent of the Lightcycle
        lightCycle.parentNode = player

        //Create Renderable of of the plane Mesh
        plane = Renderable(mutableListOf(planeMesh))

        //Create PointLight with LightCycle as the Parent
        pointLight = PointLight(lightCycle, Vector3f(0f,1f,0f), Vector3f(1f,.66f,0f))

        //Create SpotLight with LightCycle as the Parent
        spotLight = SpotLight(lightCycle, Vector3f(0f,1f,-1.2f), Vector3f(1f,1f,1f), Math.toRadians(10f), Math.toRadians(40f))
        spotLight.rotateLocal(Math.toRadians(-5f),Math.toRadians(0f),0f)


        //Create Camera and set Player as Parent
        camera = TronCamera(Dolly)

        //Set up Camera angles and position
        camera.rotateLocal(Math.toRadians(-35f),0f,0f)
        camera.translateLocal(Vector3f(0f,0f, 4f))

        //Scale Lightcycle
        lightCycle.scaleLocal(Vector3f(0.8f))

    }



    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        tronShader.use()
        camera.bind(tronShader)
        tronShader.setUniform("ambientColor", Vector3f(0.1f,0.1f, 0.1f))
        spotLight.bind(tronShader, "secondLight", camera.getCalculateViewMatrix())
        pointLight.Color = Vector3f(Math.sin(Math.toRadians(t)*30),Math.sin(Math.toRadians(t + 100)*30),Math.sin(Math.toRadians(t + 200)*30))
        pointLight.bind(tronShader, "firstLight")
        tronShader.setUniform("emissionColor", Vector3f(Math.sin(Math.toRadians(t)*30),Math.sin(Math.toRadians(t + 100)*30),Math.sin(Math.toRadians(t + 200)*30)))
        lightCycle.render(tronShader)
        tronShader.setUniform("emissionColor", Vector3f(0f,1f,0f))
        plane.render(tronShader)
    }

    fun update(dt: Float, t: Float) {
        var deltaRot = 0f
        val rotSpeed = 2f
        val maxSpeed = 15f
        val accel = 5f
        if(Math.abs(speed) <= maxSpeed){

        }
        if(window.getKeyState(GLFW_KEY_W)){
            speed += accel * dt
        }
        else if(window.getKeyState(GLFW_KEY_S)){
            speed -= accel * dt
        }
        if(window.getKeyState(GLFW_KEY_A)){
            deltaRot += rotSpeed
        }
        if(window.getKeyState(GLFW_KEY_D)){
            deltaRot -= rotSpeed
        }
        speed -= accel * (speed/maxSpeed) * dt
        if(Math.abs(speed) <= .01f){
            speed = 0f
        }
        player.translateLocal(Vector3f(0f,0f, -speed * dt))
        player.rotateAroundPoint(0f, deltaRot * speed / maxSpeed * dt, 0f, Vector3f(0f,0f,0.3f))
        if(rot != 0f){
            Dolly.rotateLocal(0f, -rot * dt, 0f)
            rot = 0f
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        if(firstMouse){
            oldx = xpos
            firstMouse = false
        }
        if(xpos != oldx){
            rot = (xpos - oldx).toFloat() * 0.5f
            oldx = xpos
        }

    }


    fun cleanup() {}
}