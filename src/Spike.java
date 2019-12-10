import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Spike extends activeEntity {

    private static final Random rand = new Random();
    private static final String CRAB_KEY = "crab";
    private static final int CRAB_PERIOD_SCALE = 4;
    private static final int CRAB_ANIMATION_MIN = 50;
    private static final int CRAB_ANIMATION_MAX = 150;

    public Spike(Point position, List<PImage> images,
                 int actionPeriod) {
        super(position,images,0,actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point nextPos;
        if(this.getPosition().y + 1 > 19){
            nextPos = new Point(this.getPosition().x ,this.getPosition().y - 1);
        }
        if(this.getPosition().y - 1 < 0){
            nextPos = new Point(this.getPosition().x ,this.getPosition().y+1);
        }
        else {
            int nextY;
            Random random = new Random();
            boolean randum = random.nextBoolean();
            if (randum) nextY = 1;
            else nextY = -1;
            nextPos = new Point(this.getPosition().x,this.getPosition().y + nextY);
        }

        if (!this.getPosition().equals(nextPos))
        {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent() && (occupant.get().getClass() == Sonic.class || occupant.get().getClass() == Tails.class ))
            {
                scheduler.unscheduleAllEvents(occupant.get());
                world.setEndTime(System.currentTimeMillis());
                VirtualWorld.redraw = true;
                world.removeEntity(occupant.get());
                world.moveEntity(this, nextPos);

            }

            if(!occupant.isPresent()){
                world.moveEntity(this, nextPos);
            }
        }

        scheduler.scheduleEvent( this,
                Activity.createActivityAction( this,world, imageStore),
                this.getActionPeriod());
    }


    public static Spike createSpike(Point pos, int actionPeriod,
                                   List<PImage> images)
    {
        return new Spike(pos, images,
                actionPeriod);
    }

    }
