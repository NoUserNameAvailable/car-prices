CREATE TABLE job_log (
  job_log_id     bigserial not null,
  model          varchar(50),
  start_time     timestamp,
  end_time       timestamp,
  status         varchar,
  added_lines    integer,
  modified_lines integer,
  deleted_lines  integer,
  site           varchar(256),
  brand          varchar(50),
  url            varchar(256),
  primary key (job_log_id)
)