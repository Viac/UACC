package ua.com.glady.uacc.main_menu;

/**
 * Defines content of the main menu item
 *
 * Created by Slava on 14.04.2015.
 */
public class MainMenuItem {

    String title;
    String text;
    int image;
    float scale = 1f;

    public MainMenuItem(String title, String text, int image, float scale) {
        this.title = title;
        this.text = text;
        this.image = image;
        this.scale = scale;
    }

    public MainMenuItem(String title, String text, int image) {
        this.title = title;
        this.text = text;
        this.image = image;
    }

}