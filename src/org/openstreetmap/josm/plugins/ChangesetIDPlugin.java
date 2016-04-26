package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.Changeset;
import org.openstreetmap.josm.data.osm.ChangesetCache;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.gui.*;
import org.openstreetmap.josm.gui.help.HelpUtil;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import static org.openstreetmap.josm.tools.I18n.marktr;

public class ChangesetIDPlugin extends Plugin implements  DataSetListenerAdapter.Listener,MapView.LayerChangeListener{

    public static JCheckBoxMenuItem jCheckboxMenuItem;
    DataSetListenerAdapter dataSetListenerAdapter = new DataSetListenerAdapter(this);
    public ChangesetIDPlugin(PluginInformation info) {
        super(info);
        MainMenu menu = Main.main.menu;
        JMenu showChangesetID;
        showChangesetID = menu.addMenu(marktr("ChangesetID"),"Changeset Link", KeyEvent.VK_K, menu.getDefaultMenuPos(), HelpUtil.ht("/Plugin/changesetID"));
        jCheckboxMenuItem = new JCheckBoxMenuItem(new PreferenceAction("Copy changeset link to clipboard"));
        jCheckboxMenuItem.setSelected(true);
        showChangesetID.add(jCheckboxMenuItem);
        MapView.addLayerChangeListener(this);
        Main.addMapFrameListener(new MapFrameListener() {
            @Override
            public void mapFrameInitialized(final MapFrame mapFrame, MapFrame mapFrame1) {
            }
        });
    }

    @Override
    public void activeLayerChange(Layer layer, Layer layer1) {
    }

    @Override
    public void layerAdded(Layer layer) {
        if (!(layer instanceof UploadNotifyLayer)) {
            if(layer instanceof OsmDataLayer)
                registerNewLayer((OsmDataLayer) layer);
        }
    }

    @Override
    public void layerRemoved(Layer layer) {
        if (!(layer instanceof UploadNotifyLayer)) {
            if (layer instanceof OsmDataLayer)
                unRegisterNewLayer((OsmDataLayer) layer);
        }
    }
    private void unRegisterNewLayer(OsmDataLayer layer) {
        layer.data.removeDataSetListener(dataSetListenerAdapter);
    }
    private void registerNewLayer(OsmDataLayer layer) {
        Main.main.addLayer(new UploadNotifyLayer(layer.data, layer.getName(), layer.getAssociatedFile()) {
            @Override
            public void onUploadNotifier() {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        for (final Changeset cs : ChangesetCache.getInstance().getChangesets()) {
                            if(Main.pref.get("myplugin.clipboard").equals("yes")) {
                                new Notification("Changeset Link: https://www.openstreetmap.org/changeset/" + cs.getId()).setDuration(Notification.TIME_LONG).show();
                                String changesetLink = "https://www.openstreetmap.org/changeset/" + cs.getId();
                                StringSelection selection = new StringSelection(changesetLink);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(selection, selection);
                                break;
                            }
                        }
                    }
                });
            }
        });
        Main.main.removeLayer(layer);

    }

    @Override
    public void processDatasetEvent(AbstractDatasetChangedEvent abstractDatasetChangedEvent) {

    }
}

