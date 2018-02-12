# --- !Ups

CREATE TABLE User (
  id              BIGINT(20)   NOT NULL                                                    AUTO_INCREMENT,
  email           VARCHAR(128) NOT NULL,
  password        VARCHAR(255) NOT NULL,
  firstName       VARCHAR(50)  NOT NULL,
  lastName        VARCHAR(50)  NOT NULL,
  userRole        VARCHAR(20)  NOT NULL,
  summonerName    VARCHAR(50)                                                              DEFAULT NULL,
  summonerId      BIGINT(20)                                                               DEFAULT NULL,
  region          VARCHAR(5)                                                               DEFAULT NULL,
  teamId          BIGINT(20)                                                               DEFAULT NULL,
  activated       BOOLEAN                                                                  DEFAULT FALSE,
  verified        BOOLEAN                                                                  DEFAULT FALSE,
  roles           SET ('Middle', 'Jungle', 'Top', 'Bottom', 'Support', 'Coach', 'Analyst') DEFAULT NULL,
  description     TEXT                                                                     DEFAULT NULL,
  discordId       VARCHAR(100)                                                             DEFAULT NULL,
  profileImageUrl VARCHAR(100)                                                             DEFAULT NULL,
  timezone        VARCHAR(30)                                                              DEFAULT 'EST',
  rank            VARCHAR(20)                                                              DEFAULT NULL,
  updatedAt       TIMESTAMP                                                                DEFAULT NOW() ON UPDATE NOW(),
  createdAt       TIMESTAMP                                                                DEFAULT NOW(),

  CONSTRAINT user_pk PRIMARY KEY (id),
  UNIQUE INDEX email_idx (email),
  UNIQUE INDEX summoner_idx (summonerName, region),
  UNIQUE INDEX summoner_id_idx (summonerId, region),
  INDEX team_id_idx (teamId)
);

CREATE TABLE UserAlt (
  userId       BIGINT(20)  NOT NULL,
  summonerName VARCHAR(50) NOT NULL,
  summonerId   BIGINT(20)  NOT NULL,
  region       VARCHAR(5)  NOT NULL,

  UNIQUE INDEX summoner_idx (summonerName, region),
  UNIQUE INDEX summoner_id_idx (summonerId, region),
  CONSTRAINT user_alts_user_id FOREIGN KEY (userId) REFERENCES User (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE AuthToken (
  id      VARCHAR(50) NOT NULL,
  userId BIGINT(20)  NOT NULL,
  expiry  TIMESTAMP   NOT NULL,

  CONSTRAINT auth_token_pk PRIMARY KEY (id),
  CONSTRAINT auth_token_user_id FOREIGN KEY (userId) REFERENCES User (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE VerificationCode (
  userId BIGINT(20)  NOT NULL,
  code    VARCHAR(50) NOT NULL,

  CONSTRAINT verification_code_pk PRIMARY KEY (userId),
  CONSTRAINT verification_code_user_id FOREIGN KEY (userId) REFERENCES User (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE VerificationCode;
DROP TABLE AuthToken;
DROP TABLE User;