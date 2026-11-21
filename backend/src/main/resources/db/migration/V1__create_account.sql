create schema if not exists app;

create table app.account
(
    account_id uuid primary key,
    name       text not null,
    email      text not null,
    document   text not null,
    password   text not null
);