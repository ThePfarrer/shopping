# --- !Ups
create table if not exists products(
name varchar not null,
code varchar  not null,
description varchar  not null,
price double precision not null,
primary key(code)
);

insert into products(name, code, description, price)
values
('NAO', 'ALD1', 'NAO is an humanoid robot', 3500),
('PEPPER', 'ALD2', 'PEPPER is a robot moving with wheels and with a screen as human interaction', 7000),
('BEOBOT', 'BE01', 'Beobot is a multipurpose robot', 159);


create table if not exists cart(
id serial ,
auser varchar  not null,
code varchar  not null,
qty int  not null,
primary key (id),
constraint uc_cart unique (auser, code)
--foreign key (code) references products(code)
);

# --- !Downs
drop table products;
drop table cart;
