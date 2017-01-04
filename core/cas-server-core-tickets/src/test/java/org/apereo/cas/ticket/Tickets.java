package org.apereo.cas.ticket;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.BasicCredentialMetaData;
import org.apereo.cas.authentication.CredentialMetaData;
import org.apereo.cas.authentication.DefaultAuthentication;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.NullPrincipal;
import org.apereo.cas.ticket.support.TimeoutExpirationPolicy;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link Tickets} that facilitates the creation of CAS ticket objects used for testing purposes.
 *
 * @author Misagh Moayyed
 * @author David Rodriguez
 * @since 5.1.0
 */
public final class Tickets {

    protected Tickets() {
    }

    /**
     * Creates a new expired ticketGrantingTicket with this id.
     * @param id id that the ticket will have
     * @return ticketGrantingTicket created
     */
    public static TicketGrantingTicket createExpiredTicketGrantingTicket(final String id) {
        final TicketGrantingTicketImpl tgt = createTicketGrantingTicket(id);
        tgt.markTicketExpired();
        return tgt;
    }

    /**
     * Creates a new ticketGrantingTicket with this id.
     * @param id id that the ticket will have
     * @return ticketGrantingTicket created
     */
    public static TicketGrantingTicketImpl createTicketGrantingTicket(final String id) {
        final Map<String, HandlerResult> successes = new HashMap<>();
        successes.put("something", null);
        final CredentialMetaData meta = new BasicCredentialMetaData(new UsernamePasswordCredential());
        final ArrayList<CredentialMetaData> credentials = new ArrayList<>();
        credentials.add(meta);
        final Authentication defaultAuthentication = new DefaultAuthentication(ZonedDateTime.now(), credentials,
                NullPrincipal.getInstance(), new HashMap<>(),
                successes, new HashMap<>());
        final int timeToKillInSeconds = 3000;
        return new TicketGrantingTicketImpl(id, defaultAuthentication, new TimeoutExpirationPolicy(timeToKillInSeconds));
    }
}
