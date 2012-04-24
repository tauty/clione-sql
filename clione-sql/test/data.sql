CREATE TABLE employees
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
    id              CHAR(1) NOT NULL,
    name            VARCHAR2(30) NOT NULL,
    age             NUMBER(3) NOT NULL
)
/
ALTER TABLE people
    ADD(CONSTRAINT PK_PEOPLE PRIMARY KEY (ID) USING INDEX)

insert into EMPLOYEES values(0,100000,'柔道部男子課','泰山天伍','tengo@judo.com');
insert into EMPLOYEES values(1,100001,'柔道部女子課','猪熊柔','yawara@urasawa.com');
insert into EMPLOYEES values(2,100002,'相撲部モンゴル課','朝青龍','asashoryu@docomo.com');
insert into EMPLOYEES values(3,100003,'バレー部ホモ課','河合俊一','shun1@homo.com');

insert into people values(0,'Takao Kawada',35);
insert into people values(1,'Takako Kawada',34);
insert into people values(2,'Tao Kawada',2);
insert into people values(3,'Hiromitsu Hara',31);
insert into people values(4,'Hiroko Hara',31);

commit;
