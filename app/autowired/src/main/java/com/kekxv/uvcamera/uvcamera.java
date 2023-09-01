package com.kekxv.uvcamera;

import android.graphics.Bitmap;
import android.util.Size;

public class uvcamera {
  public static class Size {
    public int width = -1;
    public int height = -1;

    public Size() {
    }

    public Size(int width, int height) {
      this.width = width;
      this.height = height;
    }
  }

  // Used to load the 'uvcamera' library on application startup.
  static {
    System.loadLibrary("uvcamera");
  }

  /**
   * 版本
   */
  public native String version();

  /**
   * 打开摄像头
   *
   * @param video_path 摄像头路径
   * @param width      摄像头宽度
   * @param height     摄像头高度
   * @return 摄像头操作句柄
   */
  public native int open(String video_path, int width, int height);

  /**
   * 打开摄像头
   *
   * @param video_index 摄像头序号
   * @param width       摄像头宽度
   * @param height      摄像头高度
   * @return 摄像头操作句柄
   */
  public native int open(int video_index, int width, int height);

  /**
   * 关闭摄像头
   *
   * @param fd 摄像头操作句柄
   * @return 关闭结果
   */
  public native int close(int fd);

  /**
   * 摄像头是否打开
   *
   * @param fd 摄像头操作句柄
   * @return 返回结果
   */
  public native boolean is_opened(int fd);

  private native int take_photo(int fd, Bitmap bmp);

  public native Bitmap createBitmap(int fd);

  private native int getWidth(int fd);

  private native int getHeight(int fd);

  public Size getSize(int fd) {
    return new Size(getWidth(fd), getHeight(fd));
  }

  public Bitmap createLocalBitmap(int fd) {
    Size size = getSize(fd);
    if (size.width <= 0 || size.height <= 0) return null;
    return Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
  }

  public Bitmap takePhoto(int cameraId, Bitmap bmp) {
    if (bmp == null) return null;
    int ret = take_photo(cameraId, bmp);
    if (ret < 0) {
      return null;
    }
    return bmp;
  }
}
