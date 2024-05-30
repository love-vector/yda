package ai.yda.knowledge.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Knowledge not found")
public class KnowledgeNotFoundException extends RuntimeException {}
