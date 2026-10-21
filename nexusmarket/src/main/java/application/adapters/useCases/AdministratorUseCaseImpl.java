package application.adapters.useCases;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.services.returns.ApproveReturnService;
import application.domain.services.returns.ProcessRefundService;
import application.domain.services.returns.RejectReturnService;
import application.domain.services.seller.RegisterSellerService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.RegisterInternalUserService;
import application.domain.services.warehouse.CreateWarehouseService;
import application.domain.valueobjects.UserStatus;

import java.util.List;

public class AdministratorUseCaseImpl implements AdministratorPort {

    private final RegisterSellerService registerSellerService;
    private final CreateWarehouseService createWarehouseService;
    private final ApproveReturnService approveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ProcessRefundService processRefundService;
    private final RegisterInternalUserService registerInternalUserService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ConsultUserService consultUserService;

    public AdministratorUseCaseImpl(RegisterSellerService registerSellerService,
                                    CreateWarehouseService createWarehouseService,
                                    ApproveReturnService approveReturnService,
                                    RejectReturnService rejectReturnService,
                                    ProcessRefundService processRefundService,
                                    RegisterInternalUserService registerInternalUserService,
                                    ChangeUserStatusService changeUserStatusService,
                                    ConsultUserService consultUserService) {
        this.registerSellerService = registerSellerService;
        this.createWarehouseService = createWarehouseService;
        this.approveReturnService = approveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.processRefundService = processRefundService;
        this.registerInternalUserService = registerInternalUserService;
        this.changeUserStatusService = changeUserStatusService;
        this.consultUserService = consultUserService;
    }

    @Override
    public Seller registerSeller(User actor, RegisterSellerCommand command) {
        return registerSellerService.register(actor, command);
    }

    @Override
    public Warehouse createWarehouse(User actor, CreateWarehouseCommand command) {
        return createWarehouseService.create(actor, command);
    }

    @Override
    public User registerInternalUser(User actor, RegisterInternalUserCommand command) {
        return registerInternalUserService.register(actor, command);
    }

    @Override
    public User changeUserStatus(User actor, Long userId, UserStatus newStatus) {
        return changeUserStatusService.change(actor, userId, newStatus);
    }

    @Override
    public List<User> consultUsers(User actor) {
        return consultUserService.consultUsers(actor);
    }

    @Override
    public ReturnRequest approveReturn(User actor, Long returnId) {
        return approveReturnService.approve(actor, returnId);
    }

    @Override
    public ReturnRequest rejectReturn(User actor, Long returnId, String reason) {
        return rejectReturnService.reject(actor, returnId, reason);
    }

    @Override
    public Refund processRefund(User actor, Long refundId) {
        return processRefundService.process(actor, refundId);
    }
}