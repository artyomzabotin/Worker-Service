package epam.az.worker.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public abstract class BaseController {

    public void setLocationHeaderForResource(HttpServletResponse response, Object id) {

        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id);

        URI uri = uriComponents.toUri();
        String uriPath = uri.getPath();

        response.setHeader("Location", uriPath);
    }
}
