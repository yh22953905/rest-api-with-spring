package me.kimyounghan.restapiwithspring.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

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
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors); // Errors 는 자바빈 스펙을 따르지 않기 때문에 body 메소드의 파라미터가 될 수 없음(JSON 으로 serialize 할 수 없음)
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors); // Errors 는 자바빈 스펙을 따르지 않기 때문에 body 메소드의 파라미터가 될 수 없음(JSON 으로 serialize 할 수 없음)
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event) ;
    }

}
