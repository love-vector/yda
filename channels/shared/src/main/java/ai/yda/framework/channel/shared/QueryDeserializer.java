/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.channel.shared;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.reflections.Reflections;

import org.springframework.ai.rag.Query;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

public class QueryDeserializer extends JsonDeserializer<Query> {

    private final Set<Class<? extends Query>> requestSubclasses;

    public QueryDeserializer(final ApplicationContext applicationContext) {
        var rootPackage = this.getClass().getPackage().getName().split("\\.")[0];
        var springBootApplications = List.copyOf(applicationContext
                .getBeansWithAnnotation(SpringBootApplication.class)
                .values());
        if (springBootApplications.isEmpty()) {
            this.requestSubclasses = new Reflections(rootPackage).getSubTypesOf(Query.class);
        } else {
            var packages = springBootApplications.parallelStream()
                    .map(application -> application.getClass().getPackage().getName())
                    .toList();
            this.requestSubclasses = new Reflections(rootPackage, packages).getSubTypesOf(Query.class);
        }
    }

    @Override
    public Query deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        var codec = parser.getCodec();
        var jsonTree = codec.readTree(parser);
        if (!requestSubclasses.isEmpty()) {
            for (var requestSubclass : requestSubclasses) {
                if (isMatchingSubclass(requestSubclass, jsonTree)) {
                    return codec.treeToValue(jsonTree, requestSubclass);
                }
            }
        }
        var query = jsonTree.get("query") == null ? "" : ((TextNode) jsonTree.get("query")).asText();
        return Query.builder().text(query).build();
    }

    private Boolean isMatchingSubclass(final Class<? extends Query> subclass, final TreeNode rootNode) {
        for (var field : getAllFields(subclass)) {
            if (rootNode.get(field.getName()) == null) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Recursively retrieves all fields from the specified class, including fields from its superclass.
     *
     * @param clazz the class from which to retrieve fields.
     * @return a set of {@link Field} objects representing all fields in the class hierarchy.
     */
    private Set<Field> getAllFields(final Class<?> clazz) {
        var fields = new HashSet<Field>();
        if (clazz == null) {
            return fields;
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(getAllFields(clazz.getSuperclass()));
        return fields;
    }
}
