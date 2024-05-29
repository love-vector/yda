package ai.yda.context.internal;

import org.springframework.stereotype.Service;

@Service
public class ContextGeneratorImpl implements ContextGenerator {

    @Override
    public Context generateContext(final RawContext rawContext) {
        return new Context();
    }
}
