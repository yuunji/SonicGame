public class Level2 implements Level {
    private boolean hasBeenSet = false;

    @Override
    public int getNewActionP() {
        return 300;
    }

    @Override
    public int getLevel() {
        return 2;
    }


}
