import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class Utils {

    public static double generateArrivalInterval(Random stream) {
        int rand = stream.nextInt(99) + 1;

        NavigableMap<Integer, Integer> map = new TreeMap<>();
        map.put(1, 1);   //[1..36] ==> 1
        map.put(37, 2);  //[37..57] ==> 2
        map.put(58, 3);  //[58..72] ==> 3
        map.put(73, 4);  //[73..83] ==> 4
        map.put(84, 5);  //[84..86] ==> 5
        map.put(87, 6);  //[87] ==> 6
        map.put(88, 7);  //[88..92] ==> 7
        map.put(93, 8);  //[93] ==> 8
        map.put(94, 10);  //[94] ==> 10
        map.put(95, 12);  //[95] ==> 12
        map.put(96, 14);  //[96] ==> 14
        map.put(97, 20);  //[97..98] ==> 20
        map.put(99, 22);  //[99] ==> 22
        map.put(100, 33); //[100] ==> 33

        if (rand < 1 || rand > 100) {
            return 0; //out of range
        }
        return map.floorEntry(rand).getValue();
    }

    public static double generateSeverTime(Random stream) {
        int rand = stream.nextInt(99) + 1;

        NavigableMap<Integer, Integer> map = new TreeMap<>();
        map.put(1, 1);   //[1..42] ==> 1
        map.put(43, 2);  //[43..81] ==> 2
        map.put(82, 3);  //[82..92] ==> 3
        map.put(93, 4);  //[93..97] ==> 4
        map.put(98, 5);  //[98..100] ==> 5

        if (rand < 1 || rand > 100) {
            return 0; //out of range
        }
        return map.floorEntry(rand).getValue();
    }

}
