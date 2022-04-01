package software.amazon.organizations.organizationalunit;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.CreateOrganizationalUnitRequest;
import software.amazon.awssdk.services.organizations.model.CreateOrganizationalUnitResponse;
import software.amazon.awssdk.services.organizations.model.OrganizationalUnit;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy awsClientProxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<OrganizationsClient> orgsClient,
        final Logger logger) {

        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();

        String name = model.getName();
        String parentId = model.getParentId();

        // Call CreateOrganizationalUnit API
        logger.log(String.format("Requesting CreateOrganizationalUnit w/ name: %s and parentId: %s.", name, parentId));
        return ProgressEvent.progress(model, callbackContext)
            .then(progress ->
                awsClientProxy.initiate("AWS-Organizations-OrganizationalUnit::CreateOrganizationalUnit", orgsClient, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(Translator::translateToCreateOrganizationalUnitRequest)
                .makeServiceCall(this::createOrganizationalUnit)
                .stabilize(this::stabilized)
                .handleError((organizationsRequest, e, orgsClient1, model1, context) -> handleError(
                    organizationsRequest, e, orgsClient1, model1, context, logger))
                .progress()
            )
            .then(progress -> new ReadHandler().handleRequest(awsClientProxy, request, callbackContext, orgsClient, logger));
    }

    protected CreateOrganizationalUnitResponse createOrganizationalUnit(final CreateOrganizationalUnitRequest createOrganizationalUnitRequest, final ProxyClient<OrganizationsClient> orgsClient) {
        logger.log("Calling createOrganizationalUnit API.");
	    final CreateOrganizationalUnitResponse createOrganizationalUnitResponse = orgsClient.injectCredentialsAndInvokeV2(createOrganizationalUnitRequest, orgsClient.client()::createOrganizationalUnit);
	    return createOrganizationalUnitResponse;
	}

    private Boolean stabilized(CreateOrganizationalUnitRequest createOrganizationalUnitRequest, CreateOrganizationalUnitResponse createOrganizationalUnitResponse, ProxyClient<OrganizationsClient> orgsClient, ResourceModel model, CallbackContext callbackContext) {
        if (!StringUtils.isNullOrEmpty(createOrganizationalUnitResponse.organizationalUnit().id())) {
            model.setId(createOrganizationalUnitResponse.organizationalUnit().id());
            return true;
        }
        return false;
    }
}