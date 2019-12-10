import processing.core.PImage;

import java.util.List;

public abstract class activeEntity extends Entity {

    private int actionPeriod;

    public activeEntity(Point position, List<PImage> images, int imageIndex,int actionPeriod) {
        super(position, images, imageIndex);
        this.actionPeriod = actionPeriod;
    }

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }

    protected int getActionPeriod(){
        return this.actionPeriod;
    }


    protected void incrementActionPeriod(int level){
        this.actionPeriod -= (200* level);
     }

}
