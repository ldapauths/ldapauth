package com.ldapauth.authz.cas.endpoint.ticket;

import com.ldapauth.pojo.entity.apps.details.AppsCasDetails;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Abstract implementation of a ticket that handles all ticket state for
 * policies. Also incorporates properties common among all tickets. As this is
 * an abstract class, it cannnot be instanciated. It is recommended that
 * implementations of the Ticket interface extend the AbstractTicket as it
 * handles common functionality amongst different ticket types (such as state
 * updating).
 *
 * AbstractTicket does not provide a logger instance to
 * avoid instantiating many such Loggers at runtime (there will be many instances
 * of subclasses of AbstractTicket in a typical running CAS server).  Instead
 * subclasses should use static Logger instances.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public abstract class AbstractTicket implements Ticket {

    private static final long serialVersionUID = -8506442397878267555L;



    /** The unique identifier for this ticket. */
    @Id
    @Column(name="ID", nullable=false)
    protected String id;

    /** The last time this ticket was used. */
    @Column(name="LAST_TIME_USED")
    protected ZonedDateTime lastTimeUsed;

    /** The previous last time this ticket was used. */
    @Column(name="PREVIOUS_LAST_TIME_USED")
    protected ZonedDateTime previousLastTimeUsed;

    /** The time the ticket was created. */
    @Column(name="CREATION_TIME")
    protected ZonedDateTime creationTime;

    /** The number of times this was used. */
    @Column(name="NUMBER_OF_TIMES_USED")
    protected int countOfUses;

    protected Authentication authentication;

    protected AppsCasDetails casDetails;
    /**
     * Instantiates a new abstract ticket.
     */
    protected AbstractTicket() {
        // nothing to do
    }

    /**
     * Constructs a new Ticket with a unique id, a possible parent Ticket (can
     * be null) and a specified Expiration Policy.
     *
     * @param id the unique identifier for the ticket
     * @param expirationPolicy the expiration policy for the ticket.
     * @throws IllegalArgumentException if the id or expiration policy is null.
     */
    public AbstractTicket(final String id) {
        Assert.notNull(id, "id cannot be null");

        this.id = id;
        this.creationTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.lastTimeUsed = ZonedDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void update() {
        this.previousLastTimeUsed = this.lastTimeUsed;
        this.lastTimeUsed = ZonedDateTime.now(ZoneOffset.UTC);
        this.countOfUses++;
    }

    @Override
    public int getCountOfUses() {
        return this.countOfUses;
    }

    @Override
    public ZonedDateTime getCreationTime() {
        return this.creationTime;
    }

    @Override
    public ZonedDateTime getLastTimeUsed() {
        return this.lastTimeUsed;
    }

    @Override
    public ZonedDateTime getPreviousTimeUsed() {
        return this.previousLastTimeUsed;
    }

    @Override
    public boolean isExpired() {
        return  isExpiredInternal();
    }

    protected boolean isExpiredInternal() {
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 133).append(this.getId()).toHashCode();
    }

    @Override
    public String toString() {
        return this.getId();
    }

	@Override
	public AppsCasDetails getCasDetails() {
		return this.casDetails;
	}

	@Override
	public Authentication getAuthentication() {
		return this.authentication;
	}

    @Override
    public int compareTo(final Ticket o) {
        return getId().compareTo(o.getId());
    }
}
