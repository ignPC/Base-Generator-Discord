import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGenerator {

    public static void CreateImageFromByteArray(byte[] data, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                int index = LSchematic.getIndexFromCoordinates(x, 0, z, width, 1, height);
                if (data[index] == 49) {
                    bufferedImage.setRGB(x, z, 0x330033);  // dark purple (obsidian)
                } else if (data[index] == 12) {
                    bufferedImage.setRGB(x, z, 0xD2B48C);  // sand colored
                } else if (data[index] == 0) {
                    bufferedImage.setRGB(x, z, 0xFFFFFF);  // white
                } else if (data[index] == 87) {
                    bufferedImage.setRGB(x, z, 0x6f0014);  // red (netherrack)
                } else if (data[index] == 4) {
                    bufferedImage.setRGB(x, z, 0x808080);  // gray (cobblestone)
                }
            }
        }

        Image image = bufferedImage.getScaledInstance(512, 512, Image.SCALE_SMOOTH);
        bufferedImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

        try {
            ImageIO.write(bufferedImage, "png", new File("schematics\\image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
