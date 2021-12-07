// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.inventory.dao.EventDao;
import io.openliberty.guides.inventory.models.Event;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.PropertyMessage;
import io.openliberty.guides.models.SystemLoad;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;


@ApplicationScoped
@Path("/inventory")
public class InventoryResource {

    private static Logger logger = Logger.getLogger(InventoryResource.class.getName());
    // tag::propertyNameEmitter[]
    private FlowableEmitter<Message<PropertyMessage>> propertyNameEmitter;
    // end::propertyNameEmitter[]

    @Inject
    private InventoryManager manager;

    @Inject
    private EventDao eventDAO;
    
    @GET
    @Path("/systems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystems() {
        List<Properties> systems = manager.getSystems()
                .values()
                .stream()
                .collect(Collectors.toList());
        logger.warning("GetSystems1: " + systems);
        return Response
                .status(Response.Status.OK)
                .entity(systems)
                .build();
    }

    @GET
    @Path("/systems/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystem(@PathParam("hostname") String hostname) {
        Optional<Properties> system = manager.getSystem(hostname);
        if (system.isPresent()) {
            logger.warning("GetSystem2: " + hostname + " - " + system);
            return Response
                    .status(Response.Status.OK)
                    .entity(system)
                    .build();
        }
        logger.warning("GetSystem3: " + hostname + " - " + system);
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("hostname does not exist.")
                .build();
    }

    // tag::updateSystemProperty[]
    @PUT
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Transactional
    /* This method sends a message and returns a CompletionStage that doesn't
        complete until the message is acknowledged. */
    // tag::USPHeader[]
    public CompletionStage<Response> updateSystemProperty(String propertyName) {
    // end::USPHeader[]
        logger.warning("UpdateSystemProperty: " + propertyName);
        // First, create an incomplete CompletableFuture named "result".
        // tag::CompletableFuture[]
        CompletableFuture<Void> result = new CompletableFuture<>();
        // end::CompletableFuture[]

        Event event = new Event(propertyName, true);
        eventDAO.createEvent(event);
        logger.warning("Event: " + event);

        PropertyMessage propertyMessage = new PropertyMessage();
        propertyMessage.id = event.getId();
        propertyMessage.key = propertyName;

        // Create a message that holds the payload.
        // tag::message[]
        Message<PropertyMessage> message = Message.of(
                // tag::payload[]
                propertyMessage,
                // end::payload[]
                // tag::acknowledgeAction[]
                () -> {
                    /* This is the ack callback, which runs when the outgoing
                        message is acknowledged. After the outgoing message is
                        acknowledged, complete the "result" CompletableFuture. */
                    result.complete(null);
                    /* An ack callback must return a CompletionStage that says
                        when it's complete. Asynchronous processing isn't necessary 
                        so a completed CompletionStage is returned to indicate that 
                        the work here is done. */
                    return CompletableFuture.completedFuture(null);
                }
                // end::acknowledgeAction[]
        );
        // end::message[]

        // Send the message
        propertyNameEmitter.onNext(message);
        /* Set up what happens when the message is acknowledged and the "result"
            CompletableFuture is completed. When "result" completes, the Response 
            object is created with the status code and message. */
        // tag::returnResult[]
        return result.thenApply(a -> Response
                .status(Response.Status.OK)
                .entity("Request successful for the " + propertyName + " property\n")
                .build());
        // end::returnResult[]
    }
    // end::updateSystemProperty[]

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetSystems() {
        manager.resetSystems();
        return Response
                .status(Response.Status.OK)
                .build();
    }

    // tag::updateStatus[]
    // tag::systemLoadIncoming[]
    @Incoming("systemLoad")
    // end::systemLoadIncoming[]
    public void updateStatus(SystemLoad sl)  {
        String hostname = sl.hostname;
        if (manager.getSystem(hostname).isPresent()) {
            manager.updateCpuStatus(hostname, sl.loadAverage);
            manager.updateLocalTime(hostname);
            logger.warning("Host " + hostname + " was updated: " + sl);
        } else {
            manager.addSystem(hostname, sl.loadAverage, sl.localDateTime);
            logger.warning("Host " + hostname + " was added: " + sl);
        }
    }
    // end::updateStatus[]

    // tag::getPropertyMessage[]
    // tag::addSystemPropertyIncoming[]
    @Incoming("addSystemProperty")
    @Transactional
    // end::addSystemPropertyIncoming[]
    public void getPropertyMessage(PropertyMessage pm)  {
        try {
            logger.warning("GetPropertyMessage: " + pm);
            Event event = new Event(pm.id, pm.hostname, pm.key, pm.value, true, true);
            String hostId = pm.hostname;
            eventDAO.updateEvent(event);
            if (manager.getSystem(hostId).isPresent()) {
                manager.updatePropertyMessage(hostId, pm.key, pm.value);
                logger.warning("Host " + hostId + " was updated: " + pm);
            } else {
                manager.addSystem(hostId, pm.key, pm.value);
                logger.warning("Host " + hostId + " was added: " + pm);
            }
        } catch (Exception e){
            logger.warning("exception: " + e);
            logger.warning("exception: " + e.getMessage());
        }
    }
    // end::getPropertyMessage[]

    // tag::sendPropertyName[]
    @Outgoing("requestSystemProperty")
    // tag::SPMHeader[]
    public Publisher<Message<PropertyMessage>> sendPropertyName() {
    // end::SPMHeader[]
        Flowable<Message<PropertyMessage>> flowable = Flowable.create(emitter ->
                this.propertyNameEmitter = emitter, BackpressureStrategy.BUFFER);
        logger.warning("SendPropertyName: " + flowable );
        return flowable;
    }
    // end::sendPropertyName[]
}
