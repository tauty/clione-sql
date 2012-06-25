/*
drop table employees;
drop table aaa;
drop table people;
drop table blobtest;
 */

CREATE TABLE employees
(
    ID                        CHAR(1) NOT NULL,
    SHAIN_NO                  CHAR(6) NOT NULL,
    SHOZOKU_BU_KA             VARCHAR2(30) NOT NULL,
    NAME                      VARCHAR2(20) NOT NULL,
    ADDRESS                   VARCHAR2(100) NOT NULL
)
/
CREATE TABLE aaa
(
    ID                        CHAR(1) NOT NULL,
    SHAIN_NO                  CHAR(6) NOT NULL,
    SHOZOKU_BU_KA             VARCHAR2(30) NOT NULL,
    NAME                      VARCHAR2(20) NOT NULL,
    ADDRESS                   VARCHAR2(100) NOT NULL
)
/
ALTER TABLE employees
    ADD(CONSTRAINT PK_EMPLOYEES PRIMARY KEY (ID) USING INDEX)
/
CREATE TABLE people
(
    id              NUMBER(3) NOT NULL,
    name            VARCHAR2(30) NOT NULL,
    age             NUMBER(3) NOT NULL
)
/
ALTER TABLE people
    ADD(CONSTRAINT PK_PEOPLE PRIMARY KEY (ID) USING INDEX)
/
CREATE TABLE blobtest
(
    id              INT(3) NOT NULL,
    data            MEDIUMBLOB,
    primary key(id)
)
/
CREATE TABLE sample
(
    id              INT(3) NOT NULL,
	bd				decimal(30, 10),
	bi				numeric(40),
	byt				tinyint,
	bytn			tinyint,
	bytp			tinyint,
	bytes			blob,
	dt				datetime,
	fl				float,
	fln				float,
	flp				float,
	lo				bigint,
	lon				bigint,
	lop				bigint,
	r				text,
	sh				smallint,
	shn				smallint,
	shp				smallint,
	sdate			date,
	ti				time,
	ts				timestamp,
	url				varchar(100),
    primary key(id)
)
/
insert into employees values(0,100000,'柔道部男子課','泰山天伍','tengo@judo.com');
insert into employees values(1,100001,'柔道部女子課','猪熊柔','yawara@urasawa.com');
insert into employees values(2,100002,'相撲部モンゴル課','朝青龍','asashoryu@docomo.com');
insert into employees values(3,100003,'バレー部ホモ課','河合俊一','shun1@homo.com');

insert into aaa values(0,100000,'柔道部男子課','泰山天伍','tengo@judo.com');
insert into aaa values(1,100001,'柔道部女子課','猪熊柔','yawara@urasawa.com');
insert into aaa values(2,100002,'相撲部モンゴル課','朝青龍','asashoryu@docomo.com');
insert into aaa values(3,100003,'バレー部ホモ課','河合俊一','shun1@homo.com');

insert into people values(0,'Takao Kawada',35);
insert into people values(1,'Takako Kawada',34);
insert into people values(2,'Tao Kawada',2);
insert into people values(3,'Hiromitsu Hara',31);
insert into people values(4,'Hiroko Hara',31);

INSERT
    INTO
        test.sample(
            id
            ,bd
            ,bi
            ,byt
            ,bytn
            ,bytp
            ,bytes
            ,dt
            ,fl
            ,fln
            ,flp
            ,lo
            ,lon
            ,lop
            ,r
            ,sh
            ,shn
            ,shp
            ,sdate
            ,ti
            ,ts
            ,url
        )
    VALUES
        (
            102
            ,21231230.1231000000
            ,12310298
            ,8
            ,null
            ,88
            ,'takoikanamako'
            ,'2012-06-18 18:30:00'
            ,2123.12
            ,null
            ,12312.2
            ,12312098
            ,null
            ,121232139980
            ,'Well, she was just 17. You know what I mean.'
            ,4587
            ,null
            ,1231
            ,'2012-06-22'
            ,'10:00:00'
            ,'2012-06-22 09:55:21'
            ,'http://localhost:8080/sample'
        );

commit;
