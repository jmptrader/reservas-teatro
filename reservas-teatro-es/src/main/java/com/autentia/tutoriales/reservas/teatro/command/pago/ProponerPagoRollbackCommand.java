package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.adapter.payment.PaymentGatewayFactory;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ProponerPagoRollbackCommand implements Command<UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Pago.class);
        if (repository.load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        final var codigoPago = iniciarPago();
        try {
            eventPublisher.tryPublish(0L, new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos, codigoPago));
        } catch (Exception exception) { // Rollback si no se puede guardar el nuevo estado
            cancelarPago(codigoPago);
        }
    }

    private String iniciarPago() {
        final var descripcion = "Reserva " + reserva;
        final var valor = conceptos.stream()
                .mapToInt(Concepto::getPrecio)
                .sum();

        return PaymentGatewayFactory.getPaymentGateway().initiatePayment(cliente, descripcion, valor);
    }

    private void cancelarPago(final String codigoPago) {
        PaymentGatewayFactory.getPaymentGateway().cancelPayment(codigoPago);
    }
}