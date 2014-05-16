CREATE SCHEMA "admin";


-- i18n

CREATE SEQUENCE "admin"."i18n_id" AS INTEGER START WITH 0;

CREATE TABLE "admin"."i18n"(
  "id"    INTEGER     NOT NULL,
  "lang"  VARCHAR(7)  NOT NULL,
  "value" CLOB        NOT NULL
);

ALTER TABLE "admin"."i18n" ADD CONSTRAINT i18n_pk PRIMARY KEY ("id","lang");

-- domains

CREATE TABLE "admin"."domain" (
  "id"      INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "name"    VARCHAR(64) NOT NULL,
  "description" VARCHAR(512)
);

ALTER TABLE "admin"."domain" ADD CONSTRAINT domain_pk       PRIMARY KEY ("id");

-- users

CREATE TABLE "admin"."user"(
  "login"    VARCHAR(32) NOT NULL,
  "password" VARCHAR(32) NOT NULL,
  "firstname"     VARCHAR(64) NOT NULL,
  "lastname"     VARCHAR(64) NOT NULL,
  "email"     VARCHAR(64) NOT NULL
);

ALTER TABLE "admin"."user" ADD CONSTRAINT user_pk PRIMARY KEY ("login");

CREATE TABLE "admin"."role"(
  "name"    VARCHAR(32) NOT NULL
);


ALTER TABLE "admin"."role" ADD CONSTRAINT role_pk PRIMARY KEY ("name");

CREATE TABLE "admin"."domainrole"(
  "name"    VARCHAR(32) NOT NULL,
  "description" VARCHAR(512)
);

ALTER TABLE "admin"."domainrole" ADD CONSTRAINT domainrole_pk PRIMARY KEY ("name");


CREATE TABLE "admin"."user_x_role"(
  "login"   VARCHAR(32) NOT NULL,
  "role"    VARCHAR(32) NOT NULL
);

ALTER TABLE "admin"."user_x_role" ADD CONSTRAINT user_x_role_pk PRIMARY KEY ("login", "role");
ALTER TABLE "admin"."user_x_role" ADD CONSTRAINT user_x_role_login_fk FOREIGN KEY ("login") REFERENCES "admin"."user"("login");
ALTER TABLE "admin"."user_x_role" ADD CONSTRAINT user_x_role_role_fk FOREIGN KEY ("role") REFERENCES "admin"."role"("name");


CREATE TABLE "admin"."user_x_domain_x_domainrole"(
  "login"   VARCHAR(32) NOT NULL,
  "domain_id"    INTEGER NOT NULL,
  "domainrole" VARCHAR(32) NOT NULL
);

ALTER TABLE "admin"."user_x_domain_x_domainrole" ADD CONSTRAINT user_x_domain_x_role_pk PRIMARY KEY ("login", "domain_id", "domainrole");
ALTER TABLE "admin"."user_x_domain_x_domainrole" ADD CONSTRAINT user_x_domain_x_role_login_fk FOREIGN KEY ("login") REFERENCES "admin"."user"("login");
ALTER TABLE "admin"."user_x_domain_x_domainrole" ADD CONSTRAINT user_x_domain_x_role_domain_fk FOREIGN KEY ("domain_id") REFERENCES "admin"."domain"("id");
ALTER TABLE "admin"."user_x_domain_x_domainrole" ADD CONSTRAINT user_x_domain_x_role_role_fk FOREIGN KEY ("domainrole") REFERENCES "admin"."domainrole"("name");


-- sensors

CREATE TABLE "admin"."sensor"(
  "id"          INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "identifier"  VARCHAR(512) NOT NULL UNIQUE,
  "type"        VARCHAR(64)  NOT NULL,
  "parent"      VARCHAR(512) NOT NULL,
  "owner"       VARCHAR(32),
  "metadata"    CLOB
);

ALTER TABLE "admin"."sensor" ADD CONSTRAINT sensor_pk       PRIMARY KEY ("id");
ALTER TABLE "admin"."sensor" ADD CONSTRAINT sensor_owner_fk FOREIGN KEY ("owner") REFERENCES "admin"."user"("login");


