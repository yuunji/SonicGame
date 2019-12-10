public class Level0 implements Level {

    private boolean hasBeenSet = true;

    @Override
    public int getNewActionP() {
        return 550;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void setHasBeenSet(boolean b){
        this.hasBeenSet = b;
    }
}
