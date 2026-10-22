package application.infrastructure.scheduling;

import application.domain.models.Order;
import application.domain.services.order.ExpirePendingOrdersService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodically triggers the expiration of orders that were not paid in time.
 */
@Component
public class ExpirePendingOrdersScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpirePendingOrdersScheduler.class);

    private final ExpirePendingOrdersService expirePendingOrdersService;

    public ExpirePendingOrdersScheduler(ExpirePendingOrdersService expirePendingOrdersService) {
        this.expirePendingOrdersService = expirePendingOrdersService;
    }

    @Scheduled(fixedDelayString = "${business.expiration-check-ms:60000}")
    @Transactional
    public void expire() {
        List<Order> expired = expirePendingOrdersService.expireOverdueOrders();
        if (!expired.isEmpty()) {
            log.info("Expired {} unpaid order(s).", expired.size());
        }
    }
}
