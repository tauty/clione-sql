CREATE TABLE sample
(
	id				NUMBER(3) NOT NULL,
	bd				decimal(30, 10),
	bi				number(38),
	byt				number(5),
	bytn			number(5),
	bytp			number(5),
	bytes			blob,
	dt				DATE,
	fl				BINARY_FLOAT,
	fln				BINARY_FLOAT,
	flp				BINARY_FLOAT,
	lo				number(38),
	lon				number(38),
	lop				number(38),
	r				clob,
	sh				number(10),
	shn				number(10),
	shp				number(10),
	sdate			date,
	ti				DATE,
	ts				timestamp,
	url				varchar(100),
	primary key(id)
)

INSERT
    INTO
        sample(
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
            100
            ,21231230.1231000001
            ,12310298
            ,8
            ,null
            ,88
            ,HEXTORAW('74616B6F696B616E616D616B6F')
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
            ,'1970-01-01 10:00:00'
            ,'2012-06-22 09:55:21'
            ,'http://localhost:8080/sample'
        );
