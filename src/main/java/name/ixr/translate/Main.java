/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.ixr.translate;

import com.goldpalm.rd.core.utils.TranslateUtils;
import com.goldpalm.rd.core.utils.TranslateUtils.Language;
import com.goldpalm.rd.core.utils.TranslateUtils.TranslateInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.Scanner;

/**
 * 应用程序
 *
 * @author IXR
 */
public class Main {

    public static void help() {
        Language[] languages = Language.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            System.out.println(language.getName() + "：" + language.getLanguage());
        }
    }
    static Robot robot;
    
    static Language source = Language.AUTO;

    public static void main(String[] args) throws Exception {
        robot = new Robot();
        translate("程序启动！", Language.CN, false, false,false,false);
        Language[] languages = new Language[]{Language.EN};
        boolean isSend = false;
        boolean isTTS = false;
        boolean isCopy = true;
        Scanner scanner = new Scanner(System.in, "gbk");
        while (true) {
            System.out.println("--------------------------");
            try{
                String line = scanner.nextLine();
                if (line.equals("_send")) {
                    isSend = !isSend;
                    System.out.println("send : " + isSend);
                } else if (line.equals("_tts")) {
                    isTTS = !isTTS;
                    System.out.println("tts : " + isTTS);
                } else if (line.equals("_copy")) {
                    isCopy = !isCopy;
                    System.out.println("copy : " + isCopy);
                } else if (line.equals("_help")) {
                    help();
                } else if (line.startsWith("_set:")) {
                    line = line.substring("_set:".length());
                    String[] list = line.split("\\|");
                    languages = new Language[list.length];
                    for (int i = 0; i < list.length; i++) {
                        languages[i] = Language.toLanguage(list[i]);
                    }
                    toList(languages);
                } else if (line.startsWith("_source:")) {
                    line = line.substring("_source:".length());
                    source = Language.toLanguage(line);
                    System.out.println("source : " + source.getLanguage());
                }else if (line.equals("_list")) {
                    toList(languages);
                } else {
                    for (int i = 0; i < languages.length; i++) {
                        translate(line, languages[i], isSend, i == 0,isTTS,isCopy);
                    }
                }
            } catch(Exception ex) {
                System.out.println("发生了一个错误" + ex.getMessage());
            }
        }
    }
    
    private static void toList(Language[] languages) {
        System.out.print("list :");
        for (int i = 0; i < languages.length; i++) {
            System.out.print(" " + languages[i].getLanguage());
        }
        System.out.println();
    }

    public static void translate(String text, Language to, boolean isSend, boolean isTab,boolean isTTS,boolean isCopy) throws Exception {
        TranslateInfo info = TranslateUtils.getTranslateInfo(text, source, to);
        System.out.println(to.getName() + "[" + to.getLanguage() + "] : \r\n\t" + info.getSentences().get(0).getTrans());
        if(isCopy) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            clip.setContents(new StringSelection(info.getSentences().get(0).getTrans()), null);
        }
        if(isTTS) {
            String url = TranslateUtils.getTTS(info.getSentences().get(0).getTrans(), to);
            Mp3.play(url);
        }
        if (isSend) {
            send(isTab);
        }
    }

    public static void send(boolean isTab) throws Exception {
        robot.setAutoDelay(100);
        if (isTab) {
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
        }
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_CONTROL);
//        robot.keyPress(KeyEvent.VK_ALT);
//        robot.keyPress(KeyEvent.VK_TAB);
//        robot.keyRelease(KeyEvent.VK_TAB);
//        robot.keyRelease(KeyEvent.VK_ALT);
    }
}
