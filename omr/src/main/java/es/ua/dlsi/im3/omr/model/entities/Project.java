package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.score.NotationType;

import java.util.*;

/**
 * We use a parallel hierarchy just for saving using XStream making easier to it this way
 */
public class Project {
    public static final String IMAGES_FOLDER = "images";

    /**
     * Name of the project
     */
    private String name;
    /**
     * Composer
     */
    private String composer;
    /**
     * Version with which the project is encoded. It will determine its contents
     */
    private ProjectVersion version;
    /**
     * Either mensural or modern
     */
    private NotationType notationType;

    /**
     * Ordered set (given a image number) of image files
     */
    private SortedSet<Image> images;
    /**
     * Comments about the project
     */
    private String comments;
    /**
     * Last changed date
     */
    private Date lastChangedDate;

    /**
     * User name that changed it
     */
    private String changedBy;


    public Project(ProjectVersion version, NotationType notationType) {
        this.version = version;
        this.notationType = notationType;
        images = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectVersion getVersion() {
        return version;
    }

    public void setVersion(ProjectVersion version) {
        this.version = version;
    }

    public SortedSet<Image> getImages() {
        return images;
    }

    public void setImages(SortedSet<Image> images) {
        this.images = images;
    }

    public NotationType getNotationType() {
        return notationType;
    }

    public void setNotationType(NotationType notationType) {
        this.notationType = notationType;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    /**
     *
     * @param imageRelativeFileName
     * @return null if not found
     */
    public Image findImageByFileName(String imageRelativeFileName) {
        for (Image image: images) {
            if (image.getImageRelativeFileName().equals(imageRelativeFileName)) {
                return image;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) &&
                Objects.equals(composer, project.composer) &&
                version == project.version &&
                notationType == project.notationType &&
                Objects.equals(images, project.images);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, version, notationType, images);
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public Date getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Date lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}
