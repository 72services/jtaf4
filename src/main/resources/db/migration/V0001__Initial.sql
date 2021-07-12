CREATE SEQUENCE athlete_seq;

CREATE TABLE athlete
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('athlete_seq') PRIMARY KEY,

    first_name      varchar(255) NOT NULL,
    last_name       varchar(255) NOT NULL,
    gender          char(1)      NOT NULL,
    year_of_birth   int          NOT NULL,

    club_id         bigint                DEFAULT NULL,
    organization_id bigint                DEFAULT NULL
);

CREATE SEQUENCE category_seq;

CREATE TABLE category
(
    id           bigint       NOT NULL DEFAULT NEXTVAL('category_seq') PRIMARY KEY,

    abbreviation varchar(255) NOT NULL,
    name         varchar(255) NOT NULL,
    gender       char(1)      NOT NULL,
    year_from    int          NOT NULL,
    year_to      int          NOT NULL,

    series_id    bigint                DEFAULT NULL
);

CREATE TABLE category_athlete
(
    category_id bigint NOT NULL,
    athlete_id  bigint NOT NULL
);

CREATE TABLE category_event
(
    category_id bigint NOT NULL,
    event_id    bigint NOT NULL,

    position    int    NOT NULL
);

CREATE SEQUENCE club_seq;

CREATE TABLE club
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('club_seq') PRIMARY KEY,

    abbreviation    varchar(255) NOT NULL,
    name            varchar(255) NOT NULL,

    organization_id bigint                DEFAULT NULL
);

CREATE SEQUENCE competition_seq;

CREATE TABLE competition
(
    id                        bigint       NOT NULL DEFAULT NEXTVAL('competition_seq') PRIMARY KEY,

    name                      varchar(255) NOT NULL,
    competition_date          date         NOT NULL,
    always_first_three_medals boolean      NOT NULL DEFAULT false,
    medal_percentage          int          NOT NULL,
    locked                    boolean      NOT NULL DEFAULT false,

    series_id                 bigint                DEFAULT NULL
);

CREATE SEQUENCE event_seq;

CREATE TABLE event
(
    id              bigint           NOT NULL DEFAULT NEXTVAL('event_seq') PRIMARY KEY,

    abbreviation    varchar(255)              DEFAULT NULL,
    name            varchar(255)              DEFAULT NULL,
    gender          char(1)                   DEFAULT NULL,
    event_type      varchar(255)              DEFAULT NULL,
    a               double precision NOT NULL,
    b               double precision NOT NULL,
    c               double precision NOT NULL,

    organization_id bigint                    DEFAULT NULL
);

CREATE SEQUENCE organization_seq;

CREATE TABLE organization
(
    id               bigint       NOT NULL DEFAULT NEXTVAL('organization_seq') PRIMARY KEY,

    organization_key varchar(255) NOT NULL,
    name             varchar(255) NOT NULL,
    owner            varchar(255) NOT NULL
);

CREATE TABLE organization_user
(
    organization_id bigint NOT NULL,
    user_id         bigint NOT NULL
);

CREATE SEQUENCE result_seq;

CREATE TABLE result
(
    id             bigint       NOT NULL DEFAULT NEXTVAL('result_seq') PRIMARY KEY,

    position       int          NOT NULL,
    result         varchar(255) NOT NULL,
    points         int          NOT NULL,

    athlete_id     bigint       NOT NULL,
    category_id    bigint       NOT NULL,
    competition_id bigint       NOT NULL,
    event_id       bigint       NOT NULL
);

CREATE SEQUENCE security_group_seq;

CREATE TABLE security_group
(
    id   bigint       NOT NULL DEFAULT NEXTVAL('security_group_seq') PRIMARY KEY,

    name varchar(255) NOT NULL
);

CREATE SEQUENCE security_user_seq;

CREATE TABLE security_user
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('security_user_seq') PRIMARY KEY,

    first_name      varchar(255) NOT NULL,
    last_name       varchar(255) NOT NULL,
    email           varchar(255) NOT NULL,
    secret          varchar(255) NOT NULL,

    confirmation_id varchar(255),
    confirmed       boolean               DEFAULT false
);

CREATE SEQUENCE series_seq;

CREATE TABLE series
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('series_seq') PRIMARY KEY,

    name            varchar(255) NOT NULL,
    logo            bytea,
    hidden          boolean      NOT NULL DEFAULT false,
    locked          boolean      NOT NULL DEFAULT false,

    organization_id bigint                DEFAULT NULL
);

CREATE TABLE user_group
(
    user_id  bigint NOT NULL,
    group_id bigint NOT NULL
);

ALTER TABLE athlete
    ADD CONSTRAINT fk_athlete_club FOREIGN KEY (club_id) REFERENCES club (id);
ALTER TABLE athlete
    ADD CONSTRAINT fk_athlete_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE category
    ADD CONSTRAINT fk_category_series FOREIGN KEY (series_id) REFERENCES series (id);

ALTER TABLE category_athlete
    ADD PRIMARY KEY (athlete_id, category_id);
ALTER TABLE category_athlete
    ADD CONSTRAINT fk_category_athlete_athlete FOREIGN KEY (athlete_id) REFERENCES athlete (id);
ALTER TABLE category_athlete
    ADD CONSTRAINT fk_category_athlete_category FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE category_event
    ADD PRIMARY KEY (category_id, event_id);
ALTER TABLE category_event
    ADD CONSTRAINT fk_category_event_category FOREIGN KEY (category_id) REFERENCES category (id);
ALTER TABLE category_event
    ADD CONSTRAINT fk_category_event_event FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE club
    ADD CONSTRAINT fk_club_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE competition
    ADD CONSTRAINT fk_competition_series FOREIGN KEY (series_id) REFERENCES series (id);

ALTER TABLE event
    ADD CONSTRAINT fk_event_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE organization
    ADD CONSTRAINT uk_organization_key UNIQUE (organization_key);

ALTER TABLE organization_user
    ADD PRIMARY KEY (organization_id, user_id);
ALTER TABLE organization_user
    ADD CONSTRAINT fk_organization_user_organzation FOREIGN KEY (organization_id) REFERENCES organization (id);
ALTER TABLE organization_user
    ADD CONSTRAINT fk_organization_user_user FOREIGN KEY (user_id) REFERENCES security_user (id);

ALTER TABLE result
    ADD CONSTRAINT fk_result_athlete FOREIGN KEY (athlete_id) REFERENCES athlete (id);
ALTER TABLE result
    ADD CONSTRAINT fk_result_category FOREIGN KEY (category_id) REFERENCES category (id);
ALTER TABLE result
    ADD CONSTRAINT fk_result_competition FOREIGN KEY (competition_id) REFERENCES competition (id);
ALTER TABLE result
    ADD CONSTRAINT fk_result_event FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE security_group
    ADD CONSTRAINT uk_security_group_name UNIQUE (name);
ALTER TABLE security_user
    ADD CONSTRAINT uk_security_user_email UNIQUE (email);

ALTER TABLE series
    ADD CONSTRAINT fk_series_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE user_group
    ADD PRIMARY KEY (group_id, user_id);
ALTER TABLE user_group
    ADD CONSTRAINT fk_user_group_user FOREIGN KEY (user_id) REFERENCES security_user (id);
ALTER TABLE user_group
    ADD CONSTRAINT fk_user_group_group FOREIGN KEY (group_id) REFERENCES security_group (id);
