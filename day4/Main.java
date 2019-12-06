
public class Main {

    private final static int LOWER = 357253;
    private final static int UPPER = 892942;
    private final static String[] DOUBLES = new String[] { "00", "11", "22", "33", "44", "55", "66", "77", "88",
            "99", };

    public static boolean isValid(int number) {

        String snum = Integer.toString(number);
        boolean foundDouble = false;

        for (String d : DOUBLES) {

            for (int i = 0; i < snum.length() - 1; ++i) {
                if (snum.substring(i, i + 2).equals(d)) {

                    boolean found = true;

                    char c = d.charAt(0);
                    // check second half condition
                    if (i > 0) {
                        if (snum.charAt(i - 1) == c) {
                            found = false;
                        }
                    }
                    if (i < snum.length() - 2) {
                        if (snum.charAt(i + 2) == c) {
                            found = false;
                        }
                    }

                    foundDouble = foundDouble || found;
                }
            }
        }

        if (!foundDouble)
            return false;

        for (int i = 0; i < snum.length() - 1; ++i) {
            int left = Integer.parseInt(snum.substring(i, i + 1));
            int right = Integer.parseInt(snum.substring(i + 1, i + 2));

            if (right < left) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        int count = 0;
        for (int i = LOWER; i < UPPER; ++i) {
            if (isValid(i))
                ++count;
        }

        System.out.println("Is Valid" + isValid(112233));
        System.out.println("Is Valid" + isValid(123444));
        System.out.println("Is Valid" + isValid(111122));
        System.out.println("Count " + count);
    }
}