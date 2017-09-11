package no.rutebanken.nabu.event.user;


import no.rutebanken.nabu.event.user.dto.user.UserDTO;

import java.util.Collection;

public interface UserRepository {

    Collection<UserDTO> findAll();

    UserDTO getByUsername(String username);
}
