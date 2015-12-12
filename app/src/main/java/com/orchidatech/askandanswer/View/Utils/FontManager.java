package com.orchidatech.askandanswer.View.Utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class FontManager {
    public static final String ROBOTO_LIGHT = "roboto_light";
    public static final String ROBOTO_MEDIUM = "roboto_medium";

    public static FontManager instance;
    private static AssetManager mAssetManager;
    private Typeface typeface;

    public static FontManager getInstance(AssetManager assetManager){
        mAssetManager = assetManager;
        if(instance == null){
            instance = new FontManager();
        }
        return instance;
    }
    public Typeface getFont(String fontName){
        switch (fontName){
            case "roboto_light":
                typeface = Typeface.createFromAsset(mAssetManager, "Fonts/roboto_light.ttf");
                break;
            case "roboto_medium":
                typeface = Typeface.createFromAsset(mAssetManager, "Fonts/roboto_medium.ttf");
                break;
        }
        return typeface;
    }
}
