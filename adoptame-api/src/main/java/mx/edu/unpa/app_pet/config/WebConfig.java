package mx.edu.unpa.app_pet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expone la carpeta donde guardaremos las fotos
        // Puedes cambiar la ruta absoluta dependiendo de dónde se despliegue (ej. Docker o tu local)
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:src/main/resources/static/imagenes/");
    }
}
