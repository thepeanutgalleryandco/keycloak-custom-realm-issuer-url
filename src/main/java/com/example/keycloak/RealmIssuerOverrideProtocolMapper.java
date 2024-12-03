package com.example.keycloak;

// Import statements for Keycloak's internal models, protocols, and other utilities
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapperConfigException;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom Keycloak Protocol Mapper to override the "iss" (issuer) field
 * in OIDC tokens with a specified Realm Issuer URL.
 *
 * This mapper allows administrators to configure a custom issuer URL that
 * replaces the default Realm Issuer URL in access tokens.
 */
public class RealmIssuerOverrideProtocolMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper,
        OIDCIDTokenMapper, UserInfoTokenMapper {

    // Unique identifier for this Protocol Mapper
    private static final String PROVIDER_ID = "realm-issuer-override-protocol-mapper";
    // Configuration property key for the custom Realm Issuer URL
    private static final String REALM_URL_PROPERTY = "realmIssuerUrl";
    // Configuration properties for this mapper
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    // Static block to initialize configuration properties
    static {
        // Define the "Realm Issuer URL" configuration property
        ProviderConfigProperty realmIssuerUrlProperty = new ProviderConfigProperty();
        realmIssuerUrlProperty.setHelpText("URL to use as the 'iss' (issuer) field in the token."); // Description
        realmIssuerUrlProperty.setLabel("Realm Issuer URL"); // Display name
        realmIssuerUrlProperty.setName(REALM_URL_PROPERTY); // Internal property name
        realmIssuerUrlProperty.setRequired(true); // Mark as required
        realmIssuerUrlProperty.setType(ProviderConfigProperty.STRING_TYPE); // Specify the data type
        configProperties.add(realmIssuerUrlProperty); // Add to the list of properties
    }

    /**
     * Retrieves the configured Realm Issuer URL from the Protocol Mapper model.
     *
     * @param mappingModel The Protocol Mapper model containing the configuration.
     * @return The configured Realm Issuer URL.
     * @throws ProtocolMapperConfigException If the configuration is missing or invalid.
     */
    private String getRealmIssuer(ProtocolMapperModel mappingModel) throws ProtocolMapperConfigException {
        return mappingModel.getConfig().get(REALM_URL_PROPERTY);
    }

    /**
     * Returns the list of configuration properties defined for this Protocol Mapper.
     *
     * @return List of configuration properties.
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    /**
     * Returns the category under which this Protocol Mapper will appear in the admin console.
     *
     * @return The display category.
     */
    @Override
    public String getDisplayCategory() {
        return "Token Mapper";
    }

    /**
     * Returns the display name of this Protocol Mapper in the admin console.
     *
     * @return The display type.
     */
    @Override
    public String getDisplayType() {
        return "Realm Issuer URL";
    }

    /**
     * Returns a brief description of the functionality provided by this Protocol Mapper.
     *
     * @return Help text for the admin console.
     */
    @Override
    public String getHelpText() {
        return "Maps a custom Realm Issuer URL to the Access tokens";
    }

    /**
     * Returns the unique identifier for this Protocol Mapper.
     *
     * @return The provider ID.
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Transforms the access token by setting the custom Realm Issuer URL as the "iss" field.
     *
     * @param token The access token to transform.
     * @param mappingModel The Protocol Mapper model containing configuration.
     * @param session The current Keycloak session.
     * @param userSession The user session model.
     * @param clientSessionCtx The client session context.
     * @return The transformed access token.
     */
    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        try {
            // Set the custom issuer URL in the token
            return token.issuer(getRealmIssuer(mappingModel));
        } catch (ProtocolMapperConfigException e) {
            // Handle configuration errors by re-throwing as a runtime exception
            throw new RuntimeException(e);
        }
    }
}
