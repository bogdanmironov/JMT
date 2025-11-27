package bg.sofia.uni.fmi.mjt.w3.argumentpassing.sealed;

public final class GymWorkout implements Exercise {

    private static final int GYM_CALORIES = 700;

    @Override
    public int getCaloriesBurnt() {
        return GYM_CALORIES;
    }

}