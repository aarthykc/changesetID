package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

import java.io.File;

/**
 * Created by aarthychandrasekhar on 16/01/16.
 */
public abstract class UploadNotifyLayer extends OsmDataLayer {

    public UploadNotifyLayer(DataSet dataSet, String s, File file) {
        super(dataSet, s, file);
    }

    public abstract void onUploadNotifier();

    @Override
    public void onPostUploadToServer() {
        super.onPostUploadToServer();
        onUploadNotifier();
    }
}
