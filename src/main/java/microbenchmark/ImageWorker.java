package microbenchmark;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Random;

public class ImageWorker {

	public void workOnImage(String imageName) throws IOException {
		ImageCreator creator = new ImageCreator(4096, 4096, 20, ImageCreator.MAX_FONT_SIZE,
			ImageCreator.MAX_TEXT_LENGTH, new Random());
		BufferedImage imageFull = creator.createImage();
		FileSystem fs = FileSystems.getDefault();
		Path pathFull = fs.getPath(imageName + "." + ImageReadWrite.STD_IMAGE_FORMAT);
		Path pathSmall = fs.getPath(imageName + "_small." + ImageReadWrite.STD_IMAGE_FORMAT);
		ImageReadWrite.write(pathFull, imageFull);
		imageFull = ImageReadWrite.read(pathFull);
		BufferedImage imageSmall = ImageScaler.scale(imageFull, 512, 512);
		ImageReadWrite.write(pathSmall, imageSmall);
	}
}
