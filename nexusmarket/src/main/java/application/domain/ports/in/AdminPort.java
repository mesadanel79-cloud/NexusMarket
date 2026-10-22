package application.domain.ports.in;

import application.domain.enums.ApprovalDecision;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import java.util.List;

/**
 * Use cases of the ADMIN role: users, buyers, sellers, warehouses, returns and refunds.
 */
public interface AdminPort {

    User registerSeller(User admin, User newSellerUser, Warehouse firstWarehouse);

    User registerStaffUser(User admin, User newStaffUser);

    List<User> consultUsers(User admin);

    User changeUserStatus(User admin, User target, UserStatus newStatus);

    Buyer changeBuyerStatus(User admin, Buyer buyer, BuyerStatus newStatus);

    List<Seller> consultSellers(User admin);

    Seller changeSellerStatus(User admin, Seller seller, SellerStatus newStatus);

    Warehouse registerWarehouse(User admin, Warehouse warehouse);

    List<Warehouse> consultWarehouses(User admin);

    Warehouse changeWarehouseStatus(User admin, Warehouse warehouse, WarehouseStatus newStatus);

    List<ReturnRequest> consultReturns(User admin, ReturnStatus status);

    ReturnRequest reviewReturn(User admin, ReturnRequest returnRequest, ApprovalDecision decision, String rejectionReason);

    ReturnRequest issueRefund(User admin, ReturnRequest returnRequest);

    AdministrativeReport consultAdministrativeReport(User admin);

    List<AuditLog> consultAuditLog(User admin, OperationType operationType);
}
