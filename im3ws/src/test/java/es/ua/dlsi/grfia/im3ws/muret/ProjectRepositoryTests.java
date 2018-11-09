package es.ua.dlsi.grfia.im3ws.muret;

import es.ua.dlsi.grfia.im3ws.IM3WebApplication;
import es.ua.dlsi.grfia.im3ws.muret.entity.ManuscriptType;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.entity.User;
import es.ua.dlsi.grfia.im3ws.muret.repository.ProjectRepository;
import es.ua.dlsi.grfia.im3ws.muret.repository.UserRepository;
import es.ua.dlsi.im3.core.score.NotationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes= IM3WebApplication.class)
public class ProjectRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;


    @Test
    public void testUser() {
        // given
        User admin = new User("admin", "passadmin", "a@a.com");
        entityManager.persist(admin);
        entityManager.flush();

        User david = new User("david", "passdrizo", "a@a.com");
        entityManager.persist(david);
        entityManager.flush();

        Date now = new Date();
        Project project = new Project("Proyecto prueba", "prueba", "Compositor", now, now, admin, david, null, null, null, NotationType.eModern, ManuscriptType.ePrinted, null);
        entityManager.persist(project);
        entityManager.flush();

        // when
        List<Project> found = projectRepository.findAll();

        // then
        assertEquals("Found 1 projects", 1, found.size());
        Project foundProject = found.get(0);
        assertEquals("Project name", "Proyecto prueba", foundProject.getName());
        assertEquals("Project path", "prueba", foundProject.getPath());
        assertEquals("Project creation date", now, foundProject.getCreated());
        assertEquals("Project changed date", now, foundProject.getLastChange());
        assertEquals("Project created by", admin, foundProject.getCreatedBy());
        assertEquals("Project changed by", david, foundProject.getChangedBy());
    }
}
