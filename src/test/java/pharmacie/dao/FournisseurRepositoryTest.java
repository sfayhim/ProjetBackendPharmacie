package pharmacie.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.TestPropertySource;

import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.data-locations=classpath:data.sql")
public class FournisseurRepositoryTest {

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Test
    public void testDataInitialization() {
        long count = fournisseurRepository.count();
        assertEquals(5, count, "Should have 5 suppliers initialized");

        List<Categorie> categories = categorieRepository.findAll();
        assertEquals(10, categories.size(), "Should have 10 categories");

        for (Categorie cat : categories) {
            int supplierCount = cat.getFournisseurs().size();
            assertTrue(supplierCount >= 2,
                    "Category " + cat.getLibelle() + " should have at least 2 suppliers, but has " + supplierCount);
        }
    }

    @Test
    public void testCreateAndLink() {
        Fournisseur f = new Fournisseur();
        f.setNom("New Supplier");
        f.setEmail("new@test.com");

        f = fournisseurRepository.saveAndFlush(f);

        Categorie c = categorieRepository.findAll().get(0);
        f.getCategories().add(c);

        f = fournisseurRepository.saveAndFlush(f);

        assertTrue(f.getCategories().contains(c));

        Categorie cRefreshed = categorieRepository.findById(c.getCode()).orElseThrow();

        assertTrue(cRefreshed.getFournisseurs().contains(f));
    }
}
