create table wall_logs (wall_id bigint, log_id bigint, action_type int not null, action varchar(1024), primary key (wall_id, log_id));

create table sheet_logs (sheet_id bigint, log_id bigint, action_type int not null, action varchar(1024), primary key(sheet_id, log_id));

alter table walls add state_id bigint not null;

alter table sheets add state_id bigint not null;
