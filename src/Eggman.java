import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Eggman extends activeEntity {

    public Eggman(Point position, List<PImage> images, int actionPeriod) {
        super(position,images,0,actionPeriod);
    }


    public  static Eggman createEggman(Point pos, List<PImage> images, int actionPeriod)
    {
        return new Eggman(pos, images,actionPeriod);
     }

    @Override
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point nextPos;
        if(this.getPosition().x + 1 > 29){
            nextPos = new Point(this.getPosition().x -1,this.getPosition().y);
        }
        if(this.getPosition().x - 1 < 0){
            nextPos = new Point(this.getPosition().x +1,this.getPosition().y);
        }
        else {
            int nextX;
            Random random = new Random();
            boolean randum = random.nextBoolean();
            if (randum) nextX = 1;
            else nextX = -1;
            nextPos = new Point(this.getPosition().x + nextX,this.getPosition().y);
        }

        if (!this.getPosition().equals(nextPos))
        {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent() && (occupant.get().getClass() == Sonic.class))
            {
                world.removeEntity(occupant.get());
                world.setEndTime(System.currentTimeMillis());
                VirtualWorld.redraw = true;
                scheduler.unscheduleAllEvents(occupant.get());

            }
            if(!occupant.isPresent()){
                world.moveEntity(this, nextPos);

            }
        }
        scheduler.scheduleEvent( this,
                Activity.createActivityAction( this,world, imageStore),
                this.getActionPeriod());
    }
}
