package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Shortcut;

import java.awt.event.ActionEvent;

/**
 * Created by aarthychandrasekhar on 18/01/16.
 */
public class PreferenceAction extends JosmAction {
    public PreferenceAction(String name) {
        super(name, null, name, null, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Main.pref.get("myplugin.clipboard").equals("yes")) {
            ChangesetIDPlugin.jCheckboxMenuItem.setSelected(false);
            Main.pref.put("myplugin.clipboard", "no");

        }
        else {
            ChangesetIDPlugin.jCheckboxMenuItem.setSelected(true);
            Main.pref.put("myplugin.clipboard", "yes");
        }
    }
}
