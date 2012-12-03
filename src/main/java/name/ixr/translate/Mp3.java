/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.ixr.translate;

import com.goldpalm.rd.core.utils.HttpUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author IXR
 */
public class Mp3 {
    // 播放
    public static void play(String url) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HttpUtils.download(url, baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            AudioInputStream audioInputStream;// 音频文件流
            AudioFormat audioFormat;// 文件格式
            SourceDataLine sourceDataLine;// 输出设备
            // 取得文件输入流
            audioInputStream = AudioSystem.getAudioInputStream(bais);
            audioFormat = audioInputStream.getFormat();
            // 转换mp3文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                        .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }

            // 打开输出设备
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat,
                    AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            int cnt;
            byte tempBuffer[] = new byte[512];
            while ((cnt = audioInputStream.read(tempBuffer, 0,
                    tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    // 写入缓存数据
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }
            }
            // Block等待临时数据被输出为空
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    
    public static void main(String[] args) {
        play("http://translate.google.cn/translate_tts?ie=UTF-8&q=hello&tl=en");
    }
}
