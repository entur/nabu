/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.filter.EventMatcherFactory;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.event.user.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailAuthenticationException;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserNotificationEventHandlerTest {

    private UserRepository userRepository;
    private ImmediateNotificationService immediateNotificationService;
    private UserNotificationEventHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        immediateNotificationService = mock(ImmediateNotificationService.class);
        EventMatcherFactory eventMatcherFactory = mock(EventMatcherFactory.class);
        when(eventMatcherFactory.createEventMatcher(any())).thenReturn(event -> true);
        handler = new UserNotificationEventHandler(userRepository, mock(NotificationRepository.class),
                immediateNotificationService, eventMatcherFactory);
    }

    @Test
    void immediateNotificationFailureDoesNotPropagate() {
        UserDTO failing = user("failing");
        UserDTO other = user("other");
        when(userRepository.findAll()).thenReturn(List.of(failing, other));
        doThrow(new MailAuthenticationException("Authentication failed"))
                .when(immediateNotificationService).sendNotifications(any(), eq(failing));

        Event event = JobEvent.builder().domain("TIMETABLE").referential("vyg").providerId(1L).state(JobState.FAILED)
                .action("PREVALIDATION").correlationId("corr").eventTime(Instant.now()).build();

        Assertions.assertDoesNotThrow(() -> handler.onEvent(event));
        verify(immediateNotificationService).sendNotifications(any(), eq(other));
    }

    private static UserDTO user(String username) {
        UserDTO user = new UserDTO();
        user.username = username;
        user.notifications = Set.of(new NotificationConfigDTO(NotificationType.EMAIL, true, null));
        return user;
    }
}