-- providers

CREATE TABLE "admin"."provider"(
  "id"          INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "identifier"  VARCHAR(512) NOT NULL UNIQUE,
  "parent"      VARCHAR(512) NOT NULL,
  "type"        VARCHAR(8)  NOT NULL,
  "impl"        VARCHAR(32) NOT NULL,
  "config"      CLOB        NOT NULL,
  "owner"       VARCHAR(32),
  "metadata_id" VARCHAR(512),
  "metadata"    CLOB
);

ALTER TABLE "admin"."provider" ADD CONSTRAINT provider_pk       PRIMARY KEY ("id");
ALTER TABLE "admin"."provider" ADD CONSTRAINT provider_owner_fk FOREIGN KEY ("owner") REFERENCES "admin"."user"("login");


-- provider items

CREATE TABLE "admin"."style"(
  "id"          INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "name"        VARCHAR(512) NOT NULL,
  "provider"    INTEGER     NOT NULL,
  "type"        VARCHAR(32) NOT NULL,
  "date"        BIGINT      NOT NULL,
  "title"       INTEGER     NOT NULL,
  "description" INTEGER     NOT NULL,
  "body"        CLOB        NOT NULL,
  "owner"       VARCHAR(32)
);

ALTER TABLE "admin"."style" ADD CONSTRAINT style_pk          PRIMARY KEY ("id");
ALTER TABLE "admin"."style" ADD CONSTRAINT style_owner_fk    FOREIGN KEY ("owner")    REFERENCES "admin"."user"("login");
ALTER TABLE "admin"."style" ADD CONSTRAINT style_provider_fk FOREIGN KEY ("provider") REFERENCES "admin"."provider"("id") ON DELETE CASCADE;


-- Domain cross tables with for styles

CREATE TABLE "admin"."style_x_domain"(
  "style_id"   INTEGER NOT NULL,
  "domain_id"  INTEGER NOT NULL
);

ALTER TABLE "admin"."style_x_domain" ADD CONSTRAINT style_x_domain_pk PRIMARY KEY ("style_id", "domain_id");
ALTER TABLE "admin"."style_x_domain" ADD CONSTRAINT style_x_domain_login_fk FOREIGN KEY ("style_id") REFERENCES "admin"."style"("id");
ALTER TABLE "admin"."style_x_domain" ADD CONSTRAINT style_x_domain_domain_id_fk FOREIGN KEY ("domain_id") REFERENCES "admin"."domain"("id");




CREATE TABLE "admin"."data"(
  "id"            INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "name"          VARCHAR(512) NOT NULL,
  "namespace"     VARCHAR(256)NOT NULL,
  "provider"      INTEGER     NOT NULL,
  "type"          VARCHAR(32) NOT NULL,
  "subtype"       VARCHAR(32) NOT NULL DEFAULT '',
  "visible"       BOOLEAN     NOT NULL DEFAULT TRUE,
  "sensorable"    BOOLEAN     NOT NULL DEFAULT FALSE,
  "date"          BIGINT      NOT NULL,
  "title"         INTEGER     NOT NULL,
  "description"   INTEGER     NOT NULL,
  "owner"         VARCHAR(32),
  "metadata"      CLOB,
  "metadata_id"   VARCHAR(512),
  "iso_metadata"  CLOB
);

ALTER TABLE "admin"."data" ADD CONSTRAINT data_pk          PRIMARY KEY ("id");
ALTER TABLE "admin"."data" ADD CONSTRAINT data_owner_fk    FOREIGN KEY ("owner")    REFERENCES "admin"."user"("login");
ALTER TABLE "admin"."data" ADD CONSTRAINT data_provider_fk FOREIGN KEY ("provider") REFERENCES "admin"."provider"("id") ON DELETE CASCADE;

CREATE TABLE "admin"."sensored_data"(
  "sensor"      INTEGER     NOT NULL,
  "data"        INTEGER     NOT NULL
);

