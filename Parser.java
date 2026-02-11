import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Main {
    static String[] komutlar = {
            "ATM", "TOP", "CRP", "CIK", "BOL",
            "VE", "VEY", "D", "DEG", "DE",
            "DED", "DB", "DBE", "DK", "DKE",
            "OKU", "YAZ"
    };

    static String[] operandlar = {"AX", "BX", "CX", "DX"};

    static String[] etiketler = {
            "ETIKET1", "ETIKET2", "ETIKET3", "ETIKET4", "ETIKET5",
            "ETIKET6", "ETIKET7", "ETIKET8", "ETIKET9", "ETIKET10"
    };

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Insert the file path please: ");
        String filePath = input.nextLine();

        File file = new File(filePath);
        try {
            Scanner fileReader = new Scanner(file);
            boolean isWrong = false;
            int lineNo = 0;

            while (fileReader.hasNextLine()) {
                lineNo++;
                String line = fileReader.nextLine();

                if (line.isEmpty()) {
                    // boş satır atla
                } else {
                    line = trimSpaces(line);
                    boolean lineError = false;

                    String remainingLine = line;
                    int colonIndex = -1;
                    // ':' karakterinin pozisyonunu bulma (indexOf YOK!)
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ':') {
                            colonIndex = i;
                            i = line.length(); // break yok, ama bu şekilde döngüden çıkılır
                        }
                    }

                    if (colonIndex != -1) { // ':' bulunduysa
                        String possibleEtiket = "";
                        for (int i = 0; i < colonIndex; i++) {
                            possibleEtiket = possibleEtiket + line.charAt(i);
                        }

                        if (!isEtiketValid(possibleEtiket)) {
                            lineError = true;
                        }

                        // ':' dan sonraki karakter kontrolü
                        if (colonIndex + 1 < line.length()) {
                            char nextChar = line.charAt(colonIndex + 1);
                            if (nextChar != ' ') {
                                lineError = true;
                            } else {
                                String temp = "";
                                for (int i = colonIndex + 1; i < line.length(); i++) {
                                    temp = temp + line.charAt(i);
                                }
                                remainingLine = trimSpaces(temp);
                            }
                        } else {
                            remainingLine = "";
                        }
                    }

                    if (!remainingLine.isEmpty()) {
                        String[] parts = remainingLine.split(" ");

                        if (parts.length == 0 || parts[0].isEmpty()) {
                            lineError = true;
                        } else {
                            String komut = parts[0];
                            if (!isKomutValid(komut)) {
                                lineError = true;
                            } else if (komut.equals("ATM")) {
                                if (parts.length != 3) {
                                    lineError = true;
                                } else if (!isOperandValid(parts[1] , parts[2] , komut)){//bu overloaded fonksiyonda hatalı return statement bulunuyor Dikkat!
                                    lineError = true;
                                }
                            } else if (komut.equals("TOP") || komut.equals("CRP") || komut.equals("CIK")
                                    || komut.equals("BOL") || komut.equals("VE") || komut.equals("VEY")) {
                                if (parts.length != 3) {
                                    lineError = true;
                                } else if (!isOperandValid(parts[1], komut) || !isOperandValid(parts[2], komut)) {
                                    lineError = true;
                                }
                            } else if (komut.equals("D") || komut.equals("DKE") || komut.equals("DED")
                                    || komut.equals("DB") || komut.equals("DBE") || komut.equals("DK") || komut.equals("DE")) {
                                if (parts.length != 2) {
                                    lineError = true;
                                } else if (!isAdresValid(parts[1])) {
                                    lineError = true;
                                }
                            } else if (komut.equals("OKU") || komut.equals("YAZ") || komut.equals("DEG")) {
                                if (parts.length != 2) {
                                    lineError = true;
                                } else if (!isOperandValid(parts[1], komut)) {
                                    lineError = true;
                                }
                            }
                        }
                    }

                    if (lineError) {
                        isWrong = true;
                        System.out.println("Yazım hatası bulunmaktadır : Satır " + lineNo + " => " + line);
                    }
                }
            }

            fileReader.close();

            if (isWrong) {
                System.out.println("Yazım hatası bulunmaktadır!");
            } else {
                System.out.println("Yazım hatası bulunmamaktadır!");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Dosya bulunamadı " + filePath);
        }
    }

    public static String trimSpaces(String line) {
        int start = 0;
        int end = line.length() - 1;

        while (start <= end && line.charAt(start) == ' ') {
            start++;
        }
        while (end >= start && line.charAt(end) == ' ') {
            end--;
        }

        String newLine = "";
        for (int i = start; i <= end; i++) {
            newLine = newLine + line.charAt(i);
        }
        return newLine;
    }

    public static boolean isEtiketValid(String possibleEtiket) {
        for (int i = 0; i < etiketler.length; i++) {
            if (possibleEtiket.equals(etiketler[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKomutValid(String komut) {
        for (int i = 0; i < komutlar.length; i++) {
            if (komut.equals(komutlar[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperandValid(String operand , String operand2, String komut) {//ATM için
        if(isOperandValid(operand , komut)){
            boolean isNumber = true;
            for (int i = 0; i < operand.length(); i++) {
                char c = operand.charAt(i);
                if (c < '0' || c > '9') {
                    isNumber = false;
                }
            }
            if(isNumber){
                return false;
            }
            return true;//hata
        }

        if(isOperandValid(operand2 , komut)){
            return true;
        }
        return false;
    }

    public static boolean isOperandValid(String operand, String komut) {
        for (int i = 0; i < operandlar.length; i++) {
            if (operand.equals(operandlar[i])) {
                return true;
            }
        }

        boolean isNumber = true;
        for (int i = 0; i < operand.length(); i++) {
            char c = operand.charAt(i);
            if (c < '0' || c > '9') {
                isNumber = false;
            }
        }
        if(komut.equals("OKU") && isNumber) {
            return false;
        }
        if (isNumber) {
            return true;
        }

        if (!komut.equals("OKU")) {
            if (operand.length() >= 3 &&
                    operand.charAt(0) == '[' &&
                    operand.charAt(operand.length() - 1) == ']') {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdresValid(String adres) {
        for (int i = 0; i < etiketler.length; i++) {
            if (adres.equals(etiketler[i])) {
                return true;
            }
        }
        return false;
    }
}