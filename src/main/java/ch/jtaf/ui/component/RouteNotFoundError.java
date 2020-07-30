package ch.jtaf.ui.component;

import static com.vaadin.flow.component.Tag.DIV;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

@Tag(DIV)
public class RouteNotFoundError extends Component implements HasErrorParameter<NotFoundException> {
	
	private static final long serialVersionUID = 1L;

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo("");

        return HttpServletResponse.SC_TEMPORARY_REDIRECT;
    }
}
