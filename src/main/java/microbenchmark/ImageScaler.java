package microbenchmark;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageScaler {

	private ImageScaler() {
		// Intentionally left blank.
	}

	/**
	 * Scales a given image by rendering the supplied image with the given width and
	 * height into a new image and returning the new image. The method will throw an
	 * {@link IllegalArgumentException} if either of the two image sizes
	 * is 0 or below. A {@link NullPointerException} will be thrown if the
	 * supplied image is null.
	 *
	 * @param image Image to scale to the given width and height
	 * @param width Width to scale image to
	 * @param height Height to scale image to
	 * @return New image scaled to the given width and height
	 */
	public static BufferedImage scale(BufferedImage image, int width, int height) {
		if (image == null) {
			throw new NullPointerException("The supplied image is null.");
		}
		if (width <= 0) {
			throw new IllegalArgumentException("The supplied pixel width is below 1.");
		}
		if (height <= 0) {
			throw new IllegalArgumentException("The supplied pixel height is below 1.");
		}

		BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D graphics = scaledImg.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();
		return scaledImg;
	}
}
