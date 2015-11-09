package com.orchidatech.askandanswer.View.Utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class FontManager {
    public static final String ROBOTO_LIGHT = "roboto_light";

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
            case "ROBOTO_LIGHT":
                typeface = Typeface.createFromAsset(mAssetManager, "Fonts/roboto_light.ttf");
                break;
        }
        return typeface;
    }
}
