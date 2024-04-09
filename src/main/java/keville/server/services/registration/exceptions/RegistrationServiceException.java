package keville.server.services.registration.exceptions;

    public class RegistrationServiceException extends Exception {

      public RegistrationServiceError error;

      public RegistrationServiceException(RegistrationServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum RegistrationServiceError {


        EMPTY_EMAIL,
        EMTPY_USERNAME,
        EMTPY_PASSWORD,

        //TODO
        //MALFORMED_EMAIL,
        //USERNAME_FORBIDDEN (hate speech)

        EMAIL_TOO_LONG,
        USERNAME_TOO_LONG,
        PASSWORD_TOO_SHORT,
        PASSWORD_TOO_LONG,

        PASSWORD_UNEQUAL,

        EMAIL_IN_USE,
        USERNAME_IN_USE,


      }

    }
