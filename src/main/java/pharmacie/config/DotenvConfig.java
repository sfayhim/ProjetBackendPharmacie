package pharmacie.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration pour charger les variables d'environnement depuis le fichier .env
 * au démarrage de l'application Spring Boot.
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Charger le fichier .env
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Ne pas échouer si .env n'existe pas (pour déploiement)
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> dotenvMap = new HashMap<>();

            // Transférer toutes les variables du .env vers l'environnement Spring
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                // Aussi définir comme variable système pour compatibilité
                System.setProperty(entry.getKey(), entry.getValue());
            });

            // Ajouter les variables au PropertySource de Spring
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));

        } catch (Exception e) {
            // En production (Render), le .env n'existera pas, les variables d'environnement
            // seront définies directement dans le système
            System.out.println("Note: .env file not loaded (using system environment variables)");
        }
    }
}
