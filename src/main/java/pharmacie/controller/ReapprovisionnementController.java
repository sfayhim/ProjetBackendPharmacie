package pharmacie.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pharmacie.service.ReapprovisionnementService;

@RestController
@RequestMapping("/api/reapprovisionnement")
@RequiredArgsConstructor
public class ReapprovisionnementController {

    private final ReapprovisionnementService reapprovisionnementService;

    @PostMapping
    public String lancerReapprovisionnement() {
        reapprovisionnementService.envoyerDemandesDevis();
        return "Demandes de devis envoyées avec succès.";
    }
}
