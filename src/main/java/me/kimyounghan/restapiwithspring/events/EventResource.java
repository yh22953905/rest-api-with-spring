package me.kimyounghan.restapiwithspring.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {
    public static EntityModel<Event> modelOf(Event event) {
        EntityModel<Event> eventModel = EntityModel.of(event);
        eventModel.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        return eventModel;
    }
}
