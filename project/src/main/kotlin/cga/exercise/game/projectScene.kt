package cga.exercise.game

import cga.exercise.components.animation.Animation
import cga.exercise.components.animation.KeyFrame
import cga.exercise.components.camera.Camera2D
import cga.exercise.components.camera.ICamera
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
import org.joml.Math
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11
import kotlin.random.Random

class projectScene(_window:GameWindow):abstractScene(_window) {
    private val tronShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    private val ground = ModelLoader.loadModel("assets/models/ground.obj", 0f, 0f, 0f)!!
    private val tree:Renderable = ModelLoader.loadModel("assets/models/tree.obj", 0f, 0f, 0f)!!
    private val moon = ModelLoader.loadModel("assets/models/moon.obj", 0f, 0f ,0f)!!
    private val treeSpread = 30f
    private val treeArray = Array<Renderable>(150){x:Int -> var y = tree.copy();y.translateLocal(Vector3f((Random.nextFloat() - .5f) * treeSpread,0f, (Random.nextFloat() - .5f) * treeSpread)); y}
    private val birdMeshes = ArrayList<Renderable>()
    private val bird:animatedModel
    private val dolly = Transformable()
    private var spotLight = SpotLight(null, Vector3f(0f,1f,-1.2f), Vector3f(0f,0f,0f), Math.toRadians(10f), Math.toRadians(40f))
    private var pointLight: PointLight = PointLight(null , Vector3f(0f,3f,0f), Vector3f(1f,1f,1f), Vector3f(0.01f, 0.01f, 0.1f))
    private var camera1:TronCamera = TronCamera()
    private var camera2:Camera2D = Camera2D(null, 16/8f, 9/8f)
    private var activeCam:ICamera = camera1

    private var camRot = true

    private var animation = Animation()

    init{
        GL11.glClearColor(0f,0f,0f, 1.0f); GLError.checkThrow()

        birdMeshes.add(ModelLoader.loadModel("assets/models/body.obj", 0f, 0f ,0f)!!)
        birdMeshes[0].name = "body"
        birdMeshes.add(ModelLoader.loadModel("assets/models/lwing.obj", 0f, 0f ,0f)!!)
        birdMeshes[1].name = "lwing"
        birdMeshes.add(ModelLoader.loadModel("assets/models/rwing.obj", 0f, 0f ,0f)!!)
        birdMeshes[2].name = "rwing"
        bird = animatedModel(birdMeshes)
        birdMeshes[0].setChild(birdMeshes[1])
        birdMeshes[0].setChild(birdMeshes[2])
        birdMeshes[0].setParent(bird)

        bird.rotateLocal(0f,Math.toRadians(-90f), 0f)
        dolly.translateLocal(Vector3f(0f, 2f, 0f))

        var frames = Array<KeyFrame>(4){x:Int -> KeyFrame(bird) }
        frames[1].frameData.put(birdMeshes[1], Quaternionf(-0.1736482f, 0f, 0f, 0.9848078f))
        frames[1].frameData.put(birdMeshes[2], Quaternionf(0.1736482f, 0f, 0f, 0.9848078f))
        frames[1].time = .3f
        frames[2].frameData.put(birdMeshes[1], Quaternionf(0.35f, 0f, 0f, 0.9848078f))
        frames[2].frameData.put(birdMeshes[2], Quaternionf(-0.35f, 0f, 0f, 0.9848078f))
        frames[2].time = .4f
        frames[3].time = .6f

        for(f in frames){
            animation.addFrame(f)
        }

        val diffTex = Texture2D("assets/models/Leave.png", true)
        diffTex.setTexParams(GL11.GL_REPEAT, GL11.GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)
        val emitTex = Texture2D("assets/textures/ground_diff.png", true)
        emitTex.setTexParams(GL11.GL_REPEAT, GL11.GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)
        val specTex = Texture2D("assets/models/Leave.png", true)
        specTex.setTexParams(GL11.GL_REPEAT, GL11.GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)
        ground.Meshes[0].material = Material(diffTex, emitTex, specTex, 60f, Vector2f(64f))

        bird.setParent(dolly)
        camera1.setParent(dolly)
        camera2.setParent(dolly)
        pointLight.setParent(bird)
        camera1.rotateLocal(Math.toRadians(-0f),0f,Math.toRadians(-0f))
        camera1.translateLocal(Vector3f(0f,.1f, .3f))
        camera2.rotateLocal(0f,Math.toRadians(90f),0f)
        camera2.translateLocal(Vector3f(0f,0f,20f))
        moon. translateLocal(Vector3f(100f, 100f, -100f))
        moon.scaleLocal(Vector3f(50f))
        ground.scaleLocal(Vector3f(100f,0f,100f))
    }

