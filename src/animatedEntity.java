import processing.core.PImage;

import java.util.List;

public abstract class animatedEntity extends activeEntity {

    private final int animationPeriod;


    public animatedEntity(Point position, List<PImage> images, int imageIndex,int actionPeriod,int animationPeriod) {
        super(position, images, imageIndex,actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    protected int getAnimationPeriod(){
        return this.animationPeriod;
    }
    protected void nextImage(){
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size());
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        super.scheduleActions( scheduler, world, imageStore);
        scheduler.scheduleEvent( this,
                Animation.createAnimationAction( this,0), this.getAnimationPeriod());
    }
}
