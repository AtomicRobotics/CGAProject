package cga.exercise.components.animation

class Animation(private var frames:ArrayList<KeyFrame> = ArrayList<KeyFrame>()){
    fun addFrame(frame:KeyFrame){
        frames.add(frame)
    }
    fun getFrame(index:Int):KeyFrame{
        return frames[index]
    }

    fun getFramePrev(time:Float):KeyFrame{
        var frame = frames[0]
        for(f in frames){
            if(f.time <= time){
                frame = f
            }
        }
        return frame
    }

    fun getFrameNext(time:Float):KeyFrame{
        var frame = frames.last()
        for(f in frames.asReversed()){
            if(f.time > time){
                frame = f
            }
        }
        return frame
    }

    fun getAnimationLength():Float{
        return frames.last().time
    }
}