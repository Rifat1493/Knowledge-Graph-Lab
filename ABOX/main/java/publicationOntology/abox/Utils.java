package publicationOntology.abox;
import java.util.*;

public class Utils {
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return (int)(Math.random() * ((max - min) + 1)) + min;
    }
    public static String getLoremIpsum(){
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

    public static String getComment(String val) {
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("Approved", "This paper is approved.");
        commentMap.put("Rejected", "This paper is rejected.");
        return commentMap.get(val);
    }
}
