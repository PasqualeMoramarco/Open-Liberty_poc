package io.openliberty.guides.inventory;

import io.openliberty.guides.inventory.dao.EventDao;
import io.openliberty.guides.inventory.models.Event;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@RequestScoped
@Path("event")
public class EventResource {
    private static Logger logger = Logger.getLogger(EventResource.class.getName());


    @Inject
    private EventDao eventDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addEvent(Event event) {
        logger.warning("AddEvent");
        String respMessage = "Event #" + event.getId() + " created successfully.";
        return Response.status(Response.Status.CREATED).entity(respMessage).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getEvents() {

        return Response.status(Response.Status.OK).entity(eventDAO.readAllEvents()).build();
//        JsonObjectBuilder builder = Json.createObjectBuilder();
//        JsonArrayBuilder finalArray = Json.createArrayBuilder();
//        for (Event event : eventDAO.readAllEvents()) {
//            builder.add("id", event.getId())
//                    .addIfNotNull("hostname", event.getHostname())
//                    .add("property", event.getProperty())
//                    .add("value", event.getValue())
//                    .add("sended", event.isSended())
//                    .add("readed", event.isReaded());
//            finalArray.add(builder.build());
//        }
//        return finalArray.build();
    }
}
