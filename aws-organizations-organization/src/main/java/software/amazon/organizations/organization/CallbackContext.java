package software.amazon.organizations.organization;

import software.amazon.cloudformation.proxy.StdCallbackContext;

import java.util.HashMap;
import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {
    private Map<String, Integer> actionToRetryAttemptMap = new HashMap<>();

    // Used to set Propagation Delay in the CreateHandler call chain.
    public boolean propagationDelay = false;
    // used in CREATE handler re-invoking
    private boolean orgCreated = false;

    public int getCurrentRetryAttempt(final OrganizationConstants.Action actionName, final OrganizationConstants.Handler handlerName) {
        String key = actionName.toString() + handlerName.toString();
        return this.actionToRetryAttemptMap.getOrDefault(key, 0);
    }
    public void setCurrentRetryAttempt(final OrganizationConstants.Action actionName, final OrganizationConstants.Handler handlerName) {
        String key = actionName.toString() + handlerName.toString();
        this.actionToRetryAttemptMap.put(key, getCurrentRetryAttempt(actionName, handlerName) + 1);
    }
}
