import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class Execution3 {
    static int AX = 0;
    static int BX = 0;
    static int CX = 0;
    static int DX = 0;

    static boolean zeroFlag = false;
    static boolean overflowFlag = false;
    static boolean signFlag = false;

    static int[] RAM = new int[256];//AX += RAM[12]

    static String[] etiketler = {
            "ETIKET1", "ETIKET2", "ETIKET3", "ETIKET4", "ETIKET5",
            "ETIKET6", "ETIKET7", "ETIKET8", "ETIKET9", "ETIKET10"
    };

    static int[] etiketAdresleri = new int[etiketler.length];//her etiketin referans ettigi satri tutmak icin etiketler boyutu kadar referas dizisi

    public static void main(String[] args) {
        for (int i = 0; i < etiketAdresleri.length; i++) etiketAdresleri[i] = -1;//en basta hic biri bir adress referans etmiyor

        Scanner input = new Scanner(System.in);
        System.out.println("Insert the file path please: ");
        String filePath = input.nextLine();
        ArrayList<String[]> program = new ArrayList<>();

        File file = new File(filePath);
        try {
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String raw = fileReader.nextLine();
                String lineTrim = raw.trim();
                if (lineTrim.isEmpty()) continue;

                if (lineTrim.contains(":")) {

                    String[] parts = lineTrim.split(":", 2);
                    String label = parts[0].trim();
                    String rest = parts[1].trim();


                    for (int e = 0; e < etiketler.length; e++) {
                        if (etiketler[e].equals(label)) {
                            etiketAdresleri[e] = program.size(); // aranan etiketin hangi satirda oldugunu bul
                            break;
                        }
                    }

                    if (!rest.isEmpty()) {
                        program.add(rest.split(" +"));//resti attik cunku etiketi gormezden gormek istiyorum direkt komudu atyom
                    }

                } else {
                    // normal satirlar icin
                    program.add(lineTrim.split(" +"));
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        printArrayList(program);//Termınali burada yazıyoruz

        for (int i = 0; i < program.size(); i++) {
            String[] line = program.get(i);

            if (line.length == 0) continue;

            String cmd = line[0];
            if (isEtiket(cmd)) {
                if (line.length > 1) {
                    cmd = line[1];
                } else {
                    continue;
                }
            }

            //debugPrint(i, line);

            if (cmd.equals("OKU")) OKU(line[1]);
            else if (cmd.equals("YAZ")) YAZ(line[1]);
            else if (cmd.equals("ATM")) ATM(line[1], line[2]);
            else if (cmd.equals("TOP")) TOP(line[1], line[2]);
            else if (cmd.equals("CIK")) {
                CIK(line[1], line[2]);
                updateFlags(line[1], line[2]);
            } else if (cmd.equals("CRP")) CRP(line[1], line[2]);
            else if (cmd.equals("BOL")) BOL(line[1], line[2]);
            else if (cmd.equals("VE")) VE(line[1], line[2]);
            else if (cmd.equals("VEY")) VEY(line[1], line[2]);
            else if (cmd.equals("DEG")) DEG(line[1]);
            else if (cmd.equals("D")) {
                int position = findInProgram(0, program, line[1]);
                if (position != -1)
                    i = position - 1; // i++ donderdigi icin -1 kullandim(program loop)
            } else if (cmd.equals("DE")) {
                if (zeroFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }
            else if (cmd.equals("DED")) {
                if (!zeroFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }


            else if (cmd.equals("DB")) {
                if (signFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }


            else if (cmd.equals("DBE")) {
                if (zeroFlag || signFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }


            else if (cmd.equals("DK")) {
                if (signFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }


            else if (cmd.equals("DKE")) {
                if (zeroFlag ||  signFlag) {
                    int target = findInProgram(0, program, line[1]);
                    if (target != -1) {
                        i = target - 1;
                    }
                }
            }
            else if (cmd.equals("SON")) {
                i = program.size();
            }
        }
        System.out.println("\nAX : " + AX + "\nBX : " + BX + "\nCX : " + CX + "\nDX : " + DX);
    }

    static void OKU(String register) {
        Scanner input = new Scanner(System.in);
        if (register.equals("AX")) {
            System.out.println("Enter AX value : ");
            AX = input.nextInt();
        } else if (register.equals("BX")) {
            System.out.println("Enter BX value : ");
            BX = input.nextInt();
        } else if (register.equals("CX")) {
            System.out.println("Enter CX value : ");
            CX = input.nextInt();
        } else if (register.equals("DX")) {
            System.out.println("Enter DX value : ");
            DX = input.nextInt();
        }
        // [AX], [BX], [CX], [DX]
        else if (register.equals("[AX]")) {
            System.out.println("Enter RAM[" + AX + "] value : ");
            RAM[AX] = input.nextInt();
        } else if (register.equals("[BX]")) {
            System.out.println("Enter RAM[" + BX + "] value : ");
            RAM[BX] = input.nextInt();
        } else if (register.equals("[CX]")) {
            System.out.println("Enter RAM[" + CX + "] value : ");
            RAM[CX] = input.nextInt();
        } else if (register.equals("[DX]")) {
            System.out.println("Enter RAM[" + DX + "] value : ");
            RAM[DX] = input.nextInt();
        }
        else if (isRAM(register)) {
            System.out.println("Enter " + register + " value : ");
            String content = register.substring(1, register.length() - 1);
            int ic = Integer.parseInt(content);
            RAM[ic] = input.nextInt();
        }
    }

    static void YAZ(String register) {
        if (register.equals("AX"))
            System.out.println("AX : " + AX);
        else if (register.equals("BX"))
            System.out.println("BX : " + BX);
        else if (register.equals("CX"))
            System.out.println("CX : " + CX);
        else if (register.equals("DX"))
            System.out.println("DX : " + DX);

        // [AX], [BX], [CX], [DX]
        else if (register.equals("[AX]")) {
            if (AX >= 0 && AX < 256)
                System.out.println("RAM[" + AX + "] : " + RAM[AX]);
            else
                System.out.println("RAM[" + AX + "] : <out of range>");
        } else if (register.equals("[BX]")) {
            if (BX >= 0 && BX < 256)
                System.out.println("RAM[" + BX + "] : " + RAM[BX]);
            else
                System.out.println("RAM[" + BX + "] : <out of range>");
        } else if (register.equals("[CX]")) {
            if (CX >= 0 && CX < 256)
                System.out.println("RAM[" + CX + "] : " + RAM[CX]);
            else
                System.out.println("RAM[" + CX + "] : <out of range>");
        } else if (register.equals("[DX]")) {
            if (DX >= 0 && DX < 256)
                System.out.println("RAM[" + DX + "] : " + RAM[DX]);
            else
                System.out.println("RAM[" + DX + "] : <out of range>");
        }

        else if (isRAM(register)) {
            String content = register.substring(1, register.length() - 1);
            int ic = Integer.parseInt(content);
            System.out.println("RAM[" + ic + "] : " + RAM[ic]);
        }
    }

    static void ATM(String register, String value) {
        setValue(register, getValue(value));
    }

    static void TOP(String register1, String register2) {
        setValue(register1, getValue(register1) + getValue(register2));
    }

    static void CRP(String register1, String register2) {
        setValue(register1, getValue(register1) * getValue(register2));
    }

    static void CIK(String register1, String register2) {
        setValue(register1, getValue(register1) - getValue(register2));
        updateFlags(register1, register2);
    }

    static void BOL(String register1, String register2) {
        setValue(register1, getValue(register1) / getValue(register2));
    }

    static void VE(String register1, String register2) {
        setValue(register1, getValue(register1) & getValue(register2));
    }

    static void VEY(String register1, String register2) {
        setValue(register1, getValue(register1) | getValue(register2));
    }

    static void DEG(String register) {
        setValue(register, ~getValue(register));
    }

    static boolean DE(String register1, String register2) {
        return getValue(register1) == getValue(register2);
    }

    static boolean DB(String register1, String register2) {
        return getValue(register1) > getValue(register2);
    }

    static boolean DBE(String register1, String register2) {
        return getValue(register1) >= getValue(register2);
    }

    static boolean DK(String register1, String register2) {
        return getValue(register1) < getValue(register2);
    }

    static boolean DKE(String register1, String register2) {
        return getValue(register1) <= getValue(register2);
    }

    static void updateFlags(String register1, String register2){
        int val1 = getValue(register1);
        int val2 = getValue(register2);

        signFlag = val1 < val2;
        zeroFlag = val1 == val2;
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

    static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static boolean isEtiket(String etiket) {
        for (int i = 0; i < etiketler.length; i++) {
            if (etiket.equals(etiketler[i])) {
                return true;
            }
        }
        return false;
    }

    static void printArrayList(ArrayList<String[]> liste) {
        for (int i = 0; i < liste.size(); i++) {
            String[] dizi = liste.get(i);
            System.out.print(i + ": ");

            for (int j = 0; j < dizi.length; j++) {
                System.out.print(dizi[j]);

                if (j < dizi.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    // Istenilen Etiket adresini donder
    static int findInProgram(int start, ArrayList<String[]> program, String keyword) {
        for (int i = 0; i < etiketler.length; i++) {
            if (etiketler[i].equals(keyword)) {
                return etiketAdresleri[i];//daha once program.size ile atamasini yapmistik
            }
        }
        return -1;
    }

    static int getValue(String register) {
        if (register.equals("AX")) return AX;
        else if (register.equals("BX")) return BX;
        else if (register.equals("CX")) return CX;
        else if (register.equals("DX")) return DX;
        else if (isNumber(register)) return Integer.parseInt(register);

            // [AX], [BX], [CX], [DX] destegi
        else if (register.equals("[AX]")) {
            if (AX >= 0 && AX < 256) return RAM[AX];
            return 0;
        } else if (register.equals("[BX]")) {
            if (BX >= 0 && BX < 256) return RAM[BX];
            return 0;
        } else if (register.equals("[CX]")) {
            if (CX >= 0 && CX < 256) return RAM[CX];
            return 0;
        } else if (register.equals("[DX]")) {
            if (DX >= 0 && DX < 256) return RAM[DX];
            return 0;
        }

        else if (isRAM(register)) {
            String content = register.substring(1, register.length() - 1);
            int ic = Integer.parseInt(content);
            if (ic >= 0 && ic < 256) return RAM[ic];
            return 0;
        }
        return 0;
    }

    static void setValue(String register, int value) {
        if(register.equals("AX")) AX = value;
        else if(register.equals("BX")) BX = value;
        else if(register.equals("CX")) CX = value;
        else if(register.equals("DX")) DX = value;

            // [AX], [BX], [CX], [DX] destegi
        else if (register.equals("[AX]")) {
            if (AX >= 0 && AX < 256) RAM[AX] = value;
        } else if (register.equals("[BX]")) {
            if (BX >= 0 && BX < 256) RAM[BX] = value;
        } else if (register.equals("[CX]")) {
            if (CX >= 0 && CX < 256) RAM[CX] = value;
        } else if (register.equals("[DX]")) {
            if (DX >= 0 && DX < 256) RAM[DX] = value;
        }

        else if(isRAM(register)) {
            String content = register.substring(1, register.length() - 1);
            int ic = Integer.parseInt(content);
            RAM[ic] = value;
        }
    }

    static void debugPrint(int index, String[] line) {
        System.out.println("----- STEP -----");
        System.out.println("Satır: " + (index + 1));
        System.out.print("Komut: ");
        for (String s : line)
            System.out.print(s + " ");
        System.out.print("Adim sayisi: ");
        System.out.println();
        System.out.println("AX = " + AX + " | BX = " + BX + " | CX = " + CX + " | DX = " + DX);
        if (BX >= 0 && BX < 256)
            System.out.println("RAM[" + BX + "] = " + RAM[BX]);
        System.out.println("----------------");
    }
}