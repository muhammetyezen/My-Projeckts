import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class ManLanGeneration {

    static String[] etiketler = {
            "ETIKET1", "ETIKET2", "ETIKET3", "ETIKET4", "ETIKET5",
            "ETIKET6", "ETIKET7", "ETIKET8", "ETIKET9", "ETIKET10"
    };

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.print("Insert the file path please: ");
        String filePath = input.nextLine();
        String totalOpCode = "";
        String hexadecimal = "";
        ArrayList<String[]> program = new ArrayList<>();

        File file = new File(filePath);

        try (Scanner fileReader = new Scanner(file)) {

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine().trim();

                if (line.isEmpty()) continue;

                program.add(line.split("\\s+"));//bosluklara gore ayiriyoruz
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return;
        }

        for (String[] line : program) {//yazdiriyoruz
            for (String token : line) {
                System.out.print(token + " ");
            }
            System.out.println();
        }

        for(int i = 0; i < program.size(); i++){
            String[] line = program.get(i);
            String lineToBinary = parseOpCode(line);
            totalOpCode += lineToBinary;
            String hexLine = "";
            for (int j = 0; j < lineToBinary.length(); j += 4) {
                String fourBit = "";
                for (int k = j; k < j + 4 && k < lineToBinary.length(); k++) {//Burada j + 4 e kadar gidiyoruz ayrica satir uzunlugu 4 ten kucuk olabildiginden ayri bir sart eklemis olduk
                    fourBit += lineToBinary.charAt(k);
                }
                hexLine += toHexNumber(fourBit);
                hexadecimal += toHexNumber(fourBit);
            }
            System.out.println("Line : " + (i+1) + " Binary Code : " + lineToBinary);
            System.out.println("Line : " + (i+1) + " Hex Code : " + hexLine);
        }
        System.out.println("Total Hexadecimal:\n" + hexadecimal);
        System.out.println("Total op-code:\n" + totalOpCode);

    }

    static String parseOpCode(String[] statement) {
        String opCode = "";
        for (int i = 0; i < statement.length; i++) {
            if (statement[i].equals("AX")) opCode+= "000";
            else if (statement[i].equals("BX")) opCode += "001";
            else if (statement[i].equals("CX")) opCode += "010";
            else if (statement[i].equals("DX")) opCode += "011";
            else if (isRAM(statement[i])) {
                String number = statement[i].substring(1, statement[i].length() - 1);
                opCode += "100" + toBinaryNumber(Integer.parseInt(number));
            } else if (isNumber(statement[i])) opCode += toBinaryNumber(Integer.parseInt(statement[i]));
            else if (statement[i].equals("ATM")) opCode += "00000";
            else if (statement[i].equals("TOP")) opCode += "00001";
            else if (statement[i].equals("CRP")) opCode += "00010";
            else if (statement[i].equals("CIK")) opCode += "00011";
            else if (statement[i].equals("BOL")) opCode += "00100";
            else if (statement[i].equals("VE")) opCode += "00101";
            else if (statement[i].equals("VEY")) opCode += "00110";
            else if (statement[i].equals("D")) opCode += "00111";
            else if (statement[i].equals("DEG")) opCode += "01000";
            else if (statement[i].equals("DE")) opCode += "01001";
            else if (statement[i].equals("DED")) opCode += "01010";
            else if (statement[i].equals("DB")) opCode += "01011";
            else if (statement[i].equals("DBE")) opCode += "01100";
            else if (statement[i].equals("DK")) opCode += "01101";
            else if (statement[i].equals("DKE")) opCode += "01110";
            else if (statement[i].equals("OKU")) opCode += "01111";
            else if (statement[i].equals("YAZ")) opCode += "10000";
            else if (isEtiket(statement[i])) {
                if (statement[i].contains(":")) opCode += "";
                else opCode += "1111111";
            }
        }
        return opCode;
    }

    static boolean isEtiket(String s) {
        for (int i = 0; i < etiketler.length; i++) {
            if (etiketler[i].equals(s)) return true;
        }
        return s.endsWith(":");
    }
    static boolean isRAM(String register) {
        if (register == null || register.length() < 3) return false;

        if (register.charAt(0) != '[' || register.charAt(register.length() - 1) != ']') {
            return false;
        }

        String content = register.substring(1, register.length() - 1).trim();
        if (content.isEmpty()) return false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c < '0' || c > '9') return false;
        }

        return true;
    }

    static String toBinaryNumber(int n) {

        if (n < 0 || n > 127) {
            System.out.println("Hata: Sabit 0 ile 127 arasinda olmali");
            return "";
        }

        String binary = Integer.toBinaryString(n);

        return binary;
    }
    static boolean isNumber(String s) {

        if (s == null || s.length() == 0) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    static String toHexNumber(String exp) {

        int result = 0;
        int power = exp.length() - 1;

        for (int i = 0; i < exp.length(); i++) {
            int bit = exp.charAt(i) - '0';
            result += bit * usAl(2, power);
            power--;
        }

        if (result == 10) return "A";
        if (result == 11) return "B";
        if (result == 12) return "C";
        if (result == 13) return "D";
        if (result == 14) return "E";
        if (result == 15) return "F";

        return String.valueOf(result);
    }

    static int usAl(int taban, int us) {
        int sonuc = 1;
        for (int i = 0; i < us; i++) {
            sonuc *= taban;
        }
        return sonuc;
    }
}