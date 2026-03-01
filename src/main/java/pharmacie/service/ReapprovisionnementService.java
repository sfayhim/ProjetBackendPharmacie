package pharmacie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReapprovisionnementService {

    private final MedicamentRepository medicamentRepository;
    private final JavaMailSender javaMailSender;

    @Transactional(readOnly = true)
    public void envoyerDemandesDevis() {
        List<Medicament> medicamentsACommander = medicamentRepository.findMedicamentsACommander();

        if (medicamentsACommander.isEmpty()) {
            return;
        }

        Map<Fournisseur, Map<Categorie, List<Medicament>>> articlesParFournisseur = new HashMap<>();

        for (Medicament med : medicamentsACommander) {
            Categorie cat = med.getCategorie();
            List<Fournisseur> fournisseurs = cat.getFournisseurs();

            for (Fournisseur fournisseur : fournisseurs) {
                articlesParFournisseur
                        .computeIfAbsent(fournisseur, k -> new HashMap<>())
                        .computeIfAbsent(cat, k -> new ArrayList<>())
                        .add(med);
            }
        }

        for (Map.Entry<Fournisseur, Map<Categorie, List<Medicament>>> entry : articlesParFournisseur.entrySet()) {
            Fournisseur fournisseur = entry.getKey();
            Map<Categorie, List<Medicament>> articlesParCategorie = entry.getValue();

            envoyerMailFournisseur(fournisseur, articlesParCategorie);

            
        }
    }

    private void envoyerMailFournisseur(Fournisseur fournisseur,
            Map<Categorie, List<Medicament>> articlesParCategorie) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(fournisseur.getEmail());
        message.setSubject("Demande de devis - Réapprovisionnement Pharmacie");

        StringBuilder text = new StringBuilder();
        text.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
        text.append("Veuillez nous transmettre un devis pour les médicaments suivants :\n\n");

        for (Map.Entry<Categorie, List<Medicament>> catEntry : articlesParCategorie.entrySet()) {
            Categorie categorie = catEntry.getKey();
            List<Medicament> medicaments = catEntry.getValue();

            text.append("Catégorie : ").append(categorie.getLibelle()).append("\n");
            for (Medicament med : medicaments) {
                text.append("- ").append(med.getNom())
                        .append(" (Stock: ").append(med.getUnitesEnStock())
                        .append(", Niveau Reappro: ").append(med.getNiveauDeReappro())
                        .append(")\n");
            }
            text.append("\n");
        }
        message.setText(text.toString());

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {}", fournisseur.getEmail(), e);
        }
    }
}
