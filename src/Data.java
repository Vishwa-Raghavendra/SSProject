import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Data
{
    static Map<String,String> numsToBinary = new HashMap<>();
    static Map<String,String> binaryToNums = new HashMap<>();
    static Set<String > format2 = new HashSet<>();

    static {
        binaryToNums.put("0000","0");
        binaryToNums.put("0001","1");
        binaryToNums.put("0010","2");
        binaryToNums.put("0011","3");
        binaryToNums.put("0100","4");
        binaryToNums.put("0101","5");
        binaryToNums.put("0110","6");
        binaryToNums.put("0111","7");
        binaryToNums.put("1000","8");
        binaryToNums.put("1001","9");
        binaryToNums.put("1010","A");
        binaryToNums.put("1011","B");
        binaryToNums.put("1100","C");
        binaryToNums.put("1101","D");
        binaryToNums.put("1110","E");
        binaryToNums.put("1111","F");


        numsToBinary.put("0","0000");
        numsToBinary.put("1","0001");
        numsToBinary.put("2","0010");
        numsToBinary.put("3","0011");
        numsToBinary.put("4","0100");
        numsToBinary.put("5","0101");
        numsToBinary.put("6","0110");
        numsToBinary.put("7","0111");
        numsToBinary.put("8","1000");
        numsToBinary.put("9","1001");
        numsToBinary.put("A","1010");
        numsToBinary.put("B","1011");
        numsToBinary.put("C","1100");
        numsToBinary.put("D","1101");
        numsToBinary.put("E","1110");
        numsToBinary.put("F","1111");


        format2.add("CLEAR");
        format2.add("TIXR");
        format2.add("COMPR");
    }

    static int getFormat(String label)
    {
        if(label.contains("+"))
            return 4;
        else if(format2.contains(label))
            return 2;
        else
            return 3;
    }
}
