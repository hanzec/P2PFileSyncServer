//package edu.iastate.com309.cyschedulebackend.service;
//
//import com.hanzec.syncdisk_server.Service.EventService;
//import com.hanzec.syncdisk_server.model.exception.event.EventNotFoundException;
//import com.hanzec.syncdisk_server.persistence.repository.EventRepository;
//import com.hanzec.syncdisk_server.persistence.requestModel.EventRequest;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.text.ParseException;
//
//import static org.mockito.ArgumentMatchers.anyString;
//
//public class EventServiceTest {
//
//    @InjectMocks
//    EventService eventService;
//
//    @Mock
//    EventRepository eventRepository;
//
//    @Before
//    public void init(){
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test(expected = EventNotFoundException.class)
//    public void updateNotExistEvent() throws ParseException, EventNotFoundException {
//        EventRequest eventRequest = new EventRequest();
//        Mockito.when(eventRepository.existsById(anyString())).thenReturn(false);
//
//        eventService.updateEvent(eventRequest,anyString());
//    }
//}
