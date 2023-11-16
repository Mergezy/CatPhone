import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Main {
    public static void main(String[] args) {
        try {
            // Чтение ссылок на музыку и фото из текстового файла
            BufferedReader br = new BufferedReader(new FileReader("links.txt"));
            String mp3Url = br.readLine();
            String imageUrl = br.readLine();
            br.close();

            // Пути для сохранения музыки и фото
            String mp3SavePath = "downloaded_audio.mp3";
            String imageSavePath = "downloaded_image.jpg";

            // Создание пула потоков для параллельного скачивания
            ExecutorService executor = Executors.newFixedThreadPool(2);

            // Параллельное скачивание музыки и фото
            executor.execute(() -> {
                try {
                    downloadFile(mp3Url, mp3SavePath);
                    System.out.println("MP3-файл скачан");
                    playMp3(mp3SavePath);
                } catch (IOException | JavaLayerException e) {
                    e.printStackTrace();
                }
            });

            executor.execute(() -> {
                try {
                    downloadFile(imageUrl, imageSavePath);
                    System.out.println("Фото скачано");
                    openImage(imageSavePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Завершение работы пула потоков
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String fileUrl, String savePath) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void playMp3(String mp3FilePath) throws JavaLayerException {
        try (FileInputStream inputStream = new FileInputStream(mp3FilePath)) {
            Player player = new Player(inputStream);
            player.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openImage(String imagePath) {
        File imageFile = new File(imagePath);
        try {
            Desktop.getDesktop().open(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
