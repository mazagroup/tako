package org.springframework.roo.support.util;


import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>
 * </p>
 *
 * @author Neil Bartlett
 */
public class ModificationLock {

    private final AtomicInteger modifierCount = new AtomicInteger(0);

    /**
     * Perform a programmatic change. Changes are re-entrant, we can always excecute a new programmatic change inside an
     * existing one.
     *
     * @param runnable
     */
    public void modifyOperation(Runnable runnable) {
        try {
            modifierCount.incrementAndGet();
            runnable.run();
        } finally {
            modifierCount.decrementAndGet();
        }
    }

    /**
     * @return Whether a modification operation is ongoing.
     */
    public boolean isUnderModification() {
        return modifierCount.get() > 0;
    }

    /**
     * Perform an action only if no programmatic changes are in progress.
     *
     * @param runnable
     */
    public void ifNotModifying(Runnable runnable) {
        if (modifierCount.get() == 0) {
            runnable.run();
        }
    }
}
