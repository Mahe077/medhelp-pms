-- V010__create_domain_events_table.sql
CREATE TABLE audit_schema.domain_events (
                                            sequence_number BIGSERIAL PRIMARY KEY,
                                            event_id UUID NOT NULL UNIQUE,
                                            event_type VARCHAR(100) NOT NULL,
                                            event_version VARCHAR(10) NOT NULL,
                                            aggregate_type VARCHAR(50) NOT NULL,
                                            aggregate_id UUID NOT NULL,
                                            causation_id UUID,
                                            correlation_id UUID,
                                            occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                            user_id UUID,
                                            event_data JSONB NOT NULL,
                                            metadata JSONB,
                                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_domain_events_aggregate
    ON audit_schema.domain_events(aggregate_type, aggregate_id);

CREATE INDEX idx_domain_events_type
    ON audit_schema.domain_events(event_type);

CREATE INDEX idx_domain_events_occurred_at
    ON audit_schema.domain_events(occurred_at);

CREATE INDEX idx_domain_events_correlation
    ON audit_schema.domain_events(correlation_id);

CREATE INDEX idx_domain_events_user
    ON audit_schema.domain_events(user_id);

-- GIN index for JSONB queries
CREATE INDEX idx_domain_events_event_data_gin
    ON audit_schema.domain_events USING GIN (event_data);