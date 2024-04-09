package keville.server.services.registration;

import keville.server.dto.RegisterUserDTO;
import keville.server.services.registration.exceptions.RegistrationServiceException;

public interface RegistrationService {

    public void registerUser(RegisterUserDTO dto) throws RegistrationServiceException;

}
