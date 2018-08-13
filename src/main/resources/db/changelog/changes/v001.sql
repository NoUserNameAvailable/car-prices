create TABLE car (
  car_id bigserial not null,
  brand varchar(50) not null,
  model varchar(50) not null,
  price double precision not null,
  primary key (car_id)
)