public class Animation extends Action{


    public Animation(animatedEntity entity, WorldModel world, ImageStore imageStore, int repeatCount){
        super(entity,world,imageStore,repeatCount);
    }


    public void executeAction(EventScheduler scheduler)
    {
        ((animatedEntity)this.getEntity()).nextImage();

        if (this.getRepeatCount() != 1)
        {
            scheduler.scheduleEvent(this.getEntity(),
                    createAnimationAction((animatedEntity)this.getEntity(), Math.max(this.getRepeatCount() - 1, 0)),
                    ((animatedEntity)this.getEntity()).getAnimationPeriod());
        }
    }

    public static Animation createAnimationAction(animatedEntity entity, int repeatCount)
    {
        return new Animation(entity, null, null, repeatCount);
    }
}
