create sequence athlete_seq;
create table athlete
(
    id              bigint       not null default next value for athlete_seq,
    first_name      varchar(255) not null,
    last_name       varchar(255) not null,
    gender          char(1)      not null,
    year_of_birth   int          not null,
    club_id         bigint                default null,
    organization_id bigint                default null,
    primary key (id)
);
create sequence category_seq;
create table category
(
    id           bigint       not null default next value for category_seq,
    abbreviation varchar(255) not null,
    name         varchar(255) not null,
    gender       char(1)      not null,
    year_from    int          not null,
    year_to      int          not null,
    series_id    bigint                default null,
    primary key (id)
);
create table category_athlete
(
    category_id bigint not null,
    athlete_id  bigint not null
);
create table category_event
(
    category_id bigint not null,
    event_id    bigint not null,
    position    int    not null
);
create sequence club_seq;
create table club
(
    id              bigint       not null default next value for club_seq,
    abbreviation    varchar(255) not null,
    name            varchar(255) not null,
    organization_id bigint                default null,
    primary key (id)
);
create sequence competition_seq;
create table competition
(
    id                        bigint       not null default next value for competition_seq,
    name                      varchar(255) not null,
    competition_date          date         not null,
    always_first_three_medals tinyint(1) not null default 0,
    medal_percentage          int          not null,
    locked                    tinyint(1) not null default 0,
    series_id                 bigint                default null,
    primary key (id)
);
create sequence event_seq;
create table event
(
    id              bigint not null default next value for event_seq,
    abbreviation    varchar(255)    default null,
    name            varchar(255)    default null,
    gender          char(1)         default null,
    event_type      varchar(255)    default null,
    a               double not null,
    b               double not null,
    c               double not null,
    organization_id bigint          default null,
    primary key (id)
);
create sequence organization_seq;
create table organization
(
    id               bigint       not null default next value for organization_seq,
    organization_key varchar(255) not null,
    name             varchar(255) not null,
    owner            varchar(255) not null,
    primary key (id)
);
create table organization_user
(
    organization_id bigint not null,
    user_id         bigint not null
);
create sequence result_seq;
create table result
(
    id             bigint       not null default next value for result_seq,
    position       int          not null,
    result         varchar(255) not null,
    points         int          not null,
    athlete_id     bigint       not null,
    category_id    bigint       not null,
    competition_id bigint       not null,
    event_id       bigint       not null,
    primary key (id)
);
create sequence security_group_seq;
create table security_group
(
    id   bigint       not null default next value for security_group_seq,
    name varchar(255) not null,
    primary key (id)
);
create sequence security_user_seq;
create table security_user
(
    id              bigint       not null default next value for security_user_seq,
    first_name      varchar(255) not null,
    last_name       varchar(255) not null,
    email           varchar(255) not null,
    secret          varchar(255) not null,
    confirmation_id varchar(255),
    confirmed       tinyint(1) default 0,
    primary key (id)
);
create sequence series_seq;
create table series
(
    id              bigint       not null default next value for series_seq,
    name            varchar(255) not null,
    logo            blob,
    hidden          tinyint(1) not null default 0,
    locked          tinyint(1) not null default 0,
    organization_id bigint                default null,
    primary key (id)
);
create table user_group
(
    user_id  bigint not null,
    group_id bigint not null
);
alter table athlete
    add constraint fk_athlete_club
        foreign key (club_id)
            references club (id);
alter table athlete
    add constraint fk_athlete_organization
        foreign key (organization_id)
            references organization (id);
alter table category
    add constraint fk_category_series
        foreign key (series_id)
            references series (id);
alter table category_athlete
    add primary key (athlete_id, category_id);
alter table category_athlete
    add constraint fk_category_athlete_athlete
        foreign key (athlete_id)
            references athlete (id);
alter table category_athlete
    add constraint fk_category_athlete_category
        foreign key (category_id)
            references category (id);
alter table category_event
    add primary key (category_id, event_id);
alter table category_event
    add constraint fk_category_event_category
        foreign key (category_id)
            references category (id);
alter table category_event
    add constraint fk_category_event_event
        foreign key (event_id)
            references event (id);
alter table club
    add constraint fk_club_organization
        foreign key (organization_id)
            references organization (id);
alter table competition
    add constraint fk_competition_series
        foreign key (series_id)
            references series (id);
alter table event
    add constraint fk_event_organization
        foreign key (organization_id)
            references organization (id);
alter table organization
    add constraint uk_organization_key
        unique (organization_key);
alter table organization_user
    add primary key (organization_id, user_id);
alter table organization_user
    add constraint fk_organization_user_organzation
        foreign key (organization_id)
            references organization (id);
alter table organization_user
    add constraint fk_organization_user_user
        foreign key (user_id)
            references security_user (id);
alter table result
    add constraint fk_result_athlete
        foreign key (athlete_id)
            references athlete (id);
alter table result
    add constraint fk_result_category
        foreign key (category_id)
            references category (id);
alter table result
    add constraint fk_result_competition
        foreign key (competition_id)
            references competition (id);
alter table result
    add constraint fk_result_event
        foreign key (event_id)
            references event (id);
alter table security_group
    add constraint uk_security_group_name
        unique (name);
alter table security_user
    add constraint uk_security_user_email
        unique (email);
alter table series
    add constraint fk_series_organization
        foreign key (organization_id)
            references organization (id);
alter table user_group
    add primary key (group_id, user_id);
alter table user_group
    add constraint fk_user_group_user
        foreign key (user_id)
            references security_user (id);
alter table user_group
    add constraint fk_user_group_group
        foreign key (group_id)
            references security_group (id);
