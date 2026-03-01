package pharmacie.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.CategorieRepository;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Medicament;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.data-locations=classpath:data.sql")
@Transactional
public class ReapprovisionnementServiceTest {

    @Autowired
    private ReapprovisionnementService reapprovisionnementService;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    public void testEnvoyerDemandesDevis() {
        reapprovisionnementService.envoyerDemandesDevis();
        verify(javaMailSender, times(4)).send(any(SimpleMailMessage.class));
    }
}
