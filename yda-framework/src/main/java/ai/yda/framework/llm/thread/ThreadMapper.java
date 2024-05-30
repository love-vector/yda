package ai.yda.framework.llm.thread;

import java.util.List;

import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.threads.ThreadRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {List.class})
public interface ThreadMapper {

    ThreadMapper INSTANCE = Mappers.getMapper(ThreadMapper.class);

    @Mapping(target = "messages", expression = "java(List.of(toMessageRequest(message)))")
    @Mapping(target = "metadata", ignore = true)
    ThreadRequest toThreadRequest(final String message);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "content", source = "message")
    @Mapping(target = "fileIds", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    MessageRequest toMessageRequest(final String message);
}
