import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ring extends activeEntity {

    private static final Random rand = new Random();
    private static final int FISH_CORRUPT_MIN = 20000;
    private static final int FISH_CORRUPT_MAX = 30000;
    private static final String FISH_KEY = "fish";

    public Ring(Point position, List<PImage> images, int actionPeriod) {
        super(position,images,0,actionPeriod);
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }



    public static Ring createRing(Point pos, int actionPeriod,
                                    List<PImage> images)
    {
        return new Ring(pos, images, actionPeriod);
    }
}
