package ru.eshangin.compositelaunch;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Helps convert configuration items to/from JSON
 */
class JsonConfigurationHelper {
	
    /**
     * Return list of configurations parsed from JSON
     */
	public static ArrayList<CompositeConfigurationItem> fromJson(String json) {		
		if (json == "") {
			return new ArrayList<CompositeConfigurationItem>();
		}
		else {
			Type collectionType = new TypeToken<ArrayList<CompositeConfigurationItem>>(){}.getType();
			
			ArrayList<CompositeConfigurationItem> deserialized = new Gson().fromJson(json, collectionType);
	
			return deserialized;
		}
	}

    /**
     * Converts list of CompositeConfigurationItem-s into JSON
     */
	public static String toJson(ArrayList<CompositeConfigurationItem> items) {
		
		Type collectionType = new TypeToken<ArrayList<CompositeConfigurationItem>>() {}.getType();
		String json = new Gson().toJson(items, collectionType);
		
		return json;
	}
}