ALTER TABLE "admin"."sensored_data" ADD CONSTRAINT sensor_data_pk PRIMARY KEY ("sensor","data");
ALTER TABLE "admin"."sensored_data" ADD CONSTRAINT sensored_data_sensor_fk FOREIGN KEY ("sensor") REFERENCES "admin"."sensor"("id") ON DELETE CASCADE;
ALTER TABLE "admin"."sensored_data" ADD CONSTRAINT sensored_data_data_fk   FOREIGN KEY ("data")   REFERENCES "admin"."data"("id")   ON DELETE CASCADE;


-- Domain cross tables with for datas

CREATE TABLE "admin"."data_x_domain"(
  "data_id"   INTEGER NOT NULL,
  "domain_id"  INTEGER NOT NULL
);

ALTER TABLE "admin"."data_x_domain" ADD CONSTRAINT data_x_domain_pk PRIMARY KEY ("data_id", "domain_id");
ALTER TABLE "admin"."data_x_domain" ADD CONSTRAINT data_x_domain_login_fk FOREIGN KEY ("data_id") REFERENCES "admin"."data"("id");
ALTER TABLE "admin"."data_x_domain" ADD CONSTRAINT data_x_domain_domain_id_fk FOREIGN KEY ("domain_id") REFERENCES "admin"."domain"("id");



CREATE TABLE "admin"."crs"(
  "dataid"  INTEGER NOT NULL,
  "crscode" VARCHAR(64)
);

ALTER TABLE "admin"."crs" ADD CONSTRAINT crs_data_fk FOREIGN KEY ("dataid") REFERENCES "admin"."data"("id") ON DELETE CASCADE;


CREATE TABLE "admin"."styled_data"(
  "style"       INTEGER     NOT NULL,
  "data"        INTEGER     NOT NULL
);

ALTER TABLE "admin"."styled_data" ADD CONSTRAINT style_data_pk PRIMARY KEY ("style","data");
ALTER TABLE "admin"."styled_data" ADD CONSTRAINT style_fk      FOREIGN KEY ("style") REFERENCES "admin"."style"("id") ON DELETE CASCADE;
ALTER TABLE "admin"."styled_data" ADD CONSTRAINT data_fk       FOREIGN KEY ("data")  REFERENCES "admin"."data"("id")  ON DELETE CASCADE;


-- services

CREATE TABLE "admin"."service"(
  "id"          INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "identifier"  VARCHAR(512) NOT NULL,
  "type"        VARCHAR(32)  NOT NULL,
  "date"        BIGINT      NOT NULL,
  "title"       INTEGER     NOT NULL,
  "description" INTEGER     NOT NULL,
  "config"      CLOB,
  "owner"       VARCHAR(32),
  "metadata_id" VARCHAR(512),
  "metadata"    CLOB
);

ALTER TABLE "admin"."service" ADD CONSTRAINT service_pk       PRIMARY KEY ("id");
ALTER TABLE "admin"."service" ADD CONSTRAINT service_uq       UNIQUE ("identifier","type");
ALTER TABLE "admin"."service" ADD CONSTRAINT service_owner_fk FOREIGN KEY ("owner") REFERENCES "admin"."user"("login");


-- Domain cross tables with for services

CREATE TABLE "admin"."service_x_domain"(
  "service_id"   INTEGER NOT NULL,
  "domain_id"  INTEGER NOT NULL
);

ALTER TABLE "admin"."service_x_domain" ADD CONSTRAINT service_x_domain_pk PRIMARY KEY ("service_id", "domain_id");
ALTER TABLE "admin"."service_x_domain" ADD CONSTRAINT service_x_domain_layer_fk FOREIGN KEY ("service_id") REFERENCES "admin"."service"("id");
ALTER TABLE "admin"."service_x_domain" ADD CONSTRAINT service_x_domain_domain_id_fk FOREIGN KEY ("domain_id") REFERENCES "admin"."domain"("id");



