package com.company.xvspellconverter.view.anonymousmainviewtopmenu;

import com.company.xvspellconverter.app.ConvertXVText;
import com.company.xvspellconverter.view.login.LoginView;
import com.google.common.base.Strings;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * To use the view as a main view don't forget to set
 * new value (see @ViewController) to 'jmix.ui.main-view-id' property.
 * Also, the route of this view (see @Route) must differ from the route of default MainView.
 */
@Route("")
@ViewController("AnonymousMainViewTopMenu")
@ViewDescriptor("anonymous-main-view-top-menu.xml")
@AnonymousAllowed
public class AnonymousMainViewTopMenu extends StandardMainView {

    @ViewComponent
    private JmixTextArea textAreaFrom;
    @ViewComponent
    private JmixTextArea textAreaTo;
    @Autowired
    private ViewNavigators viewNavigators;

    @Subscribe(id = "buttonClean", subject = "clickListener")
    public void onButtonCleanClick(final ClickEvent<JmixButton> event) {
        textAreaFrom.setValue("");
    }

    @Subscribe(id = "buttonConvert", subject = "clickListener")
    public void onButtonConvertClick(final ClickEvent<JmixButton> event) {
        String textFrom = textAreaFrom.getValue();
        if(textFrom.isBlank()){
            Notification notification = Notification
                    .show("Sem texto para converter!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        var convertXVText = new ConvertXVText();
        convertXVText.convert(textFrom);
        textAreaTo.setValue(convertXVText.getFinalText());
    }

    @Override
    protected void updateTitle() {
        super.updateTitle();

        String viewTitle = getTitleFromOpenedView();
        UiComponentUtils.findComponent(getContent(), "viewHeaderBox")
                .ifPresent(component -> component.setVisible(!Strings.isNullOrEmpty(viewTitle)));
    }

    @Subscribe(id = "loginButton", subject = "clickListener")
    public void onLoginButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(LoginView.class).navigate();
    }
}