    override fun render(dt: Float, t: Float) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        tronShader.use()
        activeCam.bind(tronShader)
        tronShader.setUniform("ambientColor", Vector3f(0.01f,0.01f, 0.01f))
        spotLight.bind(tronShader, "secondLight", activeCam.getCalculateViewMatrix())
        pointLight.bind(tronShader, "firstLight")
        for(t in treeArray){
            if(!camRot && t.getPosition().x < -3f){
                t.render(tronShader)
            }
            else if(camRot){
                t.render(tronShader)
            }
        }
        ground.render(tronShader)
        bird.render(tronShader)
        tronShader.setUniform("emissionColor", Vector3f(1f,1f, 1f))
        moon.render(tronShader)
        tronShader.setUniform("emissionColor", Vector3f(0f,0f, 0f))

    }

    private var rot = 0.0f
    private var fallSpeed = 0f
    private var moveSpeed = 0f
    private var timer = -100f
    private var pressed = false
    private var animate = false
    override fun update(dt: Float, t: Float) {
        var birdMove = Vector3f(0f)
        if(camRot){
            if(window.getKeyState(GLFW_KEY_A)){
                moveSpeed -= .02f * dt
            }
            if(window.getKeyState(GLFW_KEY_D)){
                moveSpeed += .02f * dt
            }

            bird.setRotation(Quaternionf(   0.1227878f, -0.6963642f, 0.1227878f, 0.6963642f  ).slerp(Quaternionf(  -0.1227878f, -0.6963642f, -0.1227878f, 0.6963642f ), Math.max(-.5f, Math.min(.5f, 10* moveSpeed)) +.5f))
            birdMove.add(moveSpeed,0f,0f)
            moveSpeed *= 0.99f


            if(rot != 0f){
                camera1.rotateAroundPoint(0f, -rot * dt, 0f, Vector3f(0f,-.10f,-.30f))
                rot = 0f
            }
        }
        for(t in treeArray){
            t.translateLocal(Vector3f(0f,0f,dt))
            if(t.getPosition().z > treeSpread/2){
                t.reset()
                t.translateLocal(Vector3f((Random.nextFloat() - .5f) * treeSpread, 0f, -treeSpread/2))
            }
        }

        if(window.getKeyState(GLFW_KEY_F) && !pressed){
            if(camRot){
                activeCam = camera2
                camRot = false
            }
            else{
                activeCam = camera1
                camRot = true
            }
            pressed = true
        }
        else{
            pressed = false
        }
        if(window.getKeyState(GLFW_KEY_SPACE)){
            if(!pressed && t - timer > .9f && fallSpeed > -.01f){
                fallSpeed -= .015f
                pressed = true
                timer = t
                animate = true
                bird.resetTime()
            }
            else if(!pressed && t - timer > .5f){
                fallSpeed -= .003f
                pressed = true
                animate = true
                bird.resetTime()
                timer = t
            }
        }
        else{
            pressed = false
        }
        if(animate){
            animate = bird.animate(animation)
            bird.incrementTimer(dt)
        }
        else{
            bird.resetTime()
            bird.animate(animation)
        }
        if(dolly.getPosition().y < 0){
            dolly.setPosition(Vector3f(0f, 3f, 0f))
            fallSpeed = 0f
            moveSpeed = 0f
        }

        fallSpeed += 1f * dt *dt
        birdMove.sub(0f, fallSpeed, 0f)

        dolly.translateGlobal(birdMove)
    }

    private var firstMouse = false
    private var oldx = 0.0
    override fun onMouseMove(xpos: Double, ypos: Double) {
        if(firstMouse){
            oldx = xpos
            firstMouse = false
        }
        if(xpos != oldx){
            rot = (xpos - oldx).toFloat() * 0.5f
            oldx = xpos
        }

    }
}