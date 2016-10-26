package com.altamirasoft.blur_imageview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DrawUtil {

	public static String getFileExtension(File f) {
		int idx = f.getName().indexOf(".");
		if (idx == -1)
			return "";
		else
			return f.getName().substring(idx + 1);
	}

	public static String fileNameRemoveExtension(String fileName) {
		if (fileName == null)
			return null;

		int idx = fileName.indexOf(".");

		if (idx == -1)
			return fileName;

		else
			return fileName.substring(0, idx);
	}

	public static String stringCheck(String str) {
		StringBuilder strbuilder = new StringBuilder();

		int size = str.length();
		for (int i = 0; i < size; i++) {
			char curChar = str.charAt(i);
			if (curChar == '\\' || curChar == '/' || curChar == ':' || curChar == '*' || curChar == '?' || curChar == '"' || curChar == '<' || curChar == '>' || curChar == '|') {
				strbuilder.append('_');
			} else
				strbuilder.append(curChar);
		}
		return strbuilder.toString();
	}

	public static String getUniqueFilename(File folder, String filename, String ext) {
		if (folder == null || filename == null)
			return null;

		String curFileName;
		File curFile;

		if (filename.length() > 20) {
			filename = filename.substring(0, 19);
		}

		filename = stringCheck(filename);

		int i = 1;
		do {
			curFileName = String.format("%s_%02d.%s", filename, i++, ext);
			curFile = new File(folder, curFileName);
		} while (curFile.exists());
		return curFileName;
	}

	public static byte[] readBytedata(String aFilename) {
		byte[] imgBuffer = null;

		FileInputStream fileInputStream = null;
		try {
			File file = new File(aFilename);
			fileInputStream = new FileInputStream(file);
			int byteSize = (int) file.length();
			imgBuffer = new byte[byteSize];

			if (fileInputStream.read(imgBuffer) == -1) {
				// ////Log.d("log", "failed to read image");
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {

					fileInputStream.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return imgBuffer;
	}

	public static boolean writeBytedata(String aFilename, byte[] imgBuffer) {

		FileOutputStream fileOutputStream = null;
		boolean result = true;

		try {
			File file = new File(aFilename);
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(imgBuffer);

			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e2) {
			e2.printStackTrace();
			result = false;
		} finally {
			if (fileOutputStream != null) {
				try {

					fileOutputStream.close();

				} catch (IOException e) {
					e.printStackTrace();
					result = false;
				}
			}
		}
		return result;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void CopyStream(final ProgressBar progress, final int total_size, InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		int current_size = 0;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				current_size += count;
				os.write(bytes, 0, count);
				// //Log.d("log", current_size + "/" + total_size);
				makeProgress(progress, current_size / total_size);
			}
		} catch (Exception ex) {
		}
	}

	public static void makeProgress(final ProgressBar progress, final int percent) {
		Activity a = (Activity) progress.getContext();
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progress.setProgress(percent);
				if (percent <= 99) {
					progress.setVisibility(View.GONE);
				} else {
					progress.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	/** Get Bitmap's Width **/
	public synchronized static int getBitmapOfWidth(String fileName) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			if (getExifOrientation(fileName) == 90 || getExifOrientation(fileName) == 270) {
				return options.outHeight;
			} else {
				return options.outWidth;
			}

			// return options.outWidth;
		} catch (Exception e) {
			return 0;
		}
	}

	/** Get Bitmap's height **/
	public synchronized static int getBitmapOfHeight(String fileName) {

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);

			if (getExifOrientation(fileName) == 90 || getExifOrientation(fileName) == 270) {
				return options.outWidth;

			} else {
				return options.outHeight;
			}

		} catch (Exception e) {
			return 0;
		}
	}

	public synchronized static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;

		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			// ////Log.d("log", "cannot read exif");
			e.printStackTrace();
		}

		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

			if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}

			}
		}

		return degree;
	}

	public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != b2) {
					bitmap.recycle();
					bitmap = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}

		return bitmap;
	}

	public static boolean rotateBitMap(String filePath, int degree, int maxPixel) {

		String photopath = filePath;

		// Max image size
		final int IMAGE_MAX_SIZE = maxPixel;// GlobalConstants.getMaxImagePixelSize();
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, bfo);

		if (bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
			bfo.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
		}
		bfo.inJustDecodeBounds = false;
		bfo.inPurgeable = true;
		bfo.inDither = false;

		Bitmap bmp = BitmapFactory.decodeFile(filePath, bfo);

		Matrix matrix = new Matrix();
		matrix.postRotate(degree);

		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

		FileOutputStream fOut;
		try {

			fOut = new FileOutputStream(photopath);
			bmp.compress(CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}

		bmp.recycle();
		bmp = null;

		return true;

	}

	public static Bitmap RotatedBitmapFile(String strFilePath) {
		// DEBUG.SHOW_DEBUG(TAG, "[ImageDownloader] SafeDecodeBitmapFile : " +
		// strFilePath);

		try {

			final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath);

			int degree = getExifOrientation(strFilePath);

			return getRotatedBitmap(bitmap, degree);
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();

			return null;
		}
	}



	public synchronized static Bitmap safeDecodeBitmapFile(String strFilePath, int maxPixel) {

		try {
			File file = new File(strFilePath);
			if (file.exists() == false) {
				// //Log.d("log", "No file exsit=" + strFilePath);
				return null;
			}

			// Max image size
			final int IMAGE_MAX_SIZE = maxPixel;// GlobalConstants.getMaxImagePixelSize();
			BitmapFactory.Options bfo = new BitmapFactory.Options();
			bfo.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(strFilePath, bfo);

			if (bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
				bfo.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
			}
			bfo.inJustDecodeBounds = false;
			bfo.inPurgeable = true;
			bfo.inDither = false;

			final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);
			int degree = getExifOrientation(strFilePath);

			return getRotatedBitmap(bitmap, degree);
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();

			return null;
		}
	}

	public static Bitmap resizeBitmap(Bitmap src, int max) {
		if (src == null)
			return null;

		int width = src.getWidth();
		int height = src.getHeight();
		float rate = 0.0f;

		if (width > height) {
			rate = max / (float) width;
			height = (int) (height * rate);
			width = max;
		} else {
			rate = max / (float) height;
			width = (int) (width * rate);
			height = max;
		}

		return Bitmap.createScaledBitmap(src, width, height, true);
	}

	/**
	 * Bitmap�� ratio�� ���缭 max�� ��ŭ resize�Ѵ�.
	 * 
	 * @param src
	 * @param max
	 * @param isKeep
	 *            ���� ũ���� ��� �����Ұ��� üũ..
	 * @return
	 */
	public static Bitmap resize(Bitmap src, int max, boolean isKeep) {
		if (!isKeep)
			return resizeBitmap(src, max);

		int width = src.getWidth();
		int height = src.getHeight();
		float rate = 0.0f;

		if (width > height) {
			if (max > width) {
				rate = max / (float) width;
				height = (int) (height * rate);
				width = max;
			}
		} else {
			if (max > height) {
				rate = max / (float) height;
				width = (int) (width * rate);
				height = max;
			}
		}

		return Bitmap.createScaledBitmap(src, width, height, true);
	}

	/**
	 * Bitmap �̹����� ���簢������ �����.
	 * 
	 * @param src
	 *            ��
	 * @param max
	 *            ������
	 * @return
	 */
	public static Bitmap resizeSquare(Bitmap src, int max) {
		if (src == null)
			return null;

		return Bitmap.createScaledBitmap(src, max, max, true);
	}

	/**
	 * Bitmap �̹����� ����� �������� w, h ũ�� ��ŭ crop�Ѵ�.
	 * 
	 * @param src
	 *            ��
	 * @param w
	 *            ����
	 * @param h
	 *            ����
	 * @return
	 */
	public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
		if (src == null)
			return null;

		int width = src.getWidth();
		int height = src.getHeight();

		if (width < w && height < h)
			return src;

		int x = 0;
		int y = 0;

		if (width > w)
			x = (width - w) / 2;

		if (height > h)
			y = (height - h) / 2;

		int cw = w; // crop width
		int ch = h; // crop height

		if (w > width)
			cw = width;

		if (h > height)
			ch = height;

		return Bitmap.createBitmap(src, x, y, cw, ch);
	}

	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, int width, int height) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < width || height_tmp / 2 < height)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static Bitmap getCroppedBitmap(Bitmap bitmap) {

		Bitmap source = bitmap;
		if (bitmap.getWidth() > bitmap.getHeight()) {
			source = DrawUtil.cropCenterBitmap(bitmap, bitmap.getHeight(), bitmap.getHeight());
		}
		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

		Paint white = new Paint();
		white.setColor(Color.WHITE);
		white.setAntiAlias(true);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		// Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
		// return _bmp;

		return output;
	}

	public static boolean saveBitmapPNG(String strFileName, Bitmap bitmap) {
		if (strFileName == null || bitmap == null)
			return false;

		boolean bSuccess1 = false;
		boolean bSuccess2;
		boolean bSuccess3;
		File saveFile = new File(strFileName);

		if (saveFile.exists()) {
			if (!saveFile.delete())
				return false;
		}

		try {
			bSuccess1 = saveFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			bSuccess2 = bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
			bSuccess2 = false;
		}
		try {
			if (out != null) {
				out.flush();
				out.close();
				bSuccess3 = true;
			} else
				bSuccess3 = false;

		} catch (IOException e) {
			e.printStackTrace();
			bSuccess3 = false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return (bSuccess1 && bSuccess2 && bSuccess3);
	}


	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getWidth(), v.getHeight());
		v.draw(c);
		return b;
	}
	public static Bitmap loadBitmapFromView(View v, int width, int height) {
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		c.scale((float)v.getWidth()/(float)width,(float)v.getHeight()/(float)height,0f,0f);
		v.layout(0, 0, v.getWidth(), v.getHeight());

		v.draw(c);
		return b;
	}


}
