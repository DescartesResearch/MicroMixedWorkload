/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package microbenchmark;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Helper class drawing images from random shapes and texts.
 * @author Norbert Schmitt
 */
public final class ImageCreator {

	/**
	 * Standard number of shapes added for each image.
	 */
	public static final int STD_NR_OF_SHAPES_PER_IMAGE = 10;

	/**
	 * Standard image width.
	 */
	public static final int STD_WIDTH = 2048;

	/**
	 * Standard image height.
	 */
	public static final int STD_HEIGHT = 2048;


	/**
	 * Maximum RGB color code used in determining the color of the background, a shape or text.
	 */
	public static final int MAX_RGB = 255;

	/**
	 * Maximum font size of random text in an image.
	 */
	public static final int MAX_FONT_SIZE = 200;

	/**
	 * Maximum number of characters of random text in an image.
	 */
	public static final int MAX_TEXT_LENGTH = 30;

	/**
	 * Maximum number for ascii character.
	 */
	public static final int MAX_CHAR_SIZE = 255;

	private final int width;
	private final int height;
	private final int nrOfShapesPerImage;
	private final int maxFontSize;
	private final int maxTextLength;
	private final Random random;

	/**
	 * Creates a new ImageCreator object with the standard settings and the Java RNG implementation {@link Random}.
	 */
	public ImageCreator() {
		this(new Random());
	}

	/**
	 * Creates a new ImageCreator object with the standard settings but user specified random number generation.
	 * @param random Random number generator for creating shapes
	 */
	public ImageCreator(Random random) {
		this(STD_WIDTH, STD_HEIGHT, STD_NR_OF_SHAPES_PER_IMAGE, MAX_FONT_SIZE, MAX_TEXT_LENGTH, random);
	}

	/**
	 * Creates a new ImageCreator object with the given settings. To change the settings, a new object must be
	 * created.
	 * @param width Image width in pixel
	 * @param height Image height in pixel
	 * @param nrOfShapesPerImage Shapes to draw for each created image. Shapes will be created randomly
	 * @param maxFontSize Maximum size of the font for the text shape
	 * @param maxTextLength Maximum size of the text length for the text shape
	 * @param random Random number generator for creating shapes
	 */
	public ImageCreator(int width, int height, int nrOfShapesPerImage, int maxFontSize, int maxTextLength,
			    Random random) {
		if (random == null) {
			throw new IllegalArgumentException("Random number generator is null.");
		}
		if (width >= 0) {
			this.width = width;
		} else {
			this.width = STD_WIDTH;
		}
		if (height >= 0) {
			this.height = height;
		} else {
			this.height = STD_HEIGHT;
		}
		if (nrOfShapesPerImage >= 0) {
			this.nrOfShapesPerImage = nrOfShapesPerImage;
		} else {
			this.nrOfShapesPerImage = STD_NR_OF_SHAPES_PER_IMAGE;
		}
		if (maxFontSize >= 0) {
			this.maxFontSize = maxFontSize;
		} else {
			this.maxFontSize = MAX_FONT_SIZE;
		}
		if (maxTextLength >= 0) {
			this.maxTextLength = maxTextLength;
		} else {
			this.maxTextLength = MAX_TEXT_LENGTH;
		}
		this.random = random;
	}

	/**
	 * Create an image with a number of shapes and the given size. The shapes will be added using the internal
	 * random number generator.
	 * @return Returns a {@link BufferedImage} with added shapes.
	 */
	public BufferedImage createImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.OPAQUE);
		Graphics2D graphics = img.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		// Background fill
		switchColor(graphics);
		graphics.fillRect(0, 0, width, height);

		// Generate shapes and texts
		for (int i = 0; i < nrOfShapesPerImage; i++) {
			switch (random.nextInt(4)) {
				case 1:
					makeLine(graphics);
					break;
				case 2:
					makeOval(graphics);
					break;
				case 3:
					makeText(graphics);
					break;
				default:
					makeRectangle(graphics);
					break;
			}
		}

		graphics.dispose();
		return img;
	}

	private void switchColor(Graphics2D graphics) {
		graphics.setColor(new Color(random.nextInt(MAX_RGB + 1), random.nextInt(MAX_RGB + 1),
			random.nextInt(MAX_RGB + 1)));
	}

	private void makeRectangle(Graphics2D graphics) {
		switchColor(graphics);

		int x = random.nextInt(width);
		int y = random.nextInt(height);

		Rectangle r = new Rectangle(x, y, random.nextInt(width - x) + 1, random.nextInt(height - y) + 1);

		if (random.nextBoolean()) {
			graphics.fill(r);
		}

		graphics.draw(r);
	}

	private void makeLine(Graphics2D graphics) {
		switchColor(graphics);
		graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width),
			random.nextInt(height));
	}

	private void makeOval(Graphics2D graphics) {
		switchColor(graphics);

		int x = random.nextInt(width);
		int y = random.nextInt(height);
		int ovalWidth = random.nextInt(width - x) + 1;
		int ovalHeight = random.nextInt(height - y) + 1;

		if (random.nextBoolean()) {
			graphics.fillOval(x, y, ovalWidth, ovalHeight);
		}

		graphics.drawOval(x, y, ovalWidth, ovalHeight);
	}

	private void makeText(Graphics2D graphics) {
		switchColor(graphics);

		String fontName;
		switch (random.nextInt(4)) {
			case 1:
				fontName = Font.MONOSPACED;
				break;
			case 2:
				fontName = Font.SERIF;
				break;
			case 3:
				fontName = Font.DIALOG;
				break;
			default:
				fontName = Font.SANS_SERIF;
				break;
		}

		int fontStyle;
		switch (random.nextInt(3)) {
			case 1:
				fontStyle = Font.BOLD;
				break;
			case 2:
				fontStyle = Font.ITALIC;
				break;
			default:
				fontStyle = Font.PLAIN;
				break;
		}

		int fontSize = random.nextInt(maxFontSize + 1);

		graphics.setFont(new Font(fontName, fontStyle, fontSize));

		int textLength = random.nextInt(maxTextLength + 1);
		String str = Stream.generate(() -> random.nextInt(MAX_CHAR_SIZE))
			.limit(textLength)
			.map(i -> (char) i.intValue())
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();

		graphics.drawString(str, random.nextInt(width), random.nextInt(height));
	}

}
