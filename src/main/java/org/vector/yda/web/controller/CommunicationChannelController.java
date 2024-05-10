package org.vector.yda.web.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.vector.yda.channel.impl.DefaultCommunicationChannel;
import org.vector.yda.model.request.CommunicationRequest;
import org.vector.yda.model.response.CommunicationResponse;

@RestController
@RequestMapping("/communication")
@RequiredArgsConstructor
public class CommunicationChannelController {

    private final DefaultCommunicationChannel communicationChannel;

    @PostMapping
    public ResponseEntity<CommunicationResponse> sendRequest(@RequestBody @Validated final CommunicationRequest request) {
        return ResponseEntity.ok(communicationChannel.sendRequest(request));
    }
}
