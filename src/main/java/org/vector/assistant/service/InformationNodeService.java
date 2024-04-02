package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vector.assistant.persistance.dao.MilvusDao;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.UserAlreadyExistsException;
import org.vector.assistant.persistance.dao.InformationNodeDao;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final InformationNodeDao informationNodeDao;

    private final MilvusDao milvusDao;

    public Mono<URI> createInformationNode(String name, String description, UUID userId) {
        return informationNodeDao.existByNameAndUserId(name, userId).flatMap(exists -> {
            if (exists) {
                return Mono.error(new UserAlreadyExistsException());
            }


            return informationNodeDao
                    .createInformationNode(name, description, userId)
                    .flatMap(infoNode -> {
                        Mono.from(()->milvusDao.createCollection(name))
                    })
                    .map(informationNode -> URI.create(
                            Objects.requireNonNull(informationNode.getId()).toString()));
        });
    }
}
