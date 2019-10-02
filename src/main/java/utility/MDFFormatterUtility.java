package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MDFFormatterUtility {
    public static String convertBinaryStringToHexString(String binString){
        String hexString = Arrays.stream(splitToNChar(binString, 4)).map(s -> MDFFormatterUtility.binToHex(s)).collect(Collectors.joining());
        return hexString;
    }


    public static String convertHexStringToBinString(String hexString){
        String binString = Arrays.stream(hexString.split(""))
                .map(MDFFormatterUtility::hexToBin)
                .collect(Collectors.joining());
        return binString;
    }

    /**
     * helper function for convertHexToBinString
     *
     * @param hexString
     * @return
     */
    private static String hexToBin(String hexString){
        int hex = Integer.parseInt(hexString, 16);
        String binString = String.format("%4s", Integer.toBinaryString(hex)).replace(" ", "0");
        return binString;
    }


    private static String binToHex(String binString){
        int decimal = Integer.parseInt(binString, 2);
        String hexStr = Integer.toString(decimal, 16);
        return hexStr;
    }

    private static String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }

}
