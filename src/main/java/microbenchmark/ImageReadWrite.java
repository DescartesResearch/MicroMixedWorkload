package microbenchmark;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class ImageReadWrite {

	/**
	 * Standard image format for storage (reading and writing).
	 */
	public static final String STD_IMAGE_FORMAT = "png";

	private ImageReadWrite() {
		// Intentionally left blank.
	}

	/**
	 * Reads an image from disk at the given location.
	 * @param imagePath Path to the image
	 * @return A {@link BufferedImage} instance of the read file
	 * @throws IOException
	 */
	public static BufferedImage read(Path imagePath) throws IOException {
		if (imagePath == null) {
			throw new IllegalArgumentException("Path to image file is null.");
		}
		File f = imagePath.toFile();
		if (!f.exists()) {
			throw new IllegalArgumentException("Image file does not exist.");
		}
		if (f.isDirectory()) {
			throw new IllegalArgumentException("Image file is a directory.");
		}
		if (!f.canRead()) {
			throw new IllegalArgumentException("Image file has no read right.");
		}

		return ImageIO.read(f);
	}

	/**
	 * Writes a {@link BufferedImage} to disk to the given filepath with the standard format PNG.
	 * @param imagePath Filepath where to store the given image
	 * @param image Image to store to disk
	 * @throws IOException
	 */
	public static void write(Path imagePath, BufferedImage image) throws IOException {
		write(imagePath, image, STD_IMAGE_FORMAT);
	}

	/**
	 * Writes a {@link BufferedImage} to disk to the given filepath and with the given format.
	 * @param imagePath Filepath where to store the given image
	 * @param image Image to store to disk
	 * @param format String defining the image format, for example "png"
	 * @throws IOException
	 */
	public static void write(Path imagePath, BufferedImage image, String format) throws IOException {
		if (imagePath == null) {
			throw new IllegalArgumentException("Path to image file is null.");
		}
		if (image == null) {
			throw new IllegalArgumentException("Given image is null.");
		}
		if (format == null || format.isEmpty()) {
			format = STD_IMAGE_FORMAT;
		}
		File f = imagePath.toFile();
		// Let the system overwrite the file for benchmarking.
		//if (f.exists()) {
		//	throw new IllegalArgumentException("Image file does already exist.");
		//}

		ImageIO.write(image, format, f);
	}
}
