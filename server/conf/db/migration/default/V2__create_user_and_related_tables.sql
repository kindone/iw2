create table users (id bigint auto_increment, email varchar(1024) unique not null, password_hashed varchar(1024) not null, primary key (id));

create table walls_of_user (wall_id bigint not null, user_id bigint not null, primary key (wall_id, user_id));

insert into users (id, email, password_hashed) values(0, 'admin@infinitewall.com', '$2a$10$O37uE4ZF52d9vXX/qZ73eO2SiG76mrfzbdfoDQ7SbkWA3bEZGh8wi');