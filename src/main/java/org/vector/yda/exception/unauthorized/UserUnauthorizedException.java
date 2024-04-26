package org.vector.yda.exception.unauthorized;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User is unauthorized")
public class UserUnauthorizedException extends RuntimeException {}
