package jp.ksksue.tutorial.TWE_Control;

import android.graphics.Bitmap;

/**
 * Created by sugimura on 2015/10/10.
 */
public class CustomData {
    private Bitmap imageData_;
    private String textData_;
    private String textTime_;
    private Boolean textColor_ = false;

    public void setImagaData(Bitmap image) {
        imageData_ = image;
    }
    public Bitmap getImageData() {
        return imageData_;
    }
    public String getTextData() {
        return textData_;
     }
    public void setTextData(String text) {
        textData_ = text;
     }
    public String getTextTime() {
        return textTime_;
    }
    public void setTextTime(String text) {
        textTime_ = text;
    }
    public void setTextColor(boolean enabled) {
        textColor_ = enabled;
    }
    public Boolean getTextColor() {
        return textColor_;
    }
}
