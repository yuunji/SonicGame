public class Level3 implements Level {
    private boolean hasBeenSet = false;

    @Override
    public int getNewActionP() {
        return 100;
    }

    @Override
    public int getLevel() {
        return 3;
    }

}
