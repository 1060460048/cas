package org.apereo.cas.web.flow.resolver.impl;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.web.flow.authentication.BaseMultifactorAuthenticationProviderEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.audit.annotation.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This is {@link PrincipalAttributeMultifactorAuthenticationPolicyEventResolver}
 * that attempts to locate a principal attribute, match its value against
 * the provided pattern and decide the next event in the flow for the given service.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class PrincipalAttributeMultifactorAuthenticationPolicyEventResolver
        extends BaseMultifactorAuthenticationProviderEventResolver {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Override
    public Set<Event> resolveInternal(final RequestContext context) {
        final RegisteredService service = resolveRegisteredServiceInRequestContext(context);
        final Authentication authentication = WebUtils.getAuthentication(context);

        if (service == null || authentication == null) {
            logger.debug("No service or authentication is available to determine event for principal");
            return null;
        }

        final Principal principal = authentication.getPrincipal();
        if (StringUtils.isBlank(casProperties.getAuthn().getMfa().getGlobalPrincipalAttributeNameTriggers())) {
            logger.debug("Attribute name to determine event is not configured for {}", principal.getId());
            return null;
        }

        final Map<String, MultifactorAuthenticationProvider> providerMap =
                WebUtils.getAvailableMultifactorAuthenticationProviders(this.applicationContext);
        if (providerMap == null || providerMap.isEmpty()) {
            logger.error("No multifactor authentication providers are available in the application context");
            return null;
        }

        final Collection<MultifactorAuthenticationProvider> providers = flattenProviders(providerMap.values());
        if (providers.size() == 1 && StringUtils.isNotBlank(casProperties.getAuthn().getMfa().getGlobalPrincipalAttributeValueRegex())) {
            final MultifactorAuthenticationProvider provider = providers.iterator().next();
            logger.debug("Found a single multifactor provider {} in the application context", provider);
            return resolveEventViaPrincipalAttribute(principal,
                    org.springframework.util.StringUtils.commaDelimitedListToSet(casProperties.getAuthn().getMfa().getGlobalPrincipalAttributeNameTriggers()),
                    service, context, providers,
                    input -> input != null && input.toString().matches(casProperties.getAuthn().getMfa().getGlobalPrincipalAttributeValueRegex()));
        }

        return resolveEventViaPrincipalAttribute(principal,
                org.springframework.util.StringUtils.commaDelimitedListToSet(casProperties.getAuthn().getMfa().getGlobalPrincipalAttributeNameTriggers()),
                service, context, providers,
                input -> providers.stream()
                        .filter(provider -> input != null && provider.matches(input.toString()))
                        .count() > 0);
    }


    @Audit(action = "AUTHENTICATION_EVENT", actionResolverName = "AUTHENTICATION_EVENT_ACTION_RESOLVER",
            resourceResolverName = "AUTHENTICATION_EVENT_RESOURCE_RESOLVER")
    @Override
    public Event resolveSingle(final RequestContext context) {
        return super.resolveSingle(context);
    }
}
