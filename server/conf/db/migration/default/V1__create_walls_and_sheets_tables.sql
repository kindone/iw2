create table walls (id bigint auto_increment, x float not null, y float not null, scale float not null, title varchar(1024) not null, primary key (id));

create table sheets (id bigint auto_increment, x float not null, y float not null, width float not null, height float not null, content text not null, primary key (id));

create table sheets_in_wall (sheet_id bigint, wall_id bigint, primary key (sheet_id, wall_id));