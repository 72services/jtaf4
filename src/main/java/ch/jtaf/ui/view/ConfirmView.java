package ch.jtaf.ui.view;

import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@AnonymousAllowed
@Route(value = "confirm", layout = MainLayout.class)
public class ConfirmView extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final DSLContext dslContext;
    private final TransactionTemplate transactionTemplate;

    public ConfirmView(DSLContext dslContext, TransactionTemplate transactionTemplate) {
        this.dslContext = dslContext;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        QueryParameters queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        if (queryParameters.getParameters().containsKey("cf")) {
            List<String> cfs = queryParameters.getParameters().get("cf");
            var securityUser = dslContext
                .selectFrom(SECURITY_USER)
                .where(SECURITY_USER.CONFIRMATION_ID.eq(cfs.get(0)))
                .and(SECURITY_USER.CONFIRMED.eq(false))
                .fetchOne();

            if (securityUser != null) {
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    securityUser.setConfirmed(true);
                    securityUser.store();
                });
            }
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Confirm");
    }
}
