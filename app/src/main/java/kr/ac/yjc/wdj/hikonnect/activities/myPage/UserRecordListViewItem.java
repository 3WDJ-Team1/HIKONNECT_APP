package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.graphics.drawable.Drawable;

/**
 * Created by LEE AREUM on 2018-05-17.
 */

public class UserRecordListViewItem {
    private Drawable    iconDrawable;
    private String      titleStr;
    private String      descStr;

    public void setIcon(Drawable icon) {
        this.iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public Drawable getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getDesc() {
        return this.descStr;
    }
}
