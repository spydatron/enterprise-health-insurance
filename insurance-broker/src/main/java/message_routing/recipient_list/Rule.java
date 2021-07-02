package main.java.message_routing.recipient_list;

public class Rule {
//    private static final char DOUBLE_QUOTE = '"';
//    public static final String CATHARINA  = "#{age} >= 10 && #{treatmentCodeLetters} = " + DOUBLE_QUOTE + "ORT" + DOUBLE_QUOTE ;
    public static final String CATHARINA  = "#{age} >= 10 && #{codeValue} > 0" ;
    public static final String MAXIMA     = "#{age} >= 18";
    public static final String UMC        = "#{age} >= 0";

    public static int isRightTreatmentCode(String treatmentCode){
        String treatCodeLetters = treatmentCode.substring(0, 3);

        if(treatCodeLetters.equals("ORT")){
            return 1;
        }
        return 0;
    }
}
