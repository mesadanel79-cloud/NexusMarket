package application.domain.services.operation;

import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Operation;
import application.domain.ports.out.OperationRepositoryPort;

/**
 * Stores a successful business operation in the relational operation log.
 */
public class RegisterOperationService {

    private final OperationRepositoryPort operationRepositoryPort;

    public RegisterOperationService(OperationRepositoryPort operationRepositoryPort) {
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public Operation execute(Operation operation) {
        if (operation == null || operation.getOperationType() == null) {
            throw new InvalidOperationException("Operation type is required.");
        }
        if (operation.getExecutionDate() == null) {
            throw new InvalidOperationException("Operation execution date is required.");
        }
        return operationRepositoryPort.save(operation);
    }
}
