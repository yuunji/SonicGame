import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Sonic extends animatedEntity {


    public Sonic(Point position, List<PImage> images,
                 int actionPeriod, int animationPeriod) {
        super(position,images,0,actionPeriod,animationPeriod);
    }



    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //activity is now executed by key presses in virtual world

    }



    public static Sonic createSonic(Point pos, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new Sonic(pos, images, actionPeriod, animationPeriod);
    }

}
