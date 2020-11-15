package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class RepresentacionCreadaEvent implements Event {

    UUID rootId;
    ZonedDateTime cuando;
    Sala donde;
}
