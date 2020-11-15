package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.NoSideEffectsCommand;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class SeleccionarButacasCommand implements NoSideEffectsCommand<Representacion> {

    Set<Butaca> butacas;

    public boolean isValid(Representacion root) {
        return root.getButacasLibres().containsAll(butacas);
    }

    public List<Event> execute(Representacion root) {
        return List.of(new ButacasSeleccionadasEvent(root.getId(), butacas));
    }
}
