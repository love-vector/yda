package ai.yda.intent.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Intent not found")
public class IntentNotFoundException extends RuntimeException {}
