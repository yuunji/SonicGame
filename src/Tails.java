import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Tails extends animatedEntity {


    public Tails(Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(position,images,0,actionPeriod,animationPeriod);

    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Random rand = new Random();
        int x = rand.nextInt(30);
        int y = rand.nextInt(20);
        Point randPoint = new Point(x,y);
        while(world.isOccupied(randPoint)){
            x = rand.nextInt(30);
            y = rand.nextInt(20);
            randPoint = new Point(x,y);
        }
        Coin coin = Coin.createCoin(randPoint,1500,imageStore.getImageList("coin"));
        world.addEntity(coin);
        coin.scheduleActions(scheduler,world,imageStore);
        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                this.getActionPeriod());


    }

    public static Tails createTails(Point pos, List<PImage> images)
    {
        return new Tails(pos, images, 13000, 100);
    }

}
