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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    }

    @Override
    public void activeLayerChange(Layer layer, Layer layer1) {
    }

    @Override
    public void layerAdded(Layer layer) {
        if(layer instanceof OsmDataLayer)
            layer.addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void layerRemoved(Layer layer) {
        if (layer instanceof OsmDataLayer)
            layer.removePropertyChangeListener(propertyChangeListener);
    }


    @Override
    public void processDatasetEvent(AbstractDatasetChangedEvent abstractDatasetChangedEvent) {

    }

    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == OsmDataLayer.REQUIRES_UPLOAD_TO_SERVER_PROP) {
                if(!(Boolean)evt.getNewValue()) {
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

            }
        }
    };
}

