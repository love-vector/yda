package ai.yda.framework.rag.retriever.google_drive.service;

import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.NestedExceptionUtils;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class DocumentFunctions {

    private final DocumentMetadataPort documentMetadataPort;

    @Bean
    @Description("Get booking details")
    public Function<Void, BookingDetails> getBookingDetails() {
        return request -> {
            try {
                return documentMetadataPort.getAllDocuments();
            } catch (Exception e) {
                logger.warn("Booking details: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
                return new BookingDetails(request.bookingNumber(), request.firstName(), request.lastName,
                        null, null, null, null, null);
            }
        };
    }
}
