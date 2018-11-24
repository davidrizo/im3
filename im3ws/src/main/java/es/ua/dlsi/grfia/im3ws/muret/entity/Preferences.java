package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author drizo
 */
@Entity
public class Preferences {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (name = "image_symbol_classifier")
    private String imageSymbolClassifier;
    @ManyToOne
    @JoinColumn(name="user_id", nullable = true)
    private User user;

    public Preferences() {
    }

    public Preferences(String imageSymbolClassifier, User user) {
        this.imageSymbolClassifier = imageSymbolClassifier;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Preferences that = (Preferences) o;
        return Objects.equals(imageSymbolClassifier, that.imageSymbolClassifier) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageSymbolClassifier, user);
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "id=" + id +
                ", imageSymbolClassifier='" + imageSymbolClassifier + '\'' +
                ", user=" + user +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getImageSymbolClassifier() {
        return imageSymbolClassifier;
    }

    public void setImageSymbolClassifier(String imageSymbolClassifier) {
        this.imageSymbolClassifier = imageSymbolClassifier;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

