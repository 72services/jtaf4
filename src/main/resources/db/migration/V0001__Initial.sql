-- --------------------------------------------------------

--
-- Table structure for table athlete
--

CREATE TABLE athlete (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL ,
  gender char(1) NOT NULL ,
  year_of_birth int(11) NOT NULL,

  club_id bigint(20) DEFAULT NULL,
  organization_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table category
--

CREATE TABLE category (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  abbreviation varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  gender char(1) NOT NULL,
  year_from int(11) NOT NULL,
  year_to int(11) NOT NULL,

  series_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table category_athlete
--

CREATE TABLE category_athlete (
  category_id bigint(20) NOT NULL,
  athlete_id bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table category_event
--

CREATE TABLE category_event (
  category_id bigint(20) NOT NULL,
  event_id bigint(20) NOT NULL,

  position int(11) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table club
--

CREATE TABLE club (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  abbreviation varchar(255) NOT NULL,
  name varchar(255) NOT NULL,

  organization_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table competition
--

CREATE TABLE competition (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  name varchar(255) NOT NULL,
  competition_date date NOT NULL,
  always_first_three_medals bit(1) NOT NULL DEFAULT 0,
  medal_percentage int(2) NOT NULL,
  locked bit(1) NOT NULL DEFAULT 0,

  series_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table event
--

CREATE TABLE event (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  abbreviation varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  gender char(1) DEFAULT NULL,
  event_type varchar(255) DEFAULT NULL,
  a double NOT NULL,
  b double NOT NULL,
  c double NOT NULL,

  organization_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table organization
--

CREATE TABLE organization (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  organization_key varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  owner varchar(255) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table organization_user
--

CREATE TABLE organization_user (
  organization_id bigint(20) NOT NULL,
  user_id bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table result
--

CREATE TABLE result (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  position int(11) NOT NULL,
  result varchar(255) NOT NULL,
  points int(11) NOT NULL,

  athlete_id bigint(20) NOT NULL,
  category_id bigint(20) NOT NULL,
  competition_id bigint(20) NOT NULL,
  event_id bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table security_group
--

CREATE TABLE security_group (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  name varchar(255) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table security_user
--

CREATE TABLE security_user (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  secret varchar(255) NOT NULL,

  confirmation_id varchar(255),
  confirmed bit(1) DEFAULT 0
);

-- --------------------------------------------------------

--
-- Table structure for table series
--

CREATE TABLE series (
  id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,

  name varchar(255) NOT NULL,
  logo longblob,
  hidden bit(1) NOT NULL DEFAULT 0,
  locked bit(1) NOT NULL DEFAULT 0,

  organization_id bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table user_group
--

CREATE TABLE user_group (
  user_id bigint(20) NOT NULL,
  group_id bigint(20) NOT NULL
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table athlete
--
ALTER TABLE athlete ADD CONSTRAINT fk_athlete_club FOREIGN KEY (club_id) REFERENCES club (id);
ALTER TABLE athlete ADD CONSTRAINT fk_athlete_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

--
-- Indexes for table category
--
ALTER TABLE category ADD CONSTRAINT fk_category_series FOREIGN KEY (series_id) REFERENCES series (id);

--
-- Indexes for table category_athlete
--
ALTER TABLE category_athlete ADD PRIMARY KEY (athlete_id, category_id);
ALTER TABLE category_athlete ADD CONSTRAINT fk_category_athlete_athlete FOREIGN KEY (athlete_id) REFERENCES athlete (id);
ALTER TABLE category_athlete ADD CONSTRAINT fk_category_athlete_category FOREIGN KEY (category_id) REFERENCES category (id);

--
-- Indexes for table category_event
--
ALTER TABLE category_event ADD PRIMARY KEY (category_id, event_id);
ALTER TABLE category_event ADD CONSTRAINT fk_category_event_category FOREIGN KEY (category_id) REFERENCES category (id);
ALTER TABLE category_event ADD CONSTRAINT fk_category_event_event FOREIGN KEY (event_id) REFERENCES event (id);

--
-- Indexes for table club
--
ALTER TABLE club ADD CONSTRAINT fk_club_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

--
-- Indexes for table competition
--
ALTER TABLE competition ADD CONSTRAINT fk_competition_series FOREIGN KEY (series_id) REFERENCES series (id);

--
-- Indexes for table event
--
ALTER TABLE event ADD CONSTRAINT fk_event_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

--
-- Indexes for table organization
--
ALTER TABLE organization ADD CONSTRAINT uk_organization_key UNIQUE (organization_key);

--
-- Indexes for table organization_user
--
ALTER TABLE organization_user ADD PRIMARY KEY (organization_id, user_id);
ALTER TABLE organization_user ADD CONSTRAINT fk_organization_user_organzation FOREIGN KEY (organization_id) REFERENCES organization (id);
ALTER TABLE organization_user ADD CONSTRAINT fk_organization_user_user FOREIGN KEY (user_id) REFERENCES security_user (id);

--
-- Indexes for table result
--
ALTER TABLE result ADD CONSTRAINT fk_result_athlete FOREIGN KEY (athlete_id) REFERENCES athlete (id);
ALTER TABLE result ADD CONSTRAINT fk_result_category FOREIGN KEY (category_id) REFERENCES category (id);
ALTER TABLE result ADD CONSTRAINT fk_result_competition FOREIGN KEY (competition_id) REFERENCES competition (id);
ALTER TABLE result ADD CONSTRAINT fk_result_event FOREIGN KEY (event_id) REFERENCES event (id);

--
-- Indexes for table security_user
--
ALTER TABLE security_group ADD CONSTRAINT uk_security_group_name UNIQUE (name);

--
-- Indexes for table security_user
--
ALTER TABLE security_user ADD CONSTRAINT uk_security_user_email UNIQUE (email);

--
-- Indexes for table series
--
ALTER TABLE series ADD CONSTRAINT fk_series_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

--
-- Indexes for table user_group
--
ALTER TABLE user_group ADD PRIMARY KEY (group_id, user_id);
ALTER TABLE user_group ADD CONSTRAINT fk_user_group_user FOREIGN KEY (user_id) REFERENCES security_user (id);
ALTER TABLE user_group ADD CONSTRAINT fk_user_group_group FOREIGN KEY (group_id) REFERENCES security_group (id);
