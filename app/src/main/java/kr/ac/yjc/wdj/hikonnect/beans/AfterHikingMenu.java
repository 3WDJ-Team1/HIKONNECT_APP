package kr.ac.yjc.wdj.hikonnect.beans;

import android.graphics.drawable.Drawable;

/**
 * 등산 완료 후 메뉴 리스트 찍어낼 틀
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-12
 */
public class AfterHikingMenu {
    private Drawable    imageDrawable;  // 메뉴에 사용될 이미지 drawable
    private String      menuTitle;      // 메뉴명
    private String      menuValue;      // 메뉴 내용

    /**
     * 등산 완료 후 메뉴 생성자
     * @param imageDrawable 이미지 drawable
     * @param menuTitle     메뉴명
     * @param menuValue     메뉴 내용 (사용자 값)
     */
    public AfterHikingMenu(Drawable imageDrawable, String menuTitle, String menuValue) {
        this.imageDrawable  = imageDrawable;
        this.menuTitle      = menuTitle;
        this.menuValue      = menuValue;
    }

    // getters and setters
    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public String getMenuValue() {
        return menuValue;
    }

    public void setMenuValue(String menuValue) {
        this.menuValue = menuValue;
    }
}
