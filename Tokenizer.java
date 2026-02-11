import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String html = ""; // Okunacak html dosyası String Builder ile okunup buna atanacak
        String filePath = JOptionPane.showInputDialog(null, "Lütfen HTML dosyasının tam yolunu giriniz:", "Dosya Yolu Girişi", JOptionPane.QUESTION_MESSAGE);

        if (filePath == null || filePath.isBlank()) {//dosya null veya sadece boşluk girilirse
            JOptionPane.showMessageDialog(null, "Dosya yolu girilmedi!");
            System.exit(0);
        }

        File file = new File(filePath.trim());

        if (!file.exists() || !file.isFile()) {//dosya yok veya geçerli file değil ise
            JOptionPane.showMessageDialog(null, "Geçersiz dosya yolu!");
            System.exit(0);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {//satır satır okumayı kolaylaştırır (BufferReader)
            StringBuilder sb = new StringBuilder();//String kurucu
            String line;
            while ((line = br.readLine()) != null) {//satır sonunca kadar
                sb.append(line);//String'e ekleme
            }
            html = sb.toString();
        } catch (IOException e) {//hata yakalama
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Dosya okunamadı!");
            System.exit(0);
        }

        ArrayList<String> tags = htmlReader(html);
        tagInterpreter(tags);
    }

    public static ArrayList<String> htmlReader(String html){
        ArrayList<String> tagList = new ArrayList<>();
        boolean inTag = false;
        String currentTag = "";//tag içeriği
        String currentText = "";//tag dışı

        for(int i = 0 ; i < html.length(); i++){
            char c = html.charAt(i);

            if(c == '<'){
                if(!currentText.isBlank()){//'<' 'den önce yazı geldi mi? !(sadece boşluk)
                    tagList.add(currentText.trim());
                    currentText = "";
                }
                inTag = true;
                currentTag = "";
            }
            else if (c == '>'){
                if(currentTag.contains(" ")) {
                    currentTag = currentTag.substring(0, currentTag.indexOf(" "));
                }
                inTag = false;//tag kapandı

                tagList.add(currentTag.trim());//tag ismi kaydedildi
            }
            else if(inTag){//tag içindeyse tüm karakterleri tag ismi olacak
                currentTag += c;// tag içeriği
            }
            else {
                currentText += c; //tag dışı yazıları alır
            }
        }
        if(!currentText.isBlank()) tagList.add(currentText.trim()); // döngüden çıkınca currentText eklenmediyse ekler
        System.out.println("TagList : " + "\n" + tagList);
        return tagList;
    }

    public static void tagInterpreter(ArrayList<String> tagList){// tag yorumlayıcı
        JFrame frame = new JFrame("My Html");//frame oluştur
        JPanel bodyPanel = new JPanel();//panel oluştur
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));//boxlayout y axis bileşenlerin üs üste eklenmesini sağlar

        JScrollPane scrollPane = new JScrollPane(bodyPanel); // içerik büyükse scroll çubuğu ekler
        frame.add(scrollPane);//frame'e ekle

        for(int i = 0 ; i < tagList.size() ; i++){
            String tag = tagList.get(i).toLowerCase();
            switch(tag){
                case "doctype html" :// işlem yok frame var zaten
                    break;
                case "html" : // işlem yok frame var zaten
                    break;
                case  "head" : //işlem yok
                    break;
                case "body" : // işlem yok panel var zaten
                    break;
                case "title" :
                    StringBuilder titleText = new StringBuilder();
                    i = tagContentReader(i  , tagList , tag.toLowerCase() , titleText);
                    frame.setTitle(titleText.toString().trim());
                    break;
                case "h1" , "h2" , "h3" , "h4" , "h5" , "h6" :
                    StringBuilder hContent = new  StringBuilder();
                    i =  tagContentReader(i  , tagList , tag.toLowerCase() , hContent);
                    JLabel hTags = new JLabel("<html>" + hContent.toString().trim() + "</html>"); //html ile satır kaydırma
                    int size = switch (tag){
                        case "h1" -> 28;
                        case "h2" -> 24;
                        case "h3" -> 20;
                        case "h4" -> 18;
                        case "h5" -> 16;
                        default -> 14; // h6 için
                    };
                    hTags.setFont(new Font("Dialog", Font.BOLD, size));
                    addToPanel(bodyPanel , hTags);
                    break;
                case "p" :
                    StringBuilder pContent = new  StringBuilder();
                    i =  tagContentReader(i  , tagList , tag.toLowerCase() , pContent);
                    JLabel paragraf = new JLabel("<html>" + pContent.toString().trim() + "</html>"); // html ile satır kaydırma
                    paragraf.setFont(new Font("Dialog", Font.PLAIN, 14));
                    addToPanel(bodyPanel , paragraf);
                    break;
                case "textarea":
                    StringBuilder areaContent = new  StringBuilder();
                    i =  tagContentReader(i  , tagList , tag.toLowerCase() , areaContent);
                    JTextArea textArea = new JTextArea(areaContent.toString().trim(), 5, 20); // 5 satır, 20 sütun
                    textArea.setLineWrap(false); // satır kaydırmamak için
                    textArea.setWrapStyleWord(true);
                    JScrollPane areaScroll = new JScrollPane(textArea);//textarea'ya scroll çubuğu ekler
                    addToPanel(bodyPanel , areaScroll);
                    break;
                case "span" :
                    JLabel span = new JLabel("Bu bir " + tag + " etiketi");
                    span.setFont(new Font("Dialog", Font.ITALIC, 13));
                    addToPanel(bodyPanel , span);
                    break;
                case "label" :
                    JLabel label = new JLabel("Bu bir " + tag + " etiketi");
                    label.setFont(new Font("Dialog", Font.PLAIN, 14));
                    addToPanel(bodyPanel , label);
                    break;
                case "a":
                    StringBuilder aContent = new  StringBuilder();
                    i = tagContentReader(i  , tagList , tag.toLowerCase() , aContent);
                    final String linkText = aContent.toString().trim(); // değiştirilemez

                    JLabel linkLabel = new JLabel("<html><a href=''>" + linkText + "</a></html>");//html ile kaydırma
                    linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));//imleç işareti el işaretine döner

                    linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {//hazır metotlar (farenin hareketlerini dinler)
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            System.out.println("Linke tıklandı: " + linkText);
                            try {//programın çökmesini önler
                                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/?app=desktop&hl=tr"));//tıklandığı zaman tarayıcıda limki açar
                            } catch (Exception ex) {//hata yakalar
                                ex.printStackTrace();
                            }
                        }
                    });

                    addToPanel(bodyPanel , linkLabel);
                    break;
                case "input" :
                    JTextField input = new JTextField(15);
                    addToPanel(bodyPanel , input);
                    break;
                case "button" :
                    StringBuilder btnText = new StringBuilder();
                    i = tagContentReader(i ,  tagList , tag.toLowerCase() , btnText);
                    JButton button = new JButton(btnText.toString().isBlank() ? "Button" : btnText.toString().trim());//içerik yoksa Button yaz varsa btntxt'i yaz içine
                    addToPanel(bodyPanel , button);
                    break;
                // kapanış tag'ları, işlem yok
                case "/body","/title","/p","/h1","/h2","/h3","/h4","/h5","/h6","/button","/label","/a","/input","/span","/html" :
                    break;
                default:
                    System.out.println("Tanımsız tag girişi bulundu: !" + tag);
                    break;
            }
        }

        frame.setSize(600 , 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public static void addToPanel(JPanel bodyPanel , JComponent component){
        bodyPanel.add(component);
        bodyPanel.add(Box.createRigidArea(new Dimension(0,5)));
    }
    public static int tagContentReader(int i , ArrayList<String> tagList , String tag , StringBuilder tagContenet ){
        i++;
        while(i < tagList.size() && !tagList.get(i).equalsIgnoreCase("/" + tag)){
            tagContenet.append(tagList.get(i)).append(" ");
            i++;
        }
        return i;
    }
}
