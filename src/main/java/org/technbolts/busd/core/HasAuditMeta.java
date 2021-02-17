package org.technbolts.busd.core;

import java.time.Instant;

public interface HasAuditMeta {

    Instant createdAt() ;

    Caller createdBy() ;

    Instant updatedAt() ;

    Caller updatedBy() ;
}
