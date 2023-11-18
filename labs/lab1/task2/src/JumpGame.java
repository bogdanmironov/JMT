public class JumpGame {
    public static boolean canWin(int[] array) {
        int goal = array.length;
        int curr = 0;

        while (curr+1 < goal) {
            if (array[curr] == 0) return false;

            int currMaxVal = array[curr];
            int currMaxOffset = 0;
            for (int i = 1; i <= array[curr]; i++) {
                if (curr + i >= array.length) break;

                if (array[curr+i] + i >= currMaxVal) {
                    currMaxVal = array[curr+i] + i;
                    currMaxOffset = i;
                }
            }

            curr = curr + currMaxOffset;

        }

        return true;
    }

}
