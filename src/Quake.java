import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Quake extends animatedEntity{

    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;

    public Quake(Point position, List<PImage> images,
                int actionPeriod, int animationPeriod) {
        super(position,images,0,actionPeriod,animationPeriod);
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity( this);
    }




    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent( this,
                Activity.createActivityAction( this,world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent( this,
                Animation.createAnimationAction( this,QUAKE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

    public static Quake createQuake(Point pos, List<PImage> images)
    {
        return new Quake(pos, images, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
    }

    }
