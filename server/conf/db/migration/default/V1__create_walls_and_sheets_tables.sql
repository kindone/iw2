create table walls (id bigint, name varchar(1024), primary key (id));

create table sheets (id bigint, name varchar(1024), x float, y float, width float, height float, primary key (id));

create table sheets_in_wall (sheet_id bigint, wall_id bigint, primary key (sheet_id, wall_id));