CREATE TABLE "admin"."service_extra_config"(
  "id"          INTEGER     NOT NULL,
  "filename"    VARCHAR(32) NOT NULL,
  "content"     CLOB
);

ALTER TABLE "admin"."service_extra_config" ADD CONSTRAINT service_extra_config_pk  PRIMARY KEY ("id", "filename");
ALTER TABLE "admin"."service_extra_config" ADD CONSTRAINT service_extra_config_service_fk FOREIGN KEY ("id") REFERENCES "admin"."service"("id");

CREATE TABLE "admin"."service_metadata"(
  "id"          INTEGER     NOT NULL,
  "lang"        VARCHAR(3) NOT NULL,
  "content"     CLOB
);

ALTER TABLE "admin"."service_metadata" ADD CONSTRAINT service_metadata_pk  PRIMARY KEY ("id", "lang");
ALTER TABLE "admin"."service_metadata" ADD CONSTRAINT service_metadata_service_fk FOREIGN KEY ("id") REFERENCES "admin"."service"("id");


-- service items

CREATE TABLE "admin"."layer"(
  "id"           INTEGER     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
  "name"         VARCHAR(512) NOT NULL,
  "namespace"    VARCHAR(256)NOT NULL,
  "alias"        VARCHAR(512),
  "service"      INTEGER     NOT NULL,
  "data"         INTEGER     NOT NULL,
  "date"         BIGINT      NOT NULL,
  "title"        INTEGER     NOT NULL,
  "description"  INTEGER     NOT NULL,
  "config"       CLOB,
  "owner"        VARCHAR(32)
);

ALTER TABLE "admin"."layer" ADD CONSTRAINT layer_pk         PRIMARY KEY ("id");
ALTER TABLE "admin"."layer" ADD CONSTRAINT layer_uq         UNIQUE ("name","service");
ALTER TABLE "admin"."layer" ADD CONSTRAINT layer_service_fk FOREIGN KEY ("service") REFERENCES "admin"."service"("id") ON DELETE CASCADE;
ALTER TABLE "admin"."layer" ADD CONSTRAINT layer_data_fk    FOREIGN KEY ("data")  REFERENCES "admin"."data"("id")      ON DELETE CASCADE;
ALTER TABLE "admin"."layer" ADD CONSTRAINT layer_owner_fk   FOREIGN KEY ("owner") REFERENCES "admin"."user"("login");


-- Domain cross tables



CREATE TABLE "admin"."task"(
  "identifier"  VARCHAR(512) NOT NULL,
  "state"       VARCHAR(32) NOT NULL,
  "type"        VARCHAR(32) NOT NULL,
  "title"       INTEGER     NOT NULL,
  "description" INTEGER     NOT NULL,
  "start"       BIGINT      NOT NULL,
  "end"         BIGINT,
  "owner"       VARCHAR(32)
);

ALTER TABLE "admin"."task" ADD CONSTRAINT task_pk       PRIMARY KEY ("identifier");
ALTER TABLE "admin"."task" ADD CONSTRAINT task_owner_fk FOREIGN KEY ("owner") REFERENCES "admin"."user"("login");

CREATE TABLE "admin"."properties"(
  "key"    VARCHAR(32) NOT NULL,
  "value"  VARCHAR(64) NOT NULL
);
ALTER TABLE "admin"."properties" ADD CONSTRAINT properties_pk PRIMARY KEY ("key");


insert into "admin"."role" ("name") values('cstl-admin');
insert into "admin"."domainrole" ("name") values('manager');
insert into "admin"."domain" ("name", "description") values('default', 'default domain');

insert into "admin"."user" ("login", "password", "firstname", "lastname", "email" ) values('admin', '21232f297a57a5a743894a0e4a801fc3', 'Frédéric', 'Houbie', 'frederic.houbie@geomatys.com');
insert into "admin"."user_x_role" ("login", "role") values('admin', 'cstl-admin');

insert into "admin"."user_x_domain_x_domainrole" ("login", "domainrole", "domain_id") values('admin', 'manager', 1);
