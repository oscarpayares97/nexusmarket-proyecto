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
import application.domain.services.warehouse.CreateWarehouseService;

public class AdministratorUseCaseImpl implements AdministratorPort {

    private final RegisterSellerService registerSellerService;
    private final CreateWarehouseService createWarehouseService;
    private final ApproveReturnService approveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ProcessRefundService processRefundService;

    public AdministratorUseCaseImpl(RegisterSellerService registerSellerService,
                                    CreateWarehouseService createWarehouseService,
                                    ApproveReturnService approveReturnService,
                                    RejectReturnService rejectReturnService,
                                    ProcessRefundService processRefundService) {
        this.registerSellerService = registerSellerService;
        this.createWarehouseService = createWarehouseService;
        this.approveReturnService = approveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.processRefundService = processRefundService;
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