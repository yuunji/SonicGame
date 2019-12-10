import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Coin extends activeEntity {


    public Coin(Point position, List<PImage> images, int actionPeriod) {
        super(position, images, 0, actionPeriod);
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz,
                this.getPosition().y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 ||
                (occupant.isPresent() && !(occupant.get().getClass() == Spike.class)))
        {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get().getClass() == Spike.class)))
            {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target_sonic = world.findNearest(
                this.getPosition(), Sonic.class);
        Optional<Entity> target_tails = world.findNearest(
                this.getPosition(), Tails.class);
        long nextPeriod = this.getActionPeriod();

        if (target_sonic.isPresent()) {
            Point tgtPos = target_sonic.get().getPosition();

            if (moveToTarget(world, target_sonic.get(), scheduler)) {

                nextPeriod += this.getActionPeriod();
            }
        }
        if (target_tails.isPresent()) {
            Point tgtPos = target_tails.get().getPosition();

            if (moveToTarget(world, target_tails.get(), scheduler)) {

                nextPeriod += this.getActionPeriod();
            }
        }
        scheduler.scheduleEvent( this,
                Activity.createActivityAction( this,world, imageStore),
                nextPeriod);
    }


    private boolean moveToTarget(WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            world.removeEntity( this);
            scheduler.unscheduleAllEvents(this);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (!occupant.isPresent())
                {
                    world.moveEntity(this, nextPos);

                }

            }
            return false;
        }
    }


    public static Coin createCoin(Point pos, int actionPeriod, List<PImage> images)
    {
        return new Coin(pos, images, actionPeriod);
    }


}
