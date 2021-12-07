package io.openliberty.guides.inventory;

import io.openliberty.guides.inventory.dao.EventDao;
import io.openliberty.guides.inventory.models.Event;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@RequestScoped
@Path("person")
public class EventResource {
    private static Logger logger = Logger.getLogger(EventResource.class.getName());


    @Inject
    private EventDao eventDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addPerson(Event event) {
        logger.warning("aggiunta evento 1");
//        if(eventDAO.findEvent("name", "location", "time").isEmpty()) {
//            logger.warning("aggiunta evento 2");
//            eventDAO.createEvent(event);
//        }
        String respMessage = "Person #" + event.getId() + " created successfully.";
        return Response.status(Response.Status.CREATED).entity(respMessage).build();
    }
}
