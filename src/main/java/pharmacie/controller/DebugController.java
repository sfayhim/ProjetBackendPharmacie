package pharmacie.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final MedicamentRepository medicamentRepository;

    @GetMapping("/medicaments-a-commander")
    public Map<String, Object> getMedicamentsACommander() {
        List<Medicament> medicaments = medicamentRepository.findMedicamentsACommander();
        
        Map<String, Object> result = new HashMap<>();
        result.put("nombre", medicaments.size());
        result.put("medicaments", medicaments.stream()
            .map(m -> Map.of(
                "nom", m.getNom(),
                "stock", m.getUnitesEnStock(),
                "seuil", m.getNiveauDeReappro(),
                "categorie", m.getCategorie().getLibelle(),
                "fournisseurs", m.getCategorie().getFournisseurs().size()
            ))
            .toList());
        
        return result;
    }
}
