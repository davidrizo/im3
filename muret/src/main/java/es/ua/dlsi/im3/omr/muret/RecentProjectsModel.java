package es.ua.dlsi.im3.omr.muret;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @autor drizo
 */
public class RecentProjectsModel {
    private static String KEY_NUM_RECENT_PROJECTS = "recentProjectsCount";
    private static String RECENT_PROJECT = "recentProjectNum";


    static RecentProjectsModel instance = null;
    LinkedList<String> recentProjects;
    private Preferences preferences;

    private RecentProjectsModel() {
        loadLastProjects();
    }

    public synchronized static RecentProjectsModel getInstance() {
        if (instance == null) {
            instance = new RecentProjectsModel();
        }
        return instance;
    }


    private void loadLastProjects() {
        recentProjects = new LinkedList<>();
        preferences = Preferences.userNodeForPackage(OpenProjectController.class);
        int recentProjectsCount = preferences.getInt(KEY_NUM_RECENT_PROJECTS, 0);
        for (int i=0; i<recentProjectsCount; i++) {
            String recentProject = preferences.get(RECENT_PROJECT + i, "");
            if (!recentProject.isEmpty()) {
                recentProjects.add(recentProject);
            }
        }
    }

    /**
     * Sorted from oldest to newest
     * @return
     */
    public LinkedList<String> getRecentProjects() {
        return recentProjects;
    }

    public void remove(String mrtFileName) {
        recentProjects.remove(mrtFileName);
    }

    public void addProject(String mrtFileName) {
        // first, if file name is found, remove it
        recentProjects.remove(mrtFileName);

        recentProjects.add(mrtFileName);
        for (int i=0; i<recentProjects.size(); i++) {
            preferences.put(RECENT_PROJECT + i, recentProjects.get(i));
        }
        preferences.putInt(KEY_NUM_RECENT_PROJECTS, recentProjects.size());
    }

    public void clear() {
        recentProjects.clear();
        preferences.putInt(KEY_NUM_RECENT_PROJECTS, 0);
    }
}
