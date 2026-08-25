package cn.tofocus.account.command;

import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.standard.AbstractShellComponent;

public abstract class BaseCommands extends AbstractShellComponent
{
    protected boolean confirmation()
    {
        ConfirmationInput component = new ConfirmationInput(getTerminal(), "确认执行", false);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        ConfirmationInputContext context = component.run(ConfirmationInputContext.empty());
        Boolean b = context.getResultValue();
        return b != null && b;
    }
}
