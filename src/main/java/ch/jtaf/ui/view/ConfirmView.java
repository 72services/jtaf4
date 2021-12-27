package ch.jtaf.ui.view;

import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@AnonymousAllowed
@Route(value = "confirm", layout = MainLayout.class)
public class ConfirmView extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final DSLContext dslContext;
    private final TransactionTemplate transactionTemplate;
    private final VerticalLayout okDiv;
    private final H1 failure;

    public ConfirmView(DSLContext dslContext, TransactionTemplate transactionTemplate) {
        this.dslContext = dslContext;
        this.transactionTemplate = transactionTemplate;

        okDiv = new VerticalLayout();
        okDiv.add(new H1(getTranslation("Confirm.success")));
        okDiv.add(new RouterLink("Login", OrganizationsView.class));
        add(okDiv);

        failure = new H1(getTranslation("Confirm.failure"));
        add(failure);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        QueryParameters queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        if (queryParameters.getParameters().containsKey("cf")) {
            List<String> cfs = queryParameters.getParameters().get("cf");
            var securityUser = dslContext
                .selectFrom(SECURITY_USER)
                .where(SECURITY_USER.CONFIRMATION_ID.eq(cfs.get(0)))
                .fetchOne();

            if (securityUser != null) {
                AtomicBoolean ok = new AtomicBoolean(false);
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    securityUser.setConfirmed(true);
                    securityUser.store();
                    ok.set(true);
                });
                if (ok.get()) {
                    okDiv.setVisible(true);
                    failure.setVisible(false);
                } else {
                    okDiv.setVisible(false);
                    failure.setVisible(true);
                }
            }
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Confirm");
    }
}
