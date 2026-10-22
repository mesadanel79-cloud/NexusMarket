package application.adapters.useCases;

import application.domain.enums.ApprovalDecision;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdminPort;
import application.domain.services.buyer.ChangeBuyerStatusService;
import application.domain.services.operation.ConsultOperationsService;
import application.domain.services.report.ConsultAdministrativeReportService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.returns.IssueRefundService;
import application.domain.services.returns.ReviewReturnService;
import application.domain.services.seller.ChangeSellerStatusService;
import application.domain.services.seller.ConsultSellersService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUsersService;
import application.domain.services.user.RegisterSellerService;
import application.domain.services.user.RegisterStaffUserService;
import application.domain.services.warehouse.ChangeWarehouseStatusService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.services.warehouse.RegisterWarehouseService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUseCaseImpl implements AdminPort {

    private final RegisterSellerService registerSellerService;
    private final RegisterStaffUserService registerStaffUserService;
    private final ConsultUsersService consultUsersService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ChangeBuyerStatusService changeBuyerStatusService;
    private final ConsultSellersService consultSellersService;
    private final ChangeSellerStatusService changeSellerStatusService;
    private final RegisterWarehouseService registerWarehouseService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ChangeWarehouseStatusService changeWarehouseStatusService;
    private final ConsultReturnsService consultReturnsService;
    private final ReviewReturnService reviewReturnService;
    private final IssueRefundService issueRefundService;
    private final ConsultAdministrativeReportService consultAdministrativeReportService;
    private final ConsultOperationsService consultOperationsService;

    public AdminUseCaseImpl(RegisterSellerService registerSellerService,
                            RegisterStaffUserService registerStaffUserService,
                            ConsultUsersService consultUsersService,
                            ChangeUserStatusService changeUserStatusService,
                            ChangeBuyerStatusService changeBuyerStatusService,
                            ConsultSellersService consultSellersService,
                            ChangeSellerStatusService changeSellerStatusService,
                            RegisterWarehouseService registerWarehouseService,
                            ConsultWarehousesService consultWarehousesService,
                            ChangeWarehouseStatusService changeWarehouseStatusService,
                            ConsultReturnsService consultReturnsService,
                            ReviewReturnService reviewReturnService,
                            IssueRefundService issueRefundService,
                            ConsultAdministrativeReportService consultAdministrativeReportService,
                            ConsultOperationsService consultOperationsService) {
        this.registerSellerService = registerSellerService;
        this.registerStaffUserService = registerStaffUserService;
        this.consultUsersService = consultUsersService;
        this.changeUserStatusService = changeUserStatusService;
        this.changeBuyerStatusService = changeBuyerStatusService;
        this.consultSellersService = consultSellersService;
        this.changeSellerStatusService = changeSellerStatusService;
        this.registerWarehouseService = registerWarehouseService;
        this.consultWarehousesService = consultWarehousesService;
        this.changeWarehouseStatusService = changeWarehouseStatusService;
        this.consultReturnsService = consultReturnsService;
        this.reviewReturnService = reviewReturnService;
        this.issueRefundService = issueRefundService;
        this.consultAdministrativeReportService = consultAdministrativeReportService;
        this.consultOperationsService = consultOperationsService;
    }

    @Override
    @Transactional
    public User registerSeller(User admin, User newSellerUser, Warehouse firstWarehouse) {
        return registerSellerService.register(admin, newSellerUser, firstWarehouse);
    }

    @Override
    @Transactional
    public User registerStaffUser(User admin, User newStaffUser) {
        return registerStaffUserService.register(admin, newStaffUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> consultUsers(User admin) {
        return consultUsersService.consultAll(admin);
    }

    @Override
    @Transactional
    public User changeUserStatus(User admin, User target, UserStatus newStatus) {
        return changeUserStatusService.changeStatus(admin, target, newStatus);
    }

    @Override
    @Transactional
    public Buyer changeBuyerStatus(User admin, Buyer buyer, BuyerStatus newStatus) {
        return changeBuyerStatusService.changeStatus(admin, buyer, newStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seller> consultSellers(User admin) {
        return consultSellersService.consultAll(admin);
    }

    @Override
    @Transactional
    public Seller changeSellerStatus(User admin, Seller seller, SellerStatus newStatus) {
        return changeSellerStatusService.changeStatus(admin, seller, newStatus);
    }

    @Override
    @Transactional
    public Warehouse registerWarehouse(User admin, Warehouse warehouse) {
        return registerWarehouseService.register(admin, warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> consultWarehouses(User admin) {
        return consultWarehousesService.consultAll(admin);
    }

    @Override
    @Transactional
    public Warehouse changeWarehouseStatus(User admin, Warehouse warehouse, WarehouseStatus newStatus) {
        return changeWarehouseStatusService.changeStatus(admin, warehouse, newStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> consultReturns(User admin, ReturnStatus status) {
        return consultReturnsService.consultByStatus(admin, status == null ? ReturnStatus.REQUESTED : status);
    }

    @Override
    @Transactional
    public ReturnRequest reviewReturn(User admin, ReturnRequest returnRequest, ApprovalDecision decision,
                                      String rejectionReason) {
        return reviewReturnService.review(admin, returnRequest, decision, rejectionReason);
    }

    @Override
    @Transactional
    public ReturnRequest issueRefund(User admin, ReturnRequest returnRequest) {
        return issueRefundService.issueRefund(admin, returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public AdministrativeReport consultAdministrativeReport(User admin) {
        return consultAdministrativeReportService.generate(admin);
    }

    @Override
    public List<AuditLog> consultAuditLog(User admin, OperationType operationType) {
        return consultOperationsService.consultAuditLog(admin, operationType);
    }
}
