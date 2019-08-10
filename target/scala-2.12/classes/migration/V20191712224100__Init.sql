CREATE TABLE IF NOT EXISTS public.journal (
  ordering BIGSERIAL,
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  deleted BOOLEAN DEFAULT FALSE,
  tags VARCHAR(255) DEFAULT NULL,
  message BYTEA NOT NULL,
  PRIMARY KEY(persistence_id, sequence_number)
  );

CREATE UNIQUE INDEX journal_ordering_idx ON public.journal(ordering);

CREATE TABLE IF NOT EXISTS public.snapshot (
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  created BIGINT NOT NULL,
  snapshot BYTEA NOT NULL,
  PRIMARY KEY(persistence_id, sequence_number)
);


CREATE TABLE IF NOT EXISTS users(
  username varchar(256),
  full_name varchar(256),
  password varchar(256),
  created_at TIMESTAMP,
  repuattion int default 0,
  PRIMARY KEY (username)
);