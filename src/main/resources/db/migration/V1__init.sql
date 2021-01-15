CREATE TABLE IF NOT EXISTS tenants
(
    id         SERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    name       TEXT
);


CREATE TABLE IF NOT EXISTS topics
(
    id         SERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    tenant_id  INT,
    name       TEXT,

    CONSTRAINT topics_fk_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id)
    );

CREATE TABLE IF NOT EXISTS events
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    tenant_id  INT,
    topic_id   BIGINT,
    headers    JSONB,
    body       JSONB     NOT NULL,

    CONSTRAINT events_fk_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT events_fk_topics FOREIGN KEY (topic_id) REFERENCES topics (id)
    );
