package me.kimyounghan.restapiwithspring.events;

import me.kimyounghan.restapiwithspring.accounts.Account;
import me.kimyounghan.restapiwithspring.accounts.CurrentUser;
import me.kimyounghan.restapiwithspring.common.ErrorResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(
            @RequestBody @Valid EventDto eventDto
            , Errors errors
            , @CurrentUser Account account
    ) {
        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
//            return ResponseEntity.badRequest().body(errors); // Errors 는 자바빈 스펙을 따르지 않기 때문에 body 메소드의 파라미터가 될 수 없음(JSON 으로 serialize 할 수 없음)
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
            return badRequest(errors); // Errors 는 자바빈 스펙을 따르지 않기 때문에 body 메소드의 파라미터가 될 수 없음(JSON 으로 serialize 할 수 없음)
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setUser(account);
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EntityModel<Event> pagedResource = EventResource.modelOf(newEvent);
        pagedResource.add(linkTo(EventController.class).withRel("query-events"));
        pagedResource.add(selfLinkBuilder.withRel("update-event"));
        pagedResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(pagedResource) ;
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest()
                .body(ErrorResource.modelOf(errors));
    }

    @GetMapping
    public ResponseEntity readEvents(
            Pageable pageable
            , PagedResourcesAssembler<Event> assembler
            , @CurrentUser Account account
    ) {
        Page<Event> page = eventRepository.findAll(pageable);

        PagedModel<EntityModel<Event>> pagedResource = assembler.toModel(page, EventResource::modelOf);
        pagedResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        if (account != null) {
            pagedResource.add(linkTo(EventController.class).withRel("create-event"));
        }

        return ResponseEntity.ok(pagedResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity readEvent(
            @PathVariable Integer id
            , @CurrentUser Account currentUser
    ) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EntityModel<Event> eventResource = EventResource.modelOf(event);
        eventResource.add(Link.of("/docs/index.html#resources-event-get").withRel("profile"));
        if (event.getUser().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(currentUser.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(
            @PathVariable Integer id
            , @RequestBody @Valid EventDto eventDto
            , Errors errors
            , @CurrentUser Account currentUser
    ) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        if (!existingEvent.getUser().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        modelMapper.map(eventDto, existingEvent);
        Event savedEvent = eventRepository.save(existingEvent);

        EntityModel<Event> eventResource = EventResource.modelOf(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-event-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

